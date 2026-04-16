import { FluxCategory } from './flux-category';
import { PaymentMode } from './payment-mode';
import { FluxType } from './flux-type';

export interface FluxRequest {
  date: string;
  type: FluxType;
  libelle: string;
  montant: number;
  categorie: FluxCategory;
  modePaiement: PaymentMode;
  commentaire?: string | null;
}
