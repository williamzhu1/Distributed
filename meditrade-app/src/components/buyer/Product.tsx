import React, { useState, useEffect } from "react";
import "./product.css";
import images from "../../assets/images/products/index";
import Header from "../common/Header";
import Footer from "../common/Footer";

// Explicitly type the 'images' object
const imagesTyped: { [key: string]: string } = images;

interface Product {
 id: string;
 name: string;
 price: string;
 genre: string;
 origin: string;
 details: string;
 manufacturer?: { name: string; info: string };
 image: string;
}

interface ProductProps {
 productId: string;
 onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders" | "product") => void;
}

const Product: React.FC<ProductProps> = ({ productId, onSwitchMode }) => {
 const [product, setProduct] = useState<Product | null>(null);
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
     <Header user={null} onSwitchMode={onSwitchMode} onLogout={() => {}} />
     <div className="product-body">
       <div className="product-detail-page">
         <img
           src={product.image} // Ensure this is the correct image URL
           alt={product.name}
           className="product-image"
         />
         <div className="product-info">
           <h1>{product.name}</h1>
           <p className="product-price">{product.price}</p>
           <p className="product-genre">Genre: {product.genre}</p>
           <p className="product-origin">Origin: {product.origin}</p>
           <p className="product-description">{product.details}</p>
           {product.manufacturer && (
             <>
               <p className="product-manufacturer">Manufacturer: {product.manufacturer.name}</p>
               <p className="manufacturer-info">Info: {product.manufacturer.info}</p>
             </>
           )}
           <button className="add-to-cart-button" onClick={addToCart}>
             Add to Cart
           </button>
         </div>
       </div>
     </div>
     <Footer />
   </div>
 );
};



export default Product;