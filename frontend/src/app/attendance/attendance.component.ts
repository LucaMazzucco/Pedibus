import {Component, OnInit, OnDestroy, Input} from '@angular/core';
import { Passenger } from '../model/passenger';
import { Line } from '../model/line';
import { Ride } from '../model/ride';
import { DataService } from '../services/data.service';
import {MatDialogConfig, MatTabChangeEvent, MatTableDataSource, PageEvent} from '@angular/material';
import {Stop} from '../model/stop';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {SelectionModel} from "@angular/cdk/collections";
import {TitleService} from "../services/title.service";


declare var ol: any;
@Component({
  selector: 'app-card',
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.scss']
})
export class AttendanceComponent implements OnInit, OnDestroy {

  constructor(private dataService: DataService,private dialog: MatDialog, private titleservice: TitleService) { }
  lines: Line[];
  stopIndex: number;
  totalSize: number;
  currentPage: number;
  currentLine: Line;
  dataSource: any;
  subscription: any;
  isBackTab: boolean;
  // stopsOfLineA: Stop[];
  // stopsOfLineR: Stop[];
  tableDatasource: any;
  latitude: number = 45.06370;
  longitude: number = 7.65940;
  displayedColumns: string[] = ['seleziona', 'nome', 'cognome'];
  selection = new SelectionModel<Passenger>(true, []);
  map: any;

  ngOnInit() {

    this.initMap();
    this.titleservice.changeTitle('Registro presenze');
    this.getLines();
  }

  initMap(){
    this.map = new ol.Map({
      target: 'map',
      layers: [
        new ol.layer.Tile({
          source: new ol.source.OSM()
        })
      ],
      view: new ol.View({
        center: ol.proj.fromLonLat([45.06370, 7.65940]),
        zoom: 8
      })
    });
    this.setCenter();
    var container = document.getElementById('popup');
    var content = document.getElementById('popup-content');
    var closer = document.getElementById('popup-closer');

    var overlay = new ol.Overlay({
      element: container,
      autoPan: true,
      autoPanAnimation: {
        duration: 250
      }
    });
    this.map.addOverlay(overlay);

    closer.onclick = function() {
      overlay.setPosition(undefined);
      closer.blur();
      return false;
    };

    this.map.on('singleclick', function (event) {
      console.log('Click')
      if (this.map.hasFeatureAtPixel(event.pixel) === true) {
        var coordinate = event.coordinate;
        content.innerHTML = '<b>Hello world!</b><br />I am a popup.';
        overlay.setPosition(coordinate);
      } else {
        overlay.setPosition(undefined);
        closer.blur();
      }
    });
  }

  setCenter() {
    var view = this.map.getView();
    view.setCenter(ol.proj.fromLonLat([this.longitude, this.latitude]));
    view.setZoom(15);
  }
  updateLines(lines : Line[]){
    this.lines = lines;
    this.currentLine = this.lines[0];
    this.currentLine.rides.forEach(s =>{
      s.stops.forEach(s => {
        this.add_marker(s.lat, s.lng);
      })
    })
    this.currentPage = this.binarySearch(this.currentLine.rides,new Date(),0,this.currentLine.rides.length-1,0);
    this.dataSource = this.currentLine.rides[this.currentPage];
    this.dataSource.stops.sort((a, b) => a.time as any - (b.time as any));
    this.dataSource.stopsBack.sort((a, b) => a.time as any - (b.time as any));
    this.totalSize = this.currentLine.rides.length;
  }

  add_marker(lat, lng) {
    //this.markers.push(new Marker(lat, lng))
    var vectorLayer = new ol.layer.Vector({
      source: new ol.source.Vector({
        features: [new ol.Feature({
          geometry: new ol.geom.Point(ol.proj.transform([parseFloat(lng), parseFloat(lat)], 'EPSG:4326', 'EPSG:3857')),
        })]
      }),
      style: new ol.style.Style({
        image: new ol.style.Icon({
          anchor: [0.5, 0.5],
          anchorXUnits: "fraction",
          anchorYUnits: "fraction",
          src: "https://i.ibb.co/g6gk5Dm/678111-map-marker-512.png"
        })
      })
    });

    this.map.addLayer(vectorLayer);
  }
  ngOnDestroy(){
    this.subscription.unsubscribe();
  }

  addPerson(person: Passenger) {

    if(person.isPresent) {
      person.isPresent = false;
    } else {
      person.isPresent = true;
    }
    this.dataService.putPersonAttendance(this.dataSource.date, this.currentLine.lineName, this.isBackTab, person)
  }

  isPresent(person: Passenger): boolean {
    return person.isPresent;
  }

  getLines(): void {
    this.subscription = this.dataService.getLines().subscribe(lines => this.updateLines(lines));
  }

  handlePage(event: PageEvent) {
    this.currentPage = event.pageIndex;
    this.dataSource = this.currentLine.rides[this.currentPage];
    this.dataSource.stops.sort((a, b) => a.time as any - (b.time as any));
    this.dataSource.stopsBack.sort((a, b) => a.time as any - (b.time as any));
    this.totalSize = this.currentLine.rides.length;
  }

  setStopIndex(i: number){
    this.stopIndex = i;
  }

  addReservation(): void {
    for (let person of this.selection.selected){
      person.isPresent = true;
      if(this.isBackTab){
        console.log('Persona al ritorno'+ person.name)
        console.log(this.stopIndex)
        this.dataSource.stopsBack[this.stopIndex].children.push(person)
        this.dataSource.notReservedBack = this.dataSource.notReservedBack.filter(obj => obj !== person);
      }
      else{
        this.dataSource.stops[this.stopIndex].children.push(person)
        this.dataSource.notReserved = this.dataSource.notReserved.filter(obj => obj !== person);
      }
      this.selection.clear();
      //this.dataService.putLineAttendance(this.currentLine);
      this.dataService.putRideAttendance(this.dataSource, this.currentLine.lineName);
    }
  }


  // allReserved(): Boolean{
  //   if(this.isBackTab){
  //     for (let person of this.selectionBack.selected){
  //       return true;
  //     }
  //   } else {
  //     for (let person of this.selection.selected){
  //      return true;
  //     }
  //   }
  //   return false;
  // }
  private binarySearch(arr: Ride[], x: Date, start: number, end: number, prev: number): number {
    if (start > end) { // if no exact match found return the closest ride in the future
      return arr.length > prev+1 ? prev+1 : prev;
    }
    const mid=Math.floor((start + end)/2);

    let d = new Date(arr[mid].date)
    if (d.getDate() ===x.getDate()) { return mid; }

    if(d as any - (x as any)> 0) {
      return this.binarySearch(arr, x, start, mid-1, mid);
    } else {
      return this.binarySearch(arr, x, mid+1, end, mid);
    }
  }

  tabChanged = (tabChangeEvent: MatTabChangeEvent): void => {
    if(tabChangeEvent.index == 1){
      this.isBackTab = true;
    }
    else{
      this.isBackTab = false;
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
    for(i=0;i<stop.children.length;i++) {
      if(!this.isPresent(stop.children[i])) {
        return false;
      }
    }
    return true;
  }

  openDialog(templateRef) {
    const dialogConfig = new MatDialogConfig();
    if(this.isBackTab){
      this.tableDatasource = new MatTableDataSource(this.dataSource.notReservedBack);
    }
    else{
      this.tableDatasource = new MatTableDataSource(this.dataSource.notReserved)
    }
    let dialogRef = this.dialog.open(templateRef, {
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
    });
  }
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.tableDatasource.data.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
        this.selection.clear() :
        this.tableDatasource.data.forEach(row => this.selection.select(row));
  }
}
