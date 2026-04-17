import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Role } from '../../models/permission.model/permission.model';

export const roleGuard = (roles: Role[]) => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    const hasAccess = roles.some(role => auth.hasRole(role));

    if (!hasAccess) {
      router.navigate(['/dashboard']);
      return false;
    }

    return true;
  };
};
