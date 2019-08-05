import { Component, OnInit } from '@angular/core';
import {Message} from "../classes/message";
import {DataService} from "../services/data.service";

@Component({
  selector: 'app-messages',
  templateUrl: './messages.component.html',
  styleUrls: ['./messages.component.scss']
})
export class MessagesComponent implements OnInit {

  constructor(private dataservice: DataService) { }
  messages: Message[];

  ngOnInit() {
    this.dataservice.getMessages().subscribe(m => this.messages = m);
  }

}
