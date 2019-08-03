import { Component, OnInit, ChangeDetectorRef, OnDestroy } from '@angular/core';
import {MediaMatcher} from '@angular/cdk/layout';
import {AuthService} from "./services/auth.service";
import { Router } from "@angular/router";
import {TitleService} from "./services/title.service";
import {Observable} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnDestroy, OnInit{
  mobileQuery: MediaQueryList;
  subtitle: String;
  private _mobileQueryListener: () => void;

  constructor(changeDetectorRef: ChangeDetectorRef, media: MediaMatcher, private authService: AuthService, private router: Router, private titleservice : TitleService) {
    this.mobileQuery = media.matchMedia('(max-width: 600px)');
    this._mobileQueryListener = () => changeDetectorRef.detectChanges();
    this.mobileQuery.addListener(this._mobileQueryListener);
  }


  ngOnInit(): void {
    this.titleservice.currentTitle.subscribe(sub => this.subtitle = sub)
  }

  isLogged(): Boolean{
    return this.authService.isLoggedIn()
  }
  logout(): void{
    this.authService.logout();
    this.router.navigate(['/login'])
  }
  ngOnDestroy(): void {
    this.mobileQuery.removeListener(this._mobileQueryListener);
  }
}
