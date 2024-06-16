import React, { useState, useEffect } from "react";
import "./buyer_home.css";
import banner from "../../assets/images/banner.jpeg";
import { Product as ProductType } from "../types";

interface BuyerHomeProps {
 user: any;
 onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders" | "product") => void;
 onLogout: () => void;
 onProductClick: (productId: string) => void; // Add this line
}

const BuyerHome: React.FC<BuyerHomeProps> = ({ user, onSwitchMode, onLogout, onProductClick }) => {
 const [products, setProducts] = useState<ProductType[]>([]);
 const [searchTerm, setSearchTerm] = useState("");
 const [filteredProducts, setFilteredProducts] = useState<ProductType[]>([]);
 const [suggestions, setSuggestions] = useState<string[]>([]);

 const fetchProducts = async () => {
        try {
          const token = localStorage.getItem('token');
          if (!token) {
            throw new Error('No token found');
          }

          const response = await fetch('/api/products', {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`,
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

  const handleReloadProducts = async () => {
         try {
           const token = localStorage.getItem('token');
           if (!token) {
             throw new Error('No token found');
           }

           const response = await fetch('/api/reload-products', {
             method: 'POST',
             headers: {
               'Content-Type': 'application/json',
               'Authorization': `Bearer ${token}`,
             },
           });
           if (!response.ok) {
             throw new Error(`HTTP error! Status: ${response.status}`);
           }
           const data = await response.json();
           console.log("Response from server:", data);

           if (!data) {
               console.error("Empty response received from the server");
               return;
           }
           fetchProducts();
         } catch (error) {
           console.error("Error fetching products:", error);
         }
  };

  useEffect(() => {
    handleReloadProducts();
    fetchProducts();
  }, []);

 const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
   const term = e.target.value;
   setSearchTerm(term);

   const filtered = products.filter(
     (product) =>
       product.name.toLowerCase().includes(term.toLowerCase()) ||
       product.category.toLowerCase().includes(term.toLowerCase())
   );
   setFilteredProducts(filtered);

   const uniqueSuggestions = Array.from(
     new Set(
       products
         .filter(
           (product) =>
             product.name.toLowerCase().includes(term.toLowerCase()) ||
             product.category.toLowerCase().includes(term.toLowerCase())
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
 };

 return (
   <div className="home-page">
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
     <button className="reload-button" onClick={handleReloadProducts}>Reload Products</button>
     <div className="results-section">
       {filteredProducts.map((product) => (
         <div
           key={product.id}
           className="product-link"
           onClick={() => onProductClick(product.id)} // Update this line
         >
           <div className="product-card">
             <img

               alt={product.name}
               className="product-image"
             />
             <div className="product-info">
               <h2>{product.name}</h2>
               <p className="product-price">{product.price}</p>
               <p className="product-category">{product.category}</p>
               <p className="product-details">{product.description}</p>
               {product.manufacturer && (
                 <p className="product-manufacturer">
                   {product.manufacturer}
                 </p>
               )}
             </div>
           </div>
         </div>
       ))}
     </div>
   </div>
 );
};

export default BuyerHome;