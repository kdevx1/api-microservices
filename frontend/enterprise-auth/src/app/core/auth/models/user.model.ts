export interface User {
  email: string;
  name?: string;
  roles: string[];
  permissions: string[];
  avatar?: string;
}
