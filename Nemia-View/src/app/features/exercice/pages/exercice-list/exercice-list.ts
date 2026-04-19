import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ExerciceApi } from '../../services/exercice-api';
import { ExerciceResponse } from '../../models/exercice-response';
import { ConfirmDialog } from '../../../../shared/ui/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-exercice-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    DatePipe,
  ],
  templateUrl: './exercice-list.html',
  styleUrl: './exercice-list.scss',
})
export class ExerciceList implements OnInit {
  private readonly exerciceApi = inject(ExerciceApi);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly cdr = inject(ChangeDetectorRef);

  exercices: ExerciceResponse[] = [];
  isLoading = false;
  errorMessage = '';

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.loadExercices();
  }

  loadExercices(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.exerciceApi.getAll().subscribe({
      next: (exercices) => {
        this.exercices = exercices;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les exercices.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  allerVersModifier(id: number): void {
    this.router.navigate(['/exercices', id, 'modifier']);
  }

  supprimerExercice(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: "Supprimer l'exercice",
        message: 'Confirmer la suppression de cet exercice ?',
        confirmLabel: 'Supprimer',
        cancelLabel: 'Annuler',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;

      this.exerciceApi.delete(id).subscribe({
        next: () => {
          this.snackBar.open('Exercice supprimé avec succès.', 'Fermer', {
            duration: 3000,
            horizontalPosition: 'end',
            verticalPosition: 'top',
          });
          this.loadExercices();
        },
        error: () => {
          this.snackBar.open('La suppression a échoué.', 'Fermer', {
            duration: 4000,
            horizontalPosition: 'end',
            verticalPosition: 'top',
          });
        },
      });
    });
  }

  getStatutLabel(statut: string): string {
    const labels: Record<string, string> = {
      OUVERT: 'Ouvert',
      EN_PREPARATION_DE_CLOTURE: 'En préparation de clôture',
      CLOTURE: 'Clôturé',
    };
    return labels[statut] ?? statut;
  }

  getCompletudLabel(niveau: string | null | undefined): string {
    if (!niveau) return '—';
    const labels: Record<string, string> = {
      FAIBLE: 'Faible',
      MOYEN: 'Moyen',
      AVANCE: 'Avancé',
      COMPLET: 'Complet',
    };
    return labels[niveau] ?? niveau;
  }
}
