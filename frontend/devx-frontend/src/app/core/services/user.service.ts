import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import { UserRequest, UpdateUserRequest, UserResponse } from '../models/user.model';
import { PageResponse } from '../models/page.model';
import { environment } from '../../../app/enviroment/environment';

@Injectable({ providedIn: 'root' })
export class UserService {

  private readonly api = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  register(request: UserRequest) {
    return this.http.post<UserResponse>(`${this.api}/register`, request);
  }

  search(filters: {
    email?: string;
    name?: string;
    role?: string;
    active?: boolean;
    page?: number;
    size?: number;
  }) {
    let params = new HttpParams();
    if (filters.email) params = params.set('email', filters.email);
    if (filters.name) params = params.set('name', filters.name);
    if (filters.role) params = params.set('role', filters.role);
    if (filters.active !== undefined) params = params.set('active', filters.active);
    params = params.set('page', filters.page ?? 0);
    params = params.set('size', filters.size ?? 10);

    return this.http.get<PageResponse<UserResponse>>(this.api, { params });
  }

  update(id: number, request: UpdateUserRequest) {
    return this.http.put<UserResponse>(`${this.api}/${id}`, request);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  toggleActive(id: number) {
    return this.http.patch<UserResponse>(`${this.api}/${id}/active`, {});
  }

  me() {
    return this.http.get<UserResponse>(`${this.api}/me`);
  }

  dashboard() {
    return this.http.get<any>(`${this.api}/dashboard`);
  }

  existsByEmail(email: string) {
    return this.http.get<boolean>(`${this.api}/exists`, {
      params: { email }
    });
  }

  uploadAvatar(formData: FormData) {
    return this.http.post<{ url: string }>(`${this.api}/avatar`, formData);
  }
}
