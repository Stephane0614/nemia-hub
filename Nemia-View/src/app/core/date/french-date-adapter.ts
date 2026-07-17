import { Injectable } from '@angular/core';
import { NativeDateAdapter } from '@angular/material/core';

/**
 * NativeDateAdapter.parse() ignore la locale et délègue à `new Date(string)`,
 * qui interprète les dates saisies au clavier au format américain (MM/DD/YYYY).
 * Résultat : taper "13/07/2026" échoue (mois 13 invalide) alors que
 * "07/13/2026" est accepté à tort. Cet adapter force l'interprétation
 * jj/mm/aaaa pour la saisie manuelle, cohérente avec MAT_DATE_LOCALE 'fr-FR'.
 */
@Injectable()
export class FrenchDateAdapter extends NativeDateAdapter {
  override parse(value: unknown): Date | null {
    if (typeof value === 'string' && value.trim().length > 0) {
      const match = value.trim().match(/^(\d{1,2})\/(\d{1,2})\/(\d{2,4})$/);
      if (!match) {
        return new Date(NaN);
      }

      const day = Number(match[1]);
      const month = Number(match[2]);
      let year = Number(match[3]);
      if (match[3].length === 2) {
        year += year < 50 ? 2000 : 1900;
      }

      const date = new Date(year, month - 1, day);
      const isValid = date.getFullYear() === year && date.getMonth() === month - 1 && date.getDate() === day;
      return isValid ? date : new Date(NaN);
    }

    return super.parse(value);
  }
}
