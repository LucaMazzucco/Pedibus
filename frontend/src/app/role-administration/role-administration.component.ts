import { Component, OnInit } from '@angular/core';
import { User } from '../model/user';
import {DataService} from '../services/data.service';
import {TitleService} from '../services/title.service';
import {MatDialog, MatDialogConfig, MatSnackBar, MatTableDataSource} from "@angular/material";

@Component({
  selector: 'app-role-administration',
  templateUrl: './role-administration.component.html',
  styleUrls: ['./role-administration.component.scss']
})

export class RoleAdministrationComponent implements OnInit {
  tableDataSource: MatTableDataSource<User> = new MatTableDataSource<User>([]);
  infoMessage: string = '';
  displayedColumns: string[] = ['email', 'role', 'line', 'delete'];

  constructor(private dataService: DataService, private titleService: TitleService, private _snackBar: MatSnackBar) { }

  ngOnInit() {
    this.titleService.changeTitle("Gestione amministratori");
    this.dataService.getRoles().subscribe(user => {
      console.log(user);
      this.tableDataSource.data = user;      
    });
  }

  deleteRole(i: number){
    //console.log(this.tableDataSource.data[i])
      let response =  this.dataService.deleteRole(this.tableDataSource.data[i].email, this.tableDataSource.data[i].line);
      this.tableDataSource.data.splice(i,1);
      this.tableDataSource._updateChangeSubscription()
      response.subscribe(data => {
        this.infoMessage = "Rimosso ruolo di amministrazione!";
        this._snackBar.open(this.infoMessage, '', {duration: 2000});
        this.infoMessage = ''
      }, error =>{
        this.infoMessage = "Non è stato possibile rimuovere il ruolo";
        this._snackBar.open(this.infoMessage, '', {duration: 2000});
        this.infoMessage = '';
      })

  }

}
