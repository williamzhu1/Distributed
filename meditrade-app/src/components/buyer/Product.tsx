import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./product.css";
import Header from "../common/Header";
import Footer from "../common/Footer";
import { Product as ProductType } from "../types"; // Ensure this import is correct

const Product: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [product, setProduct] = useState<ProductType | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/products/${id}`, {
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
        setProduct(data);
      } catch (error: any) { // Assert error type as any
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  const addToCart = () => {
    const cartItems = JSON.parse(localStorage.getItem("cartItems") || "[]");
    const existingItem = cartItems.find((item: any) => item.id === product?.id);

    if (existingItem) {
      existingItem.quantity += 1;
    } else if (product) {
      cartItems.push({
        id: product.id,
        name: product.name,
        price: product.price,
        quantity: 1,
        image: product.image // Store the image path
      });
    }

    localStorage.setItem("cartItems", JSON.stringify(cartItems));
    navigate("/cart");
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  if (!product) {
    return <div>Product not found</div>;
  }

  return (
    <div className="product-page">
      <Header />
      <div className="product-detail-page">
        <img
          src={product.image}
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
            Manufacturer: {product.manufacturer.name}
          </p>
          <p className="manufacturer-info">Info: {product.manufacturer.info}</p>
          <button className="add-to-cart-button" onClick={addToCart}>
            Add to Cart
          </button>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default Product;
