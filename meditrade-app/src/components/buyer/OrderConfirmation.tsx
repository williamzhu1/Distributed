import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Header from "../common/Header";
import Footer from "../common/Footer";
import { CartItem } from "./CartItem"; // Import the CartItem type
import "./order_confirmation.css";

const OrderConfirmation: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { cartItems, total } = location.state as {
    cartItems: CartItem[];
    total: number;
  };

  const [formData, setFormData] = useState({
    name: "",
    address: "",
    paymentMethod: "Credit Card",
  });

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
  ) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    // Process the order here (e.g., send to backend)
    navigate("/order-completed");
  };

  return (
    <div className="order-confirmation-page">
      <Header />
      <div className="order-confirmation-container">
        <h1>Order Confirmation</h1>
        <table>
          <thead>
            <tr>
              <th>Product</th>
              <th>Price</th>
              <th>Quantity</th>
              <th>Total</th>
            </tr>
          </thead>
          <tbody>
            {cartItems.map((item) => (
              <tr key={item.id}>
                <td>{item.name}</td>
                <td>{item.price}</td>
                <td>{item.quantity}</td>
                <td>
                  $
                  {(parseFloat(item.price.slice(1)) * item.quantity).toFixed(2)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <h2>Total: ${total.toFixed(2)}</h2>
        <form className="order-form" onSubmit={handleSubmit}>
          <div>
            <label>Name:</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label>Address:</label>
            <input
              type="text"
              name="address"
              value={formData.address}
              onChange={handleChange}
              required
            />
          </div>
          <div>
            <label>Payment Method:</label>
            <select
              name="paymentMethod"
              value={formData.paymentMethod}
              onChange={handleChange}
            >
              <option value="Credit Card">Credit Card</option>
              <option value="PayPal">PayPal</option>
              <option value="Bank Transfer">Bank Transfer</option>
            </select>
          </div>
          <button type="submit" className="confirm-button">
            Confirm Order
          </button>
        </form>
      </div>
      <Footer />
    </div>
  );
};

export default OrderConfirmation;
