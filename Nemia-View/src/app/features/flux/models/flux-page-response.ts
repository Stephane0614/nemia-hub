import { FluxResponse } from './flux-response';

export interface FluxPageResponse {
  contenu: FluxResponse[];
  page: number;
  taille: number;
  totalElements: number;
  totalPages: number;
}
