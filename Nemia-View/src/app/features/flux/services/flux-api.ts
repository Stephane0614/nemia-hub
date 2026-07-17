import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { API_BASE_URL } from '../../../core/api/api.config';
import { FluxRequest } from '../models/flux-request';
import { FluxResponse } from '../models/flux-response';
import { FluxPageResponse } from '../models/flux-page-response';
import { FluxReferentialsResponse } from '../models/flux-referentials-response';
import { FluxFilters } from '../models/flux-filters';
import { ReferentialItem } from '../models/referential-item';

@Injectable({ providedIn: 'root' })
export class FluxApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/flux`;

  getAll(filters?: FluxFilters): Observable<FluxPageResponse> {
    let params = new HttpParams();

    if (filters?.bienId != null) {
      params = params.set('bienId', filters.bienId.toString());
    }
    if (filters?.exerciceId != null) {
      params = params.set('exerciceId', filters.exerciceId.toString());
    }
    if (filters?.type) {
      params = params.set('type', filters.type);
    }
    if (filters?.categorie) {
      params = params.set('categorie', filters.categorie);
    }
    if (filters?.dateDebut) {
      params = params.set('dateDebut', filters.dateDebut);
    }
    if (filters?.dateFin) {
      params = params.set('dateFin', filters.dateFin);
    }
    if (filters?.statutJustificatif?.length) {
      params = params.set('statutJustificatif', filters.statutJustificatif.join(','));
    }
    if (filters?.qualificationPressentie) {
      params = params.set('qualificationPressentie', filters.qualificationPressentie);
    }
    if (filters?.statutTraitement) {
      params = params.set('statutTraitement', filters.statutTraitement);
    }
    if (filters?.page != null) {
      params = params.set('page', filters.page.toString());
    }
    if (filters?.taille != null) {
      params = params.set('taille', filters.taille.toString());
    }

    return this.http.get<FluxPageResponse>(this.baseUrl, { params });
  }

  getById(id: number): Observable<FluxResponse> {
    return this.http.get<FluxResponse>(`${this.baseUrl}/${id}`);
  }

  create(payload: FluxRequest): Observable<FluxResponse> {
    return this.http.post<FluxResponse>(this.baseUrl, payload);
  }

  update(id: number, payload: FluxRequest): Observable<FluxResponse> {
    return this.http.put<FluxResponse>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getReferentials(): Observable<FluxReferentialsResponse> {
    return this.http.get<FluxReferentialsResponse>(`${this.baseUrl}/referentials`);
  }

  getCategoriesByType(typeFlux?: string | null): Observable<ReferentialItem[]> {
    let params = new HttpParams();
    if (typeFlux) {
      params = params.set('typeFlux', typeFlux);
    }
    return this.http.get<ReferentialItem[]>(`${this.baseUrl}/referentials/categories`, { params });
  }
}
