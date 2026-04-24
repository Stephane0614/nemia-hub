import { Component, OnInit, inject, Optional } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import localeFr from '@angular/common/locales/fr';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

import { TravauxApi } from '../../services/travaux-api';
import { TravauxReferentialsResponse } from '../../models/travaux-referentials-response';
import { TravauxResponse } from '../../models/travaux-response';
import { ReferentialItem } from '../../../flux/models/referential-item';
import { BienApi } from '../../../bien/services/bien-api';
import { BienResponse } from '../../../bien/models/bien-response';

export interface TravauxDialogData {
  travauxId?: number;
}

function datefinValidator(group: AbstractControl): ValidationErrors | null {
  const debut = group.get('dateDebut')?.value;
  const fin = group.get('dateFin')?.value;
  if (debut && fin && new Date(fin) < new Date(debut)) {
    return { dateFinInvalide: true };
  }
  return null;
}

@Component({
  selector: 'app-travaux-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatButtonModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatDialogModule,
  ],
  templateUrl: './travaux-form.html',
  styleUrl: './travaux-form.scss',
})
export class TravauxForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly travauxApi = inject(TravauxApi);
  private readonly bienApi = inject(BienApi);
  private readonly cdr = inject(ChangeDetectorRef);

  private readonly dialogRef = inject(MatDialogRef<TravauxForm>, { optional: true });
  private readonly dialogData = inject<TravauxDialogData>(MAT_DIALOG_DATA, { optional: true });
  private readonly route = inject(ActivatedRoute, { optional: true });
  private readonly router = inject(Router, { optional: true });

  form!: FormGroup;
  isEditMode = false;
  travauxId: number | null = null;

  isLoading = false;
  isSubmitting = false;
  errorMessage: string | null = null;

  finalitesPressenties: ReferentialItem[] = [];
  statutsTravaux: ReferentialItem[] = [];
  qualificationsPressenties: ReferentialItem[] = [];
  biens: BienResponse[] = [];

  get isDialogMode(): boolean {
    return this.dialogRef !== null;
  }

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.initForm();
    this.loadReferentials();
    this.loadBiens();
    this.loadQualifications();

    if (this.isDialogMode) {
      const id = this.dialogData?.travauxId;
      if (id) {
        this.isEditMode = true;
        this.travauxId = id;
        this.loadTravaux(id);
      }
    } else {
      const id = this.route?.snapshot.paramMap.get('id');
      if (id) {
        this.isEditMode = true;
        this.travauxId = Number(id);
        this.loadTravaux(this.travauxId);
      }
    }
  }

  private initForm(): void {
    this.form = this.fb.group(
      {
        libelleTravaux: ['', [Validators.required, Validators.maxLength(200)]],
        bienId: [null, Validators.required],
        montantTotal: [null, [Validators.required, Validators.min(0.01)]],
        finalitePressentie: ['', Validators.required],
        qualificationPressentie: ['', Validators.required],
        statutTravaux: ['', Validators.required],
        dateDebut: [null],
        dateFin: [null],
        commentaire: [''],
      },
      { validators: datefinValidator },
    );
  }

  private loadReferentials(): void {
    this.travauxApi.getReferentials().subscribe({
      next: (refs: TravauxReferentialsResponse) => {
        this.finalitesPressenties = refs.finalitesPressenties;
        this.statutsTravaux = refs.statutsTravaux;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les référentiels.';
        this.cdr.detectChanges();
      },
    });
  }

  private loadBiens(): void {
    this.bienApi.getAll().subscribe({
      next: (biens) => {
        this.biens = biens;
        this.cdr.detectChanges();
      },
    });
  }

  // Référentiel qualification réutilisé depuis FluxApi
  private loadQualifications(): void {
    // Valeurs fixes alignées sur le vocabulaire produit
    this.qualificationsPressenties = [
      { code: 'CHARGE_COURANTE', label: 'Charge courante' },
      { code: 'IMMOBILISATION', label: 'Immobilisation' },
      { code: 'MIXTE_OU_A_VENTILER', label: 'Mixte ou à ventiler' },
      { code: 'A_ARBITRER', label: 'À arbitrer' },
    ];
  }

  private loadTravaux(id: number): void {
    this.isLoading = true;
    this.travauxApi.getById(id).subscribe({
      next: (travaux) => {
        this.form.patchValue({
          libelleTravaux: travaux.libelleTravaux,
          bienId: travaux.bienId,
          montantTotal: travaux.montantTotal,
          finalitePressentie: travaux.finalitePressentie,
          qualificationPressentie: travaux.qualificationPressentie,
          statutTravaux: travaux.statutTravaux,
          dateDebut: travaux.dateDebut ? new Date(travaux.dateDebut) : null,
          dateFin: travaux.dateFin ? new Date(travaux.dateFin) : null,
          commentaire: travaux.commentaire ?? '',
        });
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les travaux.';
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
    this.errorMessage = null;

    const raw = this.form.value;
    const payload = {
      libelleTravaux: raw.libelleTravaux.trim(),
      bienId: Number(raw.bienId),
      montantTotal: raw.montantTotal,
      finalitePressentie: raw.finalitePressentie,
      qualificationPressentie: raw.qualificationPressentie,
      statutTravaux: raw.statutTravaux,
      dateDebut: raw.dateDebut ? new Date(raw.dateDebut).toISOString().split('T')[0] : null,
      dateFin: raw.dateFin ? new Date(raw.dateFin).toISOString().split('T')[0] : null,
      commentaire: raw.commentaire || null,
    };

    const request$ =
      this.isEditMode && this.travauxId
        ? this.travauxApi.update(this.travauxId, payload)
        : this.travauxApi.create(payload);

    request$.subscribe({
      next: (travaux: TravauxResponse) => {
        this.isSubmitting = false;
        if (this.isDialogMode) {
          this.dialogRef!.close(travaux);
        } else {
          this.router?.navigateByUrl('/travaux');
        }
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Une erreur est survenue.';
        this.isSubmitting = false;
        this.cdr.detectChanges();
      },
    });
  }

  onCancel(): void {
    if (this.isDialogMode) {
      this.dialogRef!.close(null);
    } else {
      this.router?.navigateByUrl('/travaux');
    }
  }
}
