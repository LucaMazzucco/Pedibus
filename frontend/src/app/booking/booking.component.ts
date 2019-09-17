import { Component, OnInit } from '@angular/core';
import {DataService} from '../services/data.service';
import {Reservation} from '../model/reservation';
import {TitleService} from '../services/title.service';
import {MatDialog, MatDialogConfig, MatSnackBar, MatTableDataSource} from '@angular/material';
import {Line} from '../model/line';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Child} from '../model/child';



@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss']
})
export class BookingComponent implements OnInit {

  reservations: Reservation[];
  subscription: any;
  isDisabled: boolean[];
  lines: Line[];
  selectLineForm: FormGroup;
  selectRideForm: FormGroup;
  selectGoingForm: FormGroup;
  selectBackForm: FormGroup;
  isLinear: boolean;
  children: Child[];
  currentChild: Child;
  infoMessage = '';
  stopOfLine: string[];
  editMode: boolean;


  // tslint:disable-next-line:variable-name
  // tslint:disable-next-line:max-line-length variable-name
  constructor(private dataService: DataService, private _snackBar: MatSnackBar, private titleservice: TitleService, private dialog: MatDialog, private _formBuilder: FormBuilder) { }




  ngOnInit() {
    this.editMode = false;
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
      this.lines = lines;
      console.log(this.lines);
    });
    this.isLinear = true;
    this.getChildren();
    this.titleservice.changeTitle('Le mie prenotazioni');
    this.isDisabled = [];

  }

  getChildren(): void {
    this.subscription = this.dataService.getChildren(localStorage.getItem('current_user')).subscribe( children => {
      this.children = children;
      this.updateStopOfLines(this.children);
      this.currentChild = this.children[0];
      this.getReservations(this.currentChild.registrationNumber);
    });
  }

  selectChild(c: Child) {
    this.currentChild = c;
    this.dataService.getChildReservation(this.currentChild.registrationNumber).subscribe( r => this.reservations = r);
  }

  // tslint:disable-next-line:variable-name
  getReservations(c_id: string): void {
    console.log('Reservation per bambino' + c_id);
    this.subscription = this.dataService.getChildReservation(c_id).subscribe(reservations => {
      this.reservations = reservations;
      this.stopOfLine = [];
      console.log(this.reservations);
      this.initializeArray();
    }, error => this.reservations = []);
  }


  initializeArray() {
    // tslint:disable-next-line:prefer-for-of
    for (let i = 0; i < this.reservations.length; i++) {
      this.isDisabled.push(true);
    }
  }



  openDialog(templateRef) {
    const dialogConfig = new MatDialogConfig();
    const dialogRef = this.dialog.open(templateRef, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  enableEditing(index): void {
    if (this.isDisabled[index]) {
      this.isDisabled[index] = false;
    } else {
      this.isDisabled[index] = true;
    }
    this.editMode = true;

  }

  updateStopOfLines(children: Child[]) {
    for (const c of children) {
      this.dataService.getStopsOfLine(c.defaultLine).subscribe(
          r => {
            this.stopOfLine = this.stopOfLine.concat(r);
            console.log(this.stopOfLine);
          }
      );
    }
  }

  // updateStopsOfLine(i): void{
  //   console.log(this.reservations[i].lineName);
  //   this.dataService.getStopsOfLine(this.reservations[i].lineName).subscribe( s => {
  //     this.stopOfLine = s;
  //     console.log(this.stopOfLine)
  //   })
  // }

  addReservation(): void {
    console.log('Fermate predefinite ');
    const avStopsA: string[] = [];
    const avStopsR: string[] = [];

    this.selectRideForm.controls.selectedRide.value.stops.forEach(
        s => avStopsA.push(s.stopName)
    );
    this.selectRideForm.controls.selectedRide.value.stopsBack.forEach(
        s => avStopsR.push(s.stopName)
    );

    for (const s of this.selectRideForm.controls.selectedRide.value.stopsBack) {

    }
    console.log();
    const newRes = new Reservation(this.selectLineForm.controls.selectedLine.value.lineName,
                                  this.selectGoingForm.controls.selectedGoing.value,
                                  this.selectBackForm.controls.selectedBack.value,
                                  this.selectRideForm.controls.selectedRide.value.date,
                                  this.currentChild.registrationNumber,
                                  localStorage.getItem('current_user'),
                                  [], []
        );
    this.dataService.addChildReservation(this.currentChild.registrationNumber, newRes).subscribe(res => {
          this.infoMessage = 'Prenotazione aggiunta!';
          this._snackBar.open(this.infoMessage, '', {duration: 2000});
          this.infoMessage = '';
          newRes.availableStopsA = avStopsA;
          newRes.availableStopsR = avStopsR;
          this.reservations.push(newRes);
          this.isDisabled.push(true);
        },
        error => {
          this.infoMessage = 'Non è stato possibile aggiungere la prenotazione';
          this._snackBar.open(this.infoMessage, '', {duration: 2000});
          this.infoMessage = '';
        }
    );

  }

  deleteReservation(i): void {
    this.dataService.deleteChildReservation(this.currentChild.registrationNumber, this.reservations[i]).subscribe(res => {
          this.reservations.splice(i, 1);
          this.infoMessage = 'Eliminato!';
          this._snackBar.open(this.infoMessage, '', {duration: 2000});
          this.infoMessage = '';
        },
        error => {
          this.infoMessage = 'Non è stato possibile elminare la prenotazione!';
          this._snackBar.open(this.infoMessage, '', {duration: 2000});
          this.infoMessage = '';
        });
  }

  saveReservation(i): void {
    this.dataService.editChildReservation(this.currentChild.registrationNumber, this.reservations[i]).subscribe(res => {
          this.infoMessage = 'Modifiche salvate!';
          this._snackBar.open(this.infoMessage, '', {duration: 2000});
          this.infoMessage = '';
          this.isDisabled[i] = true;
          this.editMode = false;
        },
        error => {
          this.infoMessage = 'Non è stato possibile salvare le modifiche';
          this._snackBar.open(this.infoMessage, '', {duration: 2000});
          this.infoMessage = '';
        });
  }


}
