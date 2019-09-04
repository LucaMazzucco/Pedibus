import { Component, OnInit } from '@angular/core';
import { Validators, FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { TitleService } from '../services/title.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import { group } from '@angular/animations';
import { Observable, Subscription } from 'rxjs';
import { first } from 'rxjs/operators';
import { duration } from 'moment';
import { Router } from '@angular/router';

function checkPasswords(group: FormGroup){
  let pass = group.controls.password.value;
  let confirmPass = group.controls.confirmPassword.value;

  return pass === confirmPass ? null : group.controls['confirmPassword'].setErrors({ notSame: true });
}

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})

export class RegisterComponent implements OnInit {
  email: String = '';
  token: String = '';
  registrationForm: FormGroup;
  checkEmailTaken: Subscription;
  infoMessage: string = '';

  constructor( private formBuilder: FormBuilder, private authService: AuthService, private router: Router, private _snackBar: MatSnackBar, private titleService: TitleService) {
    this.titleService.changeTitle("Aggiungi un nuovo utente"); 
    this.registrationForm = this.formBuilder.group({
      firstname: ['', [Validators.required, Validators.minLength(2)]],
      lastname: ['', [Validators.required, Validators.minLength(2)]], 
      registrationNumber: ['', [Validators.required, Validators.minLength(6)]],
      //email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['']
    }, { validators: [checkPasswords]});
  }

  ngOnInit() {
    this.checkEmailTaken = null;
    let url = this.router.url;
    this.email = url.split('/')[2]; 
    this.token = url.split('/')[3];

    this.authService.checkToken(this.email, this.token)
    .pipe(first())
    .subscribe(res => {
      if(!res){ this.router.navigateByUrl(''); }
    });
  }

  getFirstnameErrorMessage() {
    let firstname = this.registrationForm.controls.firstname;
    return firstname.hasError('required') ? 'Il nome non può essere vuoto' :
          firstname.hasError('minlength') ? 'Per favore, inserisci un nome valido (lunghezza minima = 2)' :
            '';
  }

  getLastnameErrorMessage() {
    let lastname = this.registrationForm.controls.lastname;
    return lastname.hasError('required') ? 'Il cognome non può essere vuoto' :
          lastname.hasError('minlength') ? 'Per favore, inserisci un cognome valido (lunghezza minima = 2)' :
            '';
  }

  getRegistrationNumberErrorMessage() {
    let registrationNumber = this.registrationForm.controls.registrationNumber;
    return registrationNumber.hasError('required') ? 'Il codice fiscale non può essere vuoto' :
          registrationNumber.hasError('minlength') ? 'Per favore, inserisci un codice fiscale valido (lunghezza minima = 6)' :
            '';
  }

  onSubmit(): void{ 
    let response = this.authService.register(this.registrationForm.controls.firstname.value, 
                              this.registrationForm.controls.lastname.value,
                              this.registrationForm.controls.registrationNumber.value,
                              this.email,
                              this.registrationForm.controls.password.value,
                              this.token);
    response.subscribe(data => {
      this.infoMessage = data.body['result'];
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = ''
    }) 
  }

}
