import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import {MediaMatcher} from '@angular/cdk/layout';
import {AuthService} from "./services/auth.service";
import { Router } from "@angular/router";
import {TitleService} from "./services/title.service";
import {Observable} from "rxjs";
import {DataService} from "./services/data.service";
import {NotificationService} from "./services/notification.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnDestroy, OnInit{
  mobileQuery: MediaQueryList;
  subtitle: String;
  private _mobileQueryListener: () => void;
  unreadMessages: string;

  constructor(changeDetectorRef: ChangeDetectorRef, media: MediaMatcher, private authService: AuthService, private router: Router, private titleservice : TitleService, private dataservice: DataService, private notificationService: NotificationService) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
    this._mobileQueryListener = () => changeDetectorRef.detectChanges();
    this.mobileQuery.addListener(this._mobileQueryListener);
  }


  ngOnInit(): void {
    this.titleservice.currentTitle.subscribe(sub => this.subtitle = sub);
    this.notificationService.unreadMessage.subscribe(n => this.unreadMessages = n);
  }
  isLogged(): Boolean{
    return this.authService.isLoggedIn();
  }

  /*TODO: isAdmin
  isAdmin(): Boolean{
    returno this.authService.isAdmin();
  }
  */

  logout(): void{
    this.authService.logout();
    this.router.navigate(['/login'])
  }
  ngOnDestroy(): void {
    this.mobileQuery.removeListener(this._mobileQueryListener);
  }

  updateNotifications(){
    this.notificationService.updateMessageCount(localStorage.getItem('current_user'));
  }

}
