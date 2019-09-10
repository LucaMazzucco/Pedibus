import { Passenger } from './passenger';

export class Stop {

    stopName: string;
    time: number;
    children: Passenger[];

    constructor(name: string, time: number, children: Passenger[]) {
        this.stopName = name;
        this.time = time;
        this.children = children;
    }
}
