import { Component, OnInit } from '@angular/core';
import {DataService} from "../services/data.service";
import {Reservation} from "../classes/reservation";
import {TitleService} from "../services/title.service";

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss']
})
export class BookingComponent implements OnInit {

  constructor(private dataService: DataService, private titleservice: TitleService) { }

  reservations: Reservation[];
  subscription: any;
  isDisabled: Boolean[];

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
  enableEditing(index): void{
    console.log(index);
    if(this.isDisabled[index]){
      this.isDisabled[index] = false;
    }
    else {
      this.isDisabled[index] = true;
    }
  }


}
