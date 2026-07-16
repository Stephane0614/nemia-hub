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
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ConfirmDialog } from '../../../../shared/ui/confirm-dialog/confirm-dialog';

import { JustificatifApi } from '../../services/justificatif-api';
import { JustificatifReferentialsResponse } from '../../models/justificatif-referentials-response';
import { JustificatifResponse } from '../../models/justificatif-response';
import { ReferentialItem } from '../../../flux/models/referential-item';
import { FluxApi } from '../../../flux/services/flux-api';
import { FluxRequest } from '../../../flux/models/flux-request';

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
    MatIconModule,
  ],
  templateUrl: './justificatif-form.html',
  styleUrl: './justificatif-form.scss',
})
export class JustificatifForm implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly justificatifApi = inject(JustificatifApi);
  private readonly fluxApi = inject(FluxApi);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly dialog = inject(MatDialog);

  // Injectés optionnellement — présents en mode dialog, absents en mode page
  private readonly dialogRef = inject(MatDialogRef<JustificatifForm>, { optional: true });
  private readonly dialogData = inject<JustificatifDialogData>(MAT_DIALOG_DATA, { optional: true });

  // Injectés optionnellement — présents en mode page, absents en mode dialog
  private readonly route = inject(ActivatedRoute, { optional: true });
  private readonly router = inject(Router, { optional: true });

  form!: FormGroup;
  isEditMode = false;
  justificatifId: number | null = null;
  justificatif: JustificatifResponse | null = null;
  // ID du flux depuis lequel ce justificatif est créé — permet la liaison automatique
  linkedFluxId: number | null = null;

  isLoading = false;
  isSubmitting = false;
  isUploading = false;
  errorMessage: string | null = null;
  fileError: string | null = null;

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

      // fluxId éventuel passé en query param (création depuis un flux) — permet la liaison automatique
      const fluxIdParam = this.route?.snapshot.queryParamMap.get('fluxId');
      if (fluxIdParam) {
        this.linkedFluxId = Number(fluxIdParam);
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
        this.justificatif = justificatif;
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
          // Ferme la dialog en renvoyant le justificatif créé/modifié
          this.dialogRef!.close(justificatif);
        } else if (this.isEditMode) {
          // Mise à jour locale sans rechargement de page
          this.justificatif = justificatif;
          this.cdr.detectChanges();
          this.router?.navigateByUrl('/flux');
        } else if (this.linkedFluxId) {
          // Création depuis un flux : lie automatiquement le justificatif à ce flux
          this.linkJustificatifToFlux(this.linkedFluxId, justificatif.id);
        } else {
          // Création : redirige vers la page d'édition du justificatif créé
          this.router?.navigate(['/justificatifs', justificatif.id, 'modifier']);
        }
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Une erreur est survenue.';
        this.isSubmitting = false;
        this.cdr.detectChanges();
      },
    });
  }

  // Relie le justificatif nouvellement créé au flux depuis lequel il a été initié,
  // puis redirige vers la page d'édition pour permettre l'upload du fichier.
  private linkJustificatifToFlux(fluxId: number, justificatifId: number): void {
    this.fluxApi.getById(fluxId).subscribe({
      next: (flux) => {
        const payload: FluxRequest = {
          date: flux.date,
          type: flux.type,
          libelle: flux.libelle,
          montant: flux.montant,
          categorie: flux.categorie,
          modePaiement: flux.modePaiement,
          occurrence: flux.occurrence,
          statutJustificatif: flux.statutJustificatif,
          qualificationPressentie: flux.qualificationPressentie,
          statutTraitement: flux.statutTraitement,
          dateValeur: flux.dateValeur,
          commentaire: flux.commentaire,
          bienId: flux.bienId,
          exerciceId: flux.exerciceId,
          justificatifId: justificatifId,
          travauxId: flux.travauxId,
          mobilierId: flux.mobilierId,
          empruntId: flux.empruntId,
        };

        this.fluxApi.update(fluxId, payload).subscribe({
          next: () => this.navigateToJustificatifEdition(justificatifId, fluxId),
          error: () => this.navigateToJustificatifEdition(justificatifId, fluxId),
        });
      },
      error: () => this.navigateToJustificatifEdition(justificatifId, fluxId),
    });
  }

  private navigateToJustificatifEdition(justificatifId: number, fluxId: number): void {
    this.router?.navigate(['/justificatifs', justificatifId, 'modifier'], { queryParams: { fluxId } });
  }

  onCancel(): void {
    if (this.isDialogMode) {
      this.dialogRef!.close(null);
    } else {
      this.router?.navigateByUrl('/');
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    this.fileError = null;

    // validation format
    const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png'];
    const fileExt = file.name.split('.').pop()?.toLowerCase();
    const allowedExts = ['pdf', 'jpg', 'jpeg', 'png'];
    if (!allowedTypes.includes(file.type) && !allowedExts.includes(fileExt || '')) {
      this.fileError = 'Type de fichier non autorisé. Seuls les formats PDF, JPG, JPEG et PNG sont acceptés.';
      input.value = '';
      return;
    }

    // validation taille (5 Mo)
    if (file.size > 5 * 1024 * 1024) {
      this.fileError = 'La taille du fichier dépasse la limite autorisée de 5 Mo.';
      input.value = '';
      return;
    }

    this.isUploading = true;
    this.justificatifApi.uploadFichier(this.justificatifId!, file).subscribe({
      next: (res) => {
        this.justificatif = res;
        this.isUploading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.fileError = err?.error?.message ?? 'Une erreur est survenue lors du chargement du fichier.';
        this.isUploading = false;
        this.cdr.detectChanges();
      }
    });
  }

  consulterFichier(): void {
    if (!this.justificatifId) return;
    this.fileError = null;
    this.justificatifApi.downloadFichier(this.justificatifId).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        window.open(url, '_blank');
      },
      error: (err) => {
        this.fileError = 'Impossible d’ouvrir la pièce justificative.';
        this.cdr.detectChanges();
      }
    });
  }

  supprimerFichier(): void {
    if (!this.justificatifId) return;
    this.fileError = null;

    const dialogRef = this.dialog.open(ConfirmDialog, {
      width: '420px',
      data: {
        title: 'Supprimer la pièce jointe',
        message: 'Êtes-vous sûr de vouloir supprimer cette pièce jointe ?',
        confirmLabel: 'Supprimer',
        cancelLabel: 'Annuler',
      },
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;

      this.justificatifApi.deleteFichier(this.justificatifId!).subscribe({
        next: (res) => {
          this.justificatif = res;
          this.cdr.detectChanges();
        },
        error: (err) => {
          this.fileError = 'Impossible de supprimer la pièce jointe.';
          this.cdr.detectChanges();
        }
      });
    });
  }

  formatBytes(bytes: number | undefined | null): string {
    if (!bytes) return '0 Octet';
    const k = 1024;
    const sizes = ['Octets', 'Ko', 'Mo', 'Go'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  getFileTypeLabel(mimeType: string | undefined | null): string {
    if (!mimeType) return 'Inconnu';
    if (mimeType.includes('pdf')) return 'PDF';
    if (mimeType.includes('jpeg') || mimeType.includes('jpg')) return 'JPEG';
    if (mimeType.includes('png')) return 'PNG';
    return mimeType.split('/').pop()?.toUpperCase() || 'Inconnu';
  }
}
