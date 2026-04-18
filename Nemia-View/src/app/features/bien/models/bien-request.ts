import { StatutActiviteBien } from './statut-activite-bien';
import { TypeLocation } from './type-location';
import { RegimeVise } from './regime-vise';

export interface BienRequest {
  nomUsuel: string;
  adresseSimplifiee: string;
  statutActivite: StatutActiviteBien;
  photoUrl?: string | null;
  typeLocation?: TypeLocation | null;
  dateMiseEnLocation?: string | null;
  regimeVise?: RegimeVise | null;
  commentaire?: string | null;
}
