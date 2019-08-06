import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { tap, map, first } from 'rxjs/operators';
import * as moment from "moment";
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
export const TOKEN_NAME: string = 'jwt_token';
@Injectable({
  providedIn: 'root',
})


export class AuthService {
  
  constructor(private http: HttpClient) { }

  login(username:string, password:string ) {
    return this.http.post<UserLogin>(REST_URL + '/login', {username, password})
  }
  
  setSession(authResult) {
    if (this.getToken() != null){
      this.logout()
    }
    localStorage.setItem('current_user', authResult.email);
    localStorage.setItem(TOKEN_NAME, authResult.token);
  }
  logout() {
    localStorage.removeItem('current_user');
    localStorage.removeItem(TOKEN_NAME);
  }

  getToken(): string {
    return localStorage.getItem(TOKEN_NAME);
  }

  isLoggedIn(token?: string): boolean {
    if(!token) token = this.getToken();
    if(!token) return false;
    const date = this.getTokenExpirationDate(token);
    if(date === undefined) return false;
    return (date.valueOf() > new Date().valueOf());
  }

  getTokenExpirationDate(token: string): Date {
    const decoded = jwt_decode(token);
    if (decoded.exp === undefined) return null;

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

  isLoggedOut() {
      return !this.isLoggedIn();
  }

  getEmailPresence(email: string): Observable<Boolean>{
    // if(email === "a@a.it") return true;
    // return false;
    return this.http.get<Boolean>(REST_URL + "/checkEmail/" + email);                            
  }
  
  register(name: String, surname: String, registrationNumber: String, email: String, password: String): Observable<HttpResponse<UserRegistration>>{
    return this.http.post<UserRegistration>(REST_URL + '/register', {name, surname, registrationNumber, email, password}, {observe: 'response'})
  }
}
