import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FluxResponse } from '../../models/flux-response';
import { FluxApi } from '../../services/flux-api';

@Component({
  selector: 'app-flux-list',
  imports: [RouterLink],
  templateUrl: './flux-list.html',
  styleUrl: './flux-list.scss',
})
export class FluxList implements OnInit {
  private readonly fluxApi = inject(FluxApi);

  fluxes: FluxResponse[] = [];
  isLoading = false;
  loadErrorMessage = '';
  private readonly cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.loadFluxes();
  }

  loadFluxes(): void {
    this.isLoading = true;
    this.loadErrorMessage = '';

    console.log('loadFluxes:start');

    this.fluxApi.getAll().subscribe({
      next: (fluxes) => {
        this.fluxes = fluxes;
        this.isLoading = false;

        console.log('state after next', {
          isLoading: this.isLoading,
          fluxCount: this.fluxes.length,
          fluxes: this.fluxes,
        });

        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('loadFluxes:error', error);
        this.loadErrorMessage = 'Impossible de charger les flux.';
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      complete: () => {
        console.log('loadFluxes:complete');
      },
    });
  }

  deleteFlux(id: number): void {
    const confirmed = window.confirm('Confirmer la suppression de ce flux ?');

    if (!confirmed) {
      return;
    }

    this.fluxApi.delete(id).subscribe({
      next: () => {
        this.loadFluxes();
      },
      error: (error) => {
        console.error('Erreur lors de la suppression du flux', error);
        this.loadErrorMessage = 'Impossible de supprimer le flux.';
      },
    });
  }
}
