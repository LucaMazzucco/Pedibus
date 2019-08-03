import { Component, OnInit } from '@angular/core';
import {DataService} from "../services/data.service";
import {Reservation} from "../classes/reservation";
import {TitleService} from "../services/title.service";
import { Router } from "@angular/router";

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss']
})
export class BookingComponent implements OnInit {

  constructor(private dataService: DataService, private titleservice: TitleService) { }

  reservations: Reservation[];
  subscription: any;

  ngOnInit() {
    this.getReservations();
    this.titleservice.changeTitle('Le mie prenotazioni')
  }


  getReservations() : void{
    this.subscription = this.dataService.getReservations().subscribe(reservations => this.reservations = reservations)
  }

}
