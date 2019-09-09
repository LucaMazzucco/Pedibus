import { Component, OnInit } from '@angular/core';
import {DataService} from "../services/data.service";
import {Reservation} from "../model/reservation";
import {TitleService} from "../services/title.service";
import {MatDialog, MatDialogConfig, MatTableDataSource} from "@angular/material";
import {Line} from "../model/line";
import {Stop} from "../model/stop";
import {Ride} from "../model/ride";
import {Passenger} from "../model/passenger";
import {FormBuilder, FormGroup, Validators} from '@angular/forms';



@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss']
})
export class BookingComponent implements OnInit {

  constructor(private dataService: DataService, private titleservice: TitleService, private dialog: MatDialog, private _formBuilder: FormBuilder) { }

  reservations: Reservation[];
  subscription: any;
  isDisabled: Boolean[];
  lines: Line[];
  selectLineForm: FormGroup;
  selectRideForm: FormGroup;
  selectGoingForm: FormGroup;
  selectBackForm: FormGroup;
  isLinear: boolean;
  children: Passenger[];
  currentChild: Passenger;


  ngOnInit() {
    this.getChildren();
    this.titleservice.changeTitle('Le mie prenotazioni')
    this.isDisabled = []
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
  }

  getChildren(): void{
    this.subscription = this.dataService.getChildren(localStorage.getItem('email')).subscribe( children => {
      this.children = children;
      this.currentChild = this.children[0]
      this.getReservations(this.currentChild.registrationNumber)
    })
  }

  selectChild(c: Passenger){
    this.currentChild = c;
  }

  getReservations(c_id: string) : void{
    console.log('Reservation per bambino' + c_id)
    this.subscription = this.dataService.getReservations(c_id).subscribe(reservations => {
      this.reservations = reservations;
      this.initializeArray();
    })
  }


  initializeArray(){
    for(let i = 0; i < this.reservations.length; i++){
      this.isDisabled.push(true);
    }
  }

  openDialog(templateRef) {
    const dialogConfig = new MatDialogConfig();
    this.dataService.getLines().subscribe(lines => this.lines=lines);
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
    //this.reservations.push(new Reservation(this.selectedLine.lineName, this.selectedRide.date, this.selectedRide.stops, this.selectedRide.stopsBack, this.selectedStopA, this.selectedStopR));
    this.isDisabled.push(true)
  }

  deleteReservation(i): void{
    this.reservations.splice(i,1)
  }


}
