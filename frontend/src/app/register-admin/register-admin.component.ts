import { Component, OnInit } from '@angular/core';
import { Validators, FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { TitleService } from '../services/title.service';
import { DataService} from '../services/data.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import { Observable, Subscription } from 'rxjs';
import { first, tap } from 'rxjs/operators';
import { Line } from '../model/line';

@Component({
  selector: 'app-register-admin',
  templateUrl: './register-admin.component.html',
  styleUrls: ['./register-admin.component.scss']
})
export class RegisterAdminComponent implements OnInit {
  registrationAdminForm: FormGroup;
  checkEmailTaken: Subscription;
  infoMessage: string = '';
  roles: String[] = ["Amministratore", "Accompagnatore", "Genitore"];
  lines: Line[];

  constructor(private formBuilder: FormBuilder, private dataService: DataService, private authService: AuthService, private _snackBar: MatSnackBar, private titleService: TitleService) {
    this.titleService.changeTitle("Aggiungi un nuovo utente");
    this.registrationAdminForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      role: ['', [Validators.required]],
      line: ['', []]
    }, {validators: []});
  }

  ngOnInit() {
    this.checkEmailTaken = null;
  }

  checkEmailPresence(){
    let email = this.registrationAdminForm.controls.email.value;
    this.checkEmailTaken = this.authService.getEmailPresence(email)
      .pipe(first())
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

  getRolesErrorMessage() {
    let role = this.registrationAdminForm.controls.role;
    return role.hasError('required') ? 'Questo campo non può essere vuoto' :
            '';
  }

  onSubmit(): void{
    var line = "";
    if(this.registrationAdminForm.controls.role.value == "Amministratore"){
      line = this.registrationAdminForm.controls.line.value.lineName;
    }
    let response = this.authService.registerAdmin(this.registrationAdminForm.controls.email.value,
                                                  this.registrationAdminForm.controls.role.value,
                                                  line);

    response.subscribe(data => {
      this.infoMessage = data.body['result'];
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = '';
    })
  }

  manageRole(role: any){
    //let role = this.registrationAdminForm.controls.role.value;
    if(role.value == "Amministratore"){
      this.dataService.getNoAdminLines().subscribe(lines => {
        this.lines = lines;
      });
    } else {
      this.lines = [];
    }
  }
}
