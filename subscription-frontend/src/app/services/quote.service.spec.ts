import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { API_BASE_URL } from '../core/api.config';
import { Quote } from '../models/quote.model';
import { QuoteService } from './quote.service';

describe('QuoteService', () => {
  let service: QuoteService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [QuoteService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(QuoteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should POST a new quote to the API', () => {
    const payload = { clientId: 1, produitId: 2, montant: 5000 };
    service.create(payload).subscribe();

    const req = httpMock.expectOne(`${API_BASE_URL}/quotes`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({} as Quote);
  });

  it('should pass the statut as a query param when filtering', () => {
    service.getAll('PENDING_MANAGER').subscribe();

    const req = httpMock.expectOne(
      (r) => r.url === `${API_BASE_URL}/quotes` && r.params.get('statut') === 'PENDING_MANAGER'
    );
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('should call the approve endpoint', () => {
    service.approve(7).subscribe();

    const req = httpMock.expectOne(`${API_BASE_URL}/quotes/7/approve`);
    expect(req.request.method).toBe('POST');
    req.flush({} as Quote);
  });
});
