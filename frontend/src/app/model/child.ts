export class Child {

    name: string;
    surname: string;
    registrationNumber: string;
    defaultLine: string;
    defaultStop: string;
    availableStops: string[];




    constructor(name: string, surname: string, registrationNumber: string, defaultLine: string, defaultStop: string, availableStops: string[]) {
        this.name = name;
        this.surname = surname;
        this.registrationNumber = registrationNumber;
        this.defaultLine = defaultLine;
        this.defaultStop = defaultStop;
        this.availableStops = availableStops;
    }
}
