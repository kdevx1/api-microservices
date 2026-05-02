import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd, RouterModule } from '@angular/router';
import { filter, map } from 'rxjs/operators';
import { AsyncPipe } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { Notifications } from '../../shared/components/notifications/notifications';

@Component({
  selector: 'app-topbar',
  standalone: true,
  imports: [CommonModule, RouterModule, AsyncPipe, Notifications],
  templateUrl: './topbar.html',
  styleUrl: './topbar.scss'
})
export class Topbar {

  pageTitle$: any;

  constructor(
    private router: Router,
    public authService: AuthService
  ) {
    this.pageTitle$ = this.router.events.pipe(
      filter(e => e instanceof NavigationEnd),
      map(() => this.getTitle(this.router.url))
    );
  }

  private getTitle(url: string): string {
    const titles: Record<string, string> = {
      '/dashboard': 'Dashboard',
      '/users': 'Usuários',
      '/services': 'Serviços',
      '/orders': 'Ordens de Serviço',
      '/profile': 'Meu Perfil'
    };
    return titles[url] ?? 'DevX';
  }

  logout() {
    this.authService.logout();
  }
}
