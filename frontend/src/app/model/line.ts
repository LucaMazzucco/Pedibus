import { Ride } from './ride';

export class Line {

    lineName: string;
    rides: Ride[];

    constructor(name: string, rides: Ride[]) {
        this.rides = rides;
        this.lineName = name;
    }
}
