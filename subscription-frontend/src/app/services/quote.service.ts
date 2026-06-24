import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../core/api.config';
import { Quote, QuoteRequest, QuoteStatus } from '../models/quote.model';

@Injectable({ providedIn: 'root' })
export class QuoteService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/quotes`;

  getAll(statut?: QuoteStatus): Observable<Quote[]> {
    let params = new HttpParams();
    if (statut) {
      params = params.set('statut', statut);
    }
    return this.http.get<Quote[]>(this.baseUrl, { params });
  }

  create(quote: QuoteRequest): Observable<Quote> {
    return this.http.post<Quote>(this.baseUrl, quote);
  }

  approve(id: number): Observable<Quote> {
    return this.http.post<Quote>(`${this.baseUrl}/${id}/approve`, {});
  }
}
