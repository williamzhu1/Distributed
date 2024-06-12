class Order{
    id: number;
    orderDate: Date;
    orderStatus: string;
    items: number[]; //id
    constructor(id: number, orderDate: Date, orderStatus: string, orderTotal: number, orderItems: number[]){
        this.id = id;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.items = orderItems;
    }

    
}