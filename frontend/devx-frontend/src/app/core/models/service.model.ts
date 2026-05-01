export interface CategoryRequest {
  name: string;
  description?: string;
  color: string;
}

export interface CategoryResponse {
  id: number;
  name: string;
  description?: string;
  color: string;
  active: boolean;
  serviceCount: number;
  createdAt: string;
}

export interface ServiceRequest {
  name: string;
  description?: string;
  categoryId: number;
  price: number;
  durationMinutes?: number;
  type: string;
}

export interface ServiceResponse {
  id: number;
  name: string;
  description?: string;
  categoryId: number;
  categoryName: string;
  categoryColor: string;
  price: number;
  durationMinutes?: number;
  type: string;
  active: boolean;
  createdAt: string;
}

export interface ServiceStatsResponse {
  totalServices: number;
  activeServices: number;
  totalCategories: number;
  totalOrders: number;
  pendingOrders: number;
  totalRevenue: number;
}

export interface ServiceOrderRequest {
  serviceId: number;
  clientId: number;
  scheduledAt?: string;
  notes?: string;
}

export interface ServiceOrderResponse {
  id: number;
  serviceName: string;
  serviceCategory: string;
  clientName: string;
  clientEmail: string;
  status: string;
  scheduledAt?: string;
  notes?: string;
  createdAt: string;
}
