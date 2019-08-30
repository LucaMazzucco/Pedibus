import { Component, OnInit } from '@angular/core';
import {TitleService} from "../services/title.service";
import {DataService} from "../services/data.service";
import {Shift} from "../model/shift";
import {MatDialog, MatDialogConfig, MatSnackBar, MatTableDataSource} from "@angular/material";

@Component({
  selector: 'app-shift',
  templateUrl: './shift.component.html',
  styleUrls: ['./shift.component.scss']
})
export class ShiftComponent implements OnInit {

  tableDataSource: MatTableDataSource<Shift> = new MatTableDataSource<Shift>([]);
  displayedColumns: string[] = ['lineName', 'rideDate', 'flagGoing', 'delete'];
  infoMessage: string = '';

  constructor(private dataService: DataService, private titleservice: TitleService) { }

  ngOnInit() {
    this.titleservice.changeTitle('Gestione turni accompagnatori');
    this.dataService.getShifts(localStorage.getItem('current_user')).subscribe(a => {
      this.tableDataSource.data = a;
    });
  }

}
