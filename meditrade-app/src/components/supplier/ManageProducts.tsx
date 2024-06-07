import React, { useState } from "react";
import Header from "../common/Header";
import Footer from "../common/Footer";
import "./manage_products.css";

interface Product {
  id: number;
  name: string;
  price: string;
  genre: string;
  origin: string;
  details: string;
  image: string | null;
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
  },
  // Add more initial products as needed
];

const ManageProducts: React.FC = () => {
  const [products, setProducts] = useState<Product[]>(initialProducts);
  const [newProduct, setNewProduct] = useState<Product>({
    id: products.length + 1,
    name: "",
    price: "",
    genre: "",
    origin: "",
    details: "",
    image: null,
  });
  const [imagePreview, setImagePreview] = useState<string | null>(null);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
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

  const addProduct = () => {
    setProducts([...products, newProduct]);
    setNewProduct({
      id: products.length + 1,
      name: "",
      price: "",
      genre: "",
      origin: "",
      details: "",
      image: null,
    });
    setImagePreview(null);
  };

  const deleteProduct = (id: number) => {
    setProducts(products.filter((product) => product.id !== id));
  };

  const editProduct = (id: number) => {
    const product = products.find((product) => product.id === id);
    if (product) {
      setNewProduct(product);
      setImagePreview(product.image);
    }
  };

  const updateProduct = () => {
    setProducts(
      products.map((product) =>
        product.id === newProduct.id ? newProduct : product
      )
    );
    setNewProduct({
      id: products.length + 1,
      name: "",
      price: "",
      genre: "",
      origin: "",
      details: "",
      image: null,
    });
    setImagePreview(null);
  };

  return (
    <div className="manage-products-page">
      <Header />
      <div className="manage-products-content">
        <h1>Manage Products</h1>
        <div className="add-product-form">
          <h2>{newProduct.id > products.length ? "Add New Product" : "Edit Product"}</h2>
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
          {imagePreview && <img src={imagePreview} alt="Preview" className="image-preview" />}
          <button onClick={newProduct.id > products.length ? addProduct : updateProduct}>
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
                {product.image && <img src={product.image} alt={product.name} className="product-list-image" />}
                <button onClick={() => editProduct(product.id)}>Edit</button>
                <button onClick={() => deleteProduct(product.id)}>Delete</button>
              </li>
            ))}
          </ul>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default ManageProducts;