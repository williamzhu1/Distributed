import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./buyer_home.css";
import banner from "../../assets/images/banner.jpeg";
import Header from "../common/Header";
import Footer from "../common/Footer";
import { Product } from "../types"; // Import the Product type

const Home: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredProducts, setFilteredProducts] = useState<Product[]>([]);
  const [suggestions, setSuggestions] = useState<string[]>([]);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/products', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
          },
        });
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const data = await response.json();
        setProducts(data);
        setFilteredProducts(data);
      } catch (error) {
        console.error("Error fetching products:", error);
      }
    };

    fetchProducts();
  }, []);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const term = e.target.value;
    setSearchTerm(term);

    // Filter products based on the search term
    const filtered = products.filter(
      (product) =>
        product.name.toLowerCase().includes(term.toLowerCase()) ||
        product.genre.toLowerCase().includes(term.toLowerCase()) ||
        product.origin.toLowerCase().includes(term.toLowerCase())
    );
    setFilteredProducts(filtered);

    // Generate suggestions based on the search term
    const uniqueSuggestions = Array.from(
      new Set(
        products
          .filter(
            (product) =>
              product.name.toLowerCase().includes(term.toLowerCase()) ||
              product.genre.toLowerCase().includes(term.toLowerCase()) ||
              product.origin.toLowerCase().includes(term.toLowerCase())
          )
          .map((product) => product.name)
      )
    );
    setSuggestions(uniqueSuggestions);
  };

  const handleSuggestionClick = (suggestion: string) => {
    setSearchTerm(suggestion);
    const filtered = products.filter((product) =>
      product.name.toLowerCase().includes(suggestion.toLowerCase())
    );
    setFilteredProducts(filtered);
    setSuggestions([]);
  };

  const handleSearchSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    // Additional logic for search submit can be added here
  };

  return (
    <div className="home-page">
      <Header />
      <div className="hero">
        <img src={banner} alt="Herbal Banner" className="banner-image" />
        <h1>Discover Ancient Remedies from Around the World</h1>
        <p>
          Explore unique medicines and herbs from various places like China and
          the Amazon Forest.
        </p>
      </div>
      <form className="search-bar" onSubmit={handleSearchSubmit}>
        <input
          type="text"
          placeholder="Search for medicines or herbs"
          value={searchTerm}
          onChange={handleSearchChange}
        />
        {searchTerm && (
          <div className="suggestions">
            {suggestions.map((suggestion, index) => (
              <div
                key={index}
                className="suggestion-item"
                onClick={() => handleSuggestionClick(suggestion)}
              >
                {suggestion}
              </div>
            ))}
          </div>
        )}
        <button type="submit">Search</button>
      </form>
      <div className="results-section">
        {filteredProducts.map((product) => (
          <Link
            key={product.id}
            to={`/product/${product.id}`}
            className="product-link"
          >
            <div className="product-card">
              <img
                src={product.image}
                alt={product.name}
                className="product-image"
              />
              <div className="product-info">
                <h2>{product.name}</h2>
                <p className="product-price">{product.price}</p>
                <p className="product-genre">{product.genre}</p>
                <p className="product-origin">{product.origin}</p>
                <p className="product-details">{product.details}</p>
                <p className="product-manufacturer">
                  {product.manufacturer.name} - {product.manufacturer.info}
                </p>
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
