import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../app/enviroment/environment';

@Injectable({ providedIn: 'root' })
export class ReportService {

  private readonly api = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  downloadUsersExcel() {
    return this.http.get(`${this.api}/users/excel`, { responseType: 'blob' });
  }

  downloadServicesPdf() {
    return this.http.get(`${this.api}/services/pdf`, { responseType: 'blob' });
  }

  downloadServicesExcel() {
    return this.http.get(`${this.api}/services/excel`, { responseType: 'blob' });
  }

  downloadUsersPdf() {
    return this.http.get(`${this.api}/users/pdf`, { responseType: 'blob' });
  }

  downloadOrdersExcel() {
    return this.http.get(`${this.api}/orders/excel`, { responseType: 'blob' });
  }

  downloadOrdersPdf() {
    return this.http.get(`${this.api}/orders/pdf`, { responseType: 'blob' });
  }
}
