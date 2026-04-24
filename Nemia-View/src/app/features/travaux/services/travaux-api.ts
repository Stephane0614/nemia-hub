import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/api/api.config';
import { TravauxRequest } from '../models/travaux-request';
import { TravauxResponse } from '../models/travaux-response';
import { TravauxReferentialsResponse } from '../models/travaux-referentials-response';

@Injectable({ providedIn: 'root' })
export class TravauxApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/travaux`;

  getAll(bienId?: number): Observable<TravauxResponse[]> {
    let params = new HttpParams();
    if (bienId != null) {
      params = params.set('bienId', bienId.toString());
    }
    return this.http.get<TravauxResponse[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<TravauxResponse> {
    return this.http.get<TravauxResponse>(`${this.baseUrl}/${id}`);
  }

  create(payload: TravauxRequest): Observable<TravauxResponse> {
    return this.http.post<TravauxResponse>(this.baseUrl, payload);
  }

  update(id: number, payload: TravauxRequest): Observable<TravauxResponse> {
    return this.http.put<TravauxResponse>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getReferentials(): Observable<TravauxReferentialsResponse> {
    return this.http.get<TravauxReferentialsResponse>(`${this.baseUrl}/referentials`);
  }
}
