// src/components/supplier/ViewOrders.tsx
import React, { useState, useEffect } from "react";
import "./view_orders.css";
import { useUser } from "../../contexts/UserContext";

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

interface ViewOrdersProps {
  user: any;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders" | "profile") => void;
  onLogout: () => void;
}

async function fetchOrders() {
  const { user } = useUser();
  const uid = user?.uid;
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("No token found");
    }
    const response = await fetch(`/api/supplierorders?uid=${uid}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
    const data = await response.json();
    return data;
  } catch (error) {
    console.error("Error fetching orders:", error);
    return [];
  }
}

const ViewOrders: React.FC<ViewOrdersProps> = ({ user, onSwitchMode, onLogout }) => {
  const [orders, setOrders] = useState<Order[]>([]);

  useEffect(() => {
    const loadOrders = async () => {
      const fetchedOrders = await fetchOrders();
      setOrders(fetchedOrders);
    };
    loadOrders();
  }, []);

  const updateOrderStatus = (
    id: number,
    newStatus: "Pending" | "Shipped" | "Delivered",
  ) => {
    setOrders((prevOrders) =>
      prevOrders.map((order) =>
        order.id === id
          ? { ...order, previousStatus: order.status, status: newStatus }
          : order,
      ),
    );
  };

  const handleStatusChange = (
    id: number,
    newStatus: "Pending" | "Shipped" | "Delivered",
  ) => {
    const confirmation = window.confirm(
      `Are you sure you want to mark this order as ${newStatus}?`,
    );
    if (confirmation) {
      updateOrderStatus(id, newStatus);
    }
  };

  const revertOrderStatus = (id: number) => {
    setOrders((prevOrders) =>
      prevOrders.map((order) =>
        order.id === id && order.previousStatus
          ? {
            ...order,
            status: order.previousStatus,
            previousStatus: undefined,
          }
          : order,
      ),
    );
  };

  return (
    <div className="view-orders-page">
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
        <button
          className="view-history-button"
          onClick={() => onSwitchMode("viewOrders")}
        >
          View Order History
        </button>
      </div>
    </div>
  );
};

export default ViewOrders;
