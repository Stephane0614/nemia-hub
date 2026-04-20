export interface JustificatifRequest {
  typePiece: string;
  statutDocumentaire: string;
  datePiece?: string;
  referencePiece?: string;
  emetteur?: string;
  commentaire?: string;
  fichierAssocie?: string;
}
