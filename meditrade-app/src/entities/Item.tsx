class Item{
    id: number;
    name: string;
    description: string;
    price: number;
    stock: number;
    category: string;
    image: string;
    constructor(id: number, name: string, description: string, price: number, stock: number, category: string, image: string){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.image = image;
    }
}