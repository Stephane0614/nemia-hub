import { Routes } from '@angular/router';
import { TravauxList } from './pages/travaux-list/travaux-list';
import { TravauxForm } from './pages/travaux-form/travaux-form';

export const travauxRoutes: Routes = [
  {
    path: '',
    component: TravauxList,
  },
  {
    path: 'nouveau',
    component: TravauxForm,
  },
  {
    path: ':id/modifier',
    component: TravauxForm,
  },
];
