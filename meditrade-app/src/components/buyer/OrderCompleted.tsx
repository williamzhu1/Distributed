import React from "react";
import "./order_completed.css";

interface OrderCompletedProps {
  response: any;
}

const OrderCompleted: React.FC<OrderCompletedProps> = ({ response }) => {
  // Extract the message from the response
  const message = response?.message || "No message available";

  return (
    <div className="order-completed-page">
      <div className="order-completed-container">
        <h1>Order Completed</h1>
        <div>
          <h2>Order Response:</h2>
          <pre>{message}</pre>
        </div>
      </div>
    </div>
  );
};

export default OrderCompleted;
