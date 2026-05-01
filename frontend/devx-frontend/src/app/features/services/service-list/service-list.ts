import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ServiceService } from '../../../core/services/service.service';
import { CategoryResponse, ServiceResponse, ServiceStatsResponse } from '../../../core/models/service.model';
import { AllServicesModal } from '../all-services-modal/all-services-modal';
import { ServiceFormModal } from '../service-form-modal/service-form-modal';
import { CategoryFormModal } from '../category-form-modal/category-form-modal';

@Component({
  selector: 'app-service-list',
  standalone: true,
  imports: [CommonModule, FormsModule, AllServicesModal, ServiceFormModal, CategoryFormModal],
  templateUrl: './service-list.html',
  styleUrl: './service-list.scss'
})
export class ServiceList implements OnInit {

  // Stats
  stats = signal<ServiceStatsResponse | null>(null);

  // Categories
  categories = signal<CategoryResponse[]>([]);
  selectedCategory = signal<CategoryResponse | null>(null);

  // Services
  services = signal<ServiceResponse[]>([]);
  totalElements = signal(0);
  totalPages = signal(0);
  currentPage = signal(0);
  pageSize = 10;

  // Filters
  searchName = '';
  selectedType = '';

  // Loading
  loadingStats = signal(false);
  loadingServices = signal(false);

  // Modals
  allServicesVisible = signal(false);
  serviceFormVisible = signal(false);
  categoryFormVisible = signal(false);
  editingService = signal<ServiceResponse | null>(null);
  editingCategory = signal<CategoryResponse | null>(null);

  // Chart data
  chartBars = computed(() => {
    const cats = this.categories();
    const max = Math.max(...cats.map(c => c.serviceCount), 1);
    return cats.map(c => ({
      label: c.name,
      color: c.color,
      height: Math.max(10, (c.serviceCount / max) * 70)
    }));
  });

  constructor(private serviceService: ServiceService) {}

  ngOnInit() {
    this.loadStats();
    this.loadCategories();
    this.loadServices();
  }

  loadStats() {
    this.loadingStats.set(true);
    this.serviceService.getStats().subscribe({
      next: (data) => { this.stats.set(data); this.loadingStats.set(false); },
      error: () => this.loadingStats.set(false)
    });
  }

  loadCategories() {
    this.serviceService.getCategories().subscribe({
      next: (data) => this.categories.set(data),
      error: (err) => console.error(err)
    });
  }

  loadServices() {
    this.loadingServices.set(true);
    this.serviceService.search({
      categoryId: this.selectedCategory()?.id,
      name: this.searchName || undefined,
      type: this.selectedType || undefined,
      page: this.currentPage(),
      size: this.pageSize
    }).subscribe({
      next: (data) => {
        this.services.set(data.content);
        this.totalElements.set(data.totalElements);
        this.totalPages.set(data.totalPages);
        this.loadingServices.set(false);
      },
      error: () => this.loadingServices.set(false)
    });
  }

  selectCategory(cat: CategoryResponse | null) {
    this.selectedCategory.set(cat);
    this.currentPage.set(0);
    this.searchName = '';
    this.selectedType = '';
    this.loadServices();
  }

  onSearch() {
    this.currentPage.set(0);
    this.loadServices();
  }

  onTypeFilter(type: string) {
    this.selectedType = this.selectedType === type ? '' : type;
    this.currentPage.set(0);
    this.loadServices();
  }

  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages()) return;
    this.currentPage.set(page);
    this.loadServices();
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

  openNewService() {
    this.editingService.set(null);
    this.serviceFormVisible.set(true);
  }

  openEditService(service: ServiceResponse) {
    this.editingService.set(service);
    this.serviceFormVisible.set(true);
  }

  openNewCategory() {
    this.editingCategory.set(null);
    this.categoryFormVisible.set(true);
  }

  deleteService(service: ServiceResponse) {
    if (!confirm(`Deseja desativar o serviço "${service.name}"?`)) return;
    this.serviceService.delete(service.id).subscribe({
      next: () => { this.loadServices(); this.loadStats(); },
      error: (err) => console.error(err)
    });
  }

  onServiceSaved() {
    this.loadServices();
    this.loadStats();
    this.loadCategories();
  }

  onCategorySaved() {
    this.loadCategories();
    this.loadStats();
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }

  formatRevenue(value: number): string {
    if (value >= 1000) return `R$${(value / 1000).toFixed(1)}k`;
    return this.formatCurrency(value);
  }
}
