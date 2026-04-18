export type Occurrence = 'RECURRENT' | 'PONCTUEL' | 'INDETERMINE';

export type StatutJustificatif =
  | 'NON_REQUIS'
  | 'A_FOURNIR'
  | 'FOURNI'
  | 'INCOMPLET'
  | 'A_VERIFIER'
  | 'REJETE';

export type QualificationPressentie =
  | 'CHARGE_COURANTE'
  | 'IMMOBILISATION'
  | 'MIXTE_OU_A_VENTILER'
  | 'HORS_RESULTAT'
  | 'A_ARBITRER'
  | 'NON_APPLICABLE';

export type StatutTraitement = 'BRUT' | 'QUALIFIE' | 'A_REVOIR' | 'VALIDE';
