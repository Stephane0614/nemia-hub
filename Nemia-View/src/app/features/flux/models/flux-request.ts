// flux-request.ts
import { FluxCategory } from './flux-category';
import { FluxType } from './flux-type';
import {
  Occurrence,
  QualificationPressentie,
  StatutJustificatif,
  StatutTraitement,
} from './flux-enums';
import { PaymentMode } from './payment-mode';

export interface FluxRequest {
  date: string;
  type: FluxType;
  libelle: string;
  montant: number;
  categorie: FluxCategory;
  modePaiement: PaymentMode;
  occurrence: Occurrence;
  statutJustificatif: StatutJustificatif;
  qualificationPressentie: QualificationPressentie;
  statutTraitement: StatutTraitement;
  dateValeur?: string | null;
  commentaire?: string | null;
  bienId?: number | null;
  exerciceId?: number | null;
  justificatifId?: number | null;
  travauxId: number | null;
  mobilierId: number | null;
}
