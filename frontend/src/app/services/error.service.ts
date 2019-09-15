import { ErrorHandler, Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class ErrorService implements ErrorHandler {

  constructor(private injector: Injector, private authService: AuthService) { }

  handleError(error) {
    const router = this.injector.get(Router);
    if (error.rejection.status === 401 || error.rejection.status === 403) {
      this.authService.logout()
    }

    throw error;
  }
}
