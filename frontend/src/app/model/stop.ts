import { Passenger } from './passenger';
import {GeoJSON} from "geojson";
import {log} from "util";

export class Stop {

    stopName: string;
    time: number;
    children: Passenger[];
    lat: number;
    lng: number;

    constructor(name: string, time: number, children: Passenger[], lat: number, lng: number) {
        this.stopName = name;
        this.time = time;
        this.children = children;
        this.lat = lat;
        this.lng = lng;
    }
}
