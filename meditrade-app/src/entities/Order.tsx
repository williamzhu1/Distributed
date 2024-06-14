// Define a class to represent the structure of your JSON object
export class Order {
    id: string;
    masterId: string;
    address: string;
    items: {[key: number]: number};
    status: string;

    constructor(id: string, masterId: string, address: string, items: {[key: string]: number}, status: string) {
        this.id = id;
        this.masterId = masterId;
        this.address = address;
        this.items = items;
        this.status = status;
    }
}
