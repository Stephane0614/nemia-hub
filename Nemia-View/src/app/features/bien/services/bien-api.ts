import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/api/api.config';
import { BienRequest } from '../models/bien-request';
import { BienResponse } from '../models/bien-response';
import { BienReferentialsResponse } from '../models/bien-referentials-response';

@Injectable({
  providedIn: 'root',
})
export class BienApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/biens`;

  getAll(): Observable<BienResponse[]> {
    return this.http.get<BienResponse[]>(this.baseUrl);
  }

  getById(id: number): Observable<BienResponse> {
    return this.http.get<BienResponse>(`${this.baseUrl}/${id}`);
  }

  create(payload: BienRequest): Observable<BienResponse> {
    return this.http.post<BienResponse>(this.baseUrl, payload);
  }

  update(id: number, payload: BienRequest): Observable<BienResponse> {
    return this.http.put<BienResponse>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getReferentials(): Observable<BienReferentialsResponse> {
    return this.http.get<BienReferentialsResponse>(`${this.baseUrl}/referentials`);
  }
}
