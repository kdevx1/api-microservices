import { Permission, Role } from "../../auth/models/permission.model/permission.model";

export const ROLE_PERMISSIONS: Record<Role, Permission[]> = {
[Role.USER]: [
    Permission.READ,
    Permission.DASHBOARD_VIEW
  ],
  [Role.ADMIN]: [
    Permission.READ,
    Permission.WRITE,
    Permission.DELETE,
    Permission.ALTER,
    Permission.DASHBOARD_VIEW
  ],  

  [Role.MANAGER]: [
    Permission.READ,
    Permission.WRITE,
    Permission.DASHBOARD_VIEW
  ]
};