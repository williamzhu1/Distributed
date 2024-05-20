import React from "react";
import { Link } from "react-router-dom";
import logo_horizontal from "../assets/images/logo_horizontal.jpeg";
import "./header.css";

const Header = () => {
  return (
    <header className="home-header">
      <img src={logo_horizontal} alt="MediTrade Logo" className="logo_horizontal" />
      <div className="nav-links">
        <Link to="/home">Home</Link>
        <Link to="/login">Login</Link>
        <Link to="/register">Register</Link>
        <Link to="/contact">Contact</Link>
      </div>
    </header>
  );
};

export default Header;
