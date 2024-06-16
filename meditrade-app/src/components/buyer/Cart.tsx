// src/components/buyer/Cart.tsx
import React, { useState, useEffect } from "react";
import { CartItem } from "./CartItem"; // Adjust this import according to your project structure
import ordersImg from "../../assets/images/orders.jpeg";
import { parsePrice } from "../utils/utils"; // Adjust this import according to your project structure
import "./cart.css";

interface CartProps {
  user: any;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "orderConfirmation" | "viewOrders") => void;
  onLogout: () => void;
  onProceedToOrder: (cartItems: CartItem[], total: number) => void;
}

const Cart: React.FC<CartProps> = ({ user, onSwitchMode, onLogout, onProceedToOrder }) => {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);

  useEffect(() => {
    const storedCartItems = JSON.parse(localStorage.getItem("cartItems") || "[]");
    setCartItems(storedCartItems);
  }, []);

  const calculateTotal = () => {
    return cartItems.reduce((total, item) => {
      const itemPrice = parsePrice(item.price);
      return total + itemPrice * item.quantity;
    }, 0);
  };

  const handleQuantityChange = (id: string, quantity: number) => {
    const updatedItems = cartItems.map((item) =>
      item.id === id ? { ...item, quantity: Math.max(quantity, 1) } : item
    );
    setCartItems(updatedItems);
    localStorage.setItem("cartItems", JSON.stringify(updatedItems));
  };

  const handleRemoveItem = (id: string) => {
    const updatedItems = cartItems.filter((item) => item.id !== id);
    setCartItems(updatedItems);
    localStorage.setItem("cartItems", JSON.stringify(updatedItems));
  };

  const handleProceedToOrderClick = () => {
    const total = calculateTotal();
    onProceedToOrder(cartItems, total);
  };

  return (
    <div className="cart-page">
      <div className="cart-container">
        <div className="image-container">
          <img src={ordersImg} alt="View Orders" />
        </div>
        <h2>Welcome to Your Cart!</h2>
        <p>Did you forget something? Check your cart before you proceed to order.</p>
        <hr className="divider" />
        {cartItems.length === 0 ? (
          <p>Your cart is empty.</p>
        ) : (
          <div>
            <table>
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Price</th>
                  <th>Quantity</th>
                  <th>Total</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {cartItems.map((item) => (
                  <tr key={item.id}>
                    <td>
                      <div>
                        <img src={item.image} alt={item.name} className="cart-product-image" />
                        <div>{item.name}</div>
                      </div>
                    </td>
                    <td>{`$${parsePrice(item.price).toFixed(2)}`}</td>
                    <td>
                      <input
                        type="number"
                        min="1"
                        value={item.quantity}
                        onChange={(e) => handleQuantityChange(item.id, parseInt(e.target.value))}
                      />
                    </td>
                    <td>{`$${(parsePrice(item.price) * item.quantity).toFixed(2)}`}</td>
                    <td>
                      <button onClick={() => handleRemoveItem(item.id)}>Remove</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <h2>Total: ${calculateTotal().toFixed(2)}</h2>
            <button className="order-button" onClick={handleProceedToOrderClick}>
              Proceed to Order
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Cart;
