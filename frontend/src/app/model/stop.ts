import { Child } from './child';

export class Stop {

    stopName: string;
    time: number;
    children: Child[];

    constructor(name: string, time: number, children: Child[]) {
        this.stopName = name;
        this.time = time;
        this.children = children;
    }
}
