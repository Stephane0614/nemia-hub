import { StatutEmprunt } from './statut-emprunt';

export interface EmpruntResponse {
  id: number;
  referencePret: string;
  bienId: number;
  statutEmprunt: StatutEmprunt;
  organismePreteur?: string;
  mensualiteTotale?: number;
  datePremiereEcheance?: string;
  dateDerniereEcheance?: string;
  commentaire?: string;
  createdAt: string;
  updatedAt: string;
}