import { Component, inject } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../core/auth/services/auth.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class Login {

  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
 // 🚀 redireciona
  private router = inject(Router);

  private snack = inject(MatSnackBar);

  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  hide = true;

  onSubmit() {
    if (this.form.invalid) return;

    const payload = this.form.getRawValue();
    this.auth.login(payload).subscribe({

      next: (res) => {
        this.auth.setSession(res.accessToken);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {

        if (err.status === 403) {
          this.snack.open('Email ou senha inválidos', 'Fechar', {
            duration: 3000
          });
        } else {
          this.snack.open('Erro ao conectar com o servidor', 'Fechar', {
            duration: 3000
          });
        }

      }
    });

   this.auth.logout();
  }

}
