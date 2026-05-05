import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/api/api.config';
import { EmpruntRequest } from '../models/emprunt-request';
import { EmpruntResponse } from '../models/emprunt-response';
import { EmpruntReferentialsResponse } from '../models/emprunt-referentials-response';

@Injectable({ providedIn: 'root' })
export class EmpruntApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/emprunts`;

  getAll(bienId?: number): Observable<EmpruntResponse[]> {
    let params = new HttpParams();
    if (bienId !== undefined) {
      params = params.set('bienId', bienId);
    }
    return this.http.get<EmpruntResponse[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<EmpruntResponse> {
    return this.http.get<EmpruntResponse>(`${this.baseUrl}/${id}`);
  }

  create(payload: EmpruntRequest): Observable<EmpruntResponse> {
    return this.http.post<EmpruntResponse>(this.baseUrl, payload);
  }

  update(id: number, payload: EmpruntRequest): Observable<EmpruntResponse> {
    return this.http.put<EmpruntResponse>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getReferentials(): Observable<EmpruntReferentialsResponse> {
    return this.http.get<EmpruntReferentialsResponse>(`${this.baseUrl}/referentials`);
  }
}