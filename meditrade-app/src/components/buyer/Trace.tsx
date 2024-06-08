import React, { useState } from "react";
import { Link } from "react-router-dom";
import Header from "../common/Header";
import Footer from "../common/Footer";
import confirmedIcon from "../../assets/images/shipping_status/confirmed.jpeg";
import shippedIcon from "../../assets/images/shipping_status/shipped.jpeg";
import deliveredIcon from "../../assets/images/shipping_status/delivered.jpeg";
import classNames from "classnames";
import styles from "./trace.module.css";

// Define the interface for the order status
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

const initialProducts: Product[] = [
  {
    id: 1,
    name: "Herbal Tea",
    image: require("../../assets/images/products/Sample1.jpeg"),
    status: [
      { status: "Order Confirmed", date: "2023-06-01" },
      { status: "Shipped", date: "2023-06-02" },
      { status: "Delivered", date: "2023-06-03" },
    ],
  },
  {
    id: 2,
    name: "Ginseng Extract",
    image: require("../../assets/images/products/Sample2.jpeg"),
    status: [
      { status: "Order Confirmed", date: "2023-06-01" },
      { status: "Shipped", date: "2023-06-02" },
      { status: "Delivered", date: "2023-06-03" },
    ],
  },
];

const statusIcons = {
  "Order Confirmed": confirmedIcon,
  Shipped: shippedIcon,
  Delivered: deliveredIcon,
};

const Trace: React.FC = () => {
  const [products] = useState<Product[]>(initialProducts);

  return (
    <div className={styles.tracePage}>
      <Header />
      <div className={styles.traceContainer}>
        <h1>Order Status</h1>
        {products.map((product) => (
          <div key={product.id} className={styles.productStatusContainer}>
            <Link to={`/product/${product.id}`} className={styles.productInfo}>
              <img
                src={product.image}
                alt={product.name}
                className={styles.productImage}
              />
              <h2>{product.name}</h2>
            </Link>
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
      <Footer />
    </div>
  );
};

export default Trace;
