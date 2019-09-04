import { Person } from './person';

export class Stop {

    stopName: string;
    time: number;
    children: Person[];

    constructor(name: string, time: number, children: Person[]) {
        this.stopName = name;
        this.time = time;
        this.children = children;
    }
}
