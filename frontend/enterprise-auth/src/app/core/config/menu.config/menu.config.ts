// import { MenuItem } from '../../auth/models/menu.model/menu.model';
import { MenuItem } from '../../auth/models/menu.model/menu.model';

export const MENU_ITEMS: MenuItem[] = [
  {
    label: 'Dashboard',
    icon: 'dashboard',
    route: '/dashboard',
    permission: 'DASHBOARD_VIEW'
  },
  {
    label: 'Admin',
    icon: 'admin_panel_settings',
    route: '/admin',
    permission: 'ADMIN_PANEL'
  },
  {
    label: 'Perfil',
    icon: 'person',
    route: '/profile',
    permission: 'DASHBOARD_VIEW'
  }
];
