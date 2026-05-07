import { Routes } from '@angular/router';
import { Home } from './features/home/pages/home/home';
import { fluxRoutes } from './features/flux/flux.routes';
import { bienRoutes } from './features/bien/bien.routes';
import { exerciceRoutes } from './features/exercice/exercice.routes';
import { justificatifRoutes } from './features/justificatif/justificatif.routes';
import { ExerciceSynthese } from './features/exercice/pages/exercice-synthese/exercice-synthese';
import { travauxRoutes } from './features/travaux/travaux.routes';
import { mobilierRoutes } from './features/mobilier/mobilier.routes';
import { empruntRoutes } from './features/emprunt/emprunt.routes';

import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login').then(
        (m) => m.LoginComponent
      ),
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/home/pages/home/home').then((m) => m.Home),
  },
  {
    path: 'flux',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/flux/flux.routes').then((m) => m.fluxRoutes),
  },
  {
    path: 'biens',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/bien/bien.routes').then((m) => m.bienRoutes),
  },
  {
    path: 'exercices',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/exercice/exercice.routes').then(
        (m) => m.exerciceRoutes
      ),
  },
  {
    path: 'exercice',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/exercice/pages/exercice-synthese/exercice-synthese').then(
        (m) => m.ExerciceSynthese
      ),
  },
  {
    path: 'justificatifs',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/justificatif/justificatif.routes').then(
        (m) => m.justificatifRoutes
      ),
  },
  {
    path: 'travaux',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/travaux/travaux.routes').then(
        (m) => m.travauxRoutes
      ),
  },
  {
    path: 'mobilier',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/mobilier/mobilier.routes').then(
        (m) => m.mobilierRoutes
      ),
  },
  {
    path: 'emprunts',
    canActivate: [authGuard],
    loadChildren: () =>
      import('./features/emprunt/emprunt.routes').then(
        (m) => m.empruntRoutes
      ),
  },
  {
    path: '**',
    redirectTo: '',
  },
];