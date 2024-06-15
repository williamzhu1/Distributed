import React, { useState } from "react";
import Header from "../common/Header";
import Footer from "../common/Footer";
import { Link } from "react-router-dom";
import orderHistoryImg from "../../assets/images/orderHistory.jpeg"; // Import the image
import "./order_history.css";

interface Order {
  id: number;
  customerName: string;
  product: string;
  quantity: number;
  totalPrice: string;
  address: string;
  status: "Pending" | "Shipped" | "Delivered";
}

const initialOrders: Order[] = [
  {
    id: 1,
    customerName: "John Doe",
    product: "Herbal Tea",
    quantity: 2,
    totalPrice: "$20.00",
    address: "123 Main St, City, Country",
    status: "Delivered",
  },
  {
    id: 2,
    customerName: "Jane Smith",
    product: "Ginseng Extract",
    quantity: 1,
    totalPrice: "$25.00",
    address: "456 Elm St, City, Country",
    status: "Delivered",
  },
  // Add more initial orders as needed
];

const OrderHistory: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>(initialOrders);

  const revertOrderStatus = (id: number) => {
    setOrders((prevOrders) => prevOrders.filter((order) => order.id !== id));
    // Ideally, you would move this order back to the view orders page
    // This is just a placeholder to simulate that action
    alert(`Order ${id} has been reverted to Shipped and removed from history.`);
  };

  return (
    <div className="order-history-page">
      <Header />
      <div className="order-history-content">
        <div className="image-container">
          <img src={orderHistoryImg} alt="Order History" />
        </div>
        <h1>Order History</h1>
        <p className="catchy-copy">
          Do you want to cancel the delivered status?
        </p>

        <div className="orders-list">
          {orders.map((order) => (
            <div key={order.id} className="order-card">
              <h2>Order #{order.id}</h2>
              <p>
                <strong>Customer:</strong> {order.customerName}
              </p>
              <p>
                <strong>Product:</strong> {order.product}
              </p>
              <p>
                <strong>Quantity:</strong> {order.quantity}
              </p>
              <p>
                <strong>Total Price:</strong> {order.totalPrice}
              </p>
              <p>
                <strong>Address:</strong> {order.address}
              </p>
              <p>
                <strong>Status:</strong> {order.status}
              </p>
              <div className="order-actions">
                {order.status === "Delivered" && (
                  <button
                    className="revert-button"
                    onClick={() => revertOrderStatus(order.id)}
                  >
                    Revert to Shipped
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
        <Link to="/view-orders">
          <button className="view-orders-button">Back to Orders</button>
        </Link>
      </div>
      <Footer />
    </div>
  );
};

export default OrderHistory;
