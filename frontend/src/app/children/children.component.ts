import { Component, OnInit } from '@angular/core';
import {DataService} from "../services/data.service";
import {TitleService} from "../services/title.service";
import {MatDialog, MatDialogConfig, MatSnackBar} from "@angular/material";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Line} from "../model/line";
import {Child} from "../model/child";
import {filter} from "rxjs/operators";

@Component({
  selector: 'app-children',
  templateUrl: './children.component.html',
  styleUrls: ['./children.component.scss']
})
export class ChildrenComponent implements OnInit {

  children: Child[];
  isDisabled: Boolean[];
  stopOfLine: string[];
  lines: Line[];
  selectNameForm: FormGroup;
  selectDefaultLineForm: FormGroup;
  selectDefaultStopForm: FormGroup;
  isLinear: boolean;
  infoMessage: string = '';



  constructor(private dataService: DataService, private _snackBar: MatSnackBar, private titleservice: TitleService, private dialog: MatDialog, private _formBuilder: FormBuilder) { }

  ngOnInit() {
    this.titleservice.changeTitle('I tuoi bambini');
    this.isDisabled = []
    this.dataService.getChildren(localStorage.getItem('current_user')).pipe(filter(result => !!result))
        .subscribe(result => {
          this.children = result
          console.log(this.children)
        },
        error => {
          console.log('Sono in errore')
          console.log(error)
          this.children = []
        }
    )
    this.selectNameForm = this._formBuilder.group({
      selectedName: ['', Validators.required],
      selectedSurname: ['', Validators.required],
      selectedSsn: ['', Validators.required]
     })
    this.selectDefaultLineForm = this._formBuilder.group({
      selectedDefaultLine: ['', Validators.required]
    });
    this.selectDefaultStopForm = this._formBuilder.group({
      selectedDefaultStop: ['', Validators.required]
    })
  }

  initializeArray(){
    for(let i = 0; i < this.children.length; i++){
      this.isDisabled.push(true);
    }
  }

  openDialog(templateRef) {
    const dialogConfig = new MatDialogConfig();
    this.dataService.getLines().subscribe(lines => this.lines=lines);
    let dialogRef = this.dialog.open(templateRef, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }

  enableEditing(index): void{
    if(this.isDisabled[index]){
      this.isDisabled[index] = false;
    }
    else {
      this.isDisabled[index] = true;
    }
  }


  addChild(): void{
    console.log('Sono in addChild');
    this.isDisabled.push(true);
    let ch = new Child(this.selectNameForm.controls.selectedName.value,
                        this.selectNameForm.controls.selectedSurname.value,
                        this.selectNameForm.controls.selectedSsn.value,
                        this.selectDefaultLineForm.controls.selectedDefaultLine.value.lineName,
                        this.selectDefaultStopForm.controls.selectedDefaultStop.value);
    console.log(ch)
    this.children.push(ch);
    let response =  this.dataService.addChild(localStorage.getItem('current_user'), ch);
    response.subscribe(data => {
      this.infoMessage = "Aggiunto!";
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = ''
    }, error =>{
      this.infoMessage = "Non è stato possibile aggiungere il bambino!";
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = '';
    })
  }
  deleteChild(i): void{
    this.children.splice(i,1);
    //TODO: chiamata al DB
  }

  updateStops(): void{
    console.log('Aggiorno le stop' + this.selectDefaultLineForm.controls.selectedDefaultLine.value.lineName);
    this.dataService.getStopsOfLine(this.selectDefaultLineForm.controls.selectedDefaultLine.value.lineName)
        .subscribe(s =>{
          this.stopOfLine = s;
        } );
  }
}
