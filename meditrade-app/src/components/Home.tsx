import React from "react";
import { Link } from "react-router-dom";
import "./home.css"; // Make sure your CSS file is linked
import logo_horizontal from "../images/logo_horizontal.jpeg"; // Ensure the logo image path is correct
import banner from "../images/banner.jpeg";
import Footer from "./Footer"; // Assuming Footer is a separate component

const Home = () => {
  return (
    <div className="home-container">
      <header className="home-header">
        <img src={logo_horizontal} alt="MediTrade Logo" className="logo_horizontal" />
        <nav>
          <Link to="/">Home</Link>
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
          <Link to="/contact">Contact</Link>
        </nav>
      </header>
      <div className="hero">
        <img src={banner} alt="Herbal Banner" className="banner-image" />
        <h1>Discover Ancient Remedies from Around the World</h1>
        <p>
          Explore unique medicines and herbs from various places like China and
          the Amazon Forest.
        </p>
      </div>
      <div className="search-bar">
        <input type="text" placeholder="Search for medicines or herbs" />
        <button>Search</button>
      </div>
      <div className="results-section">
        {/* Results of the search will be displayed here */}
      </div>
      <Footer />
    </div>
  );
};

export default Home;
