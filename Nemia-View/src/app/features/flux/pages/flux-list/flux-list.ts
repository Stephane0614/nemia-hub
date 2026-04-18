import { ChangeDetectorRef, Component, OnInit, inject, ElementRef, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ConfirmDialog } from '../../../../shared/ui/confirm-dialog/confirm-dialog';
import { FluxResponse } from '../../models/flux-response';
import { FluxApi } from '../../services/flux-api';
import { DecimalPipe, DatePipe, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';

@Component({
  selector: 'app-flux-list',
  imports: [
    RouterLink,
    MatCardModule,
    DecimalPipe,
    DatePipe,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
  ],
  templateUrl: './flux-list.html',
  styleUrl: './flux-list.scss',
})
export class FluxList implements OnInit {
  private readonly fluxApi = inject(FluxApi);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  fluxes: FluxResponse[] = [];
  isLoading = false;
  loadErrorMessage = '';
  @ViewChild('tableWrapper') tableWrapper!: ElementRef<HTMLDivElement>;

  scrollTable(direction: 'left' | 'right'): void {
    const el = this.tableWrapper.nativeElement;
    el.scrollBy({ left: direction === 'right' ? 200 : -200, behavior: 'smooth' });
  }

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.loadFluxes();
  }

  loadFluxes(): void {
    this.isLoading = true;
    this.loadErrorMessage = '';

    this.fluxApi.getAll().subscribe({
      next: (fluxes) => {
        this.fluxes = fluxes;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('loadFluxes:error', error);
        this.loadErrorMessage = 'Impossible de charger les opérations.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  deleteFlux(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: 'Supprimer l’opération',
        message: 'Confirmer la suppression de cette opération ?',
        confirmLabel: 'Supprimer',
        cancelLabel: 'Annuler',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) {
        return;
      }

      this.fluxApi.delete(id).subscribe({
        next: () => {
          this.openSuccessSnackBar('Opération supprimée avec succès.');
          this.loadFluxes();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression du flux', error);
          this.loadErrorMessage = 'Impossible de supprimer l’opération.';
          this.openErrorSnackBar('La suppression a échoué.');
        },
      });
    });
  }

  private openSuccessSnackBar(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['app-snackbar-success'],
    });
  }

  private openErrorSnackBar(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 4000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: ['app-snackbar-error'],
    });
  }
}
