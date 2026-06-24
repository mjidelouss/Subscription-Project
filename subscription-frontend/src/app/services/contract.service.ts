import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../core/api.config';
import { Contract, ContractRequest } from '../models/contract.model';

@Injectable({ providedIn: 'root' })
export class ContractService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${API_BASE_URL}/contracts`;

  getAll(): Observable<Contract[]> {
    return this.http.get<Contract[]>(this.baseUrl);
  }

  create(contract: ContractRequest): Observable<Contract> {
    return this.http.post<Contract>(this.baseUrl, contract);
  }
}
