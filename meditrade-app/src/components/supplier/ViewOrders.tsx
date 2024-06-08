import React, { useState } from "react";
import { Link } from "react-router-dom";
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
  previousStatus?: "Pending" | "Shipped" | "Delivered"; // Optional field to track previous status
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
    newStatus: "Pending" | "Shipped" | "Delivered"
  ) => {
    setOrders((prevOrders) =>
      prevOrders.map((order) =>
        order.id === id
          ? { ...order, previousStatus: order.status, status: newStatus }
          : order
      )
    );
  };

  const handleStatusChange = (id: number, newStatus: "Pending" | "Shipped" | "Delivered") => {
    const confirmation = window.confirm(`Are you sure you want to mark this order as ${newStatus}?`);
    if (confirmation) {
      updateOrderStatus(id, newStatus);
    }
  };

  const revertOrderStatus = (id: number) => {
    setOrders((prevOrders) =>
      prevOrders.map((order) =>
        order.id === id && order.previousStatus
          ? { ...order, status: order.previousStatus, previousStatus: undefined }
          : order
      )
    );
  };

  const deliveredOrders = orders.filter(order => order.status === "Delivered");

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
                  <>
                    <button
                      className="action-button"
                      onClick={() => handleStatusChange(order.id, "Shipped")}
                    >
                      Mark as Shipped
                    </button>
                    {order.previousStatus && (
                      <button
                        className="revert-button"
                        onClick={() => revertOrderStatus(order.id)}
                      >
                        Revert to {order.previousStatus}
                      </button>
                    )}
                  </>
                )}
                {order.status === "Shipped" && (
                  <>
                    <button
                      className="action-button"
                      onClick={() => handleStatusChange(order.id, "Delivered")}
                    >
                      Mark as Delivered
                    </button>
                    <button
                      className="revert-button"
                      onClick={() => revertOrderStatus(order.id)}
                    >
                      Revert to Pending
                    </button>
                  </>
                )}
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
        <Link to="/order-history">
                  <button className="view-history-button">View Order History</button>
                </Link>
      </div>
      <Footer />
    </div>
  );
};

export default ViewOrders;
