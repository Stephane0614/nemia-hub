import { ApplicationConfig, LOCALE_ID, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { DateAdapter, provideNativeDateAdapter } from '@angular/material/core';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/auth/auth.interceptor';
import { FrenchDateAdapter } from './core/date/french-date-adapter';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    provideNativeDateAdapter(),
    // Remplace le DateAdapter fourni par provideNativeDateAdapter() : garde
    // le même format d'affichage (MAT_DATE_FORMATS natif) mais corrige le
    // parsing de la saisie manuelle au format jj/mm/aaaa (voir FrenchDateAdapter).
    { provide: DateAdapter, useClass: FrenchDateAdapter },
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' },
    { provide: LOCALE_ID, useValue: 'fr-FR' },
  ],
};
