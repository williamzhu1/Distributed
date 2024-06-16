import React, { useEffect, useState } from "react";
import styles from "./trace.module.css";

interface TraceProps {
  user: any;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders") => void;
  onLogout: () => void;
}

interface Order {
  orderId: string;
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

  const handleDelete = async (orderId: string) => {
    const token = localStorage.getItem("token");
    if (!token) {
      console.error("No token found in local storage");
      return;
    }

    try {
      const response = await fetch(`/api/order/${orderId}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Remove the deleted order from the state
      setOrders((prevOrders) => prevOrders.filter((order) => order.orderId !== orderId));
    } catch (error) {
      console.error("Error deleting order:", error);
    }
  };

  const handleRetry = async (orderId: string) => {
    const token = localStorage.getItem("token");
    if (!token) {
      console.error("No token found in local storage");
      return;
    }

    try {
      const response = await fetch(`/api/retryOrder/${orderId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify({ orderId, items: formatItemsAsInteger(orders.find(order => order.orderId === orderId)?.items || {}) })
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Optionally, you can update the order status in the state to reflect the retry
      setOrders((prevOrders) =>
        prevOrders.map((order) =>
          order.orderId === orderId ? { ...order, status: 'Retrying' } : order
        )
      );
    } catch (error) {
      console.error("Error retrying order:", error);
    }
  };

  const formatItemsAsInteger = (items: { [key: string]: number }): { [key: string]: number } => {
    const formattedItems: { [key: string]: number } = {};
    for (const [productName, quantity] of Object.entries(items)) {
      formattedItems[productName] = parseInt(quantity.toString(), 10);
    }
    console.log("Items in retry order", formattedItems);
    return formattedItems;
  };

  return (
    <div className={styles.tracePage}>
      <div className={styles.traceContainer}>
        <h1>Order Status</h1>
        {orders.map((order, index) => (
          <div key={index} className={styles.orderContainer}>
            <h2>Order ID: {order.orderId}</h2>
            <h3>Order for {order.firstName} {order.lastName}</h3>
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
            {(order.status === "ROOTSTOCK" || order.status === "CANCELLED") && (
            <button onClick={() => handleDelete(order.orderId)} className={styles.deleteButton}>Delete Order</button>
            )}
            {(order.status === "ROOTSTOCK" || order.status === "CANCELLED") && (
              <button onClick={() => handleRetry(order.orderId)} className={styles.retryButton}>Retry Order</button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default Trace;