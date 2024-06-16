// src/components/supplier/SupplierHome.tsx
import React from "react";
import Header from "../common/Header";
import Footer from "../common/Footer";
import storageImg from "../../assets/images/storage.jpeg";
import ordersImg from "../../assets/images/orders.jpeg";
import "./supplier_home.css";

interface SupplierHomeProps {
  user: any;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "supplierHome" | "viewOrders" | "cart" | "trace") => void;
  onLogout: () => void;
}

const SupplierHome: React.FC<SupplierHomeProps> = ({ user, onSwitchMode, onLogout }) => {
  return (
    <div className="supplier-home-page">
      <Header user={user} onSwitchMode={onSwitchMode} onLogout={onLogout} />
      <div className="supplier-content">
        <h1>Welcome to the Supplier Page!</h1>
        <div className="supplier-actions">
          <div className="action-card">
            <div className="image-container">
              <img src={storageImg} alt="Manage Products" />
            </div>
            <h2>Manage Products</h2>
            <p>Keep your inventory up-to-date with ease.</p>
            <button
              className="action-button"
              onClick={() => onSwitchMode("manageProducts")}
            >
              Go to Product Management
            </button>
          </div>
          <div className="action-card">
            <div className="image-container">
              <img src={ordersImg} alt="View Orders" />
            </div>
            <h2>View Orders</h2>
            <p>Monitor your sales and fulfill orders efficiently.</p>
            <button
              className="action-button"
              onClick={() => onSwitchMode("viewOrders")}
            >
              View Orders
            </button>
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default SupplierHome;
