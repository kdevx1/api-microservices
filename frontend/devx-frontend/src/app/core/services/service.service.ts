import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
  CategoryRequest, CategoryResponse,
  ServiceRequest, ServiceResponse,
  ServiceStatsResponse,
  ServiceOrderRequest, ServiceOrderResponse
} from '../models/service.model';
import { environment } from '../../../app/enviroment/environment';

@Injectable({ providedIn: 'root' })
export class ServiceService {

  private readonly api = `${environment.apiUrl}/services`;
  private readonly catApi = `${environment.apiUrl}/categories`;

  constructor(private http: HttpClient) {}

  // =========================
  // STATS
  // =========================
  getStats() {
    return this.http.get<ServiceStatsResponse>(`${this.api}/stats`);
  }

  // =========================
  // CATEGORIES
  // =========================
  getCategories() {
    return this.http.get<CategoryResponse[]>(this.catApi);
  }

  createCategory(request: CategoryRequest) {
    return this.http.post<CategoryResponse>(this.catApi, request);
  }

  updateCategory(id: number, request: CategoryRequest) {
    return this.http.put<CategoryResponse>(`${this.catApi}/${id}`, request);
  }

  deleteCategory(id: number) {
    return this.http.delete<void>(`${this.catApi}/${id}`);
  }

  // =========================
  // SERVICES
  // =========================
  search(params: {
    categoryId?: number;
    name?: string;
    type?: string;
    page?: number;
    size?: number;
  }) {
    let httpParams = new HttpParams();
    if (params.categoryId) httpParams = httpParams.set('categoryId', params.categoryId);
    if (params.name) httpParams = httpParams.set('name', params.name);
    if (params.type) httpParams = httpParams.set('type', params.type);
    httpParams = httpParams.set('page', params.page ?? 0);
    httpParams = httpParams.set('size', params.size ?? 10);
    return this.http.get<any>(this.api, { params: httpParams });
  }

  getById(id: number) {
    return this.http.get<ServiceResponse>(`${this.api}/${id}`);
  }

  create(request: ServiceRequest) {
    return this.http.post<ServiceResponse>(this.api, request);
  }

  update(id: number, request: ServiceRequest) {
    return this.http.put<ServiceResponse>(`${this.api}/${id}`, request);
  }

  delete(id: number) {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  toggle(id: number) {
    return this.http.patch<ServiceResponse>(`${this.api}/${id}/active`, {});
  }

  // =========================
  // ORDERS
  // =========================
  createOrder(request: ServiceOrderRequest) {
    return this.http.post<ServiceOrderResponse>(`${this.api}/orders`, request);
  }

  getOrders(params: { page?: number; size?: number }) {
    let httpParams = new HttpParams();
    httpParams = httpParams.set('page', params.page ?? 0);
    httpParams = httpParams.set('size', params.size ?? 10);
    return this.http.get<any>(`${this.api}/orders`, { params: httpParams });
  }

  updateOrderStatus(id: number, status: string) {
    return this.http.patch<ServiceOrderResponse>(
      `${this.api}/orders/${id}/status`, null, { params: { status } }
    );
  }
}
