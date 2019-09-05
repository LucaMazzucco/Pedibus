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

  displayedColumns: string[] = ['email', 'role', 'line', 'delete'];

  constructor(private dataService: DataService, private titleService: TitleService) { }

  ngOnInit() {
    this.titleService.changeTitle("Gestione amministratori");
    this.dataService.getRoles().subscribe(user => {
      user["line"] = user["line"][0];
      var roles = user["role"];
      if(roles.includes("Amministratore")){
        user["role"] = "Amministatore";
      } else {
        user["role"] = "Accompagnatore";
      }
      this.tableDataSource.data = user;
      console.log(user);
    });
  }

}
