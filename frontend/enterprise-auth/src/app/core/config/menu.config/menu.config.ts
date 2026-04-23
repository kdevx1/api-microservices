import { Permission } from "../../auth/models/permission.model/permission.model";

export const MENU_ITEMS = [
  {
    label: 'Dashboard',
    route: '/dashboard',
    icon: 'dashboard',
    permission: 'DASHBOARD_VIEW'
  },
  {
    label: 'Usuários',
    route: '/users',
    icon: 'group',
    permission: 'READ'
  },
  {
    label: 'Criar Usuário',
    route: '/users/create',
    icon: 'person_add',
    permission: 'WRITE'
  }
];