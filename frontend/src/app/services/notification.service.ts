import { Injectable } from '@angular/core';
import {BehaviorSubject, Subject} from "rxjs";
import {HttpClient} from "@angular/common/http";

const REST_URL = 'http://localhost:8080';


@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private unreadMessageSource = new BehaviorSubject("0");
  unreadMessage = this.unreadMessageSource.asObservable();
  
  constructor(private http: HttpClient) { }

  public updateMessageCount(email: String){
    this.getUnreadMessages(email).subscribe(n => {
      let newMess = ""+ n;
      this.unreadMessageSource.next(newMess);
    })
  }
  getUnreadMessages(email: String){
    return this.http.get<string>(REST_URL + '/user/unread/' + email);
  }
}
