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
      console.log(newMess)
      this.unreadMessageSource.next(newMess);
      console.log(this.unreadMessage);
    })
  }
  getUnreadMessages(email: String){
    return this.http.get<string>(REST_URL + '/unread/' + email);
  }
}
