import { Component, OnInit } from '@angular/core';
import {Message} from "../classes/message";
import {DataService} from "../services/data.service";
import {TitleService} from "../services/title.service";

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements OnInit {

  constructor(private dataservice: DataService,  private titleservice: TitleService) { }
  messages: Message[];
  displayedColumns: string[] = ['data', 'messaggio', 'read'];



  ngOnInit() {
    this.dataservice.getMessages(localStorage.getItem('current_user')).subscribe(m => {
      this.messages = m;
      console.log(this.messages)
    });
    this.titleservice.changeTitle('I miei messaggi')
  }

  readMessage(mess: Message): void{
      mess.read = true;
      this.dataservice.putMessages(this.messages, localStorage.getItem('current_user'));
  }

}
