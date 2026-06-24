import { DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { NotificationService } from '../../core/notification.service';
import { Contract } from '../../models/contract.model';
import { Quote } from '../../models/quote.model';
import { ContractService } from '../../services/contract.service';
import { QuoteService } from '../../services/quote.service';

@Component({
  selector: 'app-contracts',
  imports: [ReactiveFormsModule, DecimalPipe],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>Contrats</h1>
        <p>Transformer un devis approuve en contrat.</p>
      </div>

      <div class="grid-2">
        <div class="card">
          <h2>Nouveau contrat</h2>
          @if (availableQuotes().length === 0) {
            <p class="muted">Aucun devis approuve disponible. Approuvez d'abord un devis.</p>
          } @else {
            <form [formGroup]="form" (ngSubmit)="submit()">
              <div class="field">
                <label for="quoteId">Devis approuve</label>
                <select id="quoteId" formControlName="quoteId" [class.invalid]="isInvalid('quoteId')">
                  <option [ngValue]="null" disabled>Selectionner un devis</option>
                  @for (quote of availableQuotes(); track quote.id) {
                    <option [ngValue]="quote.id">
                      #{{ quote.id }} - {{ quote.clientNom }} - {{ quote.montant | number: '1.2-2' }} &euro;
                    </option>
                  }
                </select>
                @if (isInvalid('quoteId')) {
                  <span class="field-error">Le devis est obligatoire.</span>
                }
              </div>

              <div class="field">
                <label for="dateEffet">Date d'effet</label>
                <input id="dateEffet" type="date" formControlName="dateEffet"
                       [class.invalid]="isInvalid('dateEffet')" />
                @if (isInvalid('dateEffet')) {
                  <span class="field-error">La date d'effet est obligatoire.</span>
                }
              </div>

              <button class="btn btn-primary btn-block" type="submit"
                      [disabled]="form.invalid || saving()">
                {{ saving() ? 'Enregistrement...' : 'Creer le contrat' }}
              </button>
            </form>
          }
        </div>

        <div class="card">
          <h2>Liste des contrats</h2>
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>N&deg; contrat</th>
                  <th>Client</th>
                  <th>Montant</th>
                  <th>Date d'effet</th>
                  <th>Statut</th>
                </tr>
              </thead>
              <tbody>
                @for (contract of contracts(); track contract.id) {
                  <tr>
                    <td>{{ contract.id }}</td>
                    <td>{{ contract.numeroContrat }}</td>
                    <td>{{ contract.clientNom }}</td>
                    <td>{{ contract.montant | number: '1.2-2' }} &euro;</td>
                    <td>{{ contract.dateEffet }}</td>
                    <td><span class="badge badge-neutral">{{ contract.statut }}</span></td>
                  </tr>
                } @empty {
                  <tr><td colspan="6" class="empty">Aucun contrat.</td></tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .muted {
        color: var(--text-muted);
        font-size: 0.9rem;
        margin: 0;
      }
    `,
  ],
})
export class ContractsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly contractService = inject(ContractService);
  private readonly quoteService = inject(QuoteService);
  private readonly notifications = inject(NotificationService);

  readonly contracts = signal<Contract[]>([]);
  readonly approvedQuotes = signal<Quote[]>([]);
  readonly saving = signal(false);

  // Only approved quotes that don't already have a contract can be converted.
  readonly availableQuotes = computed(() => {
    const usedQuoteIds = new Set(this.contracts().map((c) => c.quoteId));
    return this.approvedQuotes().filter((q) => !usedQuoteIds.has(q.id));
  });

  readonly form = this.fb.group({
    quoteId: this.fb.control<number | null>(null, Validators.required),
    dateEffet: this.fb.control('', Validators.required),
  });

  ngOnInit(): void {
    this.loadData();
  }

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    const raw = this.form.getRawValue();
    this.contractService.create({ quoteId: raw.quoteId!, dateEffet: raw.dateEffet }).subscribe({
      next: (contract) => {
        this.notifications.success(`Contrat ${contract.numeroContrat} cree.`);
        this.form.reset();
        this.loadData();
        this.saving.set(false);
      },
      error: () => this.saving.set(false),
    });
  }

  private loadData(): void {
    forkJoin({
      contracts: this.contractService.getAll(),
      approved: this.quoteService.getAll('APPROVED'),
    }).subscribe(({ contracts, approved }) => {
      this.contracts.set(contracts);
      this.approvedQuotes.set(approved);
    });
  }
}
