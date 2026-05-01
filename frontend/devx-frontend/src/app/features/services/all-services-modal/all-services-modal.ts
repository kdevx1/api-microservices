import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ServiceResponse } from '../../../core/models/service.model';
import { ServiceService } from '../../../core/services/service.service';

@Component({
  selector: 'app-all-services-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './all-services-modal.html',
  styleUrl: './all-services-modal.scss'
})
export class AllServicesModal implements OnChanges {

  @Input() visible = false;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() editService = new EventEmitter<ServiceResponse>();

  services = signal<ServiceResponse[]>([]);
  totalElements = signal(0);
  totalPages = signal(0);
  currentPage = signal(0);
  pageSize = 8;
  loading = signal(false);

  searchName = '';
  selectedType = '';

  constructor(private serviceService: ServiceService) {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes['visible'] && this.visible) {
      this.currentPage.set(0);
      this.searchName = '';
      this.selectedType = '';
      this.load();
    }
  }

  load() {
    this.loading.set(true);
    this.serviceService.search({
      name: this.searchName || undefined,
      type: this.selectedType || undefined,
      page: this.currentPage(),
      size: this.pageSize
    }).subscribe({
      next: (data) => {
        this.services.set(data.content);
        this.totalElements.set(data.totalElements);
        this.totalPages.set(data.totalPages);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  onSearch() { this.currentPage.set(0); this.load(); }
  onTypeFilter(type: string) { this.selectedType = this.selectedType === type ? '' : type; this.currentPage.set(0); this.load(); }

  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages()) return;
    this.currentPage.set(page);
    this.load();
  }

  getPages(): number[] {
    const total = this.totalPages();
    const current = this.currentPage();
    const pages: number[] = [];
    const start = Math.max(0, current - 1);
    const end = Math.min(total - 1, start + 2);
    for (let i = start; i <= end; i++) pages.push(i);
    return pages;
  }

  onEdit(service: ServiceResponse) {
    this.editService.emit(service);
    this.close();
  }

  close() { this.visibleChange.emit(false); }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }
}
