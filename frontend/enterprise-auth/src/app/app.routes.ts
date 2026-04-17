import { Routes } from '@angular/router';
import { authGuard } from './core/auth/guards/auth.guard';
import { roleGuard } from './core/auth/guards/role.guard/role.guard';
import { permissionGuard } from './core/auth/guards/permision.guard/permission.guard';

export const routes: Routes = [

  // 🔓 LOGIN (fora do layout)
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login').then(m => m.Login)
  },

// 🔒 APP (com layout)
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./layout/main-layout/main-layout').then(m => m.MainLayout),

    children: [

      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard').then(m => m.Dashboard)
      },

      {
        path: 'profile',
        loadComponent: () =>
          import('./features/profile/profile').then(m => m.Profile)
      },

      {
        path: 'admin',
        canActivate: [roleGuard(['ADMIN'])],
        loadComponent: () =>
          import('./features/auth/admin/admin').then(m => m.Admin)
      }

    ]
  },

// fallback
  {
    path: '**',
    redirectTo: 'login'
  }
];
