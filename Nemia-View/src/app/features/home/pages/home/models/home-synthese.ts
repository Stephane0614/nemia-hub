export interface Periode {
  label: string;
  dateDebut: string;
  dateFin: string;
  mois: string;
}

export interface Metriques {
  totalRecettes: number;
  totalDepenses: number;
  solde: number;
  nombreOperations: number;
}

export interface Alertes {
  fluxSansJustificatif: number;
  montantSansJustificatif: number;
  fluxAArbitrer: number;
  montantAArbitrer: number;
  fluxARevoir: number;
}

export interface RepartitionDepense {
  categorie: string;
  label: string;
  montant: number;
}

export interface RecurrenceDepenses {
  montantRecurrent: number;
  montantPonctuel: number;
}

export interface DerniereOperation {
  id: number;
  date: string;
  libelle: string;
  type: string;
  montant: number;
  categorie: string;
  statutJustificatif: string;
  qualificationPressentie: string;
  warnings: string[];
}

export interface HomeSyntheseResponse {
  periode: Periode;
  metriques: Metriques;
  alertes: Alertes;
  repartitionDepenses: RepartitionDepense[];
  recurrenceDepenses: RecurrenceDepenses;
  dernieresOperations: DerniereOperation[];
}