import { Component, OnInit } from '@angular/core';
import {TitleService} from "../services/title.service";
import {MatDialog, MatDialogConfig} from "@angular/material";
import {DataService} from "../services/data.service";
import {Line} from "../classes/line";
import {Availability} from "../classes/availability";
import {Ride} from "../classes/ride";
import {FormBuilder, FormGroup, Validators} from '@angular/forms';


@Component({
  selector: 'app-availability',
  templateUrl: './availability.component.html',
  styleUrls: ['./availability.component.scss']
})
export class AvailabilityComponent implements OnInit {
  lines: Line[];
  availabilities: Availability[];
  // selectedLine: Line;
  // selectedRide: Ride;
  // selectedFlagGoing: string;
  selectLineForm: FormGroup;
  selectRideForm: FormGroup;
  selectGoingForm: FormGroup;
  isLinear: boolean;

  constructor(private dataService: DataService, private titleservice: TitleService, private dialog: MatDialog, private _formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.titleservice.changeTitle('Renditi disponibile per accompagnare!');
    this.isLinear = true;
    this.selectLineForm = this._formBuilder.group({
      selectedLine: ['', Validators.required]
    });
    this.selectRideForm = this._formBuilder.group({
      selectedRide: ['', Validators.required]
    });
    this.selectGoingForm = this._formBuilder.group({
      selectedFlagGoing: ['', Validators.required]
    });

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
  addAvailability(){
    console.log(this.selectLineForm.controls)
    //this.availabilities.push(new Availability(localStorage.getItem('current_user'), this.selectedLine.lineName, this.selectedRide.date, this.selectedFlagGoing.includes('andata')));
    //TODO: Aggiunta al db
  }
  deleteAvailability(i){
    this.availabilities.splice(i,1)
  }

}
