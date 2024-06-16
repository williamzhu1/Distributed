import React, { useState, useEffect } from "react";
import "./product.css";
import images from "../../assets/images/products/index";
import { Product as ProductType } from "../types"; // Renaming the imported type

// Explicitly type the 'images' object
const imagesTyped: { [key: string]: string } = images;

interface ProductProps {
  productId: string;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders" | "product") => void;
}

const ProductDetail: React.FC<ProductProps> = ({ productId, onSwitchMode }) => { // Renaming the component
  const [product, setProduct] = useState<ProductType | null>(null); // Using the renamed type
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/products/${productId}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`,
          },
        });
        if (!response.ok) {
          throw new Error("Product not found");
        }
        const data = await response.json();
        setProduct(data);
      } catch (error: any) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [productId]);

  const addToCart = () => {
    const cartItems = JSON.parse(localStorage.getItem("cartItems") || "[]");
    const existingItem = cartItems.find((item: any) => item.id === product!.id);

    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      cartItems.push({
        id: product!.id,
        name: product!.name,
        price: product!.price,
        quantity: 1,
        image: product!.image,
      });
    }

    localStorage.setItem("cartItems", JSON.stringify(cartItems));
    onSwitchMode("cart");
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  if (!product) {
    return <div>Product not found</div>;
  }

  return (
    <div className="product-page">
      <div className="product-body">
        <div className="product-detail-page">

          <div className="product-info">
              <h2>{product.name}</h2>
              <p className="product-price"><strong>Price:</strong> €{product.price}</p>
              <p className="product-category"><strong>Category:</strong> {product.category}</p>
              <p className="product-details"><strong>Description:</strong> {product.description}</p>
              {product.manufacturer && (
                <p className="product-manufacturer"><strong>Manufacturer:</strong> {product.manufacturer}</p>
              )}


            <button className="add-to-cart-button" onClick={addToCart}>
              Add to Cart
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail; // Exporting the renamed component
