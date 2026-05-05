import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';

import { EmpruntApi } from '../../services/emprunt-api';
import { EmpruntResponse } from '../../models/emprunt-response';
import { BienApi } from '../../../bien/services/bien-api';
import { BienResponse } from '../../../bien/models/bien-response';
import { ConfirmDialog } from '../../../../shared/ui/confirm-dialog/confirm-dialog';

@Component({
  selector: 'app-emprunt-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatFormFieldModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './emprunt-list.html',
  styleUrl: './emprunt-list.scss',
})
export class EmpruntListComponent implements OnInit {
  private readonly empruntApi = inject(EmpruntApi);
  private readonly bienApi = inject(BienApi);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly cdr = inject(ChangeDetectorRef);

  emprunts: EmpruntResponse[] = [];
  biens: BienResponse[] = [];
  bienIdFiltre: number | null = null;

  isLoading = false;
  errorMessage = '';

  displayedColumns = [
    'referencePret',
    'bienId',
    'organismePreteur',
    'mensualiteTotale',
    'datePremiereEcheance',
    'dateDerniereEcheance',
    'statutEmprunt',
    'actions',
  ];

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.loadBiens();
    this.loadEmprunts();
  }

  loadBiens(): void {
    this.bienApi.getAll().subscribe({
      next: (biens) => {
        this.biens = biens;
        this.cdr.detectChanges();
      },
      error: () => {
        // biens non critiques pour l'affichage
      },
    });
  }

  loadEmprunts(): void {
    this.isLoading = true;
    this.errorMessage = '';
    const bienId = this.bienIdFiltre ?? undefined;

    this.empruntApi.getAll(bienId).subscribe({
      next: (emprunts) => {
        this.emprunts = emprunts;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les emprunts.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  onFiltreBienChange(): void {
    this.loadEmprunts();
  }

  getNomBien(bienId: number): string {
    const bien = this.biens.find((b) => b.id === bienId);
    return bien ? bien.nomUsuel : `Bien #${bienId}`;
  }

  goToCreation(): void {
    this.router.navigateByUrl('/emprunts/nouveau');
  }

  goToEdition(id: number): void {
    this.router.navigate(['/emprunts', id, 'modifier']);
  }

  confirmerSuppression(emprunt: EmpruntResponse): void {
    const ref = this.dialog.open(ConfirmDialog, {
      data: {
        title: 'Supprimer cet emprunt',
        message: `Supprimer l'emprunt "${emprunt.referencePret}" ?`,
      },
    });

    ref.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.empruntApi.delete(emprunt.id).subscribe({
          next: () => this.loadEmprunts(),
          error: () => {
            this.errorMessage = 'Erreur lors de la suppression.';
            this.cdr.detectChanges();
          },
        });
      }
    });
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'EN_COURS':   return 'badge--ok';
      case 'TERMINE':    return 'badge--muted';
      case 'SUSPENDU':   return 'badge--warn';
      case 'A_VERIFIER': return 'badge--error';
      default:           return 'badge--muted';
    }
  }

  getStatutLabel(statut: string): string {
    switch (statut) {
      case 'EN_COURS':   return 'En cours';
      case 'TERMINE':    return 'Terminé';
      case 'SUSPENDU':   return 'Suspendu';
      case 'A_VERIFIER': return 'À vérifier';
      default:           return statut;
    }
  }
}