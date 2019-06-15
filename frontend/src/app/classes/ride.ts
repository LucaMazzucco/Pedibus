import { Stop } from './stop';
import {Person} from "./person";

export class Ride {
    date: Date;
    stops: Stop[];
    stopsBack: Stop[];
    notReserved: Person[];
    notReservedBack: Person[];

    constructor(date: Date, stops: Stop[], stopsBack: Stop[], notReserved: Person[], notReservedBack: Person[]) {
        this.date = date;
        this.stops = stops;
        this.stopsBack = stopsBack;
        this.notReserved = notReserved;
        this.notReservedBack = notReservedBack;
    }
}
