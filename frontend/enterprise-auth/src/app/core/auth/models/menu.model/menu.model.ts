import { Permission } from '../permission.model/permission.model';

export interface MenuItem {
  label: string;
  icon: string;
  route: string;
  permission: string;
}
