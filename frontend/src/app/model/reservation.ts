import {Stop} from "./stop";

export class Reservation {

    linename: string;
    date: Date;
    stopsA: Stop[];
    selectedA: Stop;
    stopsR: Stop[];
    selectedR: Stop;

    constructor(linename: string, date: Date, stopsA: Stop[], stopsR: Stop[], selectedA: Stop, selectedR: Stop) {
        this.linename = linename;
        this.date = date;
        this.stopsA = stopsA;
        this.stopsR = stopsR;
        this.selectedA = selectedA;
        this.selectedR = selectedR;
    }

}
