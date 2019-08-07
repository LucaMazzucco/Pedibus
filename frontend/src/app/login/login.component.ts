import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {MatSnackBar} from '@angular/material/snack-bar';
import { Router } from "@angular/router";
import {TitleService} from "../services/title.service";

interface User {
  email: string;
  password: string;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})

export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  infoMessage: string = '';

  constructor(private authService: AuthService, private formBuilder: FormBuilder, private _snackBar: MatSnackBar, private router: Router, private titleservice: TitleService) { }

  ngOnInit() {
    this.titleservice.changeTitle('Login');
    this.loginForm = this.formBuilder.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    });
  }
  // convenience getter for easy access to form fields
  get f() { return this.loginForm.controls; }

  onSubmit(): void{
    console.log(this.f.email.value, this.f.password.value)
    let response = this.authService.login(this.f.email.value, this.f.password.value);
    response.subscribe(data => {
      this.authService.setSession(data);
      this.infoMessage = "You are logged!";
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = ''
      this.router.navigate(['/presenze'])
    }, error =>{
      this.infoMessage = "Password or email are wrong!";
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = '';
    })
  }

}
