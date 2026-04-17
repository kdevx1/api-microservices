import { Role, Permission } from '../../auth/models/permission.model/permission.model';

export const ROLE_PERMISSIONS: Record<Role, Permission[]> = {
  ADMIN: [
    'DASHBOARD_VIEW',
    'USER_CREATE',
    'USER_EDIT',
    'USER_DELETE',
    'ADMIN_PANEL'
  ],
  MANAGER: [
    'DASHBOARD_VIEW',
    'USER_CREATE',
    'USER_EDIT'
  ],
  USER: [
    'DASHBOARD_VIEW'
  ]
};
