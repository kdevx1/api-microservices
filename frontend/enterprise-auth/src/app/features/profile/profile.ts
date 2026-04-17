import { Component, inject } from '@angular/core';
import { UserService } from '../../core/auth/services/user.service/user.service';
import { AuthService } from '../../core/auth/services/auth.service';
import { MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { ImageCropperComponent  } from 'ngx-image-cropper';
import { MatButtonModule } from '@angular/material/button';

@Component({
  standalone: true,
  selector: 'app-profile',
  imports: [
    MatCardModule,
    CommonModule,
    MatIcon,
    MatButtonModule,
    MatIconModule,
    ImageCropperComponent
  ],
  templateUrl: './profile.html',
  styleUrl:  './profile.scss'
})
export class Profile {

  private userService = inject(UserService);
  auth = inject(AuthService);

  preview: string | null = null;
  imageChangedEvent: any = '';
  croppedImage: string = '';
  showCropper = false;

  onFileSelected(event: any) {
    const file = event.target.files[0];

    if (!file) return;

    // preview imediato
    this.preview = URL.createObjectURL(file);

    this.auth.uploadAvatar(file).subscribe({
      next: (res) => {
        this.auth.updateAvatar(res.url); // UI imediata
        this.auth.reloadUser(); // 🔥 sincroniza com backend
      },
      error: (err) => console.error(err)
    });
  }

  imageCropped(event: any) {
    this.croppedImage = event.base64;
  }

  uploadCropped() {
    if (!this.croppedImage) return;

    const blob = this.base64ToBlob(this.croppedImage);

    const file = new File([blob], 'avatar.png', { type: 'image/png' });

    this.userService.uploadAvatar(file).subscribe({
      next: (res) => {
        this.auth.updateAvatar(res.url);
        this.preview = res.url;
        this.showCropper = false;
      }
    });
  }

  base64ToBlob(base64: string): Blob {
    const byteString = atob(base64.split(',')[1]);
    const arrayBuffer = new ArrayBuffer(byteString.length);
    const intArray = new Uint8Array(arrayBuffer);

    for (let i = 0; i < byteString.length; i++) {
      intArray[i] = byteString.charCodeAt(i);
    }

    return new Blob([arrayBuffer], { type: 'image/png' });
  }
}
