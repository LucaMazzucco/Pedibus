import { Stop } from './stop';
import {Passenger} from "./passenger";

export class Ride {
    date: number;
    stops: Stop[];
    stopsBack: Stop[];
    notReserved: Passenger[];
    notReservedBack: Passenger[];

    constructor(date: number, stops: Stop[], stopsBack: Stop[], notReserved: Passenger[], notReservedBack: Passenger[]) {
        this.date = date;
        this.stops = stops;
        this.stopsBack = stopsBack;
        this.notReserved = notReserved;
        this.notReservedBack = notReservedBack;
    }
}
