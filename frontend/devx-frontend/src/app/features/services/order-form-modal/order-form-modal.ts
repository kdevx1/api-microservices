import { Component, Input, Output, EventEmitter, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ServiceService } from '../../../core/services/service.service';
import { ServiceResponse } from '../../../core/models/service.model';
import { UserService } from '../../../core/services/user.service';
import { UserResponse } from '../../../core/models/user.model';

@Component({
  selector: 'app-order-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './order-form-modal.html',
  styleUrl: './order-form-modal.scss'
})
export class OrderFormModal implements OnInit {

  @Input() visible = false;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() saved = new EventEmitter<void>();

  form: FormGroup;
  loading = false;
  services = signal<ServiceResponse[]>([]);
  users = signal<UserResponse[]>([]);

  constructor(
    private fb: FormBuilder,
    private serviceService: ServiceService,
    private userService: UserService
  ) {
    this.form = this.fb.group({
      serviceId: [null, Validators.required],
      clientId: [null, Validators.required],
      scheduledAt: [''],
      notes: ['']
    });
  }

  ngOnInit() {
    this.loadServices();
    this.loadUsers();
  }

  loadServices() {
    this.serviceService.search({ size: 100 }).subscribe({
      next: (data) => this.services.set(data.content),
      error: (err) => console.error(err)
    });
  }

  loadUsers() {
    this.userService.search({ size: 100 }).subscribe({
      next: (data) => this.users.set(data.content),
      error: (err) => console.error(err)
    });
  }

  get serviceId() { return this.form.get('serviceId')!; }
  get clientId() { return this.form.get('clientId')!; }

  close() {
    this.visibleChange.emit(false);
    this.form.reset();
  }

  onSubmit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;

    const payload = {
      serviceId: parseInt(this.form.value.serviceId),
      clientId: parseInt(this.form.value.clientId),
      scheduledAt: this.form.value.scheduledAt || undefined,
      notes: this.form.value.notes || undefined
    };

    this.serviceService.createOrder(payload).subscribe({
      next: () => { this.loading = false; this.saved.emit(); this.close(); },
      error: () => { this.loading = false; }
    });
  }
}
