import {Stop} from "./stop";

export class Reservation {

    linename: string;
    date: Date;
    stopsA: Stop[];
    stopsR: Stop[];

    constructor(linename: string, date: Date, stopsA: Stop[], stopsR: Stop[]) {
        this.linename = linename;
        this.date = date;
        this.stopsA = stopsA;
        this.stopsR = stopsR;
    }

}
