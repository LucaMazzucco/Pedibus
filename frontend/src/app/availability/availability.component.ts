import {Component, OnInit} from '@angular/core';
import {TitleService} from '../services/title.service';
import {MatDialog, MatDialogConfig, MatSnackBar, MatTableDataSource} from '@angular/material';
import {DataService} from '../services/data.service';
import {Line} from '../model/line';
import {Availability} from '../model/availability';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';


@Component({
  selector: 'app-availability',
  templateUrl: './availability.component.html',
  styleUrls: ['./availability.component.scss']
})
export class AvailabilityComponent implements OnInit {
  lines: Line[];
  // selectedLine: Line;
  // selectedRide: Ride;
  // selectedFlagGoing: string;
  selectLineForm: FormGroup;
  selectRideForm: FormGroup;
  selectGoingForm: FormGroup;
  isLinear: boolean;
  displayedColumns: string[] = ['lineName', 'rideDate', 'flagGoing', 'delete'];
  tableDataSource: MatTableDataSource<Availability> = new MatTableDataSource<Availability>([]);
  infoMessage = '';


    // tslint:disable-next-line:max-line-length variable-name
  constructor(private dataService: DataService,  private _snackBar: MatSnackBar, private titleservice: TitleService, private dialog: MatDialog, private _formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.titleservice.changeTitle('Renditi disponibile per accompagnare!');
    this.isLinear = true;
    this.dataService.getAvailabilities(localStorage.getItem('current_user')).subscribe(a => {
        this.tableDataSource.data = a;
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
    this.dataService.getLines().subscribe(lines => this.lines = lines);
    /*
    dialogConfig.data = {
      id: 1,
      title: 'Angular For Beginners'
    };
    */
    const dialogRef = this.dialog.open(templateRef, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }
  addAvailability() {
      // tslint:disable-next-line:max-line-length
    const av: Availability = new Availability(localStorage.getItem('current_user'), this.selectLineForm.controls.selectedLine.value.lineName, this.selectRideForm.controls.selectedRide.value.date, this.selectGoingForm.controls.selectedFlagGoing.value.includes('andata'));
    this.tableDataSource.data.push(av);
    this.tableDataSource._updateChangeSubscription();
    const response =  this.dataService.addAvailability(av);
    response.subscribe(data => {
          this.infoMessage = 'Aggiunta la disponibilità!';
          this._snackBar.open(this.infoMessage, '', {duration: 2000});
          this.infoMessage = '';
      }, error => {
          this.infoMessage = 'Non è stato possibile aggiungere la disponibilità';
          this._snackBar.open(this.infoMessage, '', {duration: 2000});
          this.infoMessage = '';
      });
  }
  deleteAvailability(i: number) {
      console.log(this.tableDataSource.data[i]);
      const response =  this.dataService.deleteAvailability(this.tableDataSource.data[i]);
      this.tableDataSource.data.splice(i, 1);
      this.tableDataSource._updateChangeSubscription();
      response.subscribe(data => {
        this.infoMessage = 'Rimossa la disponibilità!';
        this._snackBar.open(this.infoMessage, '', {duration: 2000});
        this.infoMessage = '';
      }, error => {
        this.infoMessage = 'Non è stato possibile rimuovere la disponibilità';
        this._snackBar.open(this.infoMessage, '', {duration: 2000});
        this.infoMessage = '';

      });
  }

}
