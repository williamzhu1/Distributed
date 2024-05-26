import React from "react";
import { useNavigate } from "react-router-dom";
import Header from "../common/Header";
import Footer from "../common/Footer";
import "./supplier_home.css";
import storageImg from "../../assets/images/storage.jpeg";
import ordersImg from "../../assets/images/orders.jpeg";

const SupplierHome: React.FC = () => {
    const navigate = useNavigate();
  return (
    <div className="supplier-home-page">
      <Header />
      <div className="supplier-content">
        <h1>Welcome to the Supplier Page!</h1>
        <div className="supplier-actions">
          <div className="action-card">
            <div className="image-container">
              <img src={storageImg} alt="Manage Products" />
            </div>
            <h2>Manage Products</h2>
            <p>Keep your inventory up-to-date with ease.</p>
            <button className="action-button" onClick={() => navigate('/manage-products')}>
              Go to Product Management
            </button>
          </div>
          <div className="action-card">
            <div className="image-container">
              <img src={ordersImg} alt="View Orders" />
            </div>
            <h2>View Orders</h2>
            <p>Monitor your sales and fulfill orders efficiently.</p>
            <button className="action-button" onClick={() => navigate('/view-orders')}>
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
