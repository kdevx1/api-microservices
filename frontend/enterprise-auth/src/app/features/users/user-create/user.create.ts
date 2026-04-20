import { Component, inject } from '@angular/core';
import { UserService } from '../../../core/auth/services/user.service/user.service';
import { Router } from '@angular/router';
import { UserForm } from '../../users/user-form/user.form';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  standalone: true,
  imports: [
    UserForm,
    CommonModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatFormFieldModule
  ],
  template: `
    <h2>Criar Usuário</h2>
    <app-user-form (save)="create($event)"></app-user-form>
  `
})
export class UserCreate {

  private service = inject(UserService);
  private router = inject(Router);

  create(data: any) {
    console.log('FORM DATA:', data); // 👈 DEBUG

    this.service.create(data).subscribe(() => {
      this.router.navigate(['/users']);
    });
  }
}