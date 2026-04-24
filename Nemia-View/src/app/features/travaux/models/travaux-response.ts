export interface TravauxResponse {
  id: number;
  libelleTravaux: string;
  bienId: number;
  montantTotal: number;
  finalitePressentie: string;
  qualificationPressentie: string;
  statutTravaux: string;
  dateDebut: string | null;
  dateFin: string | null;
  commentaire: string | null;
  createdAt: string;
  updatedAt: string;
}
