import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../core/api/api.config';
import { MobilierRequest } from '../models/mobilier-request';
import { MobilierResponse } from '../models/mobilier-response';
import { MobilierReferentialsResponse } from '../models/mobilier-referentials-response';

@Injectable({ providedIn: 'root' })
export class MobilierApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/mobilier`;

  getAll(bienId?: number): Observable<MobilierResponse[]> {
    let params = new HttpParams();
    if (bienId != null) {
      params = params.set('bienId', bienId.toString());
    }
    return this.http.get<MobilierResponse[]>(this.baseUrl, { params });
  }

  getById(id: number): Observable<MobilierResponse> {
    return this.http.get<MobilierResponse>(`${this.baseUrl}/${id}`);
  }

  create(payload: MobilierRequest): Observable<MobilierResponse> {
    return this.http.post<MobilierResponse>(this.baseUrl, payload);
  }

  update(id: number, payload: MobilierRequest): Observable<MobilierResponse> {
    return this.http.put<MobilierResponse>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getReferentials(): Observable<MobilierReferentialsResponse> {
    return this.http.get<MobilierReferentialsResponse>(`${this.baseUrl}/referentials`);
  }
}
