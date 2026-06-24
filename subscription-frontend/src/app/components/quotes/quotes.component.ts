import { DecimalPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { NotificationService } from '../../core/notification.service';
import { Client } from '../../models/client.model';
import { Product } from '../../models/product.model';
import { Quote, QuoteStatus } from '../../models/quote.model';
import { ClientService } from '../../services/client.service';
import { ProductService } from '../../services/product.service';
import { QuoteService } from '../../services/quote.service';

const MANAGER_THRESHOLD = 10000;

@Component({
  selector: 'app-quotes',
  imports: [ReactiveFormsModule, DecimalPipe],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>Devis</h1>
        <p>Generer des devis. Au-dela de 10 000 &euro;, une validation manager est requise.</p>
      </div>

      <div class="grid-2">
        <div class="card">
          <h2>Nouveau devis</h2>
          <form [formGroup]="form" (ngSubmit)="submit()">
            <div class="field">
              <label for="clientId">Client</label>
              <select id="clientId" formControlName="clientId" [class.invalid]="isInvalid('clientId')">
                <option [ngValue]="null" disabled>Selectionner un client</option>
                @for (client of clients(); track client.id) {
                  <option [ngValue]="client.id">{{ client.nom }}</option>
                }
              </select>
              @if (isInvalid('clientId')) {
                <span class="field-error">Le client est obligatoire.</span>
              }
            </div>

            <div class="field">
              <label for="produitId">Produit</label>
              <select id="produitId" formControlName="produitId" [class.invalid]="isInvalid('produitId')">
                <option [ngValue]="null" disabled>Selectionner un produit</option>
                @for (product of products(); track product.id) {
                  <option [ngValue]="product.id">{{ product.libelle }}</option>
                }
              </select>
              @if (isInvalid('produitId')) {
                <span class="field-error">Le produit est obligatoire.</span>
              }
            </div>

            <div class="field">
              <label for="montant">Montant (&euro;)</label>
              <input id="montant" type="number" min="1" step="0.01" formControlName="montant"
                     [class.invalid]="isInvalid('montant')" />
              @if (isInvalid('montant')) {
                <span class="field-error">Un montant strictement positif est obligatoire.</span>
              }
              @if (montantValue() > 0) {
                <span class="hint" [class.hint-warning]="needsApproval()">
                  {{ needsApproval()
                    ? 'Ce devis devra etre approuve par un manager.'
                    : 'Ce devis sera approuve automatiquement.' }}
                </span>
              }
            </div>

            <button class="btn btn-primary btn-block" type="submit"
                    [disabled]="form.invalid || saving()">
              {{ saving() ? 'Enregistrement...' : 'Creer le devis' }}
            </button>
          </form>
        </div>

        <div class="card">
          <div class="list-head">
            <h2>Liste des devis</h2>
            <select class="filter" [value]="filter() ?? ''" (change)="onFilterChange($event)">
              <option value="">Tous les statuts</option>
              <option value="PENDING_MANAGER">En attente manager</option>
              <option value="APPROVED">Approuves</option>
              <option value="REJECTED">Rejetes</option>
            </select>
          </div>
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Client</th>
                  <th>Produit</th>
                  <th>Montant</th>
                  <th>Statut</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                @for (quote of quotes(); track quote.id) {
                  <tr>
                    <td>{{ quote.id }}</td>
                    <td>{{ quote.clientNom }}</td>
                    <td>{{ quote.produitLibelle }}</td>
                    <td>{{ quote.montant | number: '1.2-2' }} &euro;</td>
                    <td><span class="badge" [class]="badgeClass(quote.statut)">{{ label(quote.statut) }}</span></td>
                    <td>
                      @if (quote.statut === 'PENDING_MANAGER') {
                        <button class="btn btn-success btn-sm" (click)="approve(quote)">Approuver</button>
                      }
                    </td>
                  </tr>
                } @empty {
                  <tr><td colspan="6" class="empty">Aucun devis.</td></tr>
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
      .list-head {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 1.1rem;
      }
      .filter {
        padding: 0.4rem 0.6rem;
        border: 1px solid var(--border);
        border-radius: 8px;
        font-size: 0.85rem;
      }
      .hint {
        margin-top: 0.35rem;
        font-size: 0.78rem;
        color: var(--success);
      }
      .hint-warning {
        color: var(--warning);
      }
    `,
  ],
})
export class QuotesComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly quoteService = inject(QuoteService);
  private readonly clientService = inject(ClientService);
  private readonly productService = inject(ProductService);
  private readonly notifications = inject(NotificationService);

  readonly clients = signal<Client[]>([]);
  readonly products = signal<Product[]>([]);
  readonly quotes = signal<Quote[]>([]);
  readonly filter = signal<QuoteStatus | null>(null);
  readonly saving = signal(false);

  readonly form = this.fb.group({
    clientId: this.fb.control<number | null>(null, Validators.required),
    produitId: this.fb.control<number | null>(null, Validators.required),
    montant: this.fb.control<number | null>(null, [Validators.required, Validators.min(0.01)]),
  });

  // Live preview of the business rule for nicer UX.
  readonly montantValue = signal(0);
  readonly needsApproval = computed(() => this.montantValue() > MANAGER_THRESHOLD);

  ngOnInit(): void {
    this.clientService.getAll().subscribe((c) => this.clients.set(c));
    this.productService.getAll().subscribe((p) => this.products.set(p));
    this.form.controls.montant.valueChanges.subscribe((v) => this.montantValue.set(v ?? 0));
    this.loadQuotes();
  }

  isInvalid(field: string): boolean {
    const control = this.form.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  onFilterChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.filter.set(value ? (value as QuoteStatus) : null);
    this.loadQuotes();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    const raw = this.form.getRawValue();
    this.quoteService
      .create({ clientId: raw.clientId!, produitId: raw.produitId!, montant: raw.montant! })
      .subscribe({
        next: (quote) => {
          this.notifications.success(
            quote.statut === 'APPROVED'
              ? 'Devis cree et approuve automatiquement.'
              : 'Devis cree, en attente de validation manager.'
          );
          this.form.reset();
          this.montantValue.set(0);
          this.loadQuotes();
          this.saving.set(false);
        },
        error: () => this.saving.set(false),
      });
  }

  approve(quote: Quote): void {
    this.quoteService.approve(quote.id).subscribe({
      next: () => {
        this.notifications.success(`Devis #${quote.id} approuve.`);
        this.loadQuotes();
      },
    });
  }

  badgeClass(status: QuoteStatus): string {
    switch (status) {
      case 'APPROVED':
        return 'badge-approved';
      case 'PENDING_MANAGER':
        return 'badge-pending';
      case 'REJECTED':
        return 'badge-rejected';
      default:
        return 'badge-neutral';
    }
  }

  label(status: QuoteStatus): string {
    switch (status) {
      case 'APPROVED':
        return 'Approuve';
      case 'PENDING_MANAGER':
        return 'En attente';
      case 'REJECTED':
        return 'Rejete';
      default:
        return 'Brouillon';
    }
  }

  private loadQuotes(): void {
    this.quoteService.getAll(this.filter() ?? undefined).subscribe((q) => this.quotes.set(q));
  }
}
