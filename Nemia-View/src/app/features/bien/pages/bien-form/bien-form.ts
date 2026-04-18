import { ChangeDetectorRef, Component, OnInit, computed, inject } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { BienApi } from '../../services/bien-api';
import { BienRequest } from '../../models/bien-request';
import { StatutActiviteBien } from '../../models/statut-activite-bien';
import { TypeLocation } from '../../models/type-location';
import { RegimeVise } from '../../models/regime-vise';
import { ReferentialItem } from '../../../flux/models/referential-item';
import { ApiErrorResponse } from '../../../flux/models/api-error-response';

@Component({
  selector: 'app-bien-form',
  standalone: true,
  imports: [
    CommonModule,
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
  templateUrl: './bien-form.html',
  styleUrl: './bien-form.scss',
})
export class BienForm implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);
  private readonly bienApi = inject(BienApi);
  private readonly cdr = inject(ChangeDetectorRef);

  serverValidationErrors: Record<string, string> = {};
  loadErrorMessage = '';
  submitErrorMessage = '';
  isLoading = false;
  isSubmitting = false;

  statutActivites: ReferentialItem[] = [];
  typeLocations: ReferentialItem[] = [];
  regimeVises: ReferentialItem[] = [];

  constructor() {
    registerLocaleData(localeFr);
  }

  readonly bienId = computed(() => {
    const id = this.route.snapshot.paramMap.get('id');
    return id ? Number(id) : null;
  });

  readonly isEditMode = computed(() => this.bienId() !== null);

  readonly form = this.formBuilder.group({
    nomUsuel: ['', [Validators.required, Validators.maxLength(120)]],
    adresseSimplifiee: ['', [Validators.required, Validators.maxLength(255)]],
    statutActivite: ['', Validators.required],
    typeLocation: [''],
    dateMiseEnLocation: [null as Date | null],
    regimeVise: [''],
    commentaire: ['', Validators.maxLength(500)],
  });

  ngOnInit(): void {
    this.isLoading = true;

    this.bienApi.getReferentials().subscribe({
      next: (referentials) => {
        this.statutActivites = referentials.statutActivites;
        this.typeLocations = referentials.typeLocations;
        this.regimeVises = referentials.regimeVises;

        const id = this.bienId();
        if (id !== null) {
          this.loadBien(id);
          return;
        }

        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
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
    if (!this.serverValidationErrors[controlName]) return;
    const { [controlName]: _, ...rest } = this.serverValidationErrors;
    this.serverValidationErrors = rest;
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
    const id = this.bienId();

    if (this.isEditMode() && id !== null) {
      this.bienApi.update(id, payload).subscribe({
        next: () => {
          this.isSubmitting = false;
          this.router.navigateByUrl('/biens');
        },
        error: (error: HttpErrorResponse) => {
          this.isSubmitting = false;
          this.handleError(error);
        },
      });
      return;
    }

    this.bienApi.create(payload).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigateByUrl('/biens');
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.handleError(error);
      },
    });
  }

  private buildPayload(): BienRequest {
    const raw = this.form.getRawValue();
    return {
      nomUsuel: (raw.nomUsuel ?? '').trim(),
      adresseSimplifiee: (raw.adresseSimplifiee ?? '').trim(),
      statutActivite: raw.statutActivite as StatutActiviteBien,
      typeLocation: raw.typeLocation ? raw.typeLocation as TypeLocation : null,
      dateMiseEnLocation: raw.dateMiseEnLocation
        ? this.formatDateForApi(raw.dateMiseEnLocation)
        : null,
      regimeVise: raw.regimeVise ? raw.regimeVise as RegimeVise : null,
      commentaire: (raw.commentaire ?? '').trim() || null,
    };
  }

  private formatDateForApi(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private parseApiDate(value: string | null | undefined): Date | null {
    if (!value) return null;
    const [year, month, day] = value.split('-').map(Number);
    if (!year || !month || !day) return null;
    return new Date(year, month - 1, day);
  }

  private handleError(error: HttpErrorResponse): void {
    const apiError = error.error as ApiErrorResponse | undefined;
    if (apiError?.validationErrors) {
      this.serverValidationErrors = apiError.validationErrors;
      return;
    }
    this.submitErrorMessage = 'Une erreur est survenue lors de l\'enregistrement.';
  }

  private loadBien(id: number): void {
    this.bienApi.getById(id).subscribe({
      next: (bien) => {
        this.form.patchValue({
          nomUsuel: bien.nomUsuel,
          adresseSimplifiee: bien.adresseSimplifiee,
          statutActivite: bien.statutActivite,
          typeLocation: bien.typeLocation ?? '',
          dateMiseEnLocation: this.parseApiDate(bien.dateMiseEnLocation),
          regimeVise: bien.regimeVise ?? '',
          commentaire: bien.commentaire ?? '',
        });
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loadErrorMessage = 'Impossible de charger le bien à modifier.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }
}