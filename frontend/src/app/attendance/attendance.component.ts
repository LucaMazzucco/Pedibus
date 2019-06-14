import { Component, OnInit, OnDestroy } from '@angular/core';
import { Person } from '../classes/person';
import { Line } from '../classes/line';
import { Ride } from '../classes/ride';
import { DataService } from '../services/data.service';
import { PageEvent } from '@angular/material';
import {Stop} from '../classes/stop';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';



@Component({
  selector: 'app-card',
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.scss']
})
export class AttendanceComponent implements OnInit, OnDestroy {

  constructor(private dataService: DataService,private dialog: MatDialog) { }

  lines: Line[];

  totalSize: number;
  currentPage: number;
  currentLine: Line;
  dataSource: any;
  subscription: any;

  ngOnInit() {
    this.getLines();

  }

  updateLines(lines : Line[]){
    this.lines = lines;
    this.currentLine = this.lines[0];
    this.currentPage = this.binarySearch(this.currentLine.rides,new Date(),0,this.currentLine.rides.length-1,0);
    this.dataSource = this.currentLine.rides[this.currentPage];
    this.dataSource.stops.sort((a, b) => a.time as any - (b.time as any));
    this.dataSource.stopsBack.sort((a, b) => a.time as any - (b.time as any));
    this.totalSize = this.currentLine.rides.length;
    //console.log(this.dataSource[]);
  }
  ngOnDestroy(){
    this.subscription.unsubscribe();
  }

  addPerson(person: Person) {
    if(person.isPresent) {
      person.isPresent = false;
    } else {
      person.isPresent = true;
    }
  }

  isPresent(person: Person): boolean {
    return person.isPresent;
  }

  getLines(): void {
    this.subscription = this.dataService.getLines().subscribe(lines => this.updateLines(lines));
    /*this.lines.sort((a,b)=> a.lineName.localeCompare(b.lineName))
      .forEach(l=>l.rides.sort((a,b)=>a.date as any-(b.date as any)));*/
  }

  handlePage(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.dataSource = this.currentLine.rides[this.currentPage];
    this.dataSource.stops.sort((a, b) => a.time as any - (b.time as any));
    this.dataSource.stopsBack.sort((a, b) => a.time as any - (b.time as any));
    this.totalSize = this.currentLine.rides.length;
  }

  private parseDate(date: any): Date {
    let s = date.split('/');
    return new Date(s[2], (s[1]-1), s[0], 0, 0, 0)
  }

  private binarySearch(arr: Ride[], x: Date, start: number, end: number, prev: number): number {
    if (start > end) { // if no exact match found return the closest ride in the future
      return arr.length > prev+1 ? prev+1 : prev;
    }
    const mid=Math.floor((start + end)/2);
    console.log('Tipo della dataaaaaa:' + arr[mid].date);

    let d = this.parseDate(arr[mid].date);
    console.log('Tipo della dataaaaaa doppoooooo:' + d);
    if (d.getDate() ===x.getDate()) { return mid; }

    if(d as any - (x as any)> 0) {
      return this.binarySearch(arr, x, start, mid-1, mid);
    } else {
      return this.binarySearch(arr, x, mid+1, end, mid);
    }
  }

  handleLine(line: Line) {
    this.currentLine = this.lines[this.lines.indexOf(line)];
    this.currentPage = this.binarySearch(this.currentLine.rides,new Date(),0,this.currentLine.rides.length-1,0);
    this.dataSource = this.currentLine.rides[this.currentPage];
    this.dataSource.stops.sort((a, b) => a.time as any - (b.time as any));
    this.dataSource.stopsBack.sort((a, b) => a.time as any - (b.time as any));
    this.totalSize = this.currentLine.rides.length;
  }

  stopIsFull(stop: Stop): boolean {
    let i: number;
    for(i=0;i<stop.people.length;i++) {
      if(!this.isPresent(stop.people[i])) {
        return false;
      }
    }
    return true;
  }

  openDialog(templateRef) {
    let dialogRef = this.dialog.open(templateRef, {
        width: '250px',
        // data: { name: this.name, animal: this.animal }
    });

    dialogRef.afterClosed().subscribe(result => {
        console.log('The dialog was closed');
        // this.animal = result;
    });
}
}

// @Component({
//   selector: 'bottom-sheet-overview-example-sheet',
//   templateUrl: 'bottom-sheet-overview-example-sheet.html',
// })
// export class NotReservedPeopleModal {
//   constructor(private _bottomSheetRef: MatBottomSheetRef<NotReservedPeopleModal>) {}

//   openLink(event: MouseEvent): void {
//     this._bottomSheetRef.dismiss();
//     event.preventDefault();
//   }
// }
