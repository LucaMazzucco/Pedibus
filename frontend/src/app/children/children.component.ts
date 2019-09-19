import {Component, OnDestroy, OnInit} from '@angular/core';
import {DataService} from "../services/data.service";
import {TitleService} from "../services/title.service";
import {MatDialog, MatDialogConfig, MatSnackBar} from "@angular/material";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Line} from "../model/line";
import {Child} from "../model/child";
import {filter, takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";


@Component({
  selector: 'app-children',
  templateUrl: './children.component.html',
  styleUrls: ['./children.component.scss']
})
export class ChildrenComponent implements OnInit, OnDestroy {

  children: Child[];
  isDisabled: Boolean[];
  stopOfLine: string[];
  lines: Line[];
  selectNameForm: FormGroup;
  selectDefaultLineForm: FormGroup;
  selectDefaultStopForm: FormGroup;
  editMode: boolean;
  private unsubscribe: Subject<void> = new Subject();
  infoMessage: string = '';



  constructor(private dataService: DataService, private _snackBar: MatSnackBar, private titleservice: TitleService, private dialog: MatDialog, private _formBuilder: FormBuilder) { }

  ngOnInit() {
    this.titleservice.changeTitle('I tuoi bambini');
    this.isDisabled = []
    this.editMode = false;
    this.dataService.getLines().pipe(takeUntil(this.unsubscribe)).subscribe(lines => this.lines=lines);
    this.dataService.getChildren(localStorage.getItem('current_user')).pipe(filter(result => !!result))
        .subscribe(result => {
          this.children = result
          this.initializeArray();
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


  updateStopOfLine(child: Child){
      this.dataService.getStopsAOfLine(child.defaultLine).subscribe(
          s => child.availableStops = s
      )
  }

  updateStopOfLines(children: Child[]){
    for(let c of children){
      this.dataService.getStopsOfLine(c.defaultLine).subscribe(
          r =>{
            this.stopOfLine = this.stopOfLine.concat(r);
            console.log(this.stopOfLine)
          }
      )
    }
  }

  openDialog(templateRef) {
    const dialogConfig = new MatDialogConfig();
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
    this.editMode = true;
  }


  addChild(): void{
    let ch = new Child(this.selectNameForm.controls.selectedName.value,
                        this.selectNameForm.controls.selectedSurname.value,
                        this.selectNameForm.controls.selectedSsn.value,
                        this.selectDefaultLineForm.controls.selectedDefaultLine.value.lineName,
                        this.selectDefaultStopForm.controls.selectedDefaultStop.value);
    let response =  this.dataService.addChild(localStorage.getItem('current_user'), ch);
    response.subscribe(data => {
      this.isDisabled.push(true);
      this.children.push(ch);
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
    this.dataService.deleteChildren(localStorage.getItem('current_user'),this.children[i]).subscribe(res => {
        this.children.splice(i,1);
        this.infoMessage = "Eliminato!";
        this._snackBar.open(this.infoMessage, '', {duration: 2000});
        this.infoMessage = ''
        },
      error =>{
        this.infoMessage = "Non è stato possibile elminare il bambino!";
        this._snackBar.open(this.infoMessage, '', {duration: 2000});
        this.infoMessage = '';
      })
  }

  saveChild(i): void{
    this.dataService.editChild(localStorage.getItem('current_user'), this.children[i]).subscribe(res =>{
      this.infoMessage = "Modifiche salvate!";
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = ''
      this.isDisabled[i] = true;
      this.editMode = false;
    },
    error =>{
      this.infoMessage = "Non è stato possibile salvare le modifiche";
      this._snackBar.open(this.infoMessage, '', {duration: 2000});
      this.infoMessage = ''
    })
  }

  updateStops(): void{
    console.log('Aggiorno le stop' + this.selectDefaultLineForm.controls.selectedDefaultLine.value.lineName);
    this.dataService.getStopsOfLine(this.selectDefaultLineForm.controls.selectedDefaultLine.value.lineName)
        .subscribe(s =>{
          this.stopOfLine = s;
        } );
  }

  ngOnDestroy(): void {
      this.unsubscribe.next();
      this.unsubscribe.complete();
  }

    // updateStopsOfLine(i): void{
  //   console.log(this.children[i].defaultLine)
  //     this.dataService.getStopsOfLine(this.children[i].defaultLine).subscribe( s => {
  //       this.stopOfLine = s;
  //       console.log(this.stopOfLine)
  //     })
  // }

}
