import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/auth/Login";
import Register from "./components/auth/Register";
import Home from "./components/buyer/BuyerHome";
import Product from "./components/buyer/Product"; // Import the Product component
import OrderConfirmation from "./components/buyer/OrderConfirmation";
import OrderCompleted from "./components/buyer/OrderCompleted";
import SupplierHome from "./components/supplier/SupplierHome";
import ManageProducts from "./components/supplier/ManageProducts";
import ViewOrders from "./components/supplier/ViewOrders";
import OrderHistory from "./components/supplier/OrderHistory";
import Cart from "./components/buyer/Cart";
import Trace from "./components/buyer/Trace";
import Profile from "./components/common/Profile";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/home" element={<Home />} />
        <Route path="/product/:id" element={<Product />} />
        <Route path="/order" element={<OrderConfirmation />} />
        <Route path="/order-completed" element={<OrderCompleted />} />
        <Route path="/supplier" element={<SupplierHome />} />
        <Route path="/manage-products" element={<ManageProducts />} />
        <Route path="/view-orders" element={<ViewOrders />} />
        <Route path="/order-history" element={<OrderHistory />} />
        <Route path="/cart" element={<Cart />} />
        <Route path="/trace" element={<Trace />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/" element={<Home />} />
      </Routes>
    </Router>
  );
}

export default App;
