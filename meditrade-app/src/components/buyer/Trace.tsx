// src/components/buyer/Trace.tsx
import React, { useEffect, useState } from "react";
import confirmedIcon from "../../assets/images/shipping_status/confirmed.jpeg";
import shippedIcon from "../../assets/images/shipping_status/shipped.jpeg";
import deliveredIcon from "../../assets/images/shipping_status/delivered.jpeg";
import classNames from "classnames";
import styles from "./trace.module.css";

interface TraceProps {
  user: any;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders") => void;
  onLogout: () => void;
}

interface OrderStatus {
  status: "Order Confirmed" | "Shipped" | "Delivered";
  date: string;
}

interface Product {
  id: number;
  name: string;
  image: string;
  status: OrderStatus[];
}

const statusIcons = {
  "Order Confirmed": confirmedIcon,
  Shipped: shippedIcon,
  Delivered: deliveredIcon,
};

const Trace: React.FC<TraceProps> = ({ user, onSwitchMode, onLogout }) => {
  const [products, setProducts] = useState<Product[]>([]);

  useEffect(() => {
    const fetchOrders = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        console.error("No token found in local storage");
        return;
      }

      try {
        const response = await fetch(`/api/getorders?firstName=${user.firstName}&lastName=${user.lastName}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const orderData = await response.json();
        const fetchedProducts: Product[] = orderData.map((order: any) => ({
          id: order.id,
          name: order.name,
          image: order.image,
          status: order.status,
        }));
        setProducts(fetchedProducts);
      } catch (error) {
        console.error("Error fetching orders:", error);
      }
    };

    fetchOrders();
  }, [user.firstName, user.lastName]);

  return (
    <div className={styles.tracePage}>
      <div className={styles.traceContainer}>
        <h1>Order Status</h1>
        {products.map((product) => (
          <div key={product.id} className={styles.productStatusContainer}>
            <div className={styles.productInfo}>
              <img
                src={product.image}
                alt={product.name}
                className={styles.productImage}
              />
              <h2>{product.name}</h2>
            </div>
            <div className={styles.statusTimeline}>
              {product.status.map((status, index) => (
                <div
                  key={index}
                  className={classNames(styles.statusItem, {
                    [styles.orderConfirmed]:
                      status.status === "Order Confirmed",
                    [styles.shipped]: status.status === "Shipped",
                    [styles.delivered]: status.status === "Delivered",
                  })}
                >
                  <div className={styles.statusIcon}>
                    <img src={statusIcons[status.status]} alt={status.status} />
                  </div>
                  <div className={styles.statusInfo}>
                    <h2
                      className={classNames({
                        [styles.orderConfirmed]:
                          status.status === "Order Confirmed",
                        [styles.shipped]: status.status === "Shipped",
                        [styles.delivered]: status.status === "Delivered",
                      })}
                    >
                      {status.status}
                    </h2>
                    <p>{status.date}</p>
                  </div>
                  {index < product.status.length - 1 && (
                    <div className={styles.statusLine}></div>
                  )}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Trace;
