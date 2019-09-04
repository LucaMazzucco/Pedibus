export class Message {

    timestamp: number;
    read: Boolean;
    body: String;

    constructor(timestamp: number, read: Boolean, body: String) {
        this.timestamp = timestamp;
        this.read = read;
        this.body = body;
    }
}
