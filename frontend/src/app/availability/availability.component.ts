import { Component, OnInit } from '@angular/core';
import {TitleService} from "../services/title.service";
import {MatDialog, MatDialogConfig, MatTableDataSource} from "@angular/material";
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
  displayedColumns: string[] = ['lineName', 'rideDate', 'flagGoing', 'delete'];
  tableDataSource: any;


  constructor(private dataService: DataService, private titleservice: TitleService, private dialog: MatDialog, private _formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.titleservice.changeTitle('Renditi disponibile per accompagnare!');
    this.isLinear = true;
    this.dataService.getAvailabilities(localStorage.getItem('current_user')).subscribe(a =>{
        this.availabilities = a;
        this.tableDataSource = new MatTableDataSource<Availability>(this.availabilities);
    });
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
    this.availabilities.push(new Availability(localStorage.getItem('current_user'), this.selectLineForm.controls.selectedLine.value.lineName, this.selectRideForm.controls.selectedRide.value.date, this.selectGoingForm.controls.selectedFlagGoing.value.includes('andata')));
    this.tableDataSource._updateChangeSubscription()
    console.log(this.availabilities)
    //TODO: Aggiunta al db
  }
  deleteAvailability(i: number){
      console.log(i);
      this.availabilities.splice(i,1);
      console.log(this.availabilities)
      this.tableDataSource._updateChangeSubscription()
      //TODO: Save to db
  }

}
