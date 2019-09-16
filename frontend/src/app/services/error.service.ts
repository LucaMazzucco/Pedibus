import { ErrorHandler, Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';
import {AuthService} from "./auth.service";
import {HttpErrorResponse} from "@angular/common/http";
@Injectable({
  providedIn: 'root'
})

export class ErrorService implements ErrorHandler {

  constructor(private injector: Injector, private authService: AuthService) { }

  handleError(error: Error | HttpErrorResponse) {
      
    const router = this.injector.get(Router);
    if(error instanceof HttpErrorResponse){
        console.log('Errore MADONNALEEEEEEEEE')
        if(error.status === 401 || error.status === 403){
            this.authService.logout()
            router.navigate(['/login'])
        }

    }
    else{
        console.log('Errore sconosciuto')
    }
  }
}
