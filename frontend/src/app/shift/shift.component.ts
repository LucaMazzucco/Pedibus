import {Component, OnInit, ViewChild} from '@angular/core';
import {TitleService} from '../services/title.service';
import {DataService} from '../services/data.service';
import {Shift} from '../model/shift';
import {MatDialog, MatDialogConfig, MatSnackBar, MatTable, MatTableDataSource} from '@angular/material';

@Component({
  selector: 'app-shift',
  templateUrl: './shift.component.html',
  styleUrls: ['./shift.component.scss']
})
export class ShiftComponent implements OnInit {

  tableDataSource: MatTableDataSource<Shift> = new MatTableDataSource<Shift>([]);
  displayedColumns: string[] = ['lineName', 'rideDate', 'personName', 'flagGoing', 'delete', 'confirm'];
  infoMessage = '';
  @ViewChild(MatTable) table: MatTable<any>;
  // tslint:disable-next-line:variable-name
  constructor(private dataService: DataService, private _snackBar: MatSnackBar, private titleservice: TitleService) { }

  ngOnInit() {
    this.titleservice.changeTitle('Gestione turni accompagnatori');
    this.dataService.getShifts(localStorage.getItem('current_user')).subscribe(a => {
      this.tableDataSource.data = a;
    });
  }

  // addShift(i: number) {
  //   // this.tableDataSource.data[i].confirmed = true
  //   const response = this.dataService.addShift(this.tableDataSource.data[i]);
  //   // this.tableDataSource.data.splice(i,1);
  //   // this.tableDataSource._updateChangeSubscription()
  //   response.subscribe(data => {
  //     this.infoMessage = 'Aggiunto un nuovo turno!';
  //     this._snackBar.open(this.infoMessage, '', {duration: 2000});
  //     this.infoMessage = '';
  //   }, error => {
  //     this.infoMessage = 'Non è stato possibile aggiungere il turno';
  //     this._snackBar.open(this.infoMessage, '', {duration: 2000});
  //     this.infoMessage = '';
  //   });
  // }
  confirmShift(i: number) {
    // this.tableDataSource.data[i].confirmed = true
    const response = this.dataService.confirmShiftAdmin(this.tableDataSource.data[i]);
    // this.tableDataSource.data.splice(i,1);

    response.subscribe(data => {
      this.tableDataSource.data[i].confirmed1 = true;
      this.infoMessage = 'Turno confermato!';
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = '';
      this.tableDataSource._updateChangeSubscription()
      // this.tableDataSource.data.push(data);
      this.table.renderRows();
    }, error => {
      this.infoMessage = 'Non è stato possibile confermare il turno';
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = '';
    });
  }

  deleteShift(i: number) {
    console.log(this.tableDataSource.data[i]);
    const response =  this.dataService.deleteShift(this.tableDataSource.data[i]);
    this.tableDataSource.data.splice(i, 1);
    this.tableDataSource._updateChangeSubscription();
    response.subscribe(data => {
      this.infoMessage = 'Rimosso il turno!';
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = '';
    }, error => {
      this.infoMessage = 'Non è stato possibile rimuovere il turno';
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = '';

    });
  }


}
