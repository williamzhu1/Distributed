import React from "react";
import { Link, useNavigate } from "react-router-dom";
import logo_horizontal from "../../assets/images/logo_horizontal.jpeg";
import cartIcon from "../../assets/images/cart.jpeg";
import shippingIcon from "../../assets/images/shipping.jpeg";
import { useUser } from "../../contexts/UserContext"; // Adjust the import path accordingly
import "./header.css";

const Header: React.FC = () => {
  const { user, logout } = useUser();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="home-header">
      <img
        src={logo_horizontal}
        alt="MediTrade Logo"
        className="logo_horizontal"
      />
      <div className="nav-links">
        <Link to="/home">Home</Link>
        {user ? (
          <>
            <span className="welcome-message">Welcome, {user.username || user.email}</span>
            <Link to="/profile">Profile</Link>
            <button onClick={handleLogout} className="logout-button">Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
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


