import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { NotificationService } from '../../core/notification.service';
import { Client } from '../../models/client.model';
import { ClientService } from '../../services/client.service';

@Component({
  selector: 'app-clients',
  imports: [ReactiveFormsModule],
  template: `
    <div class="page">
      <div class="page-header">
        <h1>Clients</h1>
        <p>Creer et consulter les clients de l'assurance.</p>
      </div>

      <div class="grid-2">
        <!-- Reactive form -->
        <div class="card">
          <h2>Nouveau client</h2>
          <form [formGroup]="form" (ngSubmit)="submit()">
            <div class="field">
              <label for="nom">Nom</label>
              <input id="nom" type="text" formControlName="nom"
                     [class.invalid]="isInvalid('nom')" />
              @if (isInvalid('nom')) {
                <span class="field-error">Le nom est obligatoire.</span>
              }
            </div>

            <div class="field">
              <label for="email">Email</label>
              <input id="email" type="email" formControlName="email"
                     [class.invalid]="isInvalid('email')" />
              @if (isInvalid('email')) {
                <span class="field-error">Un email valide est obligatoire.</span>
              }
            </div>

            <div class="field">
              <label for="telephone">Telephone</label>
              <input id="telephone" type="text" formControlName="telephone"
                     [class.invalid]="isInvalid('telephone')" />
              @if (isInvalid('telephone')) {
                <span class="field-error">Le telephone est obligatoire.</span>
              }
            </div>

            <button class="btn btn-primary btn-block" type="submit"
                    [disabled]="form.invalid || saving()">
              {{ saving() ? 'Enregistrement...' : 'Creer le client' }}
            </button>
          </form>
        </div>

        <!-- List -->
        <div class="card">
          <h2>Liste des clients</h2>
          <div class="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Nom</th>
                  <th>Email</th>
                  <th>Telephone</th>
                </tr>
              </thead>
              <tbody>
                @for (client of clients(); track client.id) {
                  <tr>
                    <td>{{ client.id }}</td>
                    <td>{{ client.nom }}</td>
                    <td>{{ client.email }}</td>
                    <td>{{ client.telephone }}</td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="4" class="empty">Aucun client pour le moment.</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class ClientsComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly clientService = inject(ClientService);
  private readonly notifications = inject(NotificationService);

  readonly clients = signal<Client[]>([]);
  readonly saving = signal(false);

  readonly form = this.fb.nonNullable.group({
    nom: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    telephone: ['', Validators.required],
  });

  ngOnInit(): void {
    this.loadClients();
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
    this.clientService.create(this.form.getRawValue()).subscribe({
      next: () => {
        this.notifications.success('Client cree avec succes.');
        this.form.reset();
        this.loadClients();
        this.saving.set(false);
      },
      // Errors are surfaced globally by the interceptor; just re-enable the form.
      error: () => this.saving.set(false),
    });
  }

  private loadClients(): void {
    this.clientService.getAll().subscribe((clients) => this.clients.set(clients));
  }
}
