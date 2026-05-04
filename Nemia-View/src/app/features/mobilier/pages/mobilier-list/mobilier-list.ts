import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule, DecimalPipe, DatePipe, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';

import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';

import { MobilierApi } from '../../services/mobilier-api';
import { MobilierResponse } from '../../models/mobilier-response';
import { BienApi } from '../../../bien/services/bien-api';
import { BienResponse } from '../../../bien/models/bien-response';
import { ConfirmDialog } from '../../../../shared/ui/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-mobilier-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    DecimalPipe,
    DatePipe,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    MatSelectModule,
    MatFormFieldModule,
  ],
  templateUrl: './mobilier-list.html',
  styleUrl: './mobilier-list.scss',
})
export class MobilierList implements OnInit {
  private readonly mobilierApi = inject(MobilierApi);
  private readonly bienApi = inject(BienApi);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);

  mobiliers: MobilierResponse[] = [];
  biens: BienResponse[] = [];
  selectedBienId: number | null = null;
  isLoading = false;
  loadErrorMessage = '';

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.loadBiens();
    this.loadMobilier();
  }

  private loadBiens(): void {
    this.bienApi.getAll().subscribe({
      next: (biens) => {
        this.biens = biens;
        this.cdr.detectChanges();
      },
    });
  }

  loadMobilier(): void {
    this.isLoading = true;
    this.loadErrorMessage = '';
    const bienId = this.selectedBienId ?? undefined;
    this.mobilierApi.getAll(bienId).subscribe({
      next: (mobiliers) => {
        this.mobiliers = mobiliers;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loadErrorMessage = 'Impossible de charger le mobilier.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  onBienChange(): void {
    this.loadMobilier();
  }

  deleteMobilier(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: 'Supprimer le mobilier',
        message: 'Confirmer la suppression de cet élément ?',
        confirmLabel: 'Supprimer',
        cancelLabel: 'Annuler',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;
      this.mobilierApi.delete(id).subscribe({
        next: () => {
          this.snackBar.open('Élément supprimé avec succès.', 'Fermer', {
            duration: 3000,
            horizontalPosition: 'end',
            verticalPosition: 'top',
            panelClass: ['app-snackbar-success'],
          });
          this.loadMobilier();
        },
        error: () => {
          this.snackBar.open('La suppression a échoué.', 'Fermer', {
            duration: 4000,
            horizontalPosition: 'end',
            verticalPosition: 'top',
            panelClass: ['app-snackbar-error'],
          });
        },
      });
    });
  }

  getBienNom(bienId: number): string {
    return this.biens.find((b) => b.id === bienId)?.nomUsuel ?? `Bien #${bienId}`;
  }

  formatDate(dateStr: string | null): string {
    if (!dateStr) return '—';
    const [year, month, day] = dateStr.split('-');
    return `${day}/${month}/${year}`;
  }
}