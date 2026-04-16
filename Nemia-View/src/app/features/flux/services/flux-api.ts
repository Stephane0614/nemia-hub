import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FluxReferentialsResponse } from '../models/flux-referentials-response';
import { FluxRequest } from '../models/flux-request';
import { FluxResponse } from '../models/flux-response';
import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { API_BASE_URL } from '../../../core/api/api.config';

@Injectable({
  providedIn: 'root',
})
export class FluxApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/flux`;

  getAll(): Observable<FluxResponse[]> {
    return this.http.get<FluxResponse[]>(this.baseUrl);
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
