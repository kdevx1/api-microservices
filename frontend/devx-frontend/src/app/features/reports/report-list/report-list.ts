import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReportService } from '../../../core/services/report.service';

interface ReportCard {
  title: string;
  description: string;
  icon: string;
  color: string;
  bg: string;
  excelFn: () => void;
  pdfFn: () => void;
}

@Component({
  selector: 'app-report-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './report-list.html',
  styleUrl: './report-list.scss'
})
export class ReportList {

  loading = signal<string | null>(null);

  reports: ReportCard[] = [
    {
      title: 'Usuários',
      description: 'Lista completa de usuários com perfis e status',
      icon: 'users',
      color: '#534AB7',
      bg: '#EEEDFE',
      excelFn: () => this.download('users-excel', () => this.reportService.downloadUsersExcel(), 'usuarios.xlsx'),
      pdfFn: () => this.download('users-pdf', () => this.reportService.downloadUsersPdf(), 'usuarios.pdf')
    },
    {
      title: 'Serviços',
      description: 'Catálogo de serviços e produtos por categoria',
      icon: 'services',
      color: '#185FA5',
      bg: '#E3F0FB',
      excelFn: () => this.download('services-excel', () => this.reportService.downloadServicesExcel(), 'servicos.xlsx'),
      pdfFn: () => this.download('services-pdf', () => this.reportService.downloadServicesPdf(), 'servicos.pdf')
    },
    {
      title: 'Ordens de Serviço',
      description: 'Histórico completo de ordens e seus status',
      icon: 'orders',
      color: '#3B6D11',
      bg: '#EAF3DE',
      excelFn: () => this.download('orders-excel', () => this.reportService.downloadOrdersExcel(), 'ordens.xlsx'),
      pdfFn: () => this.download('orders-pdf', () => this.reportService.downloadOrdersPdf(), 'ordens.pdf')
    }
  ];

  constructor(private reportService: ReportService) {}

  download(key: string, fn: () => any, filename: string) {
    if (this.loading()) return;
    this.loading.set(key);

    fn().subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        window.URL.revokeObjectURL(url);
        this.loading.set(null);
      },
      error: () => this.loading.set(null)
    });
  }

  isLoading(key: string): boolean {
    return this.loading() === key;
  }
}
