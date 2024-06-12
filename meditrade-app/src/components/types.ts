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

// src/types.ts

export interface CartItem {
  id: string;
  name: string;
  price: string | number;
  quantity: number;
  image: string;
}


