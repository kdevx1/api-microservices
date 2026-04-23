import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/auth/interceptors/auth.interceptor';

export const appConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptor]) 
      // 🔥 AQUI
    )
    
  ]
  
};