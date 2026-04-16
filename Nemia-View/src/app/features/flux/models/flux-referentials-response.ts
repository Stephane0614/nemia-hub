import { ReferentialItem } from './referential-item';

export interface FluxReferentialsResponse {
  types: ReferentialItem[];
  categories: ReferentialItem[];
  paymentModes: ReferentialItem[];
}
