import { Routes } from '@angular/router';

export const empruntRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/emprunt-list/emprunt-list').then(
        (m) => m.EmpruntListComponent
      ),
  },
  {
    path: 'nouveau',
    loadComponent: () =>
      import('./pages/emprunt-form/emprunt-form').then(
        (m) => m.EmpruntFormComponent
      ),
  },
  {
    path: ':id/modifier',
    loadComponent: () =>
      import('./pages/emprunt-form/emprunt-form').then(
        (m) => m.EmpruntFormComponent
      ),
  },
];