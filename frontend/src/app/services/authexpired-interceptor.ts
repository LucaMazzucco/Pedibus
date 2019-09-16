import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import {AuthService} from "./auth.service";
import {Router} from "@angular/router";

@Injectable()
export class AuthExpiredInterceptor implements HttpInterceptor {
    constructor(private authService: AuthService, public router: Router) {}

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(
            tap(
                (event: HttpEvent<any>) => {},
                (err: any) => {
                    if (err instanceof HttpErrorResponse) {
                        if (err.status === 401 || err.status === 403) {
                            this.router.navigate(['/login'])
                            this.authService.logout();
                        }
                    }
                }
            )
        );
    }
}
