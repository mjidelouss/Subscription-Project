export type ContractStatus = 'CREATED' | 'ACTIVE' | 'CANCELLED';

export interface ContractRequest {
  quoteId: number;
  dateEffet: string;
}

export interface Contract {
  id: number;
  quoteId: number;
  numeroContrat: string;
  dateEffet: string;
  statut: ContractStatus;
  clientNom: string;
  montant: number;
}
