import { Component, computed, inject } from '@angular/core';
import { MENU_ITEMS } from '../../../core/config/menu.config/menu.config';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../core/auth/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule, MatIconModule],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.scss']
})
export class Sidebar {

  private auth = inject(AuthService);

 menu = MENU_ITEMS.filter(item =>
  this.auth.hasPermission(item.permission)
 );

}
