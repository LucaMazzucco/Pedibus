import { Injectable } from '@angular/core';
import { Line } from '../model/line';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Ride } from '../model/ride';
import { User } from '../model/user';
import {catchError, first} from 'rxjs/operators';
import { Person } from '../model/person';
import { Reservation } from "../model/reservation";
import {Message} from "../model/message";
import {Availability} from "../model/availability";
import {Shift} from "../model/shift";

const REST_URL = 'http://localhost:8080';
const FAKE_URL = 'http://localhost:3000';

interface AdminRole {
  email: string;
  line: string;
}

@Injectable({
  providedIn: 'root'
})
export class DataService {

  constructor(private http: HttpClient) { }

  getLines(): Observable<Line[]> {
    return this.http.get<Line[]>(REST_URL + '/getLines')
  }

  putLineAttendance(line: Line){
    return this.http.put<Line>(REST_URL + "/putLineAttendance/" + line.lineName, {line}).pipe(first()).subscribe(data => console.log(data));
  }

  putRideAttendance(ride: Ride, lineName: String){
    return this.http.put<Ride>(REST_URL + "/putLineAttendance/" + lineName + "/ride", {ride}).pipe(first()).subscribe(data => console.log(data));
  }

  putPersonAttendance(rideDate: Date, lineName: String, isBack: boolean, person: Person){
    return this.http.put(REST_URL + "/putLineAttendance/" + lineName + "/ride/user", {rideDate, isBack, person}).pipe(first()).subscribe(data => console.log(data))
  }

  //TODO: Da implementare Backend

  getReservations(): Observable<Reservation[]>{
    return this.http.get<Reservation[]>(FAKE_URL + '/reservations');
  }

  getMessages(email: String): Observable<Message[]>{
    return this.http.get<Message[]>(REST_URL +  '/' + email + '/messages')
  }

  putMessages(messages: Message[], email: String){
      return this.http.put(REST_URL + '/' + email + '/messages', messages).pipe(first()).subscribe(data => console.log(data));
  }

  getAvailabilities(email: String): Observable<Availability[]>{
    return this.http.get<Availability[]>(REST_URL + '/getAvailabilities/' + email);
  }

  addAvailability(av: Availability){
    return this.http.post<Availability>(REST_URL + '/addAvailability', av);
  }

  deleteAvailability(av: Availability){
    return this.http.post<Availability>(REST_URL + '/deleteAvailability', av);
  }

  getNoAdminLines(): Observable<Line[]>{
    return this.http.get<Line[]>(REST_URL + "/getNoAdminLines");
  }

  getShifts(email: String): Observable<Shift[]>{
    return this.http.get<Shift[]>(REST_URL + '/getShifts/' + email);
  }

  addShift(sh: Shift){
    return this.http.post<Shift>(REST_URL + '/addShift', sh);
  }

  deleteShift(sh: Shift){
    return this.http.post<Shift>(REST_URL + '/deleteShift', sh);
  }

  getRoles(): Observable<User[]>{
    return this.http.get<User[]>(REST_URL + '/getUsersRoles');
  }

  addRole(email: String, line: String){
    return this.http.post<User>(REST_URL + '/addRole', {email, line});
  }

  deleteRole(email: String, line: String){
    return this.http.post<User>(REST_URL + '/deleteRole', {email, line});
  }


}
