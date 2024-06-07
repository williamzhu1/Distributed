import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./buyer_home.css";
import banner from "../../assets/images/banner.jpeg";
import images from "../../assets/images/products/index";
import Header from "../common/Header";
import Footer from "../common/Footer";

// Interface for Product
interface Product {
  id: string;
  name: string;
  price: string;
  genre: string;
  origin: string;
  image: string;
}

// Ensure TypeScript knows that 'images' is of type 'Images'
const imagesTyped: { [key: string]: string } = images;

const Home: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [filteredProducts, setFilteredProducts] = useState<Product[]>([]);
  const [suggestions, setSuggestions] = useState<string[]>([]);

  useEffect(() => {
    // Fetch products from the API (mocked for now)
    const fetchProducts = async () => {
      try {
        // Replace the following line with actual API call
        // const response = await axios.get<Product[]>(apiUrl);
        const response = { data: require("../../data/products.json") }; // Mocked data
        setProducts(response.data);
        setFilteredProducts(response.data);
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
        product.origin.toLowerCase().includes(term.toLowerCase()),
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
              product.origin.toLowerCase().includes(term.toLowerCase()),
          )
          .map((product) => product.name),
      ),
    );
    setSuggestions(uniqueSuggestions);
  };

  const handleSuggestionClick = (suggestion: string) => {
    setSearchTerm(suggestion);
    const filtered = products.filter((product) =>
      product.name.toLowerCase().includes(suggestion.toLowerCase()),
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
                src={imagesTyped[product.image]}
                alt={product.name}
                className="product-image"
              />
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
