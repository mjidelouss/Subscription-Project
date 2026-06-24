import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { NotificationService } from './notification.service';

/**
 * Global error handling: turns any failed HTTP response into a readable toast.
 * It understands the backend ApiError shape ({ message, fieldErrors, ... }).
 */
export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const notifications = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      notifications.error(buildMessage(error));
      return throwError(() => error);
    })
  );
};

function buildMessage(error: HttpErrorResponse): string {
  if (error.status === 0) {
    return 'Impossible de joindre le serveur. Est-il demarre ?';
  }

  const body = error.error;

  // Field validation errors (HTTP 400) -> join field-level messages.
  if (body?.fieldErrors && typeof body.fieldErrors === 'object') {
    const details = Object.entries(body.fieldErrors)
      .map(([field, message]) => `${field}: ${message}`)
      .join(' | ');
    if (details) {
      return details;
    }
  }

  if (body?.message) {
    return body.message;
  }

  return `Erreur ${error.status} ${error.statusText}`;
}
