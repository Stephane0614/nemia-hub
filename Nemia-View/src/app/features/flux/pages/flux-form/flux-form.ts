import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit, computed, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiErrorResponse } from '../../models/api-error-response';
import { FluxCategory } from '../../models/flux-category';
import { FluxRequest } from '../../models/flux-request';
import { FluxType } from '../../models/flux-type';
import { PaymentMode } from '../../models/payment-mode';
import { ReferentialItem } from '../../models/referential-item';
import { FluxApi } from '../../services/flux-api';
import { BienApi } from '../.././../bien/services/bien-api';
import { BienResponse } from '../../../bien/models/bien-response';
import { ExerciceApi } from '../../../exercice/services/exercice-api';
import { ExerciceResponse } from '../../../exercice/models/exercice-response';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TravauxApi } from '../../../travaux/services/travaux-api';
import { TravauxResponse } from '../../../travaux/models/travaux-response';
import { TravauxForm, TravauxDialogData } from '../../../travaux/pages/travaux-form/travaux-form';
import { MobilierApi } from '../../../mobilier/services/mobilier-api';
import { MobilierResponse } from '../../../mobilier/models/mobilier-response';
import {
  MobilierForm,
  MobilierDialogData,
} from '../../../mobilier/pages/mobilier-form/mobilier-form';
import { EmpruntApi } from '../../../emprunt/services/emprunt-api';
import { EmpruntResponse } from '../../../emprunt/models/emprunt-response';
import { EmpruntFormComponent, EmpruntFormDialogData } from '../../../emprunt/pages/emprunt-form/emprunt-form';
import { ConfirmDialog } from '../../../../shared/ui/confirm-dialog/confirm-dialog';

import {
  Occurrence,
  QualificationPressentie,
  StatutJustificatif,
  StatutTraitement,
} from '../../models/flux-enums';

@Component({
  selector: 'app-flux-form',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatDialogModule,
    MatAutocompleteModule,
  ],
  templateUrl: './flux-form.html',
  styleUrl: './flux-form.scss',
})
export class FluxForm implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);
  private readonly fluxApi = inject(FluxApi);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly bienApi = inject(BienApi);
  private readonly dialog = inject(MatDialog);
  readonly isEditMode = computed(() => this.fluxId() !== null);
  private readonly exerciceApi = inject(ExerciceApi);
  private readonly travauxApi = inject(TravauxApi);
  private readonly mobilierApi = inject(MobilierApi);
  private readonly empruntApi = inject(EmpruntApi);

  serverValidationErrors: Record<string, string> = {};
  loadErrorMessage = '';
  submitErrorMessage = '';
  isLoading = false;
  isSubmitting = false;
  submitWarnings: string[] = [];
  travaux: TravauxResponse[] = [];
  travauxLoading = false;
  mobiliers: MobilierResponse[] = [];
  mobiliersLoading = false;
  emprunts: EmpruntResponse[] = [];

  fluxTypes: { code: string; label: string }[] = [];
  fluxCategories: ReferentialItem[] = [];
  paymentModes: { code: string; label: string }[] = [];
  occurrences: { code: string; label: string }[] = [];
  statutsJustificatif: { code: string; label: string }[] = [];
  qualificationsPressenties: { code: string; label: string }[] = [];
  statutsTraitement: { code: string; label: string }[] = [];
  biens: BienResponse[] = [];
  exercices: ExerciceResponse[] = [];
  exercicesLoading = false;
  biensLoading = false;
  fluxCreatedId: number | null = null;
  // Justificatif lié au flux (lecture seule) — permet d'accéder à sa page de modification
  linkedJustificatifId: number | null = null;

  // Toutes les catégories connues (référentiel complet) — sert de filet de
  // sécurité pour afficher une catégorie "legacy" absente du préfiltre par type
  allCategories: ReferentialItem[] = [];
  // Catégories autorisées pour le typeFlux courant, triées alphabétiquement
  // Catégories affichées dans l'autocomplete après filtrage par texte tapé
  filteredCategories: ReferentialItem[] = [];
  // Contrôle séparé du form group : porte le texte tapé/affiché, pas le code
  readonly categorieSearchControl = new FormControl<string>('', { nonNullable: true });

  readonly fluxId = computed(() => {
    const id = this.route.snapshot.paramMap.get('id');
    return id ? Number(id) : null;
  });

  readonly form = this.formBuilder.group({
    date: [null as Date | null, Validators.required],
    type: ['', Validators.required],
    libelle: ['', [Validators.required, Validators.maxLength(120)]],
    montant: [null as number | null, [Validators.required, Validators.min(0.01)]],
    categorie: ['', Validators.required],
    modePaiement: ['', Validators.required],
    dateValeur: [null as Date | null],
    occurrence: ['', Validators.required],
    statutJustificatif: ['', Validators.required],
    qualificationPressentie: ['', Validators.required],
    statutTraitement: ['', Validators.required],
    bienId: [null as number | null],
    exerciceId: [null as number | null],
    commentaire: ['', Validators.maxLength(500)],
    travauxId: [null as number | null],
    mobilierId: [null as number | null],
    empruntId: [null as number | null],
  });

  ngOnInit(): void {
    this.isLoading = true;
    this.loadErrorMessage = '';

    // Préfiltre catégorie : à chaque changement de type (utilisateur), on
    // recharge les catégories autorisées et on vide la catégorie si elle
    // n'est plus cohérente. En mode édition, ce rechargement est déclenché
    // manuellement depuis loadFlux() sans ce comportement de vidage (voir
    // applyCategoriesForType).
    this.form.get('type')!.valueChanges.subscribe((type) => {
      this.applyCategoriesForType(type, true);
    });

    // Filtrage texte de l'autocomplete catégorie au fur et à mesure de la saisie
    this.categorieSearchControl.valueChanges.subscribe((value) => {
      if (typeof value === 'string') {
        this.updateFilteredCategories(value);
        this.cdr.detectChanges();
      }
    });

    this.fluxApi.getReferentials().subscribe({
      next: (referentials) => {
        this.fluxTypes = this.sortByLabel(referentials.types);
        this.allCategories = referentials.categories;
        this.fluxCategories = this.sortByLabel(referentials.categories);
        this.updateFilteredCategories(this.categorieSearchControl.value);
        this.paymentModes = this.sortByLabel(referentials.paymentModes);
        this.occurrences = this.sortByLabel(referentials.occurrences ?? []);
        this.statutsJustificatif = this.sortByLabel(referentials.statutJustificatifs ?? []);
        this.qualificationsPressenties = this.sortByLabel(referentials.qualificationPressenties ?? []);
        this.statutsTraitement = this.sortByLabel(referentials.statutTraitements ?? []);
        this.biensLoading = true;
        this.exercicesLoading = true;
        this.exerciceApi.getAll().subscribe({
          next: (exercices) => {
            this.exercices = exercices.sort((a, b) => b.dateDebut.localeCompare(a.dateDebut));
            this.exercicesLoading = false;
            this.cdr.detectChanges();
          },
          error: () => {
            this.exercicesLoading = false;
            this.cdr.detectChanges();
          },
        });
        this.bienApi.getAll().subscribe({
          next: (biens) => {
            this.biens = biens;
            this.biensLoading = false;
            this.cdr.detectChanges();
          },
          error: () => {
            this.biensLoading = false;
            this.cdr.detectChanges();
          },
        });
        this.travauxLoading = true;
        this.travauxApi.getAll().subscribe({
          next: (travaux) => {
            this.travaux = travaux;
            this.travauxLoading = false;
            this.cdr.detectChanges();
          },
          error: () => {
            this.travauxLoading = false;
            this.cdr.detectChanges();
          },
        });
        this.mobiliersLoading = true;
        this.mobilierApi.getAll().subscribe({
          next: (mobiliers) => {
            this.mobiliers = mobiliers;
            this.mobiliersLoading = false;
            this.cdr.detectChanges();
          },
          error: () => {
            this.mobiliersLoading = false;
            this.cdr.detectChanges();
          },
        });
        this.loadEmprunts();

        const fluxId = this.fluxId();

        if (fluxId !== null) {
          this.loadFlux(fluxId);
          return;
        }

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erreur lors du chargement des référentiels', error);
        this.loadErrorMessage = 'Impossible de charger les référentiels.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  private sortByLabel<T extends { label: string }>(items: T[]): T[] {
    return [...items].sort((a, b) => a.label.localeCompare(b.label, 'fr'));
  }

  // Recharge les catégories autorisées pour le typeFlux donné.
  // isUserChange=true (sélection en direct par l'utilisateur) : si la
  // catégorie actuellement choisie n'est plus autorisée, elle est vidée.
  // isUserChange=false (chargement initial d'un flux existant) : on ne vide
  // jamais une catégorie déjà validée côté backend ; si elle n'apparaît pas
  // dans le préfiltre (catégorie legacy), on l'ajoute à la liste affichée.
  private applyCategoriesForType(type: string | null, isUserChange: boolean): void {
    this.fluxApi.getCategoriesByType(type || undefined).subscribe({
      next: (categories) => {
        let list = categories;
        const currentCode = this.form.get('categorie')?.value;
        const stillValid = !!currentCode && list.some((c) => c.code === currentCode);

        if (currentCode && !stillValid) {
          if (isUserChange) {
            this.form.get('categorie')!.setValue(null);
          } else {
            const legacy = this.allCategories.find((c) => c.code === currentCode);
            if (legacy) {
              list = [...list, legacy];
            }
          }
        }

        this.fluxCategories = this.sortByLabel(list);
        this.syncCategorieDisplay();
        this.updateFilteredCategories(this.categorieSearchControl.value);
        this.cdr.detectChanges();
      },
    });
  }

  private updateFilteredCategories(searchText: string): void {
    const term = (searchText ?? '').trim().toLowerCase();
    this.filteredCategories = term ? this.fluxCategories.filter((c) => c.label.toLowerCase().includes(term)) : this.fluxCategories;
  }

  private getCategorieLabel(code: string | null | undefined): string | null {
    if (!code) return null;
    return (this.fluxCategories.find((c) => c.code === code) ?? this.allCategories.find((c) => c.code === code))?.label ?? null;
  }

  private syncCategorieDisplay(): void {
    const code = this.form.get('categorie')?.value;
    this.categorieSearchControl.setValue(this.getCategorieLabel(code) ?? '', { emitEvent: false });
  }

  onCategorieSelected(event: MatAutocompleteSelectedEvent): void {
    const code = event.option.value as string;
    this.form.get('categorie')!.setValue(code);
    this.form.get('categorie')!.markAsTouched();
    this.clearServerError('categorie');
    this.categorieSearchControl.setValue(event.option.viewValue, { emitEvent: false });
  }

  onCategorieBlur(): void {
    this.form.get('categorie')!.markAsTouched();
    this.syncCategorieDisplay();
    this.updateFilteredCategories(this.categorieSearchControl.value);
  }

  onDateEnterKey(event: Event): void {
    // Empêche la touche Entrée de soumettre le formulaire pendant la saisie
    // manuelle d'une date ; elle valide/ferme simplement le champ.
    event.preventDefault();
  }

  hasError(controlName: string, errorCode: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.touched && control.hasError(errorCode);
  }

  getServerError(controlName: string): string | null {
    return this.serverValidationErrors[controlName] ?? null;
  }

  clearServerError(controlName: string): void {
    if (!this.serverValidationErrors[controlName]) {
      return;
    }

    const { [controlName]: _, ...remainingErrors } = this.serverValidationErrors;
    this.serverValidationErrors = remainingErrors;
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.serverValidationErrors = {};
    this.submitErrorMessage = '';
    this.isSubmitting = true;

    const payload = this.buildPayload();

    // Si flux déjà créé (après warning en mode création) → update
    const effectiveId = this.fluxId() ?? this.fluxCreatedId;
    const effectiveEditMode = this.isEditMode() || this.fluxCreatedId !== null;

    if (effectiveEditMode && effectiveId) {
      this.fluxApi.update(effectiveId, payload).subscribe({
        next: (fluxResponse) => {
          this.isSubmitting = false;
          this.handleSaveSuccess(fluxResponse.warnings);
        },
        error: (error: HttpErrorResponse) => {
          this.isSubmitting = false;
          const apiError = error.error as ApiErrorResponse | undefined;
          if (apiError?.validationErrors) {
            this.handleSaveValidationError(apiError.validationErrors);
            return;
          }
          this.handleUnexpectedSubmitError();
        },
      });
      return;
    }

    this.fluxApi.create(payload).subscribe({
      next: (fluxResponse) => {
        this.isSubmitting = false;
        this.handleSaveSuccess(fluxResponse.warnings, fluxResponse.id);
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        const apiError = error.error as ApiErrorResponse | undefined;
        if (apiError?.validationErrors) {
          this.handleSaveValidationError(apiError.validationErrors);
          return;
        }
        this.handleUnexpectedSubmitError();
      },
    });
  }

  private buildPayload(): FluxRequest {
    const rawValue = this.form.getRawValue();

    return {
      date: this.formatDateForApi(rawValue.date),
      type: rawValue.type as FluxType,
      libelle: (rawValue.libelle ?? '').trim(),
      montant: rawValue.montant ?? 0,
      categorie: rawValue.categorie as FluxCategory,
      modePaiement: rawValue.modePaiement as PaymentMode,
      occurrence: rawValue.occurrence as Occurrence,
      statutJustificatif: rawValue.statutJustificatif as StatutJustificatif,
      qualificationPressentie: rawValue.qualificationPressentie as QualificationPressentie,
      statutTraitement: rawValue.statutTraitement as StatutTraitement,
      dateValeur: this.formatDateForApi(rawValue.dateValeur) || null,
      commentaire: (rawValue.commentaire ?? '').trim() || null,
      bienId: rawValue.bienId ? Number(rawValue.bienId) : null,
      exerciceId: rawValue.exerciceId ? Number(rawValue.exerciceId) : null,
      travauxId: null,
      mobilierId: null,
    };
  }

  private formatDateForApi(date: Date | null): string {
    if (!date) {
      return '';
    }

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  private parseApiDate(value: string | null | undefined): Date | null {
    if (!value) {
      return null;
    }

    const [year, month, day] = value.split('-').map(Number);

    if (!year || !month || !day) {
      return null;
    }

    return new Date(year, month - 1, day);
  }

  private applyServerValidationErrors(errors: Record<string, string> | undefined): void {
    this.serverValidationErrors = errors ?? {};
  }

  private handleSaveSuccess(warnings: string[], createdId?: number): void {
    this.serverValidationErrors = {};
    this.submitErrorMessage = '';
    this.submitWarnings = [];

    if (warnings.length > 0) {
      this.submitWarnings = warnings;
      if (!this.isEditMode() && createdId) {
        this.fluxCreatedId = createdId;
      }
      this.cdr.detectChanges();
      return;
    }

    if (!this.isEditMode()) {
      const dialogRef = this.dialog.open(ConfirmDialog, {
        width: '450px',
        disableClose: true,
        data: {
          title: 'Ajouter un justificatif',
          message: 'Souhaitez-vous ajouter une pièce justificative pour cette opération ?',
          confirmLabel: 'Ajouter un justificatif',
          cancelLabel: 'Plus tard',
        },
      });

      dialogRef.afterClosed().subscribe((confirmed) => {
        this.form.reset();
        if (confirmed && createdId) {
          this.router.navigate(['/justificatifs/nouveau'], { queryParams: { fluxId: createdId } });
        } else {
          this.router.navigateByUrl('/flux');
        }
      });
    } else {
      this.form.reset();
      this.router.navigateByUrl('/flux');
    }
  }

  private handleSaveValidationError(errors: Record<string, string> | undefined): void {
    this.applyServerValidationErrors(errors);
    this.submitErrorMessage = '';
  }

  private handleUnexpectedSubmitError(): void {
    this.submitErrorMessage = 'Une erreur est survenue lors de l’enregistrement.';
  }

  private loadFlux(id: number): void {
    this.fluxApi.getById(id).subscribe({
      next: (flux) => {
        // emitEvent:false — évite que le patch de "type" ne déclenche le
        // rechargement/vidage de catégorie destiné aux changements en direct
        // par l'utilisateur (voir applyCategoriesForType appelé juste après).
        this.form.patchValue(
          {
            date: this.parseApiDate(flux.date),
            type: flux.type,
            libelle: flux.libelle,
            montant: flux.montant,
            categorie: flux.categorie,
            modePaiement: flux.modePaiement,
            commentaire: flux.commentaire ?? '',
            dateValeur: this.parseApiDate(flux.dateValeur),
            occurrence: flux.occurrence,
            statutJustificatif: flux.statutJustificatif,
            qualificationPressentie: flux.qualificationPressentie,
            statutTraitement: flux.statutTraitement,
            bienId: flux.bienId,
            exerciceId: flux.exerciceId,
            mobilierId: flux.mobilierId ?? null,
            empruntId: flux.empruntId ?? null,
            travauxId: flux.travauxId ?? null,
          },
          { emitEvent: false },
        );

        this.linkedJustificatifId = flux.justificatifId ?? null;
        this.applyCategoriesForType(flux.type, false);

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erreur lors du chargement du flux', error);
        this.loadErrorMessage = 'Impossible de charger l’opération à modifier.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  confirmAndRedirect(): void {
    this.submitWarnings = [];
    const wasCreation = !this.isEditMode() && this.fluxCreatedId !== null;
    const createdId = this.fluxCreatedId;
    this.fluxCreatedId = null;
    if (wasCreation) {
      const dialogRef = this.dialog.open(ConfirmDialog, {
        width: '450px',
        disableClose: true,
        data: {
          title: 'Ajouter un justificatif',
          message: 'Souhaitez-vous ajouter une pièce justificative pour cette opération ?',
          confirmLabel: 'Ajouter un justificatif',
          cancelLabel: 'Plus tard',
        },
      });

      dialogRef.afterClosed().subscribe((confirmed) => {
        this.form.reset();
        if (confirmed && createdId) {
          this.router.navigate(['/justificatifs/nouveau'], { queryParams: { fluxId: createdId } });
        } else {
          this.router.navigateByUrl('/flux');
        }
      });
    } else {
      this.form.reset();
      this.router.navigateByUrl('/flux');
    }
  }

  formatExerciceLabel(exercice: ExerciceResponse): string {
    const formatDate = (dateStr: string): string => {
      const [year, month, day] = dateStr.split('-');
      return `${day}/${month}/${year}`;
    };
    return `${exercice.libelleExercice} (${formatDate(exercice.dateDebut)} → ${formatDate(exercice.dateFin)})`;
  }

  ouvrirDialogTravaux(): void {
    const dialogRef = this.dialog.open(TravauxForm, {
      width: '720px',
      disableClose: false,
      data: {} as TravauxDialogData,
    });

    dialogRef.afterClosed().subscribe((travaux: TravauxResponse | null) => {
      if (!travaux) return;
      this.travauxApi.getAll().subscribe({
        next: (list) => {
          this.travaux = list;
          this.form.patchValue({ travauxId: travaux.id });
          this.cdr.detectChanges();
        },
      });
    });
  }

  formatTravauxLabel(t: TravauxResponse): string {
    return `${t.libelleTravaux} — ${t.montantTotal.toLocaleString('fr-FR')} €`;
  }

  ouvrirDialogMobilier(): void {
    const dialogRef = this.dialog.open(MobilierForm, {
      width: '720px',
      disableClose: false,
      data: {} as MobilierDialogData,
    });

    dialogRef.afterClosed().subscribe((mobilier: MobilierResponse | null) => {
      if (!mobilier) return;
      this.mobilierApi.getAll().subscribe({
        next: (list) => {
          this.mobiliers = list;
          this.form.patchValue({ mobilierId: mobilier.id });
          this.cdr.detectChanges();
        },
      });
    });
  }

  formatMobilierLabel(m: MobilierResponse): string {
    return `${m.designation} — ${m.montant.toLocaleString('fr-FR')} €`;
  }

  loadEmprunts(): void {
    this.empruntApi.getAll().subscribe({
      next: (emprunts) => {
        this.emprunts = emprunts;
        this.cdr.detectChanges();
      },
      error: () => {},
    });
  }

  getEmpruntLabel(emprunt: EmpruntResponse): string {
    return emprunt.organismePreteur
      ? `${emprunt.referencePret} — ${emprunt.organismePreteur}`
      : emprunt.referencePret;
  }

  ouvrirDialogEmprunt(): void {
    const ref = this.dialog.open(EmpruntFormComponent, {
      width: '600px',
      disableClose: true,
      data: { bienId: this.form.get('bienId')?.value } as EmpruntFormDialogData,
    });

    ref.afterClosed().subscribe((emprunt: EmpruntResponse | null) => {
      if (emprunt) {
        this.loadEmprunts();
        this.form.patchValue({ empruntId: emprunt.id });
      }
    });
  }
}
