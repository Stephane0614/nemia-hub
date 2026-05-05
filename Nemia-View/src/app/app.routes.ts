import { Routes } from '@angular/router';
import { Home } from './features/home/pages/home/home';
import { fluxRoutes } from './features/flux/flux.routes';
import { bienRoutes } from './features/bien/bien.routes';
import { exerciceRoutes } from './features/exercice/exercice.routes';
import { justificatifRoutes } from './features/justificatif/justificatif.routes';
import { ExerciceSynthese } from './features/exercice/pages/exercice-synthese/exercice-synthese';
import { travauxRoutes } from './features/travaux/travaux.routes';
import { mobilierRoutes } from './features/mobilier/mobilier.routes';
import { empruntRoutes } from './features/emprunt/emprunt.routes';

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
    path: 'exercice',
    component: ExerciceSynthese,
  },
  {
    path: 'travaux',
    children: travauxRoutes,
  },
  {
    path: 'mobilier',
    children: mobilierRoutes,
  },
  {
  path: 'emprunts',
  children: empruntRoutes
},
  {
    path: '**',
    redirectTo: '',
  },
];
