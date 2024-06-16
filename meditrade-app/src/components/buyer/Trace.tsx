import React, { useEffect, useState } from "react";
import styles from "./trace.module.css";

interface TraceProps {
  user: any;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders") => void;
  onLogout: () => void;
}

interface Order {
  firstName: string;
  lastName: string;
  address: string;
  userId: string;
  items: { [key: string]: number }; // Here key is the product name, not the ID
  status: string;
}

const Trace: React.FC<TraceProps> = ({ user, onSwitchMode, onLogout }) => {
  const [orders, setOrders] = useState<Order[]>([]);

  useEffect(() => {
    const fetchOrders = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        console.error("No token found in local storage");
        return;
      }

      try {
        const response = await fetch(`/api/getorders?userId=${user.uid}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const orderData: Order[] = await response.json();
        setOrders(orderData);
      } catch (error) {
        console.error("Error fetching orders:", error);
      }
    };

    fetchOrders();
  }, [user.uid]);

  return (
    <div className={styles.tracePage}>
      <div className={styles.traceContainer}>
        <h1>Order Status</h1>
        {orders.map((order, index) => (
          <div key={index} className={styles.orderContainer}>
            <h2>Order for {order.firstName} {order.lastName}</h2>
            <p>Address: {order.address}</p>
            <p>Status: {order.status}</p>
            <h3>Items:</h3>
            <ul>
              {Object.entries(order.items).map(([productName, quantity]) => (
                <li key={productName}>
                  Product: {productName}, Quantity: {quantity}
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Trace;
