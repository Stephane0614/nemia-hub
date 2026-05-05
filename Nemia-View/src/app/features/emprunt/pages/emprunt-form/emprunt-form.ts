import { Component, OnInit, inject, Inject, Optional } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { ChangeDetectorRef } from '@angular/core';

import { EmpruntApi } from '../../services/emprunt-api';
import { EmpruntRequest } from '../../models/emprunt-request';
import { EmpruntResponse } from '../../models/emprunt-response';
import { EmpruntReferentialsResponse } from '../../models/emprunt-referentials-response';
import { BienApi } from '../../../bien/services/bien-api';
import { BienResponse } from '../../../bien/models/bien-response';
import { ReferentialItem } from '../../../flux/models/referential-item';

// Validator : dateDerniereEcheance >= datePremiereEcheance
function dateEcheanceValidator(control: AbstractControl): ValidationErrors | null {
  const premiere = control.get('datePremiereEcheance')?.value;
  const derniere = control.get('dateDerniereEcheance')?.value;
  if (premiere && derniere && new Date(derniere) < new Date(premiere)) {
    return { dateEcheanceInvalide: true };
  }
  return null;
}

export type EmpruntFormDialogData = {
  bienId?: number;
};

@Component({
  selector: 'app-emprunt-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatDatepickerModule,
    MatProgressSpinnerModule,
    MatDialogModule,
  ],
  templateUrl: './emprunt-form.html',
  styleUrl: './emprunt-form.scss',
})
export class EmpruntFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly empruntApi = inject(EmpruntApi);
  private readonly bienApi = inject(BienApi);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly cdr = inject(ChangeDetectorRef);

  @Optional() private readonly dialogRef = inject(MatDialogRef<EmpruntFormComponent>, { optional: true });
  @Optional() @Inject(MAT_DIALOG_DATA) private readonly dialogData: EmpruntFormDialogData | null = null;

  form!: FormGroup;
  biens: BienResponse[] = [];
  statutEmprunt: ReferentialItem[] = [];

  isDialog = false;
  isEditMode = false;
  empruntId: number | null = null;

  isLoading = false;
  isSubmitting = false;
  errorMessage = '';

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.isDialog = !!this.dialogRef;

    this.form = this.fb.group({
      referencePret:        ['', Validators.required],
      bienId:               [null, Validators.required],
      statutEmprunt:        ['', Validators.required],
      organismePreteur:     [''],
      mensualiteTotale:     [null, Validators.min(0)],
      datePremiereEcheance: [null],
      dateDerniereEcheance: [null],
      commentaire:          [''],
    }, { validators: dateEcheanceValidator });

    // Pré-remplir bienId si dialog avec contexte
    if (this.isDialog && this.dialogData?.bienId) {
      this.form.patchValue({ bienId: this.dialogData.bienId });
    }

    this.loadReferentials();
    this.loadBiens();

    // Mode édition en page
    if (!this.isDialog) {
      const id = this.route.snapshot.paramMap.get('id');
      if (id) {
        this.isEditMode = true;
        this.empruntId = Number(id);
        this.loadEmprunt(this.empruntId);
      }
    }
  }

  loadReferentials(): void {
    this.empruntApi.getReferentials().subscribe({
      next: (refs: EmpruntReferentialsResponse) => {
        this.statutEmprunt = refs.statutsEmprunt;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les référentiels.';
        this.cdr.detectChanges();
      },
    });
  }

  loadBiens(): void {
    this.bienApi.getAll().subscribe({
      next: (biens) => {
        this.biens = biens;
        this.cdr.detectChanges();
      },
      error: () => {},
    });
  }

  loadEmprunt(id: number): void {
    this.isLoading = true;
    this.empruntApi.getById(id).subscribe({
      next: (emprunt: EmpruntResponse) => {
        this.form.patchValue({
          referencePret:        emprunt.referencePret,
          bienId:               emprunt.bienId,
          statutEmprunt:        emprunt.statutEmprunt,
          organismePreteur:     emprunt.organismePreteur ?? '',
          mensualiteTotale:     emprunt.mensualiteTotale ?? null,
          datePremiereEcheance: emprunt.datePremiereEcheance
                                  ? new Date(emprunt.datePremiereEcheance)
                                  : null,
          dateDerniereEcheance: emprunt.dateDerniereEcheance
                                  ? new Date(emprunt.dateDerniereEcheance)
                                  : null,
          commentaire:          emprunt.commentaire ?? '',
        });
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger cet emprunt.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const raw = this.form.getRawValue();
    const payload: EmpruntRequest = {
      referencePret:        raw.referencePret.trim(),
      bienId:               raw.bienId,
      statutEmprunt:        raw.statutEmprunt,
      organismePreteur:     raw.organismePreteur?.trim() || undefined,
      mensualiteTotale:     raw.mensualiteTotale ?? undefined,
      datePremiereEcheance: raw.datePremiereEcheance
                              ? this.formatDate(raw.datePremiereEcheance)
                              : undefined,
      dateDerniereEcheance: raw.dateDerniereEcheance
                              ? this.formatDate(raw.dateDerniereEcheance)
                              : undefined,
      commentaire:          raw.commentaire?.trim() || undefined,
    };

    const call = this.isEditMode && this.empruntId
      ? this.empruntApi.update(this.empruntId, payload)
      : this.empruntApi.create(payload);

    call.subscribe({
      next: (emprunt: EmpruntResponse) => {
        this.isSubmitting = false;
        if (this.isDialog) {
          this.dialogRef!.close(emprunt);
        } else {
          this.router.navigateByUrl('/emprunts');
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err?.error?.message ?? 'Une erreur est survenue.';
        this.cdr.detectChanges();
      },
    });
  }

  annuler(): void {
    if (this.isDialog) {
      this.dialogRef!.close(null);
    } else {
      this.router.navigateByUrl('/emprunts');
    }
  }

  hasDateEcheanceError(): boolean {
    return this.form.hasError('dateEcheanceInvalide')
      && !!this.form.get('datePremiereEcheance')?.value
      && !!this.form.get('dateDerniereEcheance')?.value;
  }

  private formatDate(value: Date | string): string {
    const d = new Date(value);
    const yyyy = d.getFullYear();
    const mm   = String(d.getMonth() + 1).padStart(2, '0');
    const dd   = String(d.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  }
}