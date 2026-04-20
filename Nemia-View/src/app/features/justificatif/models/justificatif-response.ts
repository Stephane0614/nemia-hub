export interface JustificatifResponse {
  id: number;
  typePiece: string;
  statutDocumentaire: string;
  datePiece?: string;
  referencePiece?: string;
  emetteur?: string;
  commentaire?: string;
  fichierAssocie?: string;
  createdAt: string;
  updatedAt: string;
}
