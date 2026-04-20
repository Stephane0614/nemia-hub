import { Routes } from '@angular/router';
import { Home } from './features/home/pages/home/home';
import { fluxRoutes } from './features/flux/flux.routes';
import { bienRoutes } from './features/bien/bien.routes';
import { exerciceRoutes } from './features/exercice/exercice.routes';
import { justificatifRoutes } from './features/justificatif/justificatif.routes';

export const routes: Routes = [
  {
    path: '',
    component: Home,
  },
  {
    path: 'flux',
    children: fluxRoutes,
  },
  {
    path: 'biens',
    children: bienRoutes,
  },
  {
    path: 'exercices',
    children: exerciceRoutes,
  },
  {
    path: 'justificatifs',
    children: justificatifRoutes,
  },
  {
    path: '**',
    redirectTo: '',
  },
];
