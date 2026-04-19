import { StatutExercice } from './statut-exercice';
import { NiveauCompletude } from './niveau-completude';

export interface ExerciceResponse {
  id: number;
  libelleExercice: string;
  dateDebut: string;
  dateFin: string;
  statutExercice: StatutExercice;
  niveauCompletude?: NiveauCompletude | null;
  commentaire?: string | null;
  createdAt: string;
  updatedAt: string;
}
