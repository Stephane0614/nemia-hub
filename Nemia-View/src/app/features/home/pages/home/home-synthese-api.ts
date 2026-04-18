import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../../../core/api/api.config';
import { HomeSyntheseResponse } from './models/home-synthese';

@Injectable({
  providedIn: 'root',
})
export class HomeSyntheseApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/home/synthese`;

  getSynthese(mois?: string, bienId?: number): Observable<HomeSyntheseResponse> {
    let params = new HttpParams();

    if (mois) {
      params = params.set('mois', mois);
    }

    if (bienId !== undefined && bienId !== null) {
      params = params.set('bienId', bienId.toString());
    }

    return this.http.get<HomeSyntheseResponse>(this.baseUrl, { params });
  }
}
