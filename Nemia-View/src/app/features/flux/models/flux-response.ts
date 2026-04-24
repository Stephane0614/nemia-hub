// flux-response.ts
import { FluxCategory } from './flux-category';
import { FluxType } from './flux-type';
import {
  Occurrence,
  QualificationPressentie,
  StatutJustificatif,
  StatutTraitement,
} from './flux-enums';
import { PaymentMode } from './payment-mode';

export interface FluxResponse {
  id: number;
  date: string;
  type: FluxType;
  libelle: string;
  montant: number;
  categorie: FluxCategory;
  modePaiement: PaymentMode;
  commentaire: string | null;
  createdAt: string;
  updatedAt: string;
  dateValeur: string | null;
  occurrence: Occurrence;
  statutJustificatif: StatutJustificatif;
  qualificationPressentie: QualificationPressentie;
  statutTraitement: StatutTraitement;
  bienId: number | null;
  exerciceId: number | null;
  warnings: string[];
  justificatifId?: number | null;
  travauxId: number | null;
}
