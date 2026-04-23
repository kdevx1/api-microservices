import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService } from '../../../core/auth/services/user.service/user.service';
import { UserForm } from '../../users/user-form/user.form';

@Component({
  standalone: true,
  imports: [UserForm],
  template: `
    <h2>Editar Usuário</h2>

    <app-user-form
      *ngIf="user()"
      [initialData]="user()"
      (save)="update($event)">
    </app-user-form>
  `
})
export class UserEdit {

  private route = inject(ActivatedRoute);
  private service = inject(UserService);
  private router = inject(Router);

  user = signal<any>(null);

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.service.getAll().subscribe(res => {
      const list = res.content || res;
      this.user.set(list.find((u: any) => u.id === id));
    });
  }

  update(data: any) {
    this.service.update(this.user()?.id, data).subscribe(() => {
      this.router.navigate(['/users']);
    });
  }
}