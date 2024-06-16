// src/components/supplier/ManageProducts.tsx
import React, { useState } from "react";
import "./manage_products.css";

interface Product {
  id: number;
  name: string;
  price: string;
  genre: string;
  origin: string;
  details: string;
  image: string | null;
  supplierId: string; // Added supplierId to the Product interface
}

interface ManageProductsProps {
  user: any;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders" | "profile") => void;
  onLogout: () => void;
}

const initialProducts: Product[] = [
  {
    id: 1,
    name: "Herbal Tea",
    price: "$10.00",
    genre: "Herbal",
    origin: "China",
    details: "A refreshing herbal tea from the mountains of China.",
    image: null,
    supplierId: "supplier1", // Example supplier ID
  },
  // Add more initial products as needed
];

const ManageProducts: React.FC<ManageProductsProps> = ({ user, onSwitchMode, onLogout }) => {
  const [products, setProducts] = useState<Product[]>(initialProducts);
  const [newProduct, setNewProduct] = useState<Product>({
    id: products.length + 1,
    name: "",
    price: "",
    genre: "",
    origin: "",
    details: "",
    image: null,
    supplierId: "supplier1", // Example supplier ID
  });
  const [imagePreview, setImagePreview] = useState<string | null>(null);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    setNewProduct({ ...newProduct, [e.target.name]: e.target.value });
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setNewProduct({ ...newProduct, image: URL.createObjectURL(file) });
      setImagePreview(URL.createObjectURL(file));
    }
  };

  const addProduct = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('No token found');
      }

      const response = await fetch('/api/products', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(newProduct),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const data = await response.json(); // Parse the response as JSON
      console.log(data);
      setProducts([...products, { ...newProduct, id: data.id }]); // Use the returned ID
      setNewProduct({
        id: products.length + 1,
        name: "",
        price: "",
        genre: "",
        origin: "",
        details: "",
        image: null,
        supplierId: "supplier1", // Example supplier ID
      });
      setImagePreview(null);
    } catch (error) {
      console.error("Error adding product:", error);
    }
  };

  const deleteProduct = async (id: number) => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('No token found');
      }

      const response = await fetch(`/api/products/${id}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      });
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      setProducts(products.filter((product) => product.id !== id));
    } catch (error) {
      console.error("Error deleting product:", error);
    }
  };

  const editProduct = (id: number) => {
    const product = products.find((product) => product.id === id);
    if (product) {
      setNewProduct(product);
      setImagePreview(product.image);
    }
  };

  const updateProduct = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('No token found');
      }

      const response = await fetch(`/api/products/${newProduct.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(newProduct),
      });
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      const data = await response.json();
      console.log(data);
      setProducts(
        products.map((product) =>
          product.id === newProduct.id ? newProduct : product,
        ),
      );
      setNewProduct({
        id: products.length + 1,
        name: "",
        price: "",
        genre: "",
        origin: "",
        details: "",
        image: null,
        supplierId: "supplier1", // Example supplier ID
      });
      setImagePreview(null);
    } catch (error) {
      console.error("Error updating product:", error);
    }
  };

  return (
    <div className="manage-products-page">
      <div className="manage-products-content">
        <h1>Manage Products</h1>
        <div className="add-product-form">
          <h2>
            {newProduct.id > products.length
              ? "Add New Product"
              : "Edit Product"}
          </h2>
          <input
            type="text"
            name="name"
            placeholder="Product Name"
            value={newProduct.name}
            onChange={handleChange}
          />
          <input
            type="text"
            name="price"
            placeholder="Price"
            value={newProduct.price}
            onChange={handleChange}
          />
          <input
            type="text"
            name="genre"
            placeholder="Genre"
            value={newProduct.genre}
            onChange={handleChange}
          />
          <input
            type="text"
            name="origin"
            placeholder="Origin"
            value={newProduct.origin}
            onChange={handleChange}
          />
          <textarea
            name="details"
            placeholder="Product Details"
            value={newProduct.details}
            onChange={handleChange}
          ></textarea>
          <input
            type="file"
            name="image"
            accept="image/*"
            onChange={handleImageChange}
          />
          {imagePreview && (
            <img src={imagePreview} alt="Preview" className="image-preview" />
          )}
          <button
            onClick={
              newProduct.id > products.length ? addProduct : updateProduct
            }
          >
            {newProduct.id > products.length ? "Add Product" : "Update Product"}
          </button>
        </div>
        <div className="product-list">
          <h2>Product List</h2>
          <ul>
            {products.map((product) => (
              <li key={product.id}>
                <h3>{product.name}</h3>
                <p>{product.price}</p>
                <p>{product.genre}</p>
                <p>{product.origin}</p>
                <p>{product.details}</p>
                {product.image && (
                  <img
                    src={product.image}
                    alt={product.name}
                    className="product-list-image"
                  />
                )}
                <button onClick={() => editProduct(product.id)}>Edit</button>
                <button onClick={() => deleteProduct(product.id)}>Delete</button>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default ManageProducts;
