export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  tokenType: string;
}

export interface TokenPayload {
  sub: string;
  name: string;
  email: string
  authorities: string[];
  avatar?: string;
  iat: number;
  exp: number;
}
