import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../eviroments/eviroment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private http = inject(HttpClient);

  uploadAvatar(file: File) {
      
      const formData = new FormData();
      formData.append('file', file);
  
      return this.http.post<{ url: string }>(
        `${environment.apiUrl}/api/v1/users/avatar`, // 👈 AQUI
        formData
      );
    }

}
