import { ChangeDetectorRef, Component, OnInit, ElementRef, ViewChild, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { DecimalPipe, DatePipe, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { ConfirmDialog } from '../../../../shared/ui/confirm-dialog/confirm-dialog';
import { FluxResponse } from '../../models/flux-response';
import { FluxFilters } from '../../models/flux-filters';
import { FluxApi } from '../../services/flux-api';

@Component({
  selector: 'app-flux-list',
  imports: [
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    DecimalPipe,
    DatePipe,
  ],
  templateUrl: './flux-list.html',
  styleUrl: './flux-list.scss',
})
export class FluxList implements OnInit {
  private readonly fluxApi = inject(FluxApi);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  @ViewChild('tableWrapper') tableWrapper!: ElementRef<HTMLDivElement>;

  fluxes: FluxResponse[] = [];
  isLoading = false;
  loadErrorMessage = '';
  filtresActifs: FluxFilters = {};
  labelsFiltresActifs: string[] = [];

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.filtresActifs = {};
      this.labelsFiltresActifs = [];

      if (params['bienId']) {
        this.filtresActifs.bienId = Number(params['bienId']);
        this.labelsFiltresActifs.push('Bien sélectionné');
      }

      if (params['statutJustificatif']) {
        this.filtresActifs.statutJustificatif = params['statutJustificatif'].split(',');
        this.labelsFiltresActifs.push('Sans justificatif');
      }

      if (params['qualificationPressentie']) {
        this.filtresActifs.qualificationPressentie = params['qualificationPressentie'];
        this.labelsFiltresActifs.push('À arbitrer');
      }

      if (params['statutTraitement']) {
        this.filtresActifs.statutTraitement = params['statutTraitement'];
        this.labelsFiltresActifs.push('À revoir');
      }

      this.loadFluxes();
    });
  }

  get hasFiltresActifs(): boolean {
    return this.labelsFiltresActifs.length > 0;
  }

  effacerFiltres(): void {
    this.router.navigate(['/flux']);
  }

  loadFluxes(): void {
    this.isLoading = true;
    this.loadErrorMessage = '';

    const filters = this.hasFiltresActifs ? this.filtresActifs : undefined;

    this.fluxApi.getAll(filters).subscribe({
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

  scrollTable(direction: 'left' | 'right'): void {
    const el = this.tableWrapper.nativeElement;
    el.scrollBy({ left: direction === 'right' ? 200 : -200, behavior: 'smooth' });
  }

  deleteFlux(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: "Supprimer l'opération",
        message: 'Confirmer la suppression de cette opération ?',
        confirmLabel: 'Supprimer',
        cancelLabel: 'Annuler',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (!confirmed) return;

      this.fluxApi.delete(id).subscribe({
        next: () => {
          this.openSuccessSnackBar('Opération supprimée avec succès.');
          this.loadFluxes();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression du flux', error);
          this.loadErrorMessage = "Impossible de supprimer l'opération.";
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

  isCategorieRecette(cat: string): boolean {
  return ['LOYER', 'CHARGES_REFACTUREES', 'INDEMNITE_RECUE', 'AUTRE_RECETTE_EXPLOITATION'].includes(cat);
}

isCategorieCharge(cat: string): boolean {
  return ['ELECTRICITE', 'EAU', 'INTERNET', 'ASSURANCE', 'TAXE', 'COPROPRIETE',
    'FRAIS_BANCAIRES', 'HONORAIRES', 'ENTRETIEN_COURANT', 'CONSOMMABLES',
    'MENAGE', 'FOURNITURES', 'AUTRE_CHARGE_EXPLOITATION'].includes(cat);
}

isCategorieTravaux(cat: string): boolean {
  return ['TRAVAUX', 'REPARATION_IMPORTANTE', 'AMELIORATION', 'MOBILIER',
    'ELECTROMENAGER', 'EQUIPEMENT', 'DECORATION'].includes(cat);
}

isCategorieFinancement(cat: string): boolean {
  return ['FRAIS_FINANCEMENT', 'EMPRUNT_INTERETS', 'EMPRUNT_ASSURANCE', 'EMPRUNT_CAPITAL'].includes(cat);
}

isCategorieMouvement(cat: string): boolean {
  return ['APPORT', 'RETRAIT', 'VIREMENT_INTERNE'].includes(cat);
}

isCategorieDivers(cat: string): boolean {
  return ['REGULARISATION', 'AUTRE'].includes(cat);
}
}
