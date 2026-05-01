import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CategoryResponse } from '../../../core/models/service.model';
import { ServiceService } from '../../../core/services/service.service';

@Component({
  selector: 'app-category-form-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './category-form-modal.html',
  styleUrl: './category-form-modal.scss'
})
export class CategoryFormModal implements OnChanges {

  @Input() visible = false;
  @Input() category: CategoryResponse | null = null;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() saved = new EventEmitter<void>();

  form: FormGroup;
  loading = false;

  colorPresets = [
    '#534AB7', '#185FA5', '#3B6D11', '#C67C1A',
    '#E24B4A', '#26215C', '#888780', '#0F766E'
  ];

  constructor(private fb: FormBuilder, private serviceService: ServiceService) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      color: ['#534AB7', Validators.required]
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['category'] && this.category) {
      this.form.patchValue({
        name: this.category.name,
        description: this.category.description ?? '',
        color: this.category.color
      });
    } else if (changes['visible'] && this.visible && !this.category) {
      this.form.reset({ color: '#534AB7' });
    }
  }

  get name() { return this.form.get('name')!; }
  get selectedColor() { return this.form.get('color')!.value; }

  selectColor(color: string) { this.form.patchValue({ color }); }

  close() {
    this.visibleChange.emit(false);
    this.form.reset({ color: '#534AB7' });
  }

  onSubmit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;

    const request = this.form.value;
    const obs = this.category
      ? this.serviceService.updateCategory(this.category.id, request)
      : this.serviceService.createCategory(request);

    obs.subscribe({
      next: () => { this.loading = false; this.saved.emit(); this.close(); },
      error: () => { this.loading = false; }
    });
  }
}
