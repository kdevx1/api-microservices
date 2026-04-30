import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { UserResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-profile.html',
  styleUrl: './user-profile.scss'
})
export class UserProfile implements OnInit {

  user = signal<UserResponse | null>(null);
  loadingUser = signal(false);
  savingInfo = signal(false);
  savingPassword = signal(false);
  uploadingAvatar = signal(false);

  infoSuccess = signal(false);
  passwordSuccess = signal(false);
  passwordError = signal('');

  infoForm: FormGroup;
  passwordForm: FormGroup;

  userInitials = computed(() => {
    const name = this.user()?.name ?? '';
    return name.split(' ').map(n => n[0]).slice(0, 2).join('').toUpperCase();
  });

  memberSince = computed(() => {
    const date = this.user()?.createdAt;
    if (!date) return '';
    return new Date(date).toLocaleDateString('pt-BR', { month: 'long', year: 'numeric' });
  });

  lastLogin = computed(() => {
    const date = this.user()?.lastLoginAt;
    if (!date) return 'Nunca';
    const d = new Date(date);
    const today = new Date();
    if (d.toDateString() === today.toDateString()) {
      return 'Hoje, ' + d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    }
    return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' }) + ' ' +
           d.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  });

  activityLogs = signal<{ text: string; time: string; type: 'green' | 'purple' | 'gray' }[]>([]);

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    public authService: AuthService
  ) {
    this.infoForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]]
    });

    this.passwordForm = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirm: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.loadingUser.set(true);
    this.userService.me().subscribe({
      next: (user) => {
        this.user.set(user);
        this.infoForm.patchValue({ name: user.name });
        this.loadingUser.set(false);
        this.buildActivityLogs(user);
      },
      error: () => this.loadingUser.set(false)
    });
  }

  buildActivityLogs(user: UserResponse) {
    const logs: { text: string; time: string; type: 'green' | 'purple' | 'gray' }[] = [];

    if (user.lastLoginAt) {
      logs.push({ text: 'Login realizado', time: this.lastLogin(), type: 'green' });
    }

    if (user.createdAt) {
      logs.push({ text: 'Conta criada', time: new Date(user.createdAt).toLocaleDateString('pt-BR'), type: 'gray' });
    }

    this.activityLogs.set(logs);
  }

  saveInfo() {
    if (this.infoForm.invalid || !this.user()) return;

    this.savingInfo.set(true);
    this.infoSuccess.set(false);

    this.userService.update(this.user()!.id, { name: this.infoForm.value.name }).subscribe({
      next: (updated) => {
        this.user.set(updated);
        this.savingInfo.set(false);
        this.infoSuccess.set(true);
        setTimeout(() => this.infoSuccess.set(false), 3000);
      },
      error: () => this.savingInfo.set(false)
    });
  }

  savePassword() {
    this.passwordError.set('');

    if (this.passwordForm.invalid) return;

    const { password, confirm } = this.passwordForm.value;

    if (password !== confirm) {
      this.passwordError.set('As senhas não coincidem.');
      return;
    }

    this.savingPassword.set(true);

    this.userService.update(this.user()!.id, { password }).subscribe({
      next: () => {
        this.savingPassword.set(false);
        this.passwordSuccess.set(true);
        this.passwordForm.reset();
        setTimeout(() => this.passwordSuccess.set(false), 3000);
      },
      error: () => this.savingPassword.set(false)
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const file = input.files[0];
    if (file.size > 5 * 1024 * 1024) {
      alert('Arquivo muito grande. Máximo 5MB.');
      return;
    }

    this.uploadAvatar(file);
  }

  uploadAvatar(file: File) {
    this.uploadingAvatar.set(true);

    const formData = new FormData();
    formData.append('file', file);

    this.userService.uploadAvatar(formData).subscribe({
      next: () => {
        // Recarrega o perfil completo do backend
        this.loadProfile();
        this.uploadingAvatar.set(false);
      },
      error: () => this.uploadingAvatar.set(false)
    });
  }

  get name() { return this.infoForm.get('name')!; }
  get password() { return this.passwordForm.get('password')!; }
  get confirm() { return this.passwordForm.get('confirm')!; }
}
