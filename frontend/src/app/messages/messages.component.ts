import {Component, OnInit} from '@angular/core';
import {Message} from "../model/message";
import {DataService} from "../services/data.service";
import {TitleService} from "../services/title.service";
import {NotificationService} from "../services/notification.service";

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements OnInit {

  constructor(private dataservice: DataService,  private titleservice: TitleService, private notificationService: NotificationService) { }
  messages: Message[];
  displayedColumns: string[] = ['data', 'messaggio', 'read', 'delete'];

  ngOnInit() {
    this.dataservice.getMessages(localStorage.getItem('current_user')).subscribe(m => {
      this.messages = m;
      console.log(this.messages)
    });
    this.titleservice.changeTitle('I miei messaggi')
  }

  readMessage(mess: Message): void{
      mess.read = true;
      this.dataservice.putMessages(this.messages, localStorage.getItem('current_user')).subscribe(
          d =>{
                  console.log(d)
                  this.notificationService.updateMessageCount(localStorage.getItem('current_user'))
          }
      )

  }

  deleteMessage(mess: Message): void{
    this.messages = this.messages.filter(a => (a != mess));
    this.dataservice.putMessages(this.messages, localStorage.getItem('current_user'));
  }



}
