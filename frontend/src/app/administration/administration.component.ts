import {Component, OnDestroy, OnInit, Input} from '@angular/core';
import { Person } from '../classes/person';
import { Line } from '../classes/line';
import { Ride } from '../classes/ride';
import { DataService } from '../services/data.service';
import {MatDialogConfig, MatTabChangeEvent, MatTableDataSource, PageEvent} from '@angular/material';
import {Stop} from '../classes/stop';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {SelectionModel} from "@angular/cdk/collections";
import {TitleService} from "../services/title.service";
import {NotificationService} from "../services/notification.service";


@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.scss']
})
export class AdministrationComponent implements OnInit {

  constructor(private dataService: DataService, private titleservice: TitleService, private dialog: MatDialog) { }
  // displayedColumns: string[] = ['nome', 'ruolo'];

  ngOnInit() {
    this.titleservice.changeTitle('Gestione amministratori');
  }

}
