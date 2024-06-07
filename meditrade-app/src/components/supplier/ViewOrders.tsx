import React, { useState } from "react";
import Header from "../common/Header";
import Footer from "../common/Footer";
import "./view_orders.css";

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
    status: "Pending",
  },
  {
    id: 2,
    customerName: "Jane Smith",
    product: "Ginseng Extract",
    quantity: 1,
    totalPrice: "$25.00",
    address: "456 Elm St, City, Country",
    status: "Shipped",
  },
  // Add more initial orders as needed
];

const ViewOrders: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>(initialOrders);

  const updateOrderStatus = (
    id: number,
    status: "Pending" | "Shipped" | "Delivered",
  ) => {
    setOrders(
      orders.map((order) => (order.id === id ? { ...order, status } : order)),
    );
  };

  return (
    <div className="view-orders-page">
      <Header />
      <div className="view-orders-content">
        <h1>View Orders</h1>
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
                {order.status === "Pending" && (
                  <button
                    onClick={() => updateOrderStatus(order.id, "Shipped")}
                  >
                    Mark as Shipped
                  </button>
                )}
                {order.status === "Shipped" && (
                  <button
                    onClick={() => updateOrderStatus(order.id, "Delivered")}
                  >
                    Mark as Delivered
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default ViewOrders;
