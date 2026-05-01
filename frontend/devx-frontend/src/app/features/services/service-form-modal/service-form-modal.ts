import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CategoryResponse, ServiceResponse } from '../../../core/models/service.model';
import { ServiceService } from '../../../core/services/service.service';

@Component({
  selector: 'app-service-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './service-form-modal.html',
  styleUrl: './service-form-modal.scss'
})
export class ServiceFormModal implements OnChanges {

  @Input() visible = false;
  @Input() service: ServiceResponse | null = null;
  @Input() categories: CategoryResponse[] = [];
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() saved = new EventEmitter<void>();

  form: FormGroup;
  loading = false;

  types = [
    { label: 'Serviço', value: 'SERVICE' },
    { label: 'Produto', value: 'PRODUCT' }
  ];

  constructor(private fb: FormBuilder, private serviceService: ServiceService) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      categoryId: [null, Validators.required],
      price: [null, [Validators.required, Validators.min(0.01)]],
      durationMinutes: [null],
      type: ['SERVICE', Validators.required]
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['service'] && this.service) {
      this.form.patchValue({
        name: this.service.name,
        description: this.service.description ?? '',
        categoryId: this.service.categoryId,
        price: this.service.price,
        durationMinutes: this.service.durationMinutes,
        type: this.service.type
      });
    } else if (changes['visible'] && this.visible && !this.service) {
      this.form.reset({ type: 'SERVICE' });
    }
  }

  get name() { return this.form.get('name')!; }
  get price() { return this.form.get('price')!; }
  get categoryId() { return this.form.get('categoryId')!; }
  get selectedType() { return this.form.get('type')!.value; }

  selectType(type: string) { this.form.patchValue({ type }); }

  close() {
    this.visibleChange.emit(false);
    this.form.reset({ type: 'SERVICE' });
  }

  onSubmit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;

    const payload = {
      ...this.form.value,
      price: parseFloat(this.form.value.price),
      durationMinutes: this.form.value.durationMinutes ? parseInt(this.form.value.durationMinutes) : null
    };

    const obs = this.service
      ? this.serviceService.update(this.service.id, payload)
      : this.serviceService.create(payload);

    obs.subscribe({
      next: () => { this.loading = false; this.saved.emit(); this.close(); },
      error: () => { this.loading = false; }
    });
  }
}
