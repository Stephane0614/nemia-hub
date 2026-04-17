import { Component } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';

type HomeMetric = {
  label: string;
  value: string;
  detail: string;
};

type HomeOperation = {
  date: string;
  label: string;
  type: 'RECETTE' | 'DEPENSE';
  amount: number;
};

@Component({
  selector: 'app-home',
  imports: [CommonModule, MatCardModule, MatDividerModule, CurrencyPipe, DatePipe],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  readonly monthLabel = 'Avril 2026';

  readonly metrics: HomeMetric[] = [
    {
      label: 'Recettes du mois',
      value: '2 145 €',
      detail: '3 encaissements enregistrés',
    },
    {
      label: 'Dépenses du mois',
      value: '684 €',
      detail: '5 dépenses enregistrées',
    },
    {
      label: 'Solde du mois',
      value: '1 461 €',
      detail: 'Vision mensuelle provisoire',
    },
    {
      label: 'Opérations du mois',
      value: '8',
      detail: 'Suivi d’activité courant',
    },
  ];

  readonly recentOperations: HomeOperation[] = [
    {
      date: '2026-04-12',
      label: 'Loyer appartement Bordeaux',
      type: 'RECETTE',
      amount: 715,
    },
    {
      date: '2026-04-10',
      label: 'Charges de copropriété',
      type: 'DEPENSE',
      amount: 148,
    },
    {
      date: '2026-04-08',
      label: 'Assurance PNO',
      type: 'DEPENSE',
      amount: 32,
    },
    {
      date: '2026-04-05',
      label: 'Loyer studio Mérignac',
      type: 'RECETTE',
      amount: 680,
    },
    {
      date: '2026-04-03',
      label: 'Intervention plomberie',
      type: 'DEPENSE',
      amount: 210,
    },
  ];

  readonly attentionPoints: string[] = [
    'Une dépense de plomberie a été enregistrée ce mois-ci.',
    'Le niveau de dépenses reste modéré par rapport aux recettes.',
    'Cette page est une maquette fonctionnelle en attendant les vraies données métier.',
  ];
}