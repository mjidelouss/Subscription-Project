import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./components/dashboard/dashboard.component').then((m) => m.DashboardComponent),
  },
  {
    path: 'clients',
    loadComponent: () =>
      import('./components/clients/clients.component').then((m) => m.ClientsComponent),
  },
  {
    path: 'quotes',
    loadComponent: () =>
      import('./components/quotes/quotes.component').then((m) => m.QuotesComponent),
  },
  {
    path: 'contracts',
    loadComponent: () =>
      import('./components/contracts/contracts.component').then((m) => m.ContractsComponent),
  },
  { path: '**', redirectTo: 'dashboard' },
];
