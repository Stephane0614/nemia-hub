import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ChangeDetectorRef } from '@angular/core';
import { BienApi } from '../../services/bien-api';
import { BienResponse } from '../../models/bien-response';
import { ConfirmDialog } from '../../../../shared/ui/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-bien-list',
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
  templateUrl: './bien-list.html',
  styleUrl: './bien-list.scss',
})
export class BienList implements OnInit {
  private readonly bienApi = inject(BienApi);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly cdr = inject(ChangeDetectorRef);

  biens: BienResponse[] = [];
  isLoading = false;
  errorMessage = '';

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.loadBiens();
  }

  loadBiens(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.bienApi.getAll().subscribe({
      next: (biens) => {
        this.biens = biens;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les biens.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  allerVersDetail(id: number): void {
    this.router.navigate(['/biens', id]);
  }

  allerVersModifier(id: number): void {
    this.router.navigate(['/biens', id, 'modifier']);
  }

  supprimerBien(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: 'Supprimer le bien',
        message: 'Confirmer la suppression de ce bien ?',
        confirmLabel: 'Supprimer',
        cancelLabel: 'Annuler',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;

      this.bienApi.delete(id).subscribe({
        next: () => {
          this.snackBar.open('Bien supprimé avec succès.', 'Fermer', {
            duration: 3000,
            horizontalPosition: 'end',
            verticalPosition: 'top',
          });
          this.loadBiens();
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
      ACTIF: 'Actif',
      EN_PREPARATION: 'En préparation',
      SUSPENDU: 'Suspendu',
      CLOTURE: 'Clôturé',
    };
    return labels[statut] ?? statut;
  }
}
