import { Routes } from '@angular/router';
import { Home } from './features/home/pages/home/home';
import { fluxRoutes } from './features/flux/flux.routes';

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
    path: '**',
    redirectTo: '',
  },
];
