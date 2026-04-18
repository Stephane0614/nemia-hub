import { Routes } from '@angular/router';
import { BienList } from './pages/bien-list/bien-list';
import { BienForm } from './pages/bien-form/bien-form';
import { BienDetail } from './pages/bien-detail/bien-detail';

export const bienRoutes: Routes = [
  {
    path: '',
    component: BienList,
  },
  {
    path: 'nouveau',
    component: BienForm,
  },
  {
    path: ':id',
    component: BienDetail,
  },
  {
    path: ':id/modifier',
    component: BienForm,
  },
];