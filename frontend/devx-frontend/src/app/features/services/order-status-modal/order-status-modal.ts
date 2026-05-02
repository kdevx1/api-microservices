import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ServiceOrderResponse } from '../../../core/models/service.model';
import { ServiceService } from '../../../core/services/service.service';

@Component({
  selector: 'app-order-status-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-status-modal.html',
  styleUrl: './order-status-modal.scss'
})
export class OrderStatusModal implements OnChanges {

  @Input() visible = false;
  @Input() order: ServiceOrderResponse | null = null;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() updated = new EventEmitter<void>();

  loading = false;
  selectedStatus = '';

  statusOptions = [
    { label: 'Pendente', value: 'PENDING', color: '#C67C1A', bg: '#FEF3E2', desc: 'Aguardando atendimento' },
    { label: 'Em Andamento', value: 'IN_PROGRESS', color: '#185FA5', bg: '#E3F0FB', desc: 'Atendimento iniciado' },
    { label: 'Concluída', value: 'DONE', color: '#3B6D11', bg: '#EAF3DE', desc: 'Atendimento finalizado' },
    { label: 'Cancelada', value: 'CANCELLED', color: '#C0392B', bg: '#FDE8E8', desc: 'Ordem cancelada' }
  ];

  constructor(private serviceService: ServiceService) {}

  ngOnChanges(changes: SimpleChanges) {
    if (changes['order'] && this.order) {
      this.selectedStatus = this.order.status;
    }
  }

  selectStatus(status: string) { this.selectedStatus = status; }

  close() { this.visibleChange.emit(false); }

  onSubmit() {
    if (!this.order || !this.selectedStatus) return;
    this.loading = true;

    this.serviceService.updateOrderStatus(this.order.id, this.selectedStatus).subscribe({
      next: () => {
        this.loading = false;
        this.updated.emit();
        this.close();
      },
      error: () => { this.loading = false; }
    });
  }
}
