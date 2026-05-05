import { StatutEmprunt } from './statut-emprunt';

export interface EmpruntRequest {
  referencePret: string;
  bienId: number;
  statutEmprunt: StatutEmprunt;
  organismePreteur?: string;
  mensualiteTotale?: number;
  datePremiereEcheance?: string;  // yyyy-MM-dd
  dateDerniereEcheance?: string;  // yyyy-MM-dd
  commentaire?: string;
}