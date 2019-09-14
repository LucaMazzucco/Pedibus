import { Component, OnInit } from '@angular/core';
import {DataService} from "../services/data.service";
import {Reservation} from "../model/reservation";
import {TitleService} from "../services/title.service";
import {MatDialog, MatDialogConfig, MatSnackBar, MatTableDataSource} from "@angular/material";
import {Line} from "../model/line";
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Child} from "../model/child";



@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss']
})
export class BookingComponent implements OnInit {

  reservations: Reservation[];
  subscription: any;
  isDisabled: Boolean[];
  lines: Line[];
  selectLineForm: FormGroup;
  selectRideForm: FormGroup;
  selectGoingForm: FormGroup;
  selectBackForm: FormGroup;
  isLinear: boolean;
  children: Child[];
  currentChild: Child;
  infoMessage : string = "";

  constructor(private dataService: DataService, private _snackBar: MatSnackBar, private titleservice: TitleService, private dialog: MatDialog, private _formBuilder: FormBuilder) { }




  ngOnInit() {
    this.selectLineForm = this._formBuilder.group({
      selectedLine: ['', Validators.required]
    });
    this.selectRideForm = this._formBuilder.group({
      selectedRide: ['', Validators.required]
    });
    this.selectGoingForm = this._formBuilder.group({
      selectedGoing: ['', Validators.required]
    });
    this.selectBackForm = this._formBuilder.group({
      selectedBack: ['', Validators.required]
    });
    this.dataService.getLines().subscribe(lines => {
      this.lines=lines;
      console.log(this.lines)
    });
    this.isLinear = true;
    this.getChildren();
    this.titleservice.changeTitle('Le mie prenotazioni')
    this.isDisabled = []

  }

  getChildren(): void{
    this.subscription = this.dataService.getChildren(localStorage.getItem('current_user')).subscribe( children => {
      this.children = children;
      this.currentChild = this.children[0]
      this.getReservations(this.currentChild.registrationNumber)
    })
  }

  selectChild(c: Child){
    this.currentChild = c;
  }

  getReservations(c_id: string) : void{
    console.log('Reservation per bambino' + c_id)
    this.subscription = this.dataService.getChildReservation(c_id).subscribe(reservations => {
      this.reservations = reservations;
      console.log(this.reservations)
      this.initializeArray();
    },
            error => this.reservations = [])
  }


  initializeArray(){
    for(let i = 0; i < this.reservations.length; i++){
      this.isDisabled.push(true);
    }
  }

  openDialog(templateRef) {
    const dialogConfig = new MatDialogConfig();
    let dialogRef = this.dialog.open(templateRef, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  enableEditing(index): void{
    if(this.isDisabled[index]){
      this.isDisabled[index] = false;
    }
    else {
      this.isDisabled[index] = true;
    }
  }

  addReservation(): void{
    let newRes = new Reservation(this.selectLineForm.controls.selectedLine.value.lineName,
                                  this.selectGoingForm.controls.selectedGoing.value,
                                  this.selectBackForm.controls.selectedBack.value,
                                  this.selectRideForm.controls.selectedRide.value.date,
                                  this.currentChild.registrationNumber,
                                  localStorage.getItem('current_user'))
    this.dataService.addChildReservation(this.currentChild.registrationNumber, newRes).subscribe(res => {
          this.infoMessage = "Aggiunta la prenotazione!";
          this._snackBar.open(this.infoMessage, '', {duration: 2000});
          this.infoMessage = ''
          this.reservations.push(newRes);
          this.isDisabled.push(true)
        },
        error => {
          this.infoMessage = "Non è stato possibile aggiungere la prenotazione";
          this._snackBar.open(this.infoMessage, '', {duration: 2000});
          this.infoMessage = ''
        }
    )

  }

  deleteReservation(i): void{
    this.reservations.splice(i,1)
  }


}
