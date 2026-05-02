import { Component, OnInit, signal, HostListener, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationResponse } from '../../../core/models/notification.model';
import { NotificationService } from '../../../core/services/notification.services';

@Component({
  selector: 'app-notifications',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.scss'
})
export class Notifications implements OnInit {

  isOpen = signal(false);
  activeTab = signal<'all' | 'orders' | 'system'>('all');

  constructor(public notificationService: NotificationService) {}

  ngOnInit() {
    this.notificationService.loadUnread();
    // Recarrega a cada 30 segundos
    setInterval(() => this.notificationService.loadUnread(), 30000);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.notif-wrap')) {
      this.isOpen.set(false);
    }
  }

  toggle() { this.isOpen.update(v => !v); }

  filteredNotifications = computed(() => {
    const all = this.notificationService.unreadNotifications();
    if (this.activeTab() === 'orders') return all.filter(n => n.type === 'ORDER' || n.type === 'SUCCESS' || n.type === 'WARNING');
    if (this.activeTab() === 'system') return all.filter(n => n.type === 'SYSTEM' || n.type === 'USER');
    return all;
  });

  markAsRead(notif: NotificationResponse, event: MouseEvent) {
    event.stopPropagation();
    if (notif.read) return;
    this.notificationService.markAsRead(notif.id).subscribe({
      next: () => this.notificationService.loadUnread()
    });
  }

  markAllAsRead() {
    this.notificationService.markAllAsRead().subscribe({
      next: () => this.notificationService.loadUnread()
    });
  }

  getIcon(type: string): string {
    const icons: Record<string, string> = {
      ORDER: 'order',
      SUCCESS: 'success',
      WARNING: 'warning',
      USER: 'user',
      SYSTEM: 'system'
    };
    return icons[type] ?? 'system';
  }

  formatTime(date: string): string {
    const d = new Date(date);
    const now = new Date();
    const diff = Math.floor((now.getTime() - d.getTime()) / 1000);
    if (diff < 60) return 'Agora mesmo';
    if (diff < 3600) return `${Math.floor(diff / 60)} min atrás`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h atrás`;
    return `${Math.floor(diff / 86400)} dias atrás`;
  }
}
