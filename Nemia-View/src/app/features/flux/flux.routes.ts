import { Routes } from '@angular/router';
import { FluxForm } from './pages/flux-form/flux-form';
import { FluxList } from './pages/flux-list/flux-list';

export const fluxRoutes: Routes = [
  {
    path: '',
    component: FluxList,
  },
  {
    path: 'nouveau',
    component: FluxForm,
  },
  {
    path: ':id/modifier',
    component: FluxForm,
  },
];
