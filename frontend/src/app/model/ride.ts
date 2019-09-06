import { Stop } from './stop';
import {Child} from "./child";

export class Ride {
    date: number;
    stops: Stop[];
    stopsBack: Stop[];
    notReserved: Child[];
    notReservedBack: Child[];

    constructor(date: number, stops: Stop[], stopsBack: Stop[], notReserved: Child[], notReservedBack: Child[]) {
        this.date = date;
        this.stops = stops;
        this.stopsBack = stopsBack;
        this.notReserved = notReserved;
        this.notReservedBack = notReservedBack;
    }
}
