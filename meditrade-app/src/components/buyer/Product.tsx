import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./product.css";
import products from "../../data/products.json";
import images from "../../assets/images/products/index";
import Header from "../common/Header";
import Footer from "../common/Footer";

// Explicitly type the 'images' object
const imagesTyped: { [key: string]: string } = images;

const Product = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const product = products.find(
    (product) => product.id === parseInt(id || "0"),
  );

  if (!product) {
    return <div>Product not found</div>;
  }

  return (
    <div className="product-page">
      <Header />
      <body className="product-body">
        <div className="product-details">
          <img
            src={imagesTyped[product.image]}
            alt={product.name}
            className="product-image"
          />
          <div className="product-info">
            <h1>{product.name}</h1>
            <p className="product-price">{product.price}</p>
            <p className="product-genre">Genre: {product.genre}</p>
            <p className="product-origin">Origin: {product.origin}</p>
            <p className="product-description">{product.details}</p>
            <p className="product-manufacturer">
              Manufacturer: {product.manufacturer}
            </p>
            <p className="manufacturer-info">
              Info: {product.manufacturerInfo}
            </p>
            <button className="add-to-cart-button">Add to Cart</button>
          </div>
        </div>
      </body>
      <Footer />
    </div>
  );
};

export default Product;
