import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router} from "@angular/router";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class AuthguardService implements CanActivate{

  constructor(public auth: AuthService, public router: Router) {}
 canActivate(route: ActivatedRouteSnapshot): boolean {    // this will be passed from the route config
//     // on the data property
    console.log(this.auth.isLoggedIn())
    const expectedRole = route.data.expectedRole;
    var flag = false;
    console.log(expectedRole)
    console.log(this.auth.getRoles())
    expectedRole.toString().split(' ')
        .forEach(s=> {
          console.log(s.toString())
          if(this.auth.getRoles().includes(s.toString())){
            flag = true;
          }
        });
    if (!flag || !this.auth.isLoggedIn()) {
      this.router.navigate(['login']);
      console.log("false")
      return false;

    }
    console.log("true")
    return true;
  }}
// import { Injectable } from '@angular/core';
// import {
//   Router,
//   CanActivate,
//   ActivatedRouteSnapshot
// } from '@angular/router';
// import { AuthService } from './auth.service';
// import decode from 'jwt-decode';@Injectable()
// export class RoleGuardService implements CanActivate {
//   constructor(public auth: AuthService, public router: Router) {}
//
//   canActivate(route: ActivatedRouteSnapshot): boolean {    // this will be passed from the route config
//     // on the data property
//     console.log(this.auth.isLoggedIn())
//     const expectedRole = route.data.expectedRole;
//     var flag = false;
//     console.log(expectedRole)
//     console.log(this.auth.getRoles())
//     expectedRole.toString().split(' ')
//         .forEach(s=> {
//           console.log(s.toString())
//           if(this.auth.getRoles().includes(s.toString())){
//             flag = true;
//           }
//         });
//     if (!flag || !this.auth.isLoggedIn()) {
//       this.router.navigate(['login']);
//       console.log("false")
//       return false;
//
//     }
//     console.log("true")
//     return true;
//   }}