// src/components/buyer/CartItem.ts or src/types.ts
export interface CartItem {
  id: string;
  name: string;
  price: string;
  quantity: number;
  image: string;
  supplier: string; // Ensure to include all necessary fields
}
