import { Component, OnInit } from '@angular/core';
import { Validators, FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { TitleService } from '../services/title.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import { group } from '@angular/animations';
import { Observable, Subscription } from 'rxjs';
import { first } from 'rxjs/operators';
import { duration } from 'moment';

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
  registrationForm: FormGroup;
  checkEmailTaken: Subscription;
  infoMessage: string = '';

  constructor( private formBuilder: FormBuilder, private authService: AuthService, private _snackBar: MatSnackBar, private titleService: TitleService) {
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
  }
  
  /*checkEmailPresence(){
    let email = this.registrationForm.controls.email.value;
    this.checkEmailTaken = this.authService.getEmailPresence(email)
      .pipe(first())
      .subscribe(res=>{
      if(res){
        console.log(res);
        this.registrationForm.controls['email'].setErrors({ isPresent: true });
        
      }
      this.checkEmailTaken.unsubscribe();
    });
  }

  getEmailErrorMessage() {
    let email = this.registrationForm.controls.email;
    return email.hasError('required') ? 'Questo campo non può essere vuoto' :
          email.hasError('email') ? 'Per favore, inserisci una mail valida' :
          email.hasError('isPresent') ? 'La mail inserita è già stata usata' :
            '';
  }*/

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
                              this.registrationForm.controls.email.value,
                              this.registrationForm.controls.password.value);
    response.subscribe(data => {
      this.infoMessage = data.body['result'];
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = ''
    }) 
  }

}
