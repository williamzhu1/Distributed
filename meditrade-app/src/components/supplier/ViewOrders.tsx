import React, { useState, useEffect } from "react";
import "./view_orders.css";
import { useUser } from "../../contexts/UserContext";

interface Order {
  id: string;
  firstName: string;
  lastName: string;
  address: string;
  items: { [key: string]: number };
  status: "Pending" | "Shipped" | "Delivered" | "CANCELLED" | "CONFIRMED";
}

interface ViewOrdersProps {
  user: any;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders" | "profile") => void;
  onLogout: () => void;
}

const ViewOrders: React.FC<ViewOrdersProps> = ({ user, onSwitchMode, onLogout }) => {
  const [orders, setOrders] = useState<Order[]>([]);
  const { user: loggedInUser } = useUser(); // Using useUser inside the component

  useEffect(() => {
    const fetchOrders = async () => {
      const uid = loggedInUser?.uid;
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          throw new Error("No token found");
        }

        const response = await fetch(`/api/supplierOrder?userId=${uid}`, {
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

        // Ensure data contains the array of orders
        if (data && Array.isArray(data.data)) {
          setOrders(data.data);
        } else {
          console.error("Fetched data is not an array:", data);
          setOrders([]);
        }
      } catch (error) {
        console.error("Error fetching orders:", error);
      }
    };

    fetchOrders();
  }, [loggedInUser?.uid]); // Depend on loggedInUser.uid

  return (
    <div className="view-orders-page">
      <div className="view-orders-content">
        <h1>View Orders</h1>

        <div className="orders-list">
          {orders.map((order) => (
            <div key={order.id} className="order-card">
              <h2>Order #{order.id}</h2>
              <p>
                <strong>Customer:</strong> {order.firstName} {order.lastName}
              </p>
              <p>
                <strong>Address:</strong> {order.address}
              </p>
              <p>
                <strong>Items:</strong>
                <ul>
                  {Object.entries(order.items).map(([productId, quantity]) => (
                    <li key={productId}>
                      {productId}: {quantity}
                    </li>
                  ))}
                </ul>
              </p>
              <p>
                <strong>Status:</strong> {order.status}
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default ViewOrders;
