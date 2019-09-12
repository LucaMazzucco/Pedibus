export class User {

    email: string;
    role: string;
    line: string;

    constructor(email: string, role: string, line: string) {
        this.email = email;
        this.role = role;
        this.line = line;
    }
}