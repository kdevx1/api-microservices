import { Routes } from '@angular/router';
import { authGuard, publicGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'login',
    canActivate: [publicGuard],
    loadComponent: () =>
      import('./features/auth/login/login').then(m => m.Login)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./layout/main-layout/main-layout').then(m => m.MainLayout),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/users/user-list/user-list').then(m => m.UserList)
      },
      {
        path: 'users',
        loadComponent: () =>
          import('./features/users/user-list/user-list').then(m => m.UserList)
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/users/user-profile/user-profile').then(m => m.UserProfile)
      },
      {
        path: 'services',
        loadComponent: () =>
          import('./features/services/service-list/service-list').then(m => m.ServiceList)
      },
      {
        path: 'orders',
        loadComponent: () =>
          import('./features/services/service-orders/service-orders').then(m => m.ServiceOrders)
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];
