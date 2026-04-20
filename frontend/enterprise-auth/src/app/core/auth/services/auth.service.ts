import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { User } from '../models/user.model';
import { environment } from '../../../../eviroments/eviroment';
import { LoginResponse } from '../response/login.response';
import { Role, Permission } from '../models/permission.model/permission.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private router = inject(Router);
  // 🧠 estado reativo
  private _user = signal<User | null>(null);

  user = computed(() => this._user());
  isLogged = computed(() => !!this._user());

  constructor() {
    this.loadUserFromToken();
  }

  // 🔗 chamada backend
  login(data: { email: string; password: string }) {
    return this.http.post<LoginResponse>(
      `${environment.apiUrl}/api/v1/auth/login`,
      data
    );
  }

  // 💾 cria sessão
  setSession(token: string) {
    localStorage.setItem('token', token);

    this.decodeToken(token);
  }

  // 🚪 logout
  logout() {
    localStorage.removeItem('token');
    this._user.set(null);
    this.router.navigate(['/login']);
  }

  // 🔁 reidratação da sessão
  private loadUserFromToken() {
    const token = localStorage.getItem('token');

    if (token) {
      this.decodeToken(token);
    }
  }

  hasRole(role: string): boolean {
    return this._user()?.roles.includes(role) ?? false;
  }

  hasPermission(permission: string): boolean {
    return this._user()?.permissions?.includes(permission) ?? false;
  }

  // 🧠 decode JWT
  private decodeToken(token: string) {
    try {
      const payload = JSON.parse(
        atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))
      );

      console.log('TOKEN:', payload);

      const authorities: string[] =
        payload.authorities ||
        payload.roles ||
        (payload.role ? [payload.role] : []);

      // 🎯 separa roles e permissions corretamente
      const roles = authorities
        .filter(a => a.startsWith('ROLE_'))
        .map(a => a.replace('ROLE_', ''));

      const permissions = authorities
        .filter(a => !a.startsWith('ROLE_'));

      this._user.set({
        name: payload.name,
        email: payload.sub,
        roles: roles,
        permissions: permissions,
        avatar: payload.avatar
      });

      console.log('USER NORMALIZADO:', this._user());

    } catch (e) {
      console.error('Token inválido', e);
      this.logout();
    }
  }

  uploadAvatar(file: File) {
    
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<{ url: string }>(
      `${environment.apiUrl}/api/v1/users/avatar`, // 👈 AQUI
      formData
    );
  }

  reloadUser() {
    this.http.get<User>(`${environment.apiUrl}/api/v1/users/me`)
      .subscribe(user => {
        this._user.set(user);
      });
  }
  
  updateAvatar(url: string) {
    const user = this._user();

    if (!user) return;

    this._user.set({
      ...user,
      avatar: url
    });
  }

  getUserFromToken() {
  const token = localStorage.getItem('token');
  if (!token) return null;

  const payload = JSON.parse(atob(token.split('.')[1]));

  return {
    email: payload.sub,
    name: payload.name,
    authorities: payload.authorities || []
  };
}

hasAuthority(permission: string): boolean {
  const user = this.getUserFromToken();
  return user?.authorities.includes(permission);
}


}
