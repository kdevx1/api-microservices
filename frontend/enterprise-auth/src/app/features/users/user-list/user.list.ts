import { Component, inject, signal } from '@angular/core';
import { UserService } from '../../../core/auth/services/user.service/user.service';
import { AuthService } from '../../../core/auth/services/auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-user-list',
  imports: [CommonModule, RouterModule],
  templateUrl: './user.list.html'
})
export class UserList {

  private service = inject(UserService);
  auth = inject(AuthService);

  users = signal<any[]>([]);

  ngOnInit() {
    this.load();
  }

  load() {
    this.service.getAll().subscribe(res => {
      this.users.set(res.content || res); // suporta paginação ou lista
    });
  }

  delete(id: number) {
    if (!confirm('Excluir usuário?')) return;

    this.service.delete(id).subscribe(() => this.load());
  }
}