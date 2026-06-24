import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { NotificationService } from './core/notification.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <header class="app-header">
      <div class="app-header-inner">
        <a class="brand" routerLink="/dashboard">
          <span class="brand-mark">AS</span>
          <span>Assurance &middot; Souscription</span>
        </a>
        <nav class="app-nav">
          <a routerLink="/dashboard" routerLinkActive="active">Tableau de bord</a>
          <a routerLink="/clients" routerLinkActive="active">Clients</a>
          <a routerLink="/quotes" routerLinkActive="active">Devis</a>
          <a routerLink="/contracts" routerLinkActive="active">Contrats</a>
        </nav>
      </div>
    </header>

    <main>
      <router-outlet />
    </main>

    <div class="toast-container">
      @for (toast of notifications.notifications(); track toast.id) {
        <div class="toast" [class.toast-success]="toast.type === 'success'"
             [class.toast-error]="toast.type === 'error'">
          <span>{{ toast.message }}</span>
          <button type="button" (click)="notifications.dismiss(toast.id)">&times;</button>
        </div>
      }
    </div>
  `,
  styles: [
    `
      .app-header {
        background: #fff;
        border-bottom: 1px solid var(--border);
        position: sticky;
        top: 0;
        z-index: 10;
      }
      .app-header-inner {
        max-width: 1100px;
        margin: 0 auto;
        padding: 0 1.5rem;
        height: 64px;
        display: flex;
        align-items: center;
        justify-content: space-between;
      }
      .brand {
        display: flex;
        align-items: center;
        gap: 0.6rem;
        font-weight: 600;
        color: var(--text);
        text-decoration: none;
      }
      .brand-mark {
        width: 34px;
        height: 34px;
        border-radius: 9px;
        background: var(--primary);
        color: #fff;
        display: grid;
        place-items: center;
        font-size: 0.85rem;
        font-weight: 700;
      }
      .app-nav {
        display: flex;
        gap: 0.35rem;
      }
      .app-nav a {
        padding: 0.5rem 0.85rem;
        border-radius: 8px;
        text-decoration: none;
        color: var(--text-muted);
        font-size: 0.92rem;
        font-weight: 500;
        transition: background 0.15s, color 0.15s;
      }
      .app-nav a:hover {
        background: var(--bg);
        color: var(--text);
      }
      .app-nav a.active {
        background: var(--primary-soft);
        color: var(--primary);
      }
      @media (max-width: 600px) {
        .brand span:last-child {
          display: none;
        }
        .app-nav a {
          padding: 0.5rem 0.6rem;
          font-size: 0.85rem;
        }
      }
    `,
  ],
})
export class AppComponent {
  protected readonly notifications = inject(NotificationService);
}
