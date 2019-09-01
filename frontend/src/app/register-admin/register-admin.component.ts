import { Component, OnInit } from '@angular/core';
import { Validators, FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { TitleService } from '../services/title.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import { Observable, Subscription } from 'rxjs';

@Component({
  selector: 'app-register-admin',
  templateUrl: './register-admin.component.html',
  styleUrls: ['./register-admin.component.scss']
})
export class RegisterAdminComponent implements OnInit {
  registrationAdminForm: FormGroup;
  checkEmailTaken: Subscription;
  infoMessage: string = '';

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private _snackBar: MatSnackBar, private titleService: TitleService) {
    this.titleService.changeTitle("Aggiungi un nuovo utente");
    this.registrationAdminForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    }, {validators: []});
  }

  ngOnInit() {
    this.checkEmailTaken = null;
  }

  checkEmailPresence(){
    let email = this.registrationAdminForm.controls.email.value;
    this.checkEmailTaken = this.authService.getEmailPresence(email)
      .pipe()
      .subscribe(res=>{
      if(res){
        console.log(res);
        this.registrationAdminForm.controls['email'].setErrors({ isPresent: true });
        
      }
      this.checkEmailTaken.unsubscribe();
    });
  }

  getEmailErrorMessage() {
    let email = this.registrationAdminForm.controls.email;
    return email.hasError('required') ? 'Questo campo non può essere vuoto' :
          email.hasError('email') ? 'Per favore, inserisci una mail valida' :
          email.hasError('isPresent') ? 'La mail inserita è già stata usata' :
            '';
  }

  onSubmit(): void{
    let response = this.authService.registerAdmin(this.registrationAdminForm.controls.email.value);

    response.subscribe(data => {
      if(data.valueOf()) {this.infoMessage = 'Utente aggiunto con successo'}
      else {this.infoMessage = 'Inserimento del nuovo utente fallita'}
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = '';
    })
  }

}
