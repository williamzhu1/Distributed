import React from "react";
import { Link } from "react-router-dom";
import logo_horizontal from "../../assets/images/logo_horizontal.jpeg";
import cartIcon from "../../assets/images/cart.jpeg";
import shippingIcon from "../../assets/images/shipping.jpeg";
import "./header.css";

const Header = () => {
  return (
    <header className="home-header">
      <img
        src={logo_horizontal}
        alt="MediTrade Logo"
        className="logo_horizontal"
      />
      <div className="nav-links">
        <Link to="/home">Home</Link>
        <Link to="/login">Login</Link>
        <Link to="/register">Register</Link>
        <Link to="/trace">
          <img src={shippingIcon} alt="Shipping" className="shipping-icon" />
        </Link>
        <Link to="/cart">
          <img src={cartIcon} alt="Cart" className="cart-icon" />
        </Link>
      </div>
    </header>
  );
};

export default Header;
