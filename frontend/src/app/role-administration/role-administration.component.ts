import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { User } from '../model/user';
import { Line } from '../model/line';
import {DataService} from '../services/data.service';
import {TitleService} from '../services/title.service';
import {MatTable, MatDialog, MatDialogConfig, MatSnackBar, MatTableDataSource} from "@angular/material";
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-role-administration',
  templateUrl: './role-administration.component.html',
  styleUrls: ['./role-administration.component.scss']
})

export class RoleAdministrationComponent implements OnInit {
  tableDataSource: MatTableDataSource<User> = new MatTableDataSource<User>([]);
  @ViewChild(MatTable) table: MatTable<any>;
  infoMessage: string = '';
  displayedColumns: string[] = ['email', 'role', 'line', 'delete'];
  lines: Line[];
  selectLineForm: FormGroup;

  //dataSource = new BehaviorSubject<MatTableDataSource<User>>(this.tableDataSource);

  indexLine: number;

  constructor(private dataService: DataService, private titleService: TitleService, private _snackBar: MatSnackBar, private _formBuilder: FormBuilder,private changeDetectorRefs: ChangeDetectorRef, private dialog: MatDialog) { }

  ngOnInit() {
    this.titleService.changeTitle("Gestione amministratori");
    this.dataService.getRoles().subscribe(user => {
      console.log(user);
      this.tableDataSource.data = user;      
    });

    this.selectLineForm = this._formBuilder.group({
      selectedLine: ['', Validators.required]
    });
  }

  deleteRole(i: number){
    //console.log(this.tableDataSource.data[i])
      let response =  this.dataService.deleteRole(this.tableDataSource.data[i].email, this.tableDataSource.data[i].line);
      this.tableDataSource.data.splice(i,1);
      this.tableDataSource._updateChangeSubscription();
      response.subscribe(data => {
        this.infoMessage = "Rimosso ruolo di amministrazione!";
        this._snackBar.open(this.infoMessage, '', {duration: 2000});
        this.infoMessage = '';
        this.tableDataSource.data.push(data);
        this.table.renderRows();
      }, error =>{
        this.infoMessage = "Non è stato possibile rimuovere il ruolo";
        this._snackBar.open(this.infoMessage, '', {duration: 2000});
        this.infoMessage = '';
      })

  }

  openDialog(templateRef, i:number) {
    const dialogConfig = new MatDialogConfig();
    this.indexLine = i;
    this.dataService.getNoAdminLines().subscribe(lines => {
      this.lines = lines;
    });
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

  addRole(){
    //let userDelete = this.tableDataSource.data[this.indexLine]
    let email = this.tableDataSource.data[this.indexLine].email;
    let line = this.selectLineForm.controls.selectedLine.value.lineName
    this.tableDataSource.data.splice(this.indexLine, 1);
    this.tableDataSource._updateChangeSubscription()
    this.dataService.addRole(email, line).subscribe(data => {
      this.infoMessage = "Aggiunto ruolo di amministrazione!";
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = '';
      this.tableDataSource.data.push(data);
      this.table.renderRows();
    }, error => {
      this.infoMessage = "Non è stato possibile aggiungere il ruolo";
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = '';
    });
  }

}
