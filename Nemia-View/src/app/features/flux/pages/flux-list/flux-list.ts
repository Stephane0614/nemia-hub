import { ChangeDetectorRef, Component, OnInit, ElementRef, ViewChild, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ActivatedRoute, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { DecimalPipe, DatePipe, registerLocaleData } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import localeFr from '@angular/common/locales/fr';

import { ConfirmDialog } from '../../../../shared/ui/confirm-dialog/confirm-dialog';
import { FluxResponse } from '../../models/flux-response';
import { FluxFilters } from '../../models/flux-filters';
import { FluxApi } from '../../services/flux-api';
import { BienApi } from '../../../bien/services/bien-api';
import { ExerciceApi } from '../../../exercice/services/exercice-api';
import { BienResponse } from '../../../bien/models/bien-response';
import { ExerciceResponse } from '../../../exercice/models/exercice-response';
import { ReferentialItem } from '../../models/referential-item';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { JustificatifApi } from '../../../justificatif/services/justificatif-api';

@Component({
  selector: 'app-flux-list',
  imports: [
    MatPaginatorModule,
    RouterLink,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatSelectModule,
    MatDatepickerModule,
    MatInputModule,
    MatIconModule,
    MatTooltipModule,
    DecimalPipe,
    DatePipe,
  ],
  templateUrl: './flux-list.html',
  styleUrl: './flux-list.scss',
})
export class FluxList implements OnInit {
  private readonly fluxApi = inject(FluxApi);
  private readonly bienApi = inject(BienApi);
  private readonly exerciceApi = inject(ExerciceApi);
  private readonly justificatifApi = inject(JustificatifApi);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly dialog = inject(MatDialog);
  private readonly snackBar = inject(MatSnackBar);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  @ViewChild('tableWrapper') tableWrapper!: ElementRef<HTMLDivElement>;

  fluxes: FluxResponse[] = [];
  isLoading = false;
  loadErrorMessage = '';

  // Référentiels pour les selects
  biens: BienResponse[] = [];
  exercices: ExerciceResponse[] = [];
  fluxTypes: ReferentialItem[] = [];
  fluxCategories: ReferentialItem[] = [];
  statutsJustificatif: ReferentialItem[] = [];
  qualificationsPressenties: ReferentialItem[] = [];
  statutsTraitement: ReferentialItem[] = [];

  // Formulaire filtres
  filtresForm!: FormGroup;

  totalElements = 0;
  pageIndex = 0;
  pageSize = 20;

  get hasFiltresActifs(): boolean {
    const v = this.filtresForm?.value;
    if (!v) return false;
    return !!(
      v.bienId ||
      v.exerciceId ||
      v.type ||
      v.categorie ||
      v.dateDebut ||
      v.dateFin ||
      v.statutJustificatif ||
      v.qualificationPressentie ||
      v.statutTraitement
    );
  }

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.initFiltresForm();
    this.loadReferentiels();

    // Lecture des query params entrants
    this.route.queryParams.subscribe((params) => {
      this.filtresForm.patchValue(
        {
          bienId: params['bienId'] ? Number(params['bienId']) : null,
          exerciceId: params['exerciceId'] ? Number(params['exerciceId']) : null,
          type: params['type'] ?? null,
          categorie: params['categorie'] ?? null,
          dateDebut: params['dateDebut'] ?? null,
          dateFin: params['dateFin'] ?? null,
          statutJustificatif: params['statutJustificatif']
            ? params['statutJustificatif'].split(',')
            : null,
          qualificationPressentie: params['qualificationPressentie'] ?? null,
          statutTraitement: params['statutTraitement'] ?? null,
        },
        { emitEvent: false },
      );

      this.loadFluxes();
    });
  }

  private initFiltresForm(): void {
    this.filtresForm = this.fb.group({
      bienId: [null],
      exerciceId: [null],
      type: [null],
      categorie: [null],
      dateDebut: [null],
      dateFin: [null],
      statutJustificatif: [null],
      qualificationPressentie: [null],
      statutTraitement: [null],
    });
  }

  private loadReferentiels(): void {
    this.bienApi.getAll().subscribe({
      next: (biens) => {
        this.biens = biens;
        this.cdr.detectChanges();
      },
    });
    this.exerciceApi.getAll().subscribe({
      next: (exercices) => {
        this.exercices = exercices.sort((a, b) => b.dateDebut.localeCompare(a.dateDebut));
        this.cdr.detectChanges();
      },
    });
    this.fluxApi.getReferentials().subscribe({
      next: (refs) => {
        this.fluxTypes = refs.types;
        this.fluxCategories = refs.categories;
        this.statutsJustificatif = refs.statutJustificatifs ?? [];
        this.qualificationsPressenties = refs.qualificationPressenties ?? [];
        this.statutsTraitement = refs.statutTraitements ?? [];
        this.cdr.detectChanges();
      },
    });
  }

  appliquerFiltres(): void {
    this.pageIndex = 0;
    this.loadFluxes();
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadFluxes();
  }

  effacerFiltres(): void {
    this.filtresForm.reset();
    this.router.navigate(['/flux']);
  }

  loadFluxes(): void {
    this.isLoading = true;
    this.loadErrorMessage = '';

    const v = this.filtresForm.value;
    const filters: FluxFilters = {};

    if (v.bienId) filters.bienId = v.bienId;
    if (v.exerciceId) filters.exerciceId = v.exerciceId;
    if (v.type) filters.type = v.type;
    if (v.categorie) filters.categorie = v.categorie;
    if (v.dateDebut) filters.dateDebut = this.formatDate(v.dateDebut);
    if (v.dateFin) filters.dateFin = this.formatDate(v.dateFin);
    if (v.statutJustificatif?.length) filters.statutJustificatif = v.statutJustificatif;
    if (v.qualificationPressentie) filters.qualificationPressentie = v.qualificationPressentie;
    if (v.statutTraitement) filters.statutTraitement = v.statutTraitement;

    filters.page = this.pageIndex;
    filters.taille = this.pageSize;

    this.fluxApi.getAll(filters).subscribe({
      next: (page) => {
        this.fluxes = page.contenu;
        this.totalElements = page.totalElements;
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

  private formatDate(date: Date | string | null): string {
    if (!date) return '';
    if (typeof date === 'string') return date;
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
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
        error: () => {
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
    return [
      'LOYER',
      'CHARGES_REFACTUREES',
      'INDEMNITE_RECUE',
      'AUTRE_RECETTE_EXPLOITATION',
    ].includes(cat);
  }
  isCategorieCharge(cat: string): boolean {
    return [
      'ELECTRICITE',
      'EAU',
      'INTERNET',
      'ASSURANCE',
      'TAXE',
      'COPROPRIETE',
      'FRAIS_BANCAIRES',
      'HONORAIRES',
      'ENTRETIEN_COURANT',
      'CONSOMMABLES',
      'MENAGE',
      'FOURNITURES',
      'AUTRE_CHARGE_EXPLOITATION',
    ].includes(cat);
  }
  isCategorieTravaux(cat: string): boolean {
    return [
      'TRAVAUX',
      'REPARATION_IMPORTANTE',
      'AMELIORATION',
      'MOBILIER',
      'ELECTROMENAGER',
      'EQUIPEMENT',
      'DECORATION',
    ].includes(cat);
  }
  isCategorieFinancement(cat: string): boolean {
    return [
      'FRAIS_FINANCEMENT',
      'EMPRUNT_INTERETS',
      'EMPRUNT_ASSURANCE',
      'EMPRUNT_CAPITAL',
    ].includes(cat);
  }
  isCategorieMouvement(cat: string): boolean {
    return ['APPORT', 'RETRAIT', 'VIREMENT_INTERNE'].includes(cat);
  }
  isCategorieDivers(cat: string): boolean {
    return ['REGULARISATION', 'AUTRE'].includes(cat);
  }

  consulterJustificatif(justificatifId: number): void {
    // Ouvre l'onglet immédiatement (dans le même tick que le clic) pour éviter le blocage de popup
    // des navigateurs, qui n'autorisent window.open() qu'en réponse directe à un geste utilisateur.
    const newTab = window.open('', '_blank');

    this.justificatifApi.downloadFichier(justificatifId).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        if (newTab) {
          newTab.location.href = url;
        } else {
          window.open(url, '_blank');
        }
      },
      error: () => {
        newTab?.close();
        this.openErrorSnackBar('Impossible d’ouvrir la pièce justificative.');
      },
    });
  }
}
