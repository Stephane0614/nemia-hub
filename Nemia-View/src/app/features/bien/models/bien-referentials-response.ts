import { ReferentialItem } from '../../../features/flux/models/referential-item';

export interface BienReferentialsResponse {
  statutActivites: ReferentialItem[];
  typeLocations: ReferentialItem[];
  regimeVises: ReferentialItem[];
}