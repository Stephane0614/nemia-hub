import { StatutExercice } from './statut-exercice';
import { NiveauCompletude } from './niveau-completude';

export interface ExerciceRequest {
  libelleExercice: string;
  dateDebut: string;
  dateFin: string;
  statutExercice: StatutExercice;
  niveauCompletude?: NiveauCompletude | null;
  commentaire?: string | null;
}
