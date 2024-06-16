import React from "react";
import Header from "../common/Header";
import Footer from "../common/Footer";
import "./order_completed.css";

const OrderCompleted: React.FC = () => {
  return (
    <div className="order-completed-page">

      <div className="order-completed-container">
        <h1>Order Completed</h1>
        <p>
          Thank you for your purchase! Your order has been successfully placed.
        </p>
      </div>
      <Footer />
    </div>
  );
};

export default OrderCompleted;
