import { Component, OnInit, inject, Optional } from '@angular/core';
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

import { JustificatifApi } from '../../services/justificatif-api';
import { JustificatifReferentialsResponse } from '../../models/justificatif-referentials-response';
import { JustificatifResponse } from '../../models/justificatif-response';
import { ReferentialItem } from '../../../flux/models/referential-item';

export interface JustificatifDialogData {
  justificatifId?: number;
}

@Component({
  selector: 'app-justificatif-form',
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
  templateUrl: './justificatif-form.html',
  styleUrl: './justificatif-form.scss',
})
export class JustificatifForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly justificatifApi = inject(JustificatifApi);
  private readonly cdr = inject(ChangeDetectorRef);

  // Injectés optionnellement — présents en mode dialog, absents en mode page
  private readonly dialogRef = inject(MatDialogRef<JustificatifForm>, { optional: true });
  private readonly dialogData = inject<JustificatifDialogData>(MAT_DIALOG_DATA, { optional: true });

  // Injectés optionnellement — présents en mode page, absents en mode dialog
  private readonly route = inject(ActivatedRoute, { optional: true });
  private readonly router = inject(Router, { optional: true });

  form!: FormGroup;
  isEditMode = false;
  justificatifId: number | null = null;

  isLoading = false;
  isSubmitting = false;
  errorMessage: string | null = null;

  typePieces: ReferentialItem[] = [];
  statutDocumentaires: ReferentialItem[] = [];

  get isDialogMode(): boolean {
    return this.dialogRef !== null;
  }

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.initForm();
    this.loadReferentials();

    if (this.isDialogMode) {
      // Mode dialog — id éventuel passé via MAT_DIALOG_DATA
      const id = this.dialogData?.justificatifId;
      if (id) {
        this.isEditMode = true;
        this.justificatifId = id;
        this.loadJustificatif(id);
      }
    } else {
      // Mode page — id lu depuis les params de route
      const id = this.route?.snapshot.paramMap.get('id');
      if (id) {
        this.isEditMode = true;
        this.justificatifId = Number(id);
        this.loadJustificatif(this.justificatifId);
      }
    }
  }

  private initForm(): void {
    this.form = this.fb.group({
      typePiece: ['', Validators.required],
      statutDocumentaire: ['', Validators.required],
      datePiece: [null],
      referencePiece: [''],
      emetteur: [''],
      commentaire: [''],
    });
  }

  private loadReferentials(): void {
    this.justificatifApi.getReferentials().subscribe({
      next: (refs: JustificatifReferentialsResponse) => {
        this.typePieces = refs.typePieces;
        this.statutDocumentaires = refs.statutDocumentaires;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les référentiels.';
        this.cdr.detectChanges();
      },
    });
  }

  private loadJustificatif(id: number): void {
    this.isLoading = true;
    this.justificatifApi.getById(id).subscribe({
      next: (justificatif) => {
        this.form.patchValue({
          typePiece: justificatif.typePiece,
          statutDocumentaire: justificatif.statutDocumentaire,
          datePiece: justificatif.datePiece ? new Date(justificatif.datePiece) : null,
          referencePiece: justificatif.referencePiece ?? '',
          emetteur: justificatif.emetteur ?? '',
          commentaire: justificatif.commentaire ?? '',
        });
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger le justificatif.';
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
      typePiece: raw.typePiece,
      statutDocumentaire: raw.statutDocumentaire,
      datePiece: raw.datePiece ? new Date(raw.datePiece).toISOString().split('T')[0] : undefined,
      referencePiece: raw.referencePiece || undefined,
      emetteur: raw.emetteur || undefined,
      commentaire: raw.commentaire || undefined,
      fichierAssocie: undefined,
    };

    const request$ =
      this.isEditMode && this.justificatifId
        ? this.justificatifApi.update(this.justificatifId, payload)
        : this.justificatifApi.create(payload);

    request$.subscribe({
      next: (justificatif: JustificatifResponse) => {
        this.isSubmitting = false;
        if (this.isDialogMode) {
          // Ferme la dialog en renvoyant le justificatif créé
          this.dialogRef!.close(justificatif);
        } else {
          this.router?.navigateByUrl('/');
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
      this.router?.navigateByUrl('/');
    }
  }
}
