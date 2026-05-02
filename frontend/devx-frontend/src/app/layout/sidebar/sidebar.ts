import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss'
})
export class Sidebar {

  constructor(public authService: AuthService) {}

  userInitials = computed(() => {
    const name = this.authService.currentUser()?.name ?? '';
    return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
  });

  userName = computed(() => this.authService.currentUser()?.name ?? '');
  // userEmail = computed(() => this.authService.currentUser()?.email ?? '');
  userRole = computed(() => this.authService.currentUser()?.authorities?.[0] ?? '');

  menuItems = [
    {
      label: 'Principal',
      items: [
        { label: 'Dashboard', icon: 'dashboard', route: '/dashboard' }
      ]
    },
    {
      label: 'Gestão',
      items: [
        { label: 'Usuários', icon: 'users', route: '/users' },
        { label: 'Serviços', icon: 'services', route: '/services' },
        { label: 'Ordens', icon: 'orders', route: '/orders' }  // ✅ novo
      ]
    },
    {
      label: 'Conta',
      items: [
        { label: 'Perfil', icon: 'profile', route: '/profile' }
      ]
    }
  ];
}
