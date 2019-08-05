export class Message {

    timestamp: Date;
    read: Boolean;
    body: String;

    constructor(timestamp: Date, read: Boolean, body: String) {
        this.timestamp = timestamp;
        this.read = read;
        this.body = body;
    }
}
