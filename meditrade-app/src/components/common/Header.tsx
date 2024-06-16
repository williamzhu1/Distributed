// src/components/common/Header.tsx
import React from "react";
import { Link } from "react-router-dom";
import logo_horizontal from "../../assets/images/logo_horizontal.jpeg";
import cartIcon from "../../assets/images/cart.jpeg";
import shippingIcon from "../../assets/images/shipping.jpeg";
import "./header.css";
import SupplierHome from "../supplier/SupplierHome";

interface HeaderProps {
  user: any;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders") => void;
  onLogout: () => void;
}

const Header: React.FC<HeaderProps> = ({ user, onSwitchMode, onLogout }) => {
  return (
    <header className="home-header">
      <img
        src={logo_horizontal}
        alt="MediTrade Logo"
        className="logo_horizontal"
      />
      <div className="nav-links">
        <Link to="#" onClick={() => onSwitchMode("home")}>Home</Link>
        {user ? (
          <>
            <span className="welcome-message">Welcome, {user.username || user.email}</span>

            <button onClick={onLogout} className="logout-button">Logout</button>
            {/* <button onClick={supplierHome} className="logout-button">Supplier Menu</button> */}
            {/* <button onClick={() => onSwitchMode("supplierHome")} className="logout-button">Supplier Menu</button>  */}
            {user.role === "manager" && (
              <button onClick={() => onSwitchMode("supplierHome")} className="logout-button">Supplier Menu</button>
            )}
          </>
        ) : (
          <>
            <Link to="#" onClick={() => onSwitchMode("login")}>Login</Link>
            <Link to="#" onClick={() => onSwitchMode("register")}>Register</Link>
          </>
        )}
        <Link to ="#" onClick={() => onSwitchMode("trace")}>
          <img src={shippingIcon} alt="Shipping" className="shipping-icon" />
        </Link>
        <Link to ="#" onClick={() => onSwitchMode("cart")}>
          <img src={cartIcon} alt="Cart" className="cart-icon" />
        </Link>
      </div>
    </header>
  );
};

export default Header;
