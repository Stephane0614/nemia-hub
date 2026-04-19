import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/api/api.config';
import { ExerciceRequest } from '../models/exercice-request';
import { ExerciceResponse } from '../models/exercice-response';
import { ExerciceReferentialsResponse } from '../models/exercice-referentials-response';

@Injectable({
  providedIn: 'root',
})
export class ExerciceApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/exercices`;

  getAll(): Observable<ExerciceResponse[]> {
    return this.http.get<ExerciceResponse[]>(this.baseUrl);
  }

  getById(id: number): Observable<ExerciceResponse> {
    return this.http.get<ExerciceResponse>(`${this.baseUrl}/${id}`);
  }

  create(payload: ExerciceRequest): Observable<ExerciceResponse> {
    return this.http.post<ExerciceResponse>(this.baseUrl, payload);
  }

  update(id: number, payload: ExerciceRequest): Observable<ExerciceResponse> {
    return this.http.put<ExerciceResponse>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getReferentials(): Observable<ExerciceReferentialsResponse> {
    return this.http.get<ExerciceReferentialsResponse>(`${this.baseUrl}/referentials`);
  }
}
