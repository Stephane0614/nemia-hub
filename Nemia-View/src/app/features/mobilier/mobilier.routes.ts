import { Routes } from '@angular/router';
import { MobilierList } from './pages/mobilier-list/mobilier-list';
import { MobilierForm } from './pages/mobilier-form/mobilier-form';

export const mobilierRoutes: Routes = [
  {
    path: '',
    component: MobilierList,
  },
  {
    path: 'nouveau',
    component: MobilierForm,
  },
  {
    path: ':id/modifier',
    component: MobilierForm,
  },
];
