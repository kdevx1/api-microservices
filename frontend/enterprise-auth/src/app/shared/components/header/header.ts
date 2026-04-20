import { Component, inject } from '@angular/core';
import { AuthService } from '../../../core/auth/services/auth.service';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatDivider, MatDividerModule } from '@angular/material/divider';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatButtonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    MatDivider,
    RouterModule
  ],
  templateUrl: './header.html',
  styleUrls: ['./header.scss']
})
export class Header {

  auth = inject(AuthService);

  get initials(): string {
    const user = this.auth.user();
console.log('USER:', this.auth.user());
    if (!user) return '';

    const name = user.name || user.email;

    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  }

  get avatarColor(): string {
    const email = this.auth.user()?.email || '';

    let hash = 0;
    for (let i = 0; i < email.length; i++) {
      hash = email.charCodeAt(i) + ((hash << 5) - hash);
    }
    console.log('USER:', this.auth.user());
    return `hsl(${hash % 360}, 60%, 40%)`;
  }

  logout() {
    this.auth.logout();
  }


}
