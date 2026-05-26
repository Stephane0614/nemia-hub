import { Routes } from '@angular/router';

export const aideRoutes: Routes = [
  {
    path: 'guide',
    loadComponent: () =>
      import('./pages/guide/guide').then((m) => m.GuideComponent),
  },
  {
    path: 'scenarios',
    loadComponent: () =>
      import('./pages/scenarios/scenarios').then((m) => m.ScenariosComponent),
  },
];
