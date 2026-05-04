import { ReferentialItem } from '../../flux/models/referential-item';

export interface MobilierReferentialsResponse {
  categoriesMobilier: ReferentialItem[];
  etatsUsage: ReferentialItem[];
  statutsMobilier: ReferentialItem[];
}
