import { Routes } from '@angular/router';
import { ExerciceList } from './pages/exercice-list/exercice-list';
import { ExerciceForm } from './pages/exercice-form/exercice-form';

export const exerciceRoutes: Routes = [
  {
    path: '',
    component: ExerciceList,
  },
  {
    path: 'nouveau',
    component: ExerciceForm,
  },
  {
    path: ':id/modifier',
    component: ExerciceForm,
  },
];
