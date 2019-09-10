import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { tap, map, first } from 'rxjs/operators';
import * as moment from 'moment';
import { Observable } from 'rxjs';
import * as jwt_decode from 'jwt-decode';

const REST_URL = 'http://localhost:8080';

interface UserLogin {
  email: string;
  password: string;
}

interface UserRegistration {
  firstname: string;
  lastname: string;
  registrationNumber: string;
  email: string;
  password: string;
}

interface AdminRegistration {
  email: string;
  role: string;
}

export const TOKEN_NAME = 'jwt_token';
@Injectable({
  providedIn: 'root',
})


export class AuthService {

  constructor(private http: HttpClient) { }

  login(username: string, password: string ) {
    return this.http.post<UserLogin>(REST_URL + '/login', {username, password});
  }

  setSession(authResult) {
    if (this.getToken() != null) {
      this.logout();
    }
    localStorage.setItem('current_user', authResult.email);
    localStorage.setItem(TOKEN_NAME, authResult.token);
    localStorage.setItem('current_user_roles', authResult.roles);
  }
  logout() {
    localStorage.removeItem('current_user');
    localStorage.removeItem(TOKEN_NAME);
    localStorage.removeItem('current_user_roles');
  }

  getToken(): string {
    return localStorage.getItem(TOKEN_NAME);
  }

  getRoles(): string {
    return localStorage.getItem('current_user_roles');
  }

  isLoggedIn(token?: string): boolean {
    if (!token) { token = this.getToken(); }
    if (!token) { return false; }
    const date = this.getTokenExpirationDate(token);
    if (date === undefined) { return false; }
    return (date.valueOf() > new Date().valueOf());
  }

  isParent(): boolean{
    return this.getRoles().includes("ROLE_USER");
  }

  isAdmin() : boolean{
    return this.getRoles().includes("ROLE_ADMIN");
  }

  isConductor() : boolean{
    return this.getRoles().includes("ROLE_CONDUCTOR");
  }



  getTokenExpirationDate(token: string): Date {
    const decoded = jwt_decode(token);
    if (decoded.exp === undefined) { return null; }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

  isLoggedOut() {
      return !this.isLoggedIn();
  }

  getEmailPresence(email: string): Observable<boolean> {
    return this.http.get<boolean>(REST_URL + '/checkEmail/' + email);
  }

  // tslint:disable-next-line:max-line-length
  register(name: string, surname: string, registrationNumber: string, email: string, password: string, token: string): Observable<HttpResponse<UserRegistration>> {
    // tslint:disable-next-line:max-line-length
    return this.http.post<UserRegistration>(REST_URL + '/register/' + token, {name, surname, registrationNumber, email, password}, {observe: 'response'});
  }

  registerAdmin(email: string, role: string, line: string): Observable<HttpResponse<AdminRegistration>> {
    return this.http.post<AdminRegistration>(REST_URL + '/registerAdmin', {email, role, line}, {observe: 'response'});
  }

  checkToken(email: string, token: string): Observable<boolean> {
    return this.http.get<boolean>(REST_URL + '/checkToken/' + email + '/' + token);
  }
}
