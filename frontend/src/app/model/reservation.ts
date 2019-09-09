import {Stop} from "./stop";

export class Reservation {

    linename: string;
    rideDate: number;
    stopA: Stop;
    stopR: Stop;
    child: string;
    parent: string;


    constructor(linename: string, stopA: Stop, stopR: Stop, child: string, parent: string, flagGoing: boolean) {
        this.linename = linename;
        this.stopA = stopA;
        this.stopR = stopR
        this.child = child;
        this.parent = parent;
    }

}
