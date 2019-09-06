export class Child {

    name: string;
    surname: string;
    isPresent: boolean;
    registrationNumber: string
    constructor(name: string, surname: string, isPresent: boolean, registrationNumber: string) {
        this.name = name;
        this.surname = surname;
        this.isPresent = isPresent;
        this.registrationNumber = registrationNumber;
    }
}
