import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { UserResponse } from '../../../core/models/user.model';
import { DashboardResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-list.html',
  styleUrl: './user-list.scss'
})
export class UserList implements OnInit {

  // Stats
  dashboard = signal<DashboardResponse | null>(null);

  // Tabela
  users = signal<UserResponse[]>([]);
  totalElements = signal(0);
  totalPages = signal(0);
  currentPage = signal(0);
  pageSize = 10;

  // Filtros
  searchName = '';
  searchEmail = '';

  loading = signal(false);

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.loadDashboard();
    this.loadUsers();
  }

  loadDashboard() {
    this.userService.dashboard().subscribe({
      next: (data) => this.dashboard.set(data),
      error: (err) => console.error('Erro ao carregar dashboard', err)
    });
  }

  loadUsers() {
    this.loading.set(true);
    this.userService.search({
      name: this.searchName || undefined,
      email: this.searchEmail || undefined,
      page: this.currentPage(),
      size: this.pageSize
    }).subscribe({
      next: (data) => {
        this.users.set(data.content);
        this.totalElements.set(data.totalElements);
        this.totalPages.set(data.totalPages);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar usuários', err);
        this.loading.set(false);
      }
    });
  }

  onSearch() {
    this.currentPage.set(0);
    this.loadUsers();
  }

  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages()) return;
    this.currentPage.set(page);
    this.loadUsers();
  }

  toggleActive(user: UserResponse) {
    this.userService.toggleActive(user.id).subscribe({
      next: () => this.loadUsers(),
      error: (err) => console.error('Erro ao alternar status', err)
    });
  }

  deleteUser(user: UserResponse) {
    if (!confirm(`Deseja desativar o usuário ${user.name}?`)) return;
    this.userService.delete(user.id).subscribe({
      next: () => this.loadUsers(),
      error: (err) => console.error('Erro ao deletar usuário', err)
    });
  }

  getInitials(name: string): string {
    return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
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
}
