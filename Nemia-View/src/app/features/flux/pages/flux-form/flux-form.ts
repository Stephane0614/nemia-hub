import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit, computed, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiErrorResponse } from '../../models/api-error-response';
import { FluxCategory } from '../../models/flux-category';
import { FluxRequest } from '../../models/flux-request';
import { FluxType } from '../../models/flux-type';
import { PaymentMode } from '../../models/payment-mode';
import { FluxApi } from '../../services/flux-api';
import { BienApi } from '../.././../bien/services/bien-api';
import { BienResponse } from '../../../bien/models/bien-response';
import { ExerciceApi } from '../../../exercice/services/exercice-api';
import { ExerciceResponse } from '../../../exercice/models/exercice-response';
import { DatePipe } from '@angular/common';
import { JustificatifApi } from '../../../justificatif/services/justificatif-api';
import { JustificatifResponse } from '../../../justificatif/models/justificatif-response';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import {
  JustificatifForm,
  JustificatifDialogData,
} from '../../../justificatif/pages/justificatif-form/justificatif-form';

import {
  Occurrence,
  QualificationPressentie,
  StatutJustificatif,
  StatutTraitement,
} from '../../models/flux-enums';

@Component({
  selector: 'app-flux-form',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
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

  serverValidationErrors: Record<string, string> = {};
  loadErrorMessage = '';
  submitErrorMessage = '';
  isLoading = false;
  isSubmitting = false;
  submitWarnings: string[] = [];

  fluxTypes: { code: string; label: string }[] = [];
  fluxCategories: { code: string; label: string }[] = [];
  paymentModes: { code: string; label: string }[] = [];
  occurrences: { code: string; label: string }[] = [];
  statutsJustificatif: { code: string; label: string }[] = [];
  qualificationsPressenties: { code: string; label: string }[] = [];
  statutsTraitement: { code: string; label: string }[] = [];
  biens: BienResponse[] = [];
  exercices: ExerciceResponse[] = [];
  justificatifs: JustificatifResponse[] = [];
  justificatifsLoading = false;
  exercicesLoading = false;
  biensLoading = false;

  readonly fluxId = computed(() => {
    const id = this.route.snapshot.paramMap.get('id');
    return id ? Number(id) : null;
  });

  readonly isEditMode = computed(() => this.fluxId() !== null);
  private readonly exerciceApi = inject(ExerciceApi);
  private readonly justificatifApi = inject(JustificatifApi);

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
    justificatifId: [null as number | null],
  });

  ngOnInit(): void {
    this.isLoading = true;
    this.loadErrorMessage = '';

    this.fluxApi.getReferentials().subscribe({
      next: (referentials) => {
        this.fluxTypes = referentials.types;
        this.fluxCategories = referentials.categories;
        this.paymentModes = referentials.paymentModes;
        this.occurrences = referentials.occurrences ?? [];
        this.statutsJustificatif = referentials.statutJustificatifs ?? [];
        this.qualificationsPressenties = referentials.qualificationPressenties ?? [];
        this.statutsTraitement = referentials.statutTraitements ?? [];
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
        this.justificatifsLoading = true;
        this.justificatifApi.getAll().subscribe({
          next: (justificatifs) => {
            this.justificatifs = justificatifs;
            this.justificatifsLoading = false;
            this.cdr.detectChanges();
          },
          error: () => {
            this.justificatifsLoading = false;
            this.cdr.detectChanges();
          },
        });

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

    if (this.isEditMode()) {
      const fluxId = this.fluxId();

      if (fluxId === null) {
        return;
      }

      this.fluxApi.update(fluxId, payload).subscribe({
        next: (fluxResponse) => {
          this.isSubmitting = false;
          this.handleSaveSuccess(fluxResponse.warnings);
          console.log('Flux mis à jour avec succès');
        },
        error: (error: HttpErrorResponse) => {
          this.isSubmitting = false;
          const apiError = error.error as ApiErrorResponse | undefined;

          if (apiError?.validationErrors) {
            this.handleSaveValidationError(apiError.validationErrors);
            return;
          }

          this.handleUnexpectedSubmitError();
          console.error('Erreur lors de la mise à jour du flux', error);
        },
      });

      return;
    }

    this.fluxApi.create(payload).subscribe({
      next: (fluxResponse) => {
        this.isSubmitting = false;
        this.handleSaveSuccess(fluxResponse.warnings);
        console.log('Flux créé avec succès');
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        const apiError = error.error as ApiErrorResponse | undefined;

        if (apiError?.validationErrors) {
          this.handleSaveValidationError(apiError.validationErrors);
          return;
        }

        this.handleUnexpectedSubmitError();
        console.error('Erreur lors de la création du flux', error);
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
      justificatifId: rawValue.justificatifId ? Number(rawValue.justificatifId) : null,
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

  private handleSaveSuccess(warnings: string[]): void {
    this.serverValidationErrors = {};
    this.submitErrorMessage = '';
    this.submitWarnings = [];

    if (warnings.length > 0) {
      this.submitWarnings = warnings;
      this.cdr.detectChanges();
      return;
    }

    this.form.reset();
    this.router.navigateByUrl('/flux');
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
        this.form.patchValue({
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
          justificatifId: flux.justificatifId ?? null,
        });

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
    this.form.reset();
    this.router.navigateByUrl('/flux');
  }

  formatExerciceLabel(exercice: ExerciceResponse): string {
    const formatDate = (dateStr: string): string => {
      const [year, month, day] = dateStr.split('-');
      return `${day}/${month}/${year}`;
    };
    return `${exercice.libelleExercice} (${formatDate(exercice.dateDebut)} → ${formatDate(exercice.dateFin)})`;
  }

  formatJustificatifLabel(j: JustificatifResponse): string {
    if (j.referencePiece) return j.referencePiece;
    if (j.datePiece) return `${j.typePiece} — ${j.datePiece}`;
    return `${j.typePiece} #${j.id}`;
  }

  ouvrirDialogJustificatif(): void {
    const dialogRef = this.dialog.open(JustificatifForm, {
      width: '640px',
      disableClose: false,
      data: {} as JustificatifDialogData,
    });

    dialogRef.afterClosed().subscribe((justificatif: JustificatifResponse | null) => {
      if (!justificatif) return;

      // Rafraîchit la liste et positionne sur le nouveau justificatif
      this.justificatifApi.getAll().subscribe({
        next: (justificatifs) => {
          this.justificatifs = justificatifs;
          this.form.patchValue({ justificatifId: justificatif.id });
          this.cdr.detectChanges();
        },
      });
    });
  }
}
