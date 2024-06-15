import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Header from "../common/Header";
import Footer from "../common/Footer";
import { CartItem } from "./CartItem"; // Import the CartItem type
import "./order_confirmation.css";
import { Order } from "../../entities/Order";
import { useUser } from "../../contexts/UserContext";

const OrderConfirmation: React.FC = () => {
 const navigate = useNavigate();
 const location = useLocation();
 const { cartItems, total } = location.state as {
   cartItems: CartItem[];
   total: number;
 };

 const { user } = useUser();

 const [formData, setFormData] = useState({
   name: "",
   address: "",
   paymentMethod: "Credit Card",
 });

 const handleChange = (
   e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>,
 ) => {
   const { name, value } = e.target;
   setFormData((prevData) => ({
     ...prevData,
     [name]: value,
   }));
 };

 const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
   e.preventDefault();
   await sendCartCloud(formData.address);
   await sendOrderToDist(cartItems, formData);
   navigate("/order-completed");
 };

 async function sendCartCloud(address: string) {
   const token = localStorage.getItem("token");
   if (!token) {
     console.error("No token found in local storage");
     return;
   }

   try {
     const response = await fetch("/api/createorder", {
       method: "POST",
       headers: {
         "Content-Type": "application/json",
         Authorization: `Bearer ${token}`,
       },
       body: JSON.stringify({
         firstName: user?.firstName,
         lastName: user?.lastName,
         address: address,
         items: cartItems.map(item => ({ id: item.id, quantity: item.quantity })),
         price: total
       }),
     });

     const contentType = response.headers.get("Content-Type");
     if (!response.ok) {
       throw new Error("Failed to send cart to firebase");
     }

     if (contentType && contentType.includes("application/json")) {
       const responseData = await response.json();
       console.log("Response data:", responseData);
     } else {
       const textData = await response.text();
       console.log("Response data is not JSON:", textData);
     }
   } catch (error) {
     console.error("Error sending cart to firebase:", error);
   }
 };

 async function sendOrderToDist(cartItems: CartItem[], formData: any) {
   const suppliers = await fetchSuppliers();

   // Group cart items by company
   const groupedItems = cartItems.reduce((acc, item) => {
     acc[item.supplier] = acc[item.supplier] || [];
     acc[item.supplier].push(item);
     return acc;
   }, {} as { [key: string]: CartItem[] });

   // For each group, send an order
   for (const [company, items] of Object.entries(groupedItems)) {
     const supplier = suppliers.find(s => s.company === company);
     if (!supplier) {
       console.error('Supplier not found for company:', company);
       continue;
     }

     // Create an order object
     const orderItems = items.reduce((acc, item) => {
       acc[item.id] = item.quantity;
       return acc;
     }, {} as { [key: number]: number });

     const order = new Order("1", "1", formData.address, orderItems, "pending");

     // Send the order
     await sendOrder(order, supplier.apiKey, supplier.endpoint);
   }
 }

 interface Supplier {
   apiKey: string;
   company: string;
   endpoint: string;
 }

 async function fetchSuppliers(): Promise<Supplier[]> {
   try {
     const response = await fetch('/getsuppliers');
     if (!response.ok) {
       throw new Error(`HTTP error! status: ${response.status}`);
     }
     const suppliers: Supplier[] = await response.json();
     return suppliers;
   } catch (error) {
     console.error('Failed to fetch users:', error);
     return [];
   }
 }

 async function sendOrder(order: Order, apiKey: string, endpoint: string) {
   try {
     const response = await fetch('/api/' + endpoint, {
       method: 'POST',
       headers: {
         'Content-Type': 'application/json',
         'ApiKey': apiKey
       },
       body: JSON.stringify(order)
     });

     if (!response.ok) {
       throw new Error('Failed to send order');
     }

     const result = await response.json();
     console.log('Order sent successfully:', result);
   } catch (error) {
     console.error('Error sending order:', error);
   }
 }

 return (
   <div className="order-confirmation-page">
     <Header />
     <div className="order-confirmation-container">
       <h1>Order Confirmation</h1>
       <table>
         <thead>
           <tr>
             <th>Product</th>
             <th>Price</th>
             <th>Quantity</th>
             <th>Total</th>
           </tr>
         </thead>
         <tbody>
           {cartItems.map((item) => (
             <tr key={item.id}>
               <td>{item.name}</td>
               <td>{item.price}</td>
               <td>{item.quantity}</td>
               <td>
                 ${typeof item.price === 'string' ? (parseFloat(item.price.slice(1)) * item.quantity).toFixed(2) : (item.price * item.quantity).toFixed(2)}
               </td>
             </tr>
           ))}
         </tbody>
       </table>
       <h2>Total: ${total.toFixed(2)}</h2>
       <form className="order-form" onSubmit={handleSubmit}>
         <div>
           <label>Name:</label>
           <input
             type="text"
             name="name"
             value={formData.name}
             onChange={handleChange}
             required
           />
         </div>
         <div>
           <label>Address:</label>
           <input
             type="text"
             name="address"
             value={formData.address}
             onChange={handleChange}
             required
           />
         </div>
         <div>
           <label>Payment Method:</label>
           <select
             name="paymentMethod"
             value={formData.paymentMethod}
             onChange={handleChange}
           >
             <option value="Credit Card">Credit Card</option>
             <option value="PayPal">PayPal</option>
             <option value="Bank Transfer">Bank Transfer</option>
           </select>
         </div>
         <button type="submit" className="confirm-button">
           Confirm Order
         </button>
       </form>
     </div>
     <Footer />
   </div>
 );
};

export default OrderConfirmation;