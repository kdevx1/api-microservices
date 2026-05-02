import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { NotificationResponse } from '../models/notification.model';
import { environment } from '../../../app/enviroment/environment';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private readonly api = `${environment.apiUrl}/notifications`;

  unreadCount = signal(0);
  unreadNotifications = signal<NotificationResponse[]>([]);

  constructor(private http: HttpClient) {}

  findUnread() {
    return this.http.get<NotificationResponse[]>(`${this.api}/unread`);
  }

  countUnread() {
    return this.http.get<number>(`${this.api}/unread/count`);
  }

  findAll(page = 0, size = 20) {
    return this.http.get<any>(`${this.api}?page=${page}&size=${size}`);
  }

  markAsRead(id: number) {
    return this.http.patch<void>(`${this.api}/${id}/read`, {});
  }

  markAllAsRead() {
    return this.http.patch<void>(`${this.api}/read-all`, {});
  }

  loadUnread() {
    this.findUnread().subscribe({
      next: (data) => {
        this.unreadNotifications.set(data);
        this.unreadCount.set(data.length);
      },
      error: (err) => console.error(err)
    });
  }
}
