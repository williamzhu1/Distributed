import React, { useState } from "react";
import { signInWithEmailAndPassword, createUserWithEmailAndPassword } from "firebase/auth";
import { auth, db } from "../firebase-config";
import { doc, setDoc, getDoc } from "firebase/firestore";
import Footer from "./common/Footer";
import Header from "./common/Header";
import Login from "./auth/Login";
import Register from "./auth/Register";
import ManageProducts from "./supplier/ManageProducts";
import SupplierHome from "./supplier/SupplierHome";
import BuyerHome from "./buyer/BuyerHome";
import Cart from "./buyer/Cart";
import Trace from "./buyer/Trace";
import ViewOrders from "./supplier/ViewOrders";
import Profile from "./common/Profile";
import OrderConfirmation from "./buyer/OrderConfirmation";
import OrderCompleted from "./buyer/OrderCompleted";
import Product from "./buyer/Product"; // Import the Product component
import "./auth/login_register.css";
import { CartItem } from "./buyer/CartItem"; // Adjust this import according to your project structure

const SinglePageApp: React.FC = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [mode, setMode] = useState<
    "login" | "register" | "home" | "manageProducts" | "cart" | "trace" | "supplierHome" | "viewOrders" | "profile" | "orderConfirmation" | "orderCompleted" | "product"
  >("login");
  const [user, setUser] = useState<any>(null);
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [total, setTotal] = useState<number>(0);
  const [response, setResponse] = useState<any>(null);
  const [selectedProductId, setSelectedProductId] = useState<string | null>(null); // State to handle selected product

  const handleLogin = async (email: string, password: string) => {
      try {
        const userCredential = await signInWithEmailAndPassword(auth, email, password);
        setIsLoggedIn(true);
        const userId = userCredential.user.uid;
        const token = await userCredential.user.getIdToken();
        localStorage.setItem("token", token);
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

  const handleRegister = async (registerData: any) => {
    try {
      const userCredential = await createUserWithEmailAndPassword(auth, registerData.email, registerData.password);
      const user = userCredential.user;

      const userData = {
        email: registerData.email,
        username: registerData.username,
        ...(registerData.role === "customer"
          ? {
              role: "customer",
              firstName: registerData.firstName,
              lastName: registerData.lastName,
              address: registerData.address,
            }
          : {
              companyName: registerData.companyName,
              apikey: registerData.apikey,
              endpoint: registerData.endpoint,
              role: "manager",
            }),
      };

      if (user.uid) {
        await setDoc(doc(db, "users", user.uid), userData);
        setUser(user);
        setIsLoggedIn(true);
        setMode(registerData.role === "customer" ? "home" : "supplierHome");
      }
    } catch (error: any) {
      console.error("Error in user registration:", error.message);
    }
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    setUser(null);
    setMode("login");
  };

  const handleProceedToOrder = (cartItems: CartItem[], total: number) => {
    setCartItems(cartItems);
    setTotal(total);
    setMode("orderConfirmation");
  };

   const handleOrderCompleted = (response: any) => {
      setResponse(response);
      setMode("orderCompleted");
    };

  const handleProductClick = (productId: string) => {
    setSelectedProductId(productId);
    setMode("product");
  };

  return (
    <div className="single-page-app">
      <Header user={user} onSwitchMode={setMode} onLogout={handleLogout} />
      {mode === "login" && <Login onLogin={handleLogin} onSwitchMode={() => setMode("register")} />}
      {mode === "register" && <Register onRegister={handleRegister} onSwitchMode={() => setMode("login")} />}
      {mode === "home" && <BuyerHome user={user} onSwitchMode={setMode} onLogout={handleLogout} onProductClick={handleProductClick} />}
      {mode === "manageProducts" && <ManageProducts user={user} onSwitchMode={setMode} onLogout={handleLogout} />}
      {mode === "supplierHome" && <SupplierHome user={user} onSwitchMode={setMode} onLogout={handleLogout} />}
      {mode === "viewOrders" && <ViewOrders user={user} onSwitchMode={setMode} onLogout={handleLogout} />}
      {mode === "cart" && <Cart user={user} onSwitchMode={setMode} onLogout={handleLogout} onProceedToOrder={handleProceedToOrder} />}
      {mode === "trace" && <Trace user={user} onSwitchMode={setMode} onLogout={handleLogout} />}
      {mode === "orderConfirmation" && <OrderConfirmation cartItems={cartItems} total={total} onOrderCompleted={handleOrderCompleted} />}
      {mode === "orderCompleted" && <OrderCompleted response={response} />}
      {mode === "product" && selectedProductId && <Product productId={selectedProductId} onSwitchMode={setMode} />}
      <Footer />
    </div>
  );
};

export default SinglePageApp;
