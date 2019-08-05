import { Component, OnInit } from '@angular/core';
import {DataService} from "../services/data.service";
import {Reservation} from "../classes/reservation";
import {TitleService} from "../services/title.service";
import {MatDialog, MatDialogConfig, MatTableDataSource} from "@angular/material";
import {Line} from "../classes/line";
import {Stop} from "../classes/stop";
import {Ride} from "../classes/ride";
import {collectExternalReferences} from "@angular/compiler";


@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss']
})
export class BookingComponent implements OnInit {

  constructor(private dataService: DataService, private titleservice: TitleService, private dialog: MatDialog, ) { }

  reservations: Reservation[];
  subscription: any;
  isDisabled: Boolean[];
  lines: Line[];
  selectedLine: Line;
  selectedRide: Ride;
  selectedStopA: Stop;
  selectedStopR: Stop;

  ngOnInit() {
    this.getReservations();
    this.titleservice.changeTitle('Le mie prenotazioni')
    this.isDisabled = []
  }

  getReservations() : void{
    this.subscription = this.dataService.getReservations().subscribe(reservations => {
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
    /*
    dialogConfig.data = {
      id: 1,
      title: 'Angular For Beginners'
    };
    */
    let dialogRef = this.dialog.open(templateRef, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }
  enableEditing(index): void{
    console.log(index);
    if(this.isDisabled[index]){
      this.isDisabled[index] = false;
    }
    else {
      this.isDisabled[index] = true;
    }
  }

  addReservation(): void{
    this.reservations.push(new Reservation(this.selectedLine.lineName, this.selectedRide.date, this.selectedRide.stops, this.selectedRide.stopsBack, this.selectedStopA, this.selectedStopR));
    this.isDisabled.push(true)
  }

  deleteReservation(i): void{
    this.reservations.splice(i,1)
  }
}
