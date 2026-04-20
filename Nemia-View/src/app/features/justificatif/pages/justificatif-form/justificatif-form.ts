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

import { JustificatifApi } from '../../services/justificatif-api';
import { JustificatifReferentialsResponse } from '../../models/justificatif-referentials-response';
import { ReferentialItem } from '../../../flux/models/referential-item';

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
  ],
  templateUrl: './justificatif-form.html',
  styleUrl: './justificatif-form.scss',
})
export class JustificatifForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly justificatifApi = inject(JustificatifApi);
  private readonly cdr = inject(ChangeDetectorRef);

  form!: FormGroup;
  isEditMode = false;
  justificatifId: number | null = null;
  returnTo: string | null = null;

  isLoading = false;
  isSubmitting = false;
  errorMessage: string | null = null;

  typePieces: ReferentialItem[] = [];
  statutDocumentaires: ReferentialItem[] = [];

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.initForm();
    this.loadReferentials();

    const id = this.route.snapshot.paramMap.get('id');
    this.returnTo = this.route.snapshot.queryParamMap.get('returnTo');

    if (id) {
      this.isEditMode = true;
      this.justificatifId = Number(id);
      this.loadJustificatif(this.justificatifId);
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
      next: () => {
        this.isSubmitting = false;
        this.navigateBack();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Une erreur est survenue.';
        this.isSubmitting = false;
        this.cdr.detectChanges();
      },
    });
  }

  onCancel(): void {
    this.navigateBack();
  }

  private navigateBack(): void {
    if (this.returnTo === 'flux-form') {
      this.router.navigateByUrl('/flux/nouveau');
    } else {
      this.router.navigateByUrl('/');
    }
  }
}
