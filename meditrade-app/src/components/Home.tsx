import React from "react";
import { Link } from "react-router-dom";
import "./home.css";
import logo_horizontal from "../images/logo_horizontal.jpeg";
import banner from "../images/banner.jpeg";
import product1 from "../images/products/Sample1.jpeg"; // Ensure these paths are correct
import product2 from "../images/products/Sample2.jpeg"; // Ensure these paths are correct
import product3 from "../images/products/Sample3.jpeg"; // Ensure these paths are correct
import product4 from "../images/products/Sample4.jpeg"; // Ensure these paths are correct
import product5 from "../images/products/Sample5.jpeg"; // Ensure these paths are correct
import product6 from "../images/products/Sample6.jpeg"; // Ensure these paths are correct
import product7 from "../images/products/Sample7.jpeg"; // Ensure these paths are correct
import product8 from "../images/products/Sample8.jpeg"; // Ensure these paths are correct
import product9 from "../images/products/Sample9.jpeg"; // Ensure these paths are correct
import product10 from "../images/products/Sample10.jpeg"; // Ensure these paths are correct
import product11 from "../images/products/Sample11.jpeg"; // Ensure these paths are correct
import product12 from "../images/products/Sample12.jpeg"; // Ensure these paths are correct
import product13 from "../images/products/Sample13.jpeg"; // Ensure these paths are correct
import product14 from "../images/products/Sample14.jpeg"; // Ensure these paths are correct
import product15 from "../images/products/Sample15.jpeg"; // Ensure these paths are correct
import product16 from "../images/products/Sample16.jpeg"; // Ensure these paths are correct
import Footer from "./Footer";

const products = [
  {
    image: product1,
    name: "Herbal Tea",
    price: "$10.00",
    genre: "Herbal",
    origin: "China",
  },
  {
    image: product2,
    name: "Ginseng Extract",
    price: "$25.00",
    genre: "Herbal",
    origin: "Korea",
  },
  {
    image: product3,
    name: "Aloe Vera Gel",
    price: "$15.00",
    genre: "Topical",
    origin: "Egypt",
  },
  {
    image: product4,
    name: "Echinacea",
    price: "$12.00",
    genre: "Herbal",
    origin: "USA",
  },
  {
    image: product5,
    name: "Chamomile",
    price: "$8.00",
    genre: "Herbal",
    origin: "Germany",
  },
  {
    image: product6,
    name: "Lavender Oil",
    price: "$20.00",
    genre: "Essential Oil",
    origin: "France",
  },
  {
    image: product7,
    name: "Peppermint",
    price: "$5.00",
    genre: "Herbal",
    origin: "USA",
  },
  {
    image: product8,
    name: "Turmeric",
    price: "$7.00",
    genre: "Herbal",
    origin: "India",
  },
  {
    image: product9,
    name: "Rosemary",
    price: "$6.00",
    genre: "Herbal",
    origin: "Italy",
  },
  {
    image: product10,
    name: "Sage",
    price: "$5.50",
    genre: "Herbal",
    origin: "Turkey",
  },
  {
    image: product11,
    name: "Thyme",
    price: "$4.00",
    genre: "Herbal",
    origin: "Spain",
  },
  {
    image: product12,
    name: "Basil",
    price: "$4.50",
    genre: "Herbal",
    origin: "India",
  },
  {
    image: product13,
    name: "Oregano",
    price: "$5.00",
    genre: "Herbal",
    origin: "Greece",
  },
  {
    image: product14,
    name: "Garlic",
    price: "$3.00",
    genre: "Herbal",
    origin: "China",
  },
  {
    image: product15,
    name: "Ginger",
    price: "$6.00",
    genre: "Herbal",
    origin: "India",
  },
  {
    image: product16,
    name: "Cinnamon",
    price: "$7.00",
    genre: "Herbal",
    origin: "Sri Lanka",
  },
];

const Home = () => {
  return (
    <div className="home-page">
      <header className="home-header">
        <img src={logo_horizontal} alt="MediTrade Logo" className="logo_horizontal" />
        <nav>
          <Link to="/home">Home</Link>
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
          <Link to="/contact">Contact</Link>
        </nav>
      </header>
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
        {products.map((product, index) => (
          <div key={index} className="product-card">
            <img src={product.image} alt={product.name} className="product-image" />
            <div className="product-info">
              <h2>{product.name}</h2>
              <p className="product-price">{product.price}</p>
              <p className="product-genre">{product.genre}</p>
              <p className="product-origin">{product.origin}</p>
            </div>
          </div>
        ))}
      </div>
      <Footer />
    </div>
  );
};

export default Home;
