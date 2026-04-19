import { ChangeDetectorRef, Component, OnInit, computed, inject } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { ExerciceApi } from '../../services/exercice-api';
import { ExerciceRequest } from '../../models/exercice-request';
import { StatutExercice } from '../../models/statut-exercice';
import { NiveauCompletude } from '../../models/niveau-completude';
import { ReferentialItem } from '../../../flux/models/referential-item';
import { ApiErrorResponse } from '../../../flux/models/api-error-response';

function dateFinValidator(control: AbstractControl): ValidationErrors | null {
  const parent = control.parent;
  if (!parent) return null;
  const dateDebut = parent.get('dateDebut')?.value;
  const dateFin = control.value;
  if (!dateDebut || !dateFin) return null;
  return dateFin > dateDebut ? null : { dateFinInvalide: true };
}

@Component({
  selector: 'app-exercice-form',
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
  templateUrl: './exercice-form.html',
  styleUrl: './exercice-form.scss',
})
export class ExerciceForm implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly formBuilder = inject(FormBuilder);
  private readonly exerciceApi = inject(ExerciceApi);
  private readonly cdr = inject(ChangeDetectorRef);

  serverValidationErrors: Record<string, string> = {};
  loadErrorMessage = '';
  submitErrorMessage = '';
  isLoading = false;
  isSubmitting = false;
  submitWarnings: string[] = [];

  statutExercices: ReferentialItem[] = [];
  niveauxCompletude: ReferentialItem[] = [];

  constructor() {
    registerLocaleData(localeFr);
  }

  readonly exerciceId = computed(() => {
    const id = this.route.snapshot.paramMap.get('id');
    return id ? Number(id) : null;
  });

  readonly isEditMode = computed(() => this.exerciceId() !== null);

  readonly form = this.formBuilder.group({
    libelleExercice: ['', [Validators.required, Validators.maxLength(50)]],
    dateDebut: [null as Date | null, Validators.required],
    dateFin: [null as Date | null, [Validators.required, dateFinValidator]],
    statutExercice: ['', Validators.required],
    niveauCompletude: [''],
    commentaire: ['', Validators.maxLength(500)],
  });

  ngOnInit(): void {
    this.isLoading = true;

    this.form.get('dateDebut')?.valueChanges.subscribe(() => {
      this.form.get('dateFin')?.updateValueAndValidity();
    });

    this.exerciceApi.getReferentials().subscribe({
      next: (referentials) => {
        this.statutExercices = referentials.statutExercices;
        this.niveauxCompletude = referentials.niveauxCompletude;

        const id = this.exerciceId();
        if (id !== null) {
          this.loadExercice(id);
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
    const id = this.exerciceId();

    if (this.isEditMode() && id !== null) {
      this.exerciceApi.update(id, payload).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          if (response.warnings && response.warnings.length > 0) {
            this.submitWarnings = response.warnings;
            this.cdr.detectChanges();
            return;
          }
          this.router.navigateByUrl('/exercices');
        },
      });
      return;
    }

    this.exerciceApi.create(payload).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        if (response.warnings && response.warnings.length > 0) {
          this.submitWarnings = response.warnings;
          this.cdr.detectChanges();
          return;
        }
        this.router.navigateByUrl('/exercices');
      },
    });
  }

  private buildPayload(): ExerciceRequest {
    const raw = this.form.getRawValue();
    return {
      libelleExercice: (raw.libelleExercice ?? '').trim(),
      dateDebut: this.formatDateForApi(raw.dateDebut!),
      dateFin: this.formatDateForApi(raw.dateFin!),
      statutExercice: raw.statutExercice as StatutExercice,
      niveauCompletude: raw.niveauCompletude ? (raw.niveauCompletude as NiveauCompletude) : null,
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
    if (error.status === 409) {
      this.submitErrorMessage = 'Un exercice existe déjà avec ce libellé.';
      return;
    }

    const apiError = error.error as ApiErrorResponse | undefined;
    if (apiError?.validationErrors) {
      this.serverValidationErrors = apiError.validationErrors;
      return;
    }

    this.submitErrorMessage = "Une erreur est survenue lors de l'enregistrement.";
  }

  private loadExercice(id: number): void {
    this.exerciceApi.getById(id).subscribe({
      next: (exercice) => {
        this.form.patchValue({
          libelleExercice: exercice.libelleExercice,
          dateDebut: this.parseApiDate(exercice.dateDebut),
          dateFin: this.parseApiDate(exercice.dateFin),
          statutExercice: exercice.statutExercice,
          niveauCompletude: exercice.niveauCompletude ?? '',
          commentaire: exercice.commentaire ?? '',
        });
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loadErrorMessage = "Impossible de charger l'exercice à modifier.";
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  confirmAndRedirect(): void {
    this.submitWarnings = [];
    this.form.reset();
    this.router.navigateByUrl('/exercices');
  }
}
