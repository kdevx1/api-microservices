export interface UserRequest {
  name: string;
  email: string;
  password: string;
  role?: string;
}

export interface UpdateUserRequest {
  name?: string;
  password?: string;
  role?: string;
  active?: boolean;
}

export interface UserResponse {
  id: number;
  name: string;
  email:string;
  role: string;
  active: boolean;
  createdAt: string;
  lastLoginAt?: string;
}

export interface DashboardResponse {
  totalUsers: number;
  activeUsers: number;
  admins: number;
}
