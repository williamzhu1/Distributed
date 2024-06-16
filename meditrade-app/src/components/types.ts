// src/types.ts

export interface Product {
      id: string;
      name: string;
      price: number;
      category: string;
      description: string;
      manufacturer: string;
      stock: number;
      image: string | null;
      supplierId: string;
      companyName?: string;

}

// src/types.ts

export interface CartItem {
  id: string;
  name: string;
  price: string | number;
  quantity: number;
  image: string;
}


