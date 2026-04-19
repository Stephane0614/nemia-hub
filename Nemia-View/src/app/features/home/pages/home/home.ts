import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule, CurrencyPipe, registerLocaleData, DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import localeFr from '@angular/common/locales/fr';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { HomeSyntheseApi } from './home-synthese-api';
import { BienApi } from '../../../bien/services/bien-api';
import { BienResponse } from '../../../bien/models/bien-response';
import {
  HomeSyntheseResponse,
  Alertes,
  Metriques,
  RepartitionDepense,
  RecurrenceDepenses,
  DerniereOperation,
} from './models/home-synthese';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    RouterLink,
    FormsModule,
    MatCardModule,
    MatDividerModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    CurrencyPipe,
    DatePipe,
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  private readonly homeSyntheseApi = inject(HomeSyntheseApi);
  private readonly bienApi = inject(BienApi);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  isLoading = false;
  errorMessage = '';
  synthese: HomeSyntheseResponse | null = null;
  moisCourant: string = '';

  // ── Filtres ──
  biens: BienResponse[] = [];
  selectedBienId: number | null = null;
  biensLoading = false;

  constructor() {
    registerLocaleData(localeFr);
  }

  ngOnInit(): void {
    this.loadBiens();
    this.loadSynthese();
  }

  loadBiens(): void {
    this.biensLoading = true;
    this.bienApi.getAll().subscribe({
      next: (biens) => {
        this.biens = biens.sort((a, b) => a.nomUsuel.localeCompare(b.nomUsuel));
        this.biensLoading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.biensLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  onBienChange(): void {
    this.loadSynthese(this.moisCourant || undefined);
  }

  get nomBienFiltre(): string | null {
    if (!this.selectedBienId) return null;
    return this.biens.find(b => b.id === this.selectedBienId)?.nomUsuel ?? null;
  }

  loadSynthese(mois?: string): void {
    this.isLoading = true;
    this.errorMessage = '';

    const bienId = this.selectedBienId ?? undefined;

    this.homeSyntheseApi.getSynthese(mois, bienId).subscribe({
      next: (data) => {
        this.synthese = data;
        this.moisCourant = data.periode.mois;
        this.isLoading = false;
        setTimeout(() => this.cdr.detectChanges());
      },
      error: () => {
        this.errorMessage = 'Impossible de charger les données.';
        this.isLoading = false;
        setTimeout(() => this.cdr.detectChanges());
      },
    });
  }

  // ── Navigation entre mois ──

  get moisPrecedent(): string {
    return this.decrementerMois(this.moisCourant);
  }

  get moisSuivant(): string {
    return this.incrementerMois(this.moisCourant);
  }

  get isMoisCourant(): boolean {
    const now = new Date();
    const moisActuel = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    return this.moisCourant === moisActuel;
  }

  allerMoisPrecedent(): void {
    this.loadSynthese(this.moisPrecedent);
  }

  allerMoisSuivant(): void {
    if (!this.isMoisCourant) {
      this.loadSynthese(this.moisSuivant);
    }
  }

  private decrementerMois(mois: string): string {
    const [year, month] = mois.split('-').map(Number);
    const date = new Date(year, month - 2);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
  }

  private incrementerMois(mois: string): string {
    const [year, month] = mois.split('-').map(Number);
    const date = new Date(year, month);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
  }

  // ── Navigation vers flux ──

  allerVersFlux(): void {
    this.router.navigate(['/flux']);
  }

  allerVersSansJustificatif(): void {
  const params: Record<string, string> = { statutJustificatif: 'A_FOURNIR,INCOMPLET' };
  if (this.selectedBienId) params['bienId'] = this.selectedBienId.toString();
  this.router.navigate(['/flux'], { queryParams: params });
}

allerVersAArbitrer(): void {
  const params: Record<string, string> = { qualificationPressentie: 'A_ARBITRER' };
  if (this.selectedBienId) params['bienId'] = this.selectedBienId.toString();
  this.router.navigate(['/flux'], { queryParams: params });
}

allerVersARevoir(): void {
  const params: Record<string, string> = { statutTraitement: 'A_REVOIR' };
  if (this.selectedBienId) params['bienId'] = this.selectedBienId.toString();
  this.router.navigate(['/flux'], { queryParams: params });
}

  allerVersFluxDetail(id: number): void {
    this.router.navigate(['/flux', id, 'modifier']);
  }

  // ── Helpers métriques ──

  get metriques(): Metriques | null {
    return this.synthese?.metriques ?? null;
  }

  get alertes(): Alertes | null {
    return this.synthese?.alertes ?? null;
  }

  get repartitionDepenses(): RepartitionDepense[] {
    return this.synthese?.repartitionDepenses ?? [];
  }

  get recurrenceDepenses(): RecurrenceDepenses | null {
    return this.synthese?.recurrenceDepenses ?? null;
  }

  get dernieresOperations(): DerniereOperation[] {
    return this.synthese?.dernieresOperations ?? [];
  }

  get aucuneAlerte(): boolean {
    if (!this.alertes) return true;
    return (
      this.alertes.fluxSansJustificatif === 0 &&
      this.alertes.fluxAArbitrer === 0 &&
      this.alertes.fluxARevoir === 0
    );
  }

  get soldePositif(): boolean {
    return (this.metriques?.solde ?? 0) > 0;
  }
}