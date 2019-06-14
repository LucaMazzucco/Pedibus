import { Stop } from './stop';

export class Ride {
    date: Date;
    stops: Stop[];
    stopsBack: Stop[];

    constructor(date: Date, stops: Stop[], stopsBack: Stop[]) {
        this.date = date;
        this.stops = stops;
        this.stopsBack = stopsBack;
    }
}
