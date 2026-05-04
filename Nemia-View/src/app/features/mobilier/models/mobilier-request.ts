export interface MobilierRequest {
  designation: string;
  bienId: number;
  montant: number;
  categorieMobilier: string;
  qualificationPressentie: string;
  statutMobilier: string;
  dateAcquisition?: string | null;
  quantite?: number | null;
  etatUsage?: string | null;
  justificatifId?: number | null;
  commentaire?: string | null;
}
