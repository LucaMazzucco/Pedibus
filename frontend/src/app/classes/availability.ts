export class Availability{
    email: string;
    lineName: string;
    rideDate: Date;
    flagGoing: boolean;
    constructor(email: string, lineName: string, rideDate: Date, flagGoing: boolean){
        this.email = email;
        this.lineName = lineName;
        this.rideDate = rideDate;
        this.flagGoing = flagGoing;
    }
}
