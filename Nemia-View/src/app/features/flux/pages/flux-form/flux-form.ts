import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { ReactiveFormsModule, Validators, NonNullableFormBuilder } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiErrorResponse } from '../../models/api-error-response';
import { FluxCategory } from '../../models/flux-category';
import { FluxRequest } from '../../models/flux-request';
import { FluxType } from '../../models/flux-type';
import { PaymentMode } from '../../models/payment-mode';
import { FluxApi } from '../../services/flux-api';

@Component({
  selector: 'app-flux-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './flux-form.html',
  styleUrl: './flux-form.scss',
})
export class FluxForm implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private readonly fluxApi = inject(FluxApi);
  private readonly cdr = inject(ChangeDetectorRef);

  serverValidationErrors: Record<string, string> = {};
  loadErrorMessage = '';
  submitErrorMessage = '';
  isLoading = false;
  isSubmitting = false;

  fluxTypes: { code: string; label: string }[] = [];
  fluxCategories: { code: string; label: string }[] = [];
  paymentModes: { code: string; label: string }[] = [];

  readonly fluxId = computed(() => {
    const id = this.route.snapshot.paramMap.get('id');
    return id ? Number(id) : null;
  });

  readonly isEditMode = computed(() => this.fluxId() !== null);

  readonly form = this.formBuilder.group({
    date: ['', Validators.required],
    type: ['', Validators.required],
    libelle: ['', [Validators.required, Validators.maxLength(120)]],
    montant: [0, [Validators.required, Validators.min(0.01)]],
    categorie: ['', Validators.required],
    modePaiement: ['', Validators.required],
    commentaire: ['', Validators.maxLength(500)],
  });

  ngOnInit(): void {
    this.isLoading = true;
    this.loadErrorMessage = '';

    this.fluxApi.getReferentials().subscribe({
      next: (referentials) => {
        this.fluxTypes = referentials.types;
        this.fluxCategories = referentials.categories;
        this.paymentModes = referentials.paymentModes;

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
        next: () => {
          this.isSubmitting = false;
          this.handleSaveSuccess();
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
      next: () => {
        this.isSubmitting = false;
        this.handleSaveSuccess();
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
      date: rawValue.date,
      type: rawValue.type as FluxType,
      libelle: rawValue.libelle.trim(),
      montant: rawValue.montant,
      categorie: rawValue.categorie as FluxCategory,
      modePaiement: rawValue.modePaiement as PaymentMode,
      commentaire: rawValue.commentaire.trim() ? rawValue.commentaire.trim() : null,
    };
  }

  private applyServerValidationErrors(errors: Record<string, string> | undefined): void {
    this.serverValidationErrors = errors ?? {};
  }

  private handleSaveSuccess(): void {
    this.serverValidationErrors = {};
    this.submitErrorMessage = '';
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
          date: flux.date,
          type: flux.type,
          libelle: flux.libelle,
          montant: flux.montant,
          categorie: flux.categorie,
          modePaiement: flux.modePaiement,
          commentaire: flux.commentaire ?? '',
        });

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erreur lors du chargement du flux', error);
        this.loadErrorMessage = 'Impossible de charger le flux à modifier.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }
}
