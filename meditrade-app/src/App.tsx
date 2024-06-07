import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/auth/Login";
import Register from "./components/auth/Register";
import Home from "./components/buyer/BuyerHome";
import Product from "./components/buyer/Product"; // Import the Product component
import SupplierHome from "./components/supplier/SupplierHome";
import ManageProducts from "./components/supplier/ManageProducts";
import ViewOrders from "./components/supplier/ViewOrders";
import Cart from './components/buyer/Cart';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/home" element={<Home />} />
        <Route path="/product/:id" element={<Product />} />
        <Route path="/supplier" element={<SupplierHome />} />
        <Route path="/manage-products" element={<ManageProducts />} />
        <Route path="/view-orders" element={<ViewOrders />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/" element={<Home />} />
      </Routes>
    </Router>
  );
}

export default App;
