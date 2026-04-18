import { StatutActiviteBien } from './statut-activite-bien';
import { TypeLocation } from './type-location';
import { RegimeVise } from './regime-vise';

export interface BienResponse {
  id: number;
  nomUsuel: string;
  adresseSimplifiee: string;
  statutActivite: StatutActiviteBien;
  photoUrl?: string | null;
  typeLocation?: TypeLocation | null;
  dateMiseEnLocation?: string | null;
  regimeVise?: RegimeVise | null;
  commentaire?: string | null;
  createdAt: string;
  updatedAt: string;
}