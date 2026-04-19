import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FluxReferentialsResponse } from '../models/flux-referentials-response';
import { FluxRequest } from '../models/flux-request';
import { FluxResponse } from '../models/flux-response';
import { FluxFilters } from '../models/flux-filters';
import { HttpClient, HttpParams } from '@angular/common/http';
import { inject } from '@angular/core';
import { API_BASE_URL } from '../../../core/api/api.config';

@Injectable({
  providedIn: 'root',
})
export class FluxApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/flux`;

  getAll(filters?: FluxFilters): Observable<FluxResponse[]> {
    let params = new HttpParams();

    if (filters?.bienId !== undefined && filters.bienId !== null) {
      params = params.set('bienId', filters.bienId.toString());
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

    return this.http.get<FluxResponse[]>(this.baseUrl, { params });
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
}