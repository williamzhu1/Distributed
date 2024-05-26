import React from "react";
import { Link } from "react-router-dom";
import "./buyer_home.css";
import banner from "../../assets/images/banner.jpeg";
import products from "../../data/products.json";
import images from "../../assets/images/products/index";
import Header from "../common/Header";
import Footer from "../common/Footer";

// Ensure TypeScript knows that 'images' is of type 'Images'
const imagesTyped: { [key: string]: string } = images;

const Home = () => {
  return (
    <div className="home-page">
      <Header />
      <div className="hero">
        <img src={banner} alt="Herbal Banner" className="banner-image" />
        <h1>Discover Ancient Remedies from Around the World</h1>
        <p>
          Explore unique medicines and herbs from various places like China and the Amazon Forest.
        </p>
      </div>
      <div className="search-bar">
        <input type="text" placeholder="Search for medicines or herbs" />
        <button>Search</button>
      </div>
      <div className="results-section">
        {products.map((product) => (
          <Link key={product.id} to={`/product/${product.id}`} className="product-link">
            <div className="product-card">
              <img src={imagesTyped[product.image]} alt={product.name} className="product-image" />
              <div className="product-info">
                <h2>{product.name}</h2>
                <p className="product-price">{product.price}</p>
                <p className="product-genre">{product.genre}</p>
                <p className="product-origin">{product.origin}</p>
              </div>
            </div>
          </Link>
        ))}
      </div>
      <Footer />
    </div>
  );
};

export default Home;
