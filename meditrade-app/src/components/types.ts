// src/types.ts

export interface Product {
  id: string;
  name: string;
  price: string;
  genre: string;
  origin: string;
  details: string;
  image: string;
  manufacturer: {
    name: string;
    info: string;
  };
}

export interface CartItem {
  id: string;
  name: string;
  price: string;
  quantity: number;
  image: string;
}
