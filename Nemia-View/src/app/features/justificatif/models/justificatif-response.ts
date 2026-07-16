export interface JustificatifResponse {
  id: number;
  typePiece: string;
  statutDocumentaire: string;
  datePiece?: string;
  referencePiece?: string;
  emetteur?: string;
  commentaire?: string;
  fichierAssocie?: string;
  fichierNom?: string | null;
  fichierType?: string | null;
  fichierTaille?: number | null;
  createdAt: string;
  updatedAt: string;
}

