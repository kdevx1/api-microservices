import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';

export const permissionGuard = (permissions: string[]) => {
  return () => {
    const auth = inject(AuthService);

    return permissions.some(permission =>
      auth.hasAuthority(permission)
    );
  };
};


// import { Router } from '@angular/router';
// import { inject } from '@angular/core';
// import { AuthService } from '../../services/auth.service';
// import { Permission } from '../../models/permission.model/permission.model';

// export const permissionGuard = (permissions: Permission []) => {
//   return () => {
//     const auth = inject(AuthService);
//     const router = inject(Router);

//     if (auth.hasPermission(permissions)) {
//       return true;
//     }

//     router.navigate(['/dashboard']);
//     return false;
//   };
// };
