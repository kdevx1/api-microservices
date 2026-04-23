import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../../models/user.model';
import { Observable } from 'rxjs';
import { environment } from '../../../../../environments/environment';
@Injectable({ providedIn: 'root' })
export class UserService {

  private http = inject(HttpClient);
  private API = 'http://localhost:8081/api/v1/users';

  getAll(): Observable<any> {
    return this.http.get<any>(this.API);
  }

  create(data: any) {
    return this.http.post('/api/v1/users/register', data);
  }

  update(id: number, data: any): Observable<User> {
    return this.http.put<User>(`${this.API}/${id}`, data);
  }

  delete(id: number) {
    return this.http.delete(`${this.API}/${id}`);
  }

  getUsers(params: any) {
    return this.http.get<any>(
    `${environment.apiUrl}/api/v1/users`,
    { params }
    );
  }

  // 🔥 PADRÃO ENTERPRISE (server-side pagination)
  findAll(page = 0, size = 10): Observable<any> {
    return this.http.get<any>(this.API, {
      params: {
        page,
        size
      }
    });
  }
}