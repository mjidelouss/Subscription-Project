import { Injectable, signal } from '@angular/core';

export type NotificationType = 'success' | 'error';

export interface Notification {
  id: number;
  type: NotificationType;
  message: string;
}

/**
 * Lightweight signal-based toast store. The error interceptor and the
 * components push messages here; the app shell renders them.
 */
@Injectable({ providedIn: 'root' })
export class NotificationService {
  private nextId = 0;
  readonly notifications = signal<Notification[]>([]);

  success(message: string): void {
    this.push('success', message);
  }

  error(message: string): void {
    this.push('error', message);
  }

  dismiss(id: number): void {
    this.notifications.update((list) => list.filter((n) => n.id !== id));
  }

  private push(type: NotificationType, message: string): void {
    const id = this.nextId++;
    this.notifications.update((list) => [...list, { id, type, message }]);
    // Auto-dismiss after a few seconds so toasts don't pile up.
    setTimeout(() => this.dismiss(id), 5000);
  }
}
