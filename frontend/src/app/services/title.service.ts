import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TitleService {
  private titleSource = new BehaviorSubject('Raggiungi la scuola più facilmente!');
  currentTitle = this.titleSource.asObservable();

  constructor() { }

  public changeTitle(title: string){
    this.titleSource.next(title);
  }



}
