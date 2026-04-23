import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-user-form',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user.form.html'
})
export class UserForm {

  private fb = inject(FormBuilder);

  @Input() initialData: any;
  @Output() save = new EventEmitter<any>();

  form = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: [''],
    role: ['ROLE_USER']
  });

  ngOnInit() {
    if (this.initialData) {
      this.form.patchValue(this.initialData);
    }
  }

  submit() {
    if (this.form.invalid) return;
    this.save.emit(this.form.value);
  }
}