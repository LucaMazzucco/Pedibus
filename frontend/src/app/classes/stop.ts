import { Person } from './person';

export class Stop {

    stopName: string;
    time: Date;
    children: Person[];

    constructor(name: string, time: Date, children: Person[]) {
        this.stopName = name;
        this.time = time;
        this.children = children;
    }
}
