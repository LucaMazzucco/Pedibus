import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { tap, map, first } from 'rxjs/operators';
import * as moment from "moment";
import { Observable } from 'rxjs';

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

@Injectable({
  providedIn: 'root',
})


export class AuthService {
  
  constructor(private http: HttpClient) { }

  login(username:string, password:string ) {
    return this.http.post<UserLogin>(REST_URL + '/login', {username, password})
      .pipe(first())
      .subscribe(
        (data) => this.setSession(data)
      )
  }
  
  private setSession(authResult) {
    console.log(authResult)
    const expiresAt = moment().add(authResult.expiresIn,'second');
    localStorage.setItem('id_token', authResult.token);
    localStorage.setItem("expires_at", JSON.stringify(expiresAt.valueOf()) );
  
  }
  logout() {
    localStorage.removeItem("id_token");
    localStorage.removeItem("expires_at");
  }

  public isLoggedIn() {
      return moment().isBefore(this.getExpiration());
  }

  isLoggedOut() {
      return !this.isLoggedIn();
  }

  getExpiration() {
      const expiration = localStorage.getItem("expires_at");
      const expiresAt = JSON.parse(expiration);
      return moment(expiresAt);
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
