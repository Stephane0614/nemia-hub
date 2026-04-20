import { Routes } from '@angular/router';
import { JustificatifForm } from './pages/justificatif-form/justificatif-form';

export const justificatifRoutes: Routes = [
  {
    path: 'nouveau',
    component: JustificatifForm,
  },
  {
    path: ':id/modifier',
    component: JustificatifForm,
  },
];
