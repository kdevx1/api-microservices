import { Routes } from '@angular/router';
import { authGuard } from './core/auth/guards/auth.guard';
import { roleGuard } from './core/auth/guards/role.guard/role.guard';
import { permissionGuard } from './core/auth/guards/permision.guard/permission.guard';
import { Permission, Role } from './core/auth/models/permission.model/permission.model';

export const routes: Routes = [

  // 🔓 LOGIN
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login').then(m => m.Login)
  },

  // // 🔒 APP
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
        canActivate: [roleGuard([Role.ADMIN])],
        loadComponent: () =>
        import('./features/auth/admin/admin').then(m => m.Admin)
      },
      // 🚀 USERS (AGRUPADO CORRETAMENTE)
      {
        path: 'users',
        children: [

          {
            path: '',
            loadComponent: () =>
              import('./features/users/user-list/user.list').then(m => m.UserList),
            canActivate: [permissionGuard([Permission.READ])]
          },

          {
            path: 'create',
            loadComponent: () =>
              import('./features/users/user-create/user.create').then(m => m.UserCreate),
            canActivate: [permissionGuard([Permission.WRITE])]
          },

          {
            path: 'edit/:id',
            loadComponent: () =>
              import('./features/users/user-edit/user.edit').then(m => m.UserEdit),
            canActivate: [permissionGuard([Permission.ALTER])]
          }

        ]
      },
     ]
    },
  {
    path: '**',
    redirectTo: 'login'
  }
];
