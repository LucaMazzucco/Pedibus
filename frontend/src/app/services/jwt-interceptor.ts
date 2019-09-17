import {HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {Observable, throwError} from 'rxjs';
import {AuthService, TOKEN_NAME} from './auth.service';
import * as jwt_decode from 'jwt-decode';

import {catchError} from "rxjs/operators";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

    constructor(private authService: AuthService) {

    }


    intercept(req: HttpRequest<any>,
              next: HttpHandler): Observable<HttpEvent<any>> {
        const idToken = localStorage.getItem(TOKEN_NAME);
        if(idToken){
            const cloned = req.clone({
                headers: req.headers.set("Authorization",
                    "Bearer " + idToken)
            });
            // if(this.authService.isTokenExpired(idToken)){
            //     this.authService.refresh(localStorage.getItem('current_user')).subscribe(refResult =>{
            //             console.log('new token: ' + refResult)
            //             localStorage.setItem('current_user', refResult)
            //             return next.handle(cloned);
            //         }
            //     )
            // }
            return next.handle(cloned)
        }
        else{
            return next.handle(req)
        }


    }
}
