import { Injectable } from '@angular/core';
import { Line } from '../model/line';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Ride } from '../model/ride';
import { User } from '../model/user';
import {catchError, first} from 'rxjs/operators';
import { Passenger } from '../model/passenger';
import { Reservation } from '../model/reservation';
import {Message} from '../model/message';
import {Shift} from '../model/shift';
import {Child} from '../model/child';

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
    return this.http.get<Line[]>(REST_URL + '/line/getLines');
  }

  putLineAttendance(line: Line) {
    // tslint:disable-next-line:max-line-length
    return this.http.put<Line>(REST_URL + '/line/putLineAttendance/' + line.lineName, {line}).pipe(first()).subscribe(data => console.log(data));
  }

  putRideAttendance(ride: Ride, lineName: string) {
    // tslint:disable-next-line:max-line-length
    return this.http.put<Ride>(REST_URL + '/line/putLineAttendance/' + lineName + '/ride', {ride}).pipe(first()).subscribe(data => console.log(data));
  }

  putPersonAttendance(rideDate: Date, lineName: string, isBack: boolean, person: Passenger) {
    // tslint:disable-next-line:max-line-length
    return this.http.put(REST_URL + '/line/putLineAttendance/' + lineName + '/ride/user', {rideDate, isBack, person}).pipe(first()).subscribe(data => console.log(data));
  }

  getReservations(ssn: string): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(REST_URL + '/children/getChildReservations/' + ssn);
  }

  getMessages(email: string): Observable<Message[]> {
    return this.http.get<Message[]>(REST_URL +  '/user/' + email + '/messages');
  }

  putMessages(messages: Message[], email: string) {
      return this.http.put(REST_URL + '/user/' + email + '/messages', messages).pipe(first()).subscribe(data => console.log(data));
  }

  getAvailabilities(email: string): Observable<Shift[]> {
    return this.http.get<Shift[]>(REST_URL + '/shift/getAvailabilities/' + email);
  }

  addAvailability(av: Shift) {
    return this.http.post<Shift>(REST_URL + '/shift/addAvailability', av);
  }

  deleteAvailability(av: Shift) {
    return this.http.post<Shift>(REST_URL + '/shift/deleteAvailability', av);
  }

  getNoAdminLines(): Observable<Line[]> {
    return this.http.get<Line[]>(REST_URL + '/line/getNoAdminLines');
  }

  getShifts(email: string): Observable<Shift[]> {
    return this.http.get<Shift[]>(REST_URL + '/shift/getShifts/' + email);
  }

  confirmShiftAdmin(sh: Shift) {
    return this.http.post<Shift>(REST_URL + '/shift/confirm1', sh);
  }

  confirmShiftConductor(sh: Shift) {
    return this.http.post<Shift>(REST_URL + 'shift/confirm2', sh);
  }

  // deleteShift(sh: Shift) {
  //   return this.http.post<Shift>(REST_URL + '/shift/deleteShift', sh);
  // }

  getChildren(email: string): Observable<Child[]> {
    return this.http.get<Child[]>(REST_URL + '/user/getChildren/' + email);
  }

  addChild(email: string, ch: Child) {
    return this.http.post<Child>(REST_URL + '/user/addChild/' + email, ch);
  }

  getStopsOfLine(lineName: string) {
    return this.http.get<string[]>(REST_URL + '/line/getStopNames/' + lineName);
  }
  getRoles(): Observable<User[]> {
    return this.http.get<User[]>(REST_URL + '/user/getUsersRoles');
  }

  addRole(email: string, line: string) {
    return this.http.post<User>(REST_URL + '/user/addRole', {email, line});
  }

  deleteRole(email: string, line: string) {
    return this.http.post<User>(REST_URL + '/user/deleteRole', {email, line});
  }

  deleteChildren(email: string, ch: Child) {
    return this.http.post<Child>(REST_URL + '/user/deleteChild/' + email, ch);
  }

  editChild(email: string, ch: Child) {
    return this.http.put<Child>(REST_URL + '/user/editChild/' + email, ch);
  }

  addChildReservation(ssn: string, r: Reservation) {
    return this.http.post<Reservation>(REST_URL + '/children/addChildReservation/' + ssn, r);
  }

  getChildReservation(ssn: string): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(REST_URL + '/children/getChildReservations/' + ssn);
  }

  editChildReservation(ssn: string, r: Reservation) {
    return this.http.put<Reservation>(REST_URL + '/children/editChildReservation/' + ssn, r);
  }

  deleteChildReservation(ssn: string, r: Reservation) {
    return this.http.post<Reservation>(REST_URL + '/deleteChildReservation/' + ssn, r);
  }

}
