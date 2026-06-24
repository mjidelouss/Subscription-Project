import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ClientService } from '../../services/client.service';
import { QuoteService } from '../../services/quote.service';
import { ContractService } from '../../services/contract.service';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>Tableau de bord</h1>
        <p>Vue d'ensemble du parcours de souscription.</p>
      </div>

      <div class="stats">
        <a class="stat-card" routerLink="/clients">
          <span class="stat-value">{{ clientCount() }}</span>
          <span class="stat-label">Clients</span>
        </a>
        <a class="stat-card" routerLink="/quotes">
          <span class="stat-value">{{ quoteCount() }}</span>
          <span class="stat-label">Devis</span>
        </a>
        <a class="stat-card" routerLink="/quotes">
          <span class="stat-value warning">{{ pendingCount() }}</span>
          <span class="stat-label">Devis en attente</span>
        </a>
        <a class="stat-card" routerLink="/contracts">
          <span class="stat-value success">{{ contractCount() }}</span>
          <span class="stat-label">Contrats</span>
        </a>
      </div>

      <div class="card flow">
        <h2>Parcours</h2>
        <ol>
          <li>Creer un <a routerLink="/clients">client</a>.</li>
          <li>Generer un <a routerLink="/quotes">devis</a> (auto-approuve si &le; 10 000 &euro;, sinon en attente manager).</li>
          <li>Approuver le devis si necessaire.</li>
          <li>Transformer un devis approuve en <a routerLink="/contracts">contrat</a>.</li>
        </ol>
      </div>
    </div>
  `,
  styles: [
    `
      .stats {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
        gap: 1rem;
        margin-bottom: 1.5rem;
      }
      .stat-card {
        background: #fff;
        border: 1px solid var(--border);
        border-radius: var(--radius);
        box-shadow: var(--shadow);
        padding: 1.4rem;
        display: flex;
        flex-direction: column;
        gap: 0.3rem;
        text-decoration: none;
        color: var(--text);
        transition: transform 0.15s, box-shadow 0.15s;
      }
      .stat-card:hover {
        transform: translateY(-2px);
        box-shadow: var(--shadow-lg);
      }
      .stat-value {
        font-size: 2rem;
        font-weight: 700;
      }
      .stat-value.warning {
        color: var(--warning);
      }
      .stat-value.success {
        color: var(--success);
      }
      .stat-label {
        color: var(--text-muted);
        font-size: 0.9rem;
      }
      .flow ol {
        margin: 0;
        padding-left: 1.2rem;
        line-height: 1.9;
        color: var(--text-muted);
      }
      .flow a {
        color: var(--primary);
      }
    `,
  ],
})
export class DashboardComponent implements OnInit {
  private readonly clientService = inject(ClientService);
  private readonly quoteService = inject(QuoteService);
  private readonly contractService = inject(ContractService);

  readonly clientCount = signal(0);
  readonly quoteCount = signal(0);
  readonly pendingCount = signal(0);
  readonly contractCount = signal(0);

  ngOnInit(): void {
    this.clientService.getAll().subscribe((clients) => this.clientCount.set(clients.length));
    this.quoteService.getAll().subscribe((quotes) => {
      this.quoteCount.set(quotes.length);
      this.pendingCount.set(quotes.filter((q) => q.statut === 'PENDING_MANAGER').length);
    });
    this.contractService.getAll().subscribe((contracts) => this.contractCount.set(contracts.length));
  }
}
