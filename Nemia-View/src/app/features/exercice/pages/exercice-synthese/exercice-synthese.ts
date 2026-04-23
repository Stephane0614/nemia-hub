import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { ChangeDetectorRef } from '@angular/core';
import localeFr from '@angular/common/locales/fr';

import { MatCardModule } from '@angular/material/card';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { ExerciceApi } from '../../services/exercice-api';
import { ExerciceResponse } from '../../models/exercice-response';
import { ExerciceSyntheseResponse } from '../../models/exercice-synthese-response';

@Component({
  selector: 'app-exercice-synthese',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatSelectModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    RouterLink,
  ],
  templateUrl: './exercice-synthese.html',
  styleUrl: './exercice-synthese.scss',
})
export class ExerciceSynthese implements OnInit {
  private readonly exerciceApi = inject(ExerciceApi);
  private readonly cdr = inject(ChangeDetectorRef);

  exercices: ExerciceResponse[] = [];
  selectedExerciceId: number | null = null;
  synthese: ExerciceSyntheseResponse | null = null;

  isLoading = false;
  isLoadingSynthese = false;
  errorMessage: string | null = null;
  aucunExerciceEnCours = false;

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.isLoading = true;
    this.loadExercices();
  }

  private loadExercices(): void {
    this.exerciceApi.getAll().subscribe({
      next: (exercices) => {
        this.exercices = exercices.sort((a, b) => b.dateDebut.localeCompare(a.dateDebut));
        this.loadExerciceEnCours();
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les exercices.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  private loadExerciceEnCours(): void {
    this.exerciceApi.getEnCours().subscribe({
      next: (exercice) => {
        this.selectedExerciceId = exercice.id;
        this.isLoading = false;
        this.loadSynthese(exercice.id);
        this.cdr.detectChanges();
      },
      error: () => {
        this.aucunExerciceEnCours = true;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  onExerciceChange(): void {
    if (!this.selectedExerciceId) return;
    this.aucunExerciceEnCours = false;
    this.loadSynthese(this.selectedExerciceId);
  }

  private loadSynthese(id: number): void {
    this.isLoadingSynthese = true;
    this.synthese = null;
    this.errorMessage = null;

    this.exerciceApi.getSynthese(id).subscribe({
      next: (synthese) => {
        this.synthese = synthese;
        this.isLoadingSynthese = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger la synthèse.';
        this.isLoadingSynthese = false;
        this.cdr.detectChanges();
      },
    });
  }

  formatDate(dateStr: string | undefined | null): string {
    if (!dateStr) return '—';
    const parts = dateStr.split('-');
    if (parts.length !== 3) return dateStr;
    const [year, month, day] = parts;
    return `${day}/${month}/${year}`;
  }

  formatMontant(montant: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR',
    }).format(montant);
  }

  formatPourcent(value: number): string {
    return `${Math.round(value)}%`;
  }
}
