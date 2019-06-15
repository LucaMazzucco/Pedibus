import { Component, OnInit } from '@angular/core';
import { Validators, FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { AuthService } from '../services/auth.service';
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

  constructor( private formBuilder: FormBuilder, private authService: AuthService, private _snackBar: MatSnackBar) { 
    this.registrationForm = this.formBuilder.group({
      firstname: ['', [Validators.required, Validators.minLength(2)]],
      lastname: ['', [Validators.required, Validators.minLength(2)]], 
      registrationNumber: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['']
    }, { validators: [checkPasswords]});
  }

  ngOnInit() {
    this.checkEmailTaken = null;
  }
  
  checkEmailPresence(){
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
    return email.hasError('required') ? 'Email can not be blank' :
          email.hasError('email') ? 'Please, insert a valid email' :
          email.hasError('isPresent') ? 'Email already taken' :
            '';
  }

  getFirstnameErrorMessage() {
    let firstname = this.registrationForm.controls.firstname;
    return firstname.hasError('required') ? 'Firstname can not be blank' :
          firstname.hasError('minlength') ? 'Please, insert a valid firstname (minimum length = 2)' :
            '';
  }

  getLastnameErrorMessage() {
    let lastname = this.registrationForm.controls.lastname;
    return lastname.hasError('required') ? 'Lastname can not be blank' :
          lastname.hasError('minlength') ? 'Please, insert a valid lastname (minimum length = 2)' :
            '';
  }

  getRegistrationNumberErrorMessage() {
    let registrationNumber = this.registrationForm.controls.registrationNumber;
    return registrationNumber.hasError('required') ? 'Registration number can not be blank' :
          registrationNumber.hasError('minlength') ? 'Please, insert a valid registration number (minimum length = 6)' :
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
