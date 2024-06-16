import React, { useState, useEffect } from "react";
import { signInWithEmailAndPassword, createUserWithEmailAndPassword } from "firebase/auth";
import { auth, db } from "../../firebase-config";
import { doc, setDoc, getDoc } from "firebase/firestore";
import "./login_register.css";
import logo from "../../assets/images/logo.jpeg";
import storageImg from "../../assets/images/storage.jpeg";
import ordersImg from "../../assets/images/orders.jpeg";
import Footer from "../common/Footer";

const SinglePageApp: React.FC = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [mode, setMode] = useState<"login" | "register" | "home" | "manageProducts" | "supplierHome" | "viewOrders">("login");
  const [user, setUser] = useState<any>(null);
  const [loginData, setLoginData] = useState({ email: "", password: "" });
  const [registerData, setRegisterData] = useState({
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
    firstName: "",
    lastName: "",
    address: "",
    companyName: "",
    role: "customer",
  });
  const [products, setProducts] = useState<any[]>([]);
  const [newProduct, setNewProduct] = useState({
    id: 0,
    name: "",
    price: "",
    genre: "",
    origin: "",
    details: "",
    supplierId: "",
  });

  const handleLogin = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    try {
      const userCredential = await signInWithEmailAndPassword(auth, loginData.email, loginData.password);
      setIsLoggedIn(true);
      const userId = userCredential.user.uid;
      const userDoc = await getDoc(doc(db, "users", userId));
      if (userDoc.exists()) {
        const userData = userDoc.data();
        setUser({ ...userCredential.user, ...userData });
        if (userData.role === "manager") {
          setMode("supplierHome");
        } else {
          setMode("home");
        }
      }
    } catch (error) {
      console.error("Error logging in:", error);
    }
  };

  const handleRegister = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (registerData.password !== registerData.confirmPassword) {
      alert("Passwords do not match");
      return;
    }
    try {
      const userCredential = await createUserWithEmailAndPassword(auth, registerData.email, registerData.password);
      const user = userCredential.user;
      await setDoc(doc(db, "users", user.uid), {
        ...registerData,
        role: registerData.role === "supplier" ? "manager" : "customer",
      });
      setIsLoggedIn(true);
      setUser(user);
      setMode(registerData.role === "supplier" ? "supplierHome" : "home");
    } catch (error) {
      console.error("Error registering:", error);
    }
  };

  const fetchProducts = async () => {
    // Implement the fetch products logic here
  };

  useEffect(() => {
    if (isLoggedIn) {
      fetchProducts();
    }
  }, [isLoggedIn]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    if (mode === "login") {
      setLoginData({ ...loginData, [e.target.name]: e.target.value });
    } else if (mode === "register") {
      setRegisterData({ ...registerData, [e.target.name]: e.target.value });
    } else {
      setNewProduct({ ...newProduct, [e.target.name]: e.target.value });
    }
  };

  const handleAddProduct = async () => {
    // Implement the add product logic here
  };

  const handleDeleteProduct = async (id: number) => {
    // Implement the delete product logic here
  };

  const handleEditProduct = (id: number) => {
    const product = products.find((product) => product.id === id);
    if (product) {
      setNewProduct(product);
    }
  };

  const handleUpdateProduct = async () => {
    // Implement the update product logic here
  };

  return (
    <div className="single-page-app">
      {mode === "login" && (
        <div className="login-container">
          <img src={logo} alt="Logo" className="logo-img" />
          <h1 className="header">Sign In</h1>
          <form onSubmit={handleLogin} className="form">
            <input
              type="email"
              name="email"
              placeholder="Email"
              value={loginData.email}
              onChange={handleChange}
            />
            <input
              type="password"
              name="password"
              placeholder="Password"
              value={loginData.password}
              onChange={handleChange}
            />
            <button type="submit">SIGN IN</button>
          </form>
          <button onClick={() => setMode("register")}>Register</button>
        </div>
      )}
      {mode === "register" && (
        <div className="register-container">
          <img src={logo} alt="Logo" className="logo-img" />
          <h1 className="header">Register</h1>
          <form onSubmit={handleRegister} className="form">
            <input
              type="email"
              name="email"
              placeholder="Email"
              value={registerData.email}
              onChange={handleChange}
            />
            <input
              type="text"
              name="username"
              placeholder="Username"
              value={registerData.username}
              onChange={handleChange}
            />
            <input
              type="text"
              name="firstName"
              placeholder="First Name"
              value={registerData.firstName}
              onChange={handleChange}
            />
            <input
              type="text"
              name="lastName"
              placeholder="Last Name"
              value={registerData.lastName}
              onChange={handleChange}
            />
            <input
              type="password"
              name="password"
              placeholder="Password"
              value={registerData.password}
              onChange={handleChange}
            />
            <input
              type="password"
              name="confirmPassword"
              placeholder="Confirm Password"
              value={registerData.confirmPassword}
              onChange={handleChange}
            />
            {registerData.role === "customer" ? (
              <input
                type="text"
                name="address"
                placeholder="Address"
                value={registerData.address}
                onChange={handleChange}
              />
            ) : (
              <input
                type="text"
                name="companyName"
                placeholder="Company Name"
                value={registerData.companyName}
                onChange={handleChange}
              />
            )}
            <button type="submit">REGISTER</button>
          </form>
          <button onClick={() => setMode("login")}>Sign In</button>
        </div>
      )}
      {mode === "home" && (
        <div className="home-container">
          <h1>Welcome to the Home Page!</h1>
          <div className="product-list">
            {products.map((product) => (
              <div key={product.id} className="product-item">
                <h3>{product.name}</h3>
                <p>{product.price}</p>
                <p>{product.genre}</p>
                <p>{product.origin}</p>
                <p>{product.details}</p>
              </div>
            ))}
          </div>
        </div>
      )}
      {mode === "manageProducts" && (
        <div className="manage-products-container">
          <h1>Manage Products</h1>
          <form className="product-form">
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
            <button onClick={handleAddProduct}>
              {newProduct.id === 0 ? "Add Product" : "Update Product"}
            </button>
          </form>
          <div className="product-list">
            {products.map((product) => (
              <div key={product.id} className="product-item">
                <h3>{product.name}</h3>
                <p>{product.price}</p>
                <p>{product.genre}</p>
                <p>{product.origin}</p>
                <p>{product.details}</p>
                <button onClick={() => handleEditProduct(product.id)}>Edit</button>
                <button onClick={() => handleDeleteProduct(product.id)}>Delete</button>
              </div>
            ))}
          </div>
        </div>
      )}
      {mode === "supplierHome" && (
        <div className="supplier-home-page">
          <div className="supplier-content">
            <h1>Welcome to the Supplier Page!</h1>
            <div className="supplier-actions">
              <div className="action-card">
                <div className="image-container">
                  <img src={storageImg} alt="Manage Products" />
                </div>
                <h2>Manage Products</h2>
                <p>Keep your inventory up-to-date with ease.</p>
                <button
                  className="action-button"
                  onClick={() => setMode("manageProducts")}
                >
                  Go to Product Management
                </button>
              </div>
              <div className="action-card">
                <div className="image-container">
                  <img src={ordersImg} alt="View Orders" />
                </div>
                <h2>View Orders</h2>
                <p>Monitor your sales and fulfill orders efficiently.</p>
                <button
                  className="action-button"
                  onClick={() => setMode("viewOrders")}
                >
                  View Orders
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
      {mode === "viewOrders" && (
        <div className="view-orders-page">
          <h1>View Orders</h1>
          {/* Here you can add the logic and UI for viewing orders */}
          <button onClick={() => setMode("supplierHome")}>Back to Supplier Home</button>
        </div>
      )}
      <Footer />
    </div>
  );
};

export default SinglePageApp;
