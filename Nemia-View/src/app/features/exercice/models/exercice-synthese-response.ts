export interface RepartitionCategorie {
  code: string;
  label: string;
  montant: number;
}

export interface RepartitionTypeFlux {
  type: string;
  nombre: number;
  montant: number;
}

export interface RepartitionQualification {
  qualification: string;
  nombre: number;
  montant: number;
}

export interface ExerciceIdentite {
  id: number;
  libelle: string;
  dateDebut: string;
  dateFin: string;
  statut: string;
  niveauCompletude: string;
}

export interface ExerciceFinancier {
  totalRecettes: number;
  totalDepenses: number;
  solde: number;
  nombreFluxTotal: number;
  repartitionRecettes: RepartitionCategorie[];
  repartitionDepenses: RepartitionCategorie[];
  repartitionTypeFlux: RepartitionTypeFlux[];
}

export interface ExerciceCompletude {
  nombreFluxSansJustificatif: number;
  montantSansJustificatif: number;
  nombreFluxAArbitrer: number;
  montantAArbitrer: number;
  nombreFluxARevoir: number;
  montantARevoir: number;
  tauxJustificationGlobal: number;
}

export interface ExerciceQualification {
  repartition: RepartitionQualification[];
}

export interface ExerciceSyntheseResponse {
  identite: ExerciceIdentite;
  financier: ExerciceFinancier;
  completude: ExerciceCompletude;
  qualification: ExerciceQualification;
}
