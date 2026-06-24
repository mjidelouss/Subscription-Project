export type QuoteStatus = 'DRAFT' | 'PENDING_MANAGER' | 'APPROVED' | 'REJECTED';

export interface QuoteRequest {
  clientId: number;
  produitId: number;
  montant: number;
}

export interface Quote {
  id: number;
  clientId: number;
  clientNom: string;
  produitId: number;
  produitLibelle: string;
  montant: number;
  statut: QuoteStatus;
  dateCreation: string;
}
