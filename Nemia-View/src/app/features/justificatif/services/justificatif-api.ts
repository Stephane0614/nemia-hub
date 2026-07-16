import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/api/api.config';
import { JustificatifRequest } from '../models/justificatif-request';
import { JustificatifResponse } from '../models/justificatif-response';
import { JustificatifReferentialsResponse } from '../models/justificatif-referentials-response';

@Injectable({ providedIn: 'root' })
export class JustificatifApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/justificatifs`;

  getAll(): Observable<JustificatifResponse[]> {
    return this.http.get<JustificatifResponse[]>(this.baseUrl);
  }

  getById(id: number): Observable<JustificatifResponse> {
    return this.http.get<JustificatifResponse>(`${this.baseUrl}/${id}`);
  }

  create(payload: JustificatifRequest): Observable<JustificatifResponse> {
    return this.http.post<JustificatifResponse>(this.baseUrl, payload);
  }

  update(id: number, payload: JustificatifRequest): Observable<JustificatifResponse> {
    return this.http.put<JustificatifResponse>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getReferentials(): Observable<JustificatifReferentialsResponse> {
    return this.http.get<JustificatifReferentialsResponse>(`${this.baseUrl}/referentials`);
  }

  uploadFichier(id: number, file: File): Observable<JustificatifResponse> {
    const formData = new FormData();
    formData.append('fichier', file);
    return this.http.post<JustificatifResponse>(`${this.baseUrl}/${id}/fichier`, formData);
  }

  downloadFichier(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/fichier`, { responseType: 'blob' });
  }

  getFichierUrl(id: number): string {
    return `${this.baseUrl}/${id}/fichier`;
  }

  deleteFichier(id: number): Observable<JustificatifResponse> {
    return this.http.delete<JustificatifResponse>(`${this.baseUrl}/${id}/fichier`);
  }
}

