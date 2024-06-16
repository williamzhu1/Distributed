import React, { useState, useEffect } from "react";
import "./manage_products.css";
import { Product as Product } from "../types";

interface ManageProductsProps {
  user: any;
  onSwitchMode: (mode: "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders" | "profile") => void;
  onLogout: () => void;
}

const ManageProducts: React.FC<ManageProductsProps> = ({ user, onSwitchMode, onLogout }) => {
  const [products, setProducts] = useState<Product[]>([]);
  const [newProduct, setNewProduct] = useState<Product>({
    id: "",
    name: "",
    price: 0,
    category: "",
    description: "",
    manufacturer: "",
    stock: 0,
    image: null,
    supplierId: user.uid, // Set supplierId dynamically
  });
  const [imagePreview, setImagePreview] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState<string>("");

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          throw new Error('No token found');
        }

        const response = await fetch(`/api/products?supplierId=${user.uid}`, {
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
      } catch (error) {
        console.error("Error fetching products:", error);
      }
    };

    fetchProducts();
  }, [user.uid]);

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

      const productPayload = { ...newProduct, supplierId: user.uid };

      const response = await fetch('/api/products', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(productPayload),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      // Fetch the updated product list from the backend
      const reloadResponse = await fetch('/api/reload-products', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!reloadResponse.ok) {
        throw new Error(`HTTP error! Status: ${reloadResponse.status}`);
      }

      const reloadedProducts = await reloadResponse.json();
      setProducts(reloadedProducts);

      setNewProduct({
        id: "",
        name: "",
        price: 0,
        category: "",
        description: "",
        manufacturer: "",
        stock: 0,
        image: null,
        supplierId: user.uid,
      });
      setImagePreview(null);
    } catch (error) {
      console.error("Error adding product:", error);
    }
  };


  const deleteProduct = async (id: string) => {
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

  const editProduct = (id: string) => {
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

      // Fetch the updated product list from the backend
      const reloadResponse = await fetch('/api/reload-products', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!reloadResponse.ok) {
        throw new Error(`HTTP error! Status: ${reloadResponse.status}`);
      }

      const reloadedProducts = await reloadResponse.json();
      setProducts(reloadedProducts);

      // Reset the form to add product mode
      setNewProduct({
        id: "",
        name: "",
        price: 0,
        category: "",
        description: "",
        manufacturer: "",
        stock: 0,
        image: null,
        supplierId: user.uid,
      });
      setImagePreview(null);
    } catch (error) {
      console.error("Error updating product:", error);
    }
  };


  const filteredProducts = products.filter((product) =>
    product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    product.category.toLowerCase().includes(searchTerm.toLowerCase()) ||
    product.manufacturer.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="manage-products-page">
      <div className="manage-products-content">
        <h1>Manage Products</h1>
        <input
          type="text"
          placeholder="Search..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <div className="add-product-form">
          <h2>
            {newProduct.id === "" ? "Add New Product" : "Edit Product"}
          </h2>
          <label>
            Product Name:
            <input
              type="text"
              name="name"
              placeholder="Product Name"
              value={newProduct.name}
              onChange={handleChange}
            />
          </label>
          <label>
            Price:
            <input
              type="number"
              name="price"
              placeholder="Price"
              value={newProduct.price}
              onChange={handleChange}
            />
          </label>
          <label>
            Category:
            <input
              type="text"
              name="category"
              placeholder="Category"
              value={newProduct.category}
              onChange={handleChange}
            />
          </label>
          <label>
            Manufacturer:
            <input
              type="text"
              name="manufacturer"
              placeholder="Manufacturer"
              value={newProduct.manufacturer}
              onChange={handleChange}
            />
          </label>
          <label>
            Product Description:
            <textarea
              name="description"
              placeholder="Product Description"
              value={newProduct.description}
              onChange={handleChange}
            ></textarea>
          </label>
          <label>
            Stock:
            <input
              type="number"
              name="stock"
              placeholder="Stock"
              value={newProduct.stock}
              onChange={handleChange}
            />
          </label>
          <label>
            Image:
            <input
              type="file"
              name="image"
              accept="image/*"
              onChange={handleImageChange}
            />
          </label>
          {imagePreview && (
            <img src={imagePreview} alt="Preview" className="image-preview" />
          )}
          <button
            onClick={
              newProduct.id === "" ? addProduct : updateProduct
            }
          >
            {newProduct.id === "" ? "Add Product" : "Update Product"}
          </button>
        </div>
        <div className="product-list">
          <h2>Product List</h2>
          <ul>
            {filteredProducts.map((product) => (
              <li key={product.id}>
                <h3>{product.name}</h3>
                <p><strong>Price:</strong> €{product.price}</p>
                <p><strong>Category:</strong> {product.category}</p>
                <p><strong>Description:</strong> {product.description}</p>
                <p><strong>Manufacturer:</strong> {product.manufacturer}</p>
                <p><strong>Stock:</strong> {product.stock}</p>
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
