import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
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

import { MobilierApi } from '../../services/mobilier-api';
import { MobilierReferentialsResponse } from '../../models/mobilier-referentials-response';
import { MobilierResponse } from '../../models/mobilier-response';
import { ReferentialItem } from '../../../flux/models/referential-item';
import { BienApi } from '../../../bien/services/bien-api';
import { BienResponse } from '../../../bien/models/bien-response';
import { JustificatifApi } from '../../../justificatif/services/justificatif-api';
import { JustificatifResponse } from '../../../justificatif/models/justificatif-response';

export interface MobilierDialogData {
  mobilierId?: number;
}

@Component({
  selector: 'app-mobilier-form',
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
  templateUrl: './mobilier-form.html',
  styleUrl: './mobilier-form.scss',
})
export class MobilierForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly mobilierApi = inject(MobilierApi);
  private readonly bienApi = inject(BienApi);
  private readonly justificatifApi = inject(JustificatifApi);
  private readonly cdr = inject(ChangeDetectorRef);

  private readonly dialogRef = inject(MatDialogRef<MobilierForm>, { optional: true });
  private readonly dialogData = inject<MobilierDialogData>(MAT_DIALOG_DATA, { optional: true });
  private readonly route = inject(ActivatedRoute, { optional: true });
  private readonly router = inject(Router, { optional: true });

  form!: FormGroup;
  isEditMode = false;
  mobilierId: number | null = null;

  isLoading = false;
  isSubmitting = false;
  errorMessage: string | null = null;

  categoriesMobilier: ReferentialItem[] = [];
  etatsUsage: ReferentialItem[] = [];
  statutsMobilier: ReferentialItem[] = [];
  qualificationsPressenties: ReferentialItem[] = [
    { code: 'CHARGE_COURANTE', label: 'Charge courante' },
    { code: 'IMMOBILISATION', label: 'Immobilisation' },
    { code: 'MIXTE_OU_A_VENTILER', label: 'Mixte ou à ventiler' },
    { code: 'A_ARBITRER', label: 'À arbitrer' },
  ];
  biens: BienResponse[] = [];
  justificatifs: JustificatifResponse[] = [];

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
    this.loadJustificatifs();

    if (this.isDialogMode) {
      const id = this.dialogData?.mobilierId;
      if (id) {
        this.isEditMode = true;
        this.mobilierId = id;
        this.loadMobilier(id);
      }
    } else {
      const id = this.route?.snapshot.paramMap.get('id');
      if (id) {
        this.isEditMode = true;
        this.mobilierId = Number(id);
        this.loadMobilier(this.mobilierId);
      }
    }
  }

  private initForm(): void {
    this.form = this.fb.group({
      designation: ['', [Validators.required, Validators.maxLength(200)]],
      bienId: [null, Validators.required],
      montant: [null, [Validators.required, Validators.min(0.01)]],
      categorieMobilier: ['', Validators.required],
      qualificationPressentie: ['', Validators.required],
      statutMobilier: ['', Validators.required],
      dateAcquisition: [null],
      quantite: [1, [Validators.min(1)]],
      etatUsage: [null],
      justificatifId: [null],
      commentaire: [''],
    });
  }

  private loadReferentials(): void {
    this.mobilierApi.getReferentials().subscribe({
      next: (refs: MobilierReferentialsResponse) => {
        this.categoriesMobilier = refs.categoriesMobilier;
        this.etatsUsage = refs.etatsUsage;
        this.statutsMobilier = refs.statutsMobilier;
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

  private loadJustificatifs(): void {
    this.justificatifApi.getAll().subscribe({
      next: (justificatifs) => {
        this.justificatifs = justificatifs;
        this.cdr.detectChanges();
      },
    });
  }

  private loadMobilier(id: number): void {
    this.isLoading = true;
    this.mobilierApi.getById(id).subscribe({
      next: (mobilier) => {
        this.form.patchValue({
          designation: mobilier.designation,
          bienId: mobilier.bienId,
          montant: mobilier.montant,
          categorieMobilier: mobilier.categorieMobilier,
          qualificationPressentie: mobilier.qualificationPressentie,
          statutMobilier: mobilier.statutMobilier,
          dateAcquisition: mobilier.dateAcquisition ? new Date(mobilier.dateAcquisition) : null,
          quantite: mobilier.quantite ?? 1,
          etatUsage: mobilier.etatUsage ?? null,
          justificatifId: mobilier.justificatifId ?? null,
          commentaire: mobilier.commentaire ?? '',
        });
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger le mobilier.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  formatJustificatifLabel(j: JustificatifResponse): string {
    if (j.referencePiece) return j.referencePiece;
    if (j.datePiece) return `${j.typePiece} — ${j.datePiece}`;
    return `${j.typePiece} #${j.id}`;
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
      designation: raw.designation.trim(),
      bienId: Number(raw.bienId),
      montant: raw.montant,
      categorieMobilier: raw.categorieMobilier,
      qualificationPressentie: raw.qualificationPressentie,
      statutMobilier: raw.statutMobilier,
      dateAcquisition: raw.dateAcquisition
        ? new Date(raw.dateAcquisition).toISOString().split('T')[0]
        : null,
      quantite: raw.quantite ?? 1,
      etatUsage: raw.etatUsage || null,
      justificatifId: raw.justificatifId ? Number(raw.justificatifId) : null,
      commentaire: raw.commentaire || null,
    };

    const request$ =
      this.isEditMode && this.mobilierId
        ? this.mobilierApi.update(this.mobilierId, payload)
        : this.mobilierApi.create(payload);

    request$.subscribe({
      next: (mobilier: MobilierResponse) => {
        this.isSubmitting = false;
        if (this.isDialogMode) {
          this.dialogRef!.close(mobilier);
        } else {
          this.router?.navigateByUrl('/mobilier');
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
      this.router?.navigateByUrl('/mobilier');
    }
  }
}
