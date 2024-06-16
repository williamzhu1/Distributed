import React, { useState } from "react";
import { CartItem } from "./CartItem"; // Import the CartItem type correctly
import "./order_confirmation.css";
import { useUser } from "../../contexts/UserContext";

interface OrderConfirmationProps {
  cartItems: CartItem[];
  total: number;
  onOrderCompleted: (response: any) => void; // Add this prop to notify when the order is completed
}

const OrderConfirmation: React.FC<OrderConfirmationProps> = ({ cartItems, total, onOrderCompleted }) => {
  const { user } = useUser();

  const [formData, setFormData] = useState({
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

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const response = await sendCartCloud(formData.address);
    if (response) {
      onOrderCompleted(response); // Notify the parent component that the order is completed with response data
    }
  };

  async function sendCartCloud(address: string) {
    const token = localStorage.getItem("token");
    if (!token) {
      console.error("No token found in local storage");
      return null;
    }

    try {
      const response = await fetch("/api/order", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({
          userId: user?.uid,
          firstName: user?.firstName,
          lastName: user?.lastName,
          address: address,
          items: cartItems.reduce((acc: { [key: string]: number }, item) => {
              acc[item.id] = parseInt(item.quantity.toString(), 10);
              return acc;
          }, {})
        }),
      });

      if (!response.ok) {
        throw new Error("Failed to send cart to firebase");
      }

      const contentType = response.headers.get("Content-Type");
      if (contentType && contentType.includes("application/json")) {
        return await response.json();
      } else {
        return { message: await response.text() };
      }
    } catch (error) {
      console.error("Error sending cart to firebase:", error);
      return null;
    }
  };

  return (
    <div className="order-confirmation-page">
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
                  ${typeof item.price === 'string' ? (parseFloat(item.price.slice(1)) * item.quantity).toFixed(2) : (item.price * item.quantity).toFixed(2)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <h2>Total: ${total.toFixed(2)}</h2>
        <form className="order-form" onSubmit={handleSubmit}>
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
    </div>
  );
};

export default OrderConfirmation;
