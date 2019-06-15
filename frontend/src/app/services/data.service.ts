import { Injectable } from '@angular/core';
/*import { Person } from '../classes/person';
import { Stop } from '../classes/stop';
import { Ride } from '../classes/ride';
import { PEOPLE_DATA } from '../data/people_data';
import { STOPS_DATA } from '../data/stops_data';
import { RIDES_DATA } from '../data/rides_data';*/
import { Line } from '../classes/line';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Ride } from '../classes/ride';
import { first } from 'rxjs/operators';
const REST_URL = 'http://localhost:8080';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient) { }

  getLines(): Observable<Line[]> {
    return this.http.get<Line[]>(REST_URL + '/getLines')
  }

  putLineAttendance(){

  }

  putRideAttendance(ride: Ride, lineName: String){
    let date = ride.date.toDateString;
    return this.http.put<Ride>(REST_URL + "/putLineAttendance/" + lineName + "/ride", {ride}).pipe(first()).subscribe(data => console.log(data));
  }

}
