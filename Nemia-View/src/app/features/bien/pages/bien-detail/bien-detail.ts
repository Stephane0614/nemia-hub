import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { ChangeDetectorRef } from '@angular/core';
import { BienApi } from '../../services/bien-api';
import { BienResponse } from '../../models/bien-response';
import { ConfirmDialog } from '../../../../shared/ui/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-bien-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    MatDividerModule,
    DatePipe,
  ],
  templateUrl: './bien-detail.html',
  styleUrl: './bien-detail.scss',
})
export class BienDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly bienApi = inject(BienApi);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly cdr = inject(ChangeDetectorRef);

  bien: BienResponse | null = null;
  isLoading = false;
  errorMessage = '';

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadBien(id);
    }
  }

  loadBien(id: number): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.bienApi.getById(id).subscribe({
      next: (bien) => {
        this.bien = bien;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger ce bien.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  allerVersModifier(): void {
    if (this.bien) {
      this.router.navigate(['/biens', this.bien.id, 'modifier']);
    }
  }

  supprimerBien(): void {
    if (!this.bien) return;

    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: 'Supprimer le bien',
        message: 'Confirmer la suppression de ce bien ? Cette action est irréversible.',
        confirmLabel: 'Supprimer',
        cancelLabel: 'Annuler',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;

      this.bienApi.delete(this.bien!.id).subscribe({
        next: () => {
          this.snackBar.open('Bien supprimé avec succès.', 'Fermer', {
            duration: 3000,
            horizontalPosition: 'end',
            verticalPosition: 'top',
          });
          this.router.navigate(['/biens']);
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