import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ServiceService } from '../../../core/services/service.service';
import { ServiceOrderResponse } from '../../../core/models/service.model';
import { OrderFormModal } from '../order-form-modal/order-form-modal';
import { OrderStatusModal } from '../order-status-modal/order-status-modal';

@Component({
  selector: 'app-service-orders',
  standalone: true,
  imports: [CommonModule, FormsModule, OrderFormModal, OrderStatusModal],
  templateUrl: './service-orders.html',
  styleUrl: './service-orders.scss'
})
export class ServiceOrders implements OnInit {

  orders = signal<ServiceOrderResponse[]>([]);
  totalElements = signal(0);
  totalPages = signal(0);
  currentPage = signal(0);
  pageSize = 10;
  loading = signal(false);

  selectedStatus = '';
  searchName = '';

  orderFormVisible = signal(false);
  statusModalVisible = signal(false);
  selectedOrder = signal<ServiceOrderResponse | null>(null);

  statusOptions = [
    { label: 'Todas', value: '', color: '#888780' },
    { label: 'Pendentes', value: 'PENDING', color: '#C67C1A' },
    { label: 'Em Andamento', value: 'IN_PROGRESS', color: '#185FA5' },
    { label: 'Concluídas', value: 'DONE', color: '#3B6D11' },
    { label: 'Canceladas', value: 'CANCELLED', color: '#C0392B' }
  ];

  constructor(private serviceService: ServiceService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.serviceService.getOrders({
      page: this.currentPage(),
      size: this.pageSize
    }).subscribe({
      next: (data) => {
        this.orders.set(data.content);
        this.totalElements.set(data.totalElements);
        this.totalPages.set(data.totalPages);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  filterByStatus(status: string) {
    this.selectedStatus = status;
    this.currentPage.set(0);
    this.load();
  }

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

  openStatusModal(order: ServiceOrderResponse) {
    this.selectedOrder.set(order);
    this.statusModalVisible.set(true);
  }

  openOrderForm() { this.orderFormVisible.set(true); }

  onOrderSaved() { this.load(); }
  onStatusUpdated() { this.load(); }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'pending',
      IN_PROGRESS: 'progress',
      DONE: 'done',
      CANCELLED: 'cancelled'
    };
    return map[status] ?? 'pending';
  }

  getStatusLabel(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'Pendente',
      IN_PROGRESS: 'Em Andamento',
      DONE: 'Concluída',
      CANCELLED: 'Cancelada'
    };
    return map[status] ?? status;
  }

  getInitials(name: string): string {
    return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
  }

  formatDate(date?: string): string {
    if (!date) return '—';
    return new Date(date).toLocaleDateString('pt-BR', {
      day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit'
    });
  }

  formatDateShort(date: string): string {
    return new Date(date).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
  }

  filteredOrders() {
    if (!this.selectedStatus) return this.orders();
    return this.orders().filter(o => o.status === this.selectedStatus);
  }
}
