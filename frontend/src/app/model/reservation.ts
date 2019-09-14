import {Stop} from "./stop";

export class Reservation {

    lineName: string;
    rideDate: number;
    stopA: Stop;
    stopR: Stop;
    child: string;
    parent: string;


    constructor(lineName: string, stopA: Stop, stopR: Stop, rideDate: number, child: string, parent: string) {
        this.lineName = lineName;
        this.stopA = stopA;
        this.stopR = stopR;
        this.rideDate = rideDate;
        this.child = child;
        this.parent = parent;
    }

}
