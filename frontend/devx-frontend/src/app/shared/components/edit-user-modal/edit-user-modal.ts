import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UserResponse } from '../../../core/models/user.model';
import { UserService } from '../../../core/services/user.service';

@Component({
  selector: 'app-edit-user-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-user-modal.html',
  styleUrls: ['./edit-user-modal.scss']
})
export class EditUserModal implements OnChanges {

  @Input() visible = false;
  @Input() user: UserResponse | null = null;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() saved = new EventEmitter<void>();

  form: FormGroup;
  loading = false;

  roles = [
    { label: 'Administrador', value: 'ROLE_ADMIN' },
    { label: 'Gerente', value: 'ROLE_MANAGER' },
    { label: 'Usuário', value: 'ROLE_USER' }
  ];

  constructor(
    private fb: FormBuilder,
    private userService: UserService
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      role: ['', Validators.required],
      password: ['', [Validators.minLength(6)]],
      active: [true]
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['user'] && this.user) {
      this.form.patchValue({
        name: this.user.name,
        role: this.user.role,
        password: '',
        active: this.user.active
      });
    }
  }

  get name() { return this.form.get('name')!; }
  get password() { return this.form.get('password')!; }

  close() {
    this.visibleChange.emit(false);
    this.form.reset();
  }

  truncateEmail(email: string): string {
    if (!email) return '';
    return email.length > 25 ? email.substring(0, 25) + '...' : email;
  }

  onSubmit() {
    if (this.form.invalid || !this.user) return;

    this.loading = true;

    const payload: any = {
      name: this.form.value.name,
      role: this.form.value.role,
      active: this.form.value.active
    };

    if (this.form.value.password && this.form.value.password.trim() !== '') {
      payload.password = this.form.value.password;
    }

    this.userService.update(this.user.id, payload).subscribe({
      next: () => {
        this.loading = false;
        this.saved.emit();
        this.close();
      },
      error: (err) => {
        this.loading = false;
        console.error('Erro ao atualizar usuário', err);
      }
    });
  }
}
