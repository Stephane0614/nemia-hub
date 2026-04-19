import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe, registerLocaleData } from '@angular/common';
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
import { HomeSyntheseApi } from '../../../home/pages/home/home-synthese-api';
import { HomeSyntheseResponse } from '../../../home/pages/home/models/home-synthese';

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
    CurrencyPipe,
  ],
  templateUrl: './bien-detail.html',
  styleUrl: './bien-detail.scss',
})
export class BienDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly bienApi = inject(BienApi);
  private readonly homeSyntheseApi = inject(HomeSyntheseApi);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly cdr = inject(ChangeDetectorRef);

  bien: BienResponse | null = null;
  isLoading = false;
  errorMessage = '';

  synthese: HomeSyntheseResponse | null = null;
  syntheseLoading = false;
  syntheseError = false;

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.loadBien(id);
      this.loadSynthese(id);
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

  loadSynthese(bienId: number): void {
    this.syntheseLoading = true;
    this.syntheseError = false;

    this.homeSyntheseApi.getSynthese(undefined, bienId).subscribe({
      next: (data) => {
        this.synthese = data;
        this.syntheseLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.syntheseError = true;
        this.syntheseLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  get soldePositif(): boolean {
    return (this.synthese?.metriques?.solde ?? 0) > 0;
  }

  get aucuneAlerte(): boolean {
    if (!this.synthese?.alertes) return true;
    const a = this.synthese.alertes;
    return a.fluxSansJustificatif === 0 && a.fluxAArbitrer === 0 && a.fluxARevoir === 0;
  }

  get dernieresTrois() {
    return this.synthese?.dernieresOperations?.slice(0, 3) ?? [];
  }

  get aucuneOperation(): boolean {
    return (this.synthese?.metriques?.nombreOperations ?? 0) === 0;
  }

  allerVersFlux(): void {
    if (this.bien) {
      this.router.navigate(['/flux'], {
        queryParams: { bienId: this.bien.id },
      });
    }
  }

  allerVersSansJustificatif(): void {
    if (this.bien) {
      this.router.navigate(['/flux'], {
        queryParams: { bienId: this.bien.id, statutJustificatif: 'A_FOURNIR,INCOMPLET' },
      });
    }
  }

  allerVersAArbitrer(): void {
    if (this.bien) {
      this.router.navigate(['/flux'], {
        queryParams: { bienId: this.bien.id, qualificationPressentie: 'A_ARBITRER' },
      });
    }
  }

  allerVersARevoir(): void {
    if (this.bien) {
      this.router.navigate(['/flux'], {
        queryParams: { bienId: this.bien.id, statutTraitement: 'A_REVOIR' },
      });
    }
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
