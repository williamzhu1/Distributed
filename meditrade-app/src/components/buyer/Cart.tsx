import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { CartItem } from "./CartItem"; // Import the CartItem type
import Header from "../common/Header";
import Footer from "../common/Footer";
import "./cart.css";

const Cart: React.FC = () => {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);

  useEffect(() => {
    // Mock data for demonstration
    const mockCartItems: CartItem[] = [
      { id: 1, name: "Herbal Tea", price: "$10", quantity: 2, image: "path_to_image/herbal_tea.jpg" },
      { id: 2, name: "Ginseng Extract", price: "$20", quantity: 1, image: "path_to_image/ginseng_extract.jpg" },
    ];

    setCartItems(mockCartItems);
  }, []);

  const calculateTotal = () => {
    return cartItems.reduce((total, item) => {
      return total + parseFloat(item.price.slice(1)) * item.quantity;
    }, 0);
  };

  const handleQuantityChange = (id: number, quantity: number) => {
    setCartItems((prevItems) =>
      prevItems.map((item) =>
        item.id === id ? { ...item, quantity: quantity } : item
      )
    );
  };

  const handleRemoveItem = (id: number) => {
    setCartItems((prevItems) => prevItems.filter((item) => item.id !== id));
  };

  return (
    <div className="cart-page">
      <Header />
      <div className="cart-container">
        <h1>Your Cart</h1>
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
                      <Link to={`/product/${item.id}`}>
                        <img src={item.image} alt={item.name} className="cart-product-image" />
                        {item.name}
                      </Link>
                    </td>
                    <td>{item.price}</td>
                    <td>
                      <input
                        type="number"
                        min="1"
                        value={item.quantity}
                        onChange={(e) => handleQuantityChange(item.id, parseInt(e.target.value))}
                      />
                    </td>
                    <td>${(parseFloat(item.price.slice(1)) * item.quantity).toFixed(2)}</td>
                    <td>
                      <button onClick={() => handleRemoveItem(item.id)}>Remove</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <h2>Total: ${calculateTotal().toFixed(2)}</h2>
            <Link to="/order">
              <button className="order-button">Proceed to Order</button>
            </Link>
          </div>
        )}
      </div>
      <Footer />
    </div>
  );
};

export default Cart;
