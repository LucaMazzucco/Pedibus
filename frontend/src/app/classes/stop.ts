import { Person } from './person';

export class Stop {

    stopName: string;
    time: Date;
    people: Person[];

    constructor(name: string, time: Date, people: Person[]) {
        this.stopName = name;
        this.time = time;
        this.people = people;
    }
}
