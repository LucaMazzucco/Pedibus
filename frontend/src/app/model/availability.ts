export class Availability{
    email: string;
    lineName: string;
    rideDate: number;
    flagGoing: boolean;
    confirmed: boolean;
    constructor(email: string, lineName: string, rideDate: number, flagGoing: boolean){
        this.email = email;
        this.lineName = lineName;
        this.rideDate = rideDate;
        this.flagGoing = flagGoing;
        this.confirmed = false;
    }
}
