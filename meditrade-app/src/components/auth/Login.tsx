import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signInWithEmailAndPassword } from "firebase/auth";
import { auth, db } from "../../firebase-config"; // Import the auth instance from firebase-config
import { doc, getDoc } from "firebase/firestore";
import "./login_register.css";
import logo from "../../assets/images/logo.jpeg";
import Footer from "../common/Footer";
// import Item from "../../entities/Item"; 

const Login: React.FC = () => {
  const navigate = useNavigate();
  const [mode, setMode] = useState<"customer" | "supplier">("customer");
  const [loginData, setLoginData] = useState({
    email: "",
    password: "",
  });
  const [errors, setErrors] = useState({
    email: "",
    password: "",
  });

  // Validate form data
  const validateForm = () => {
    let isValid = true;
    const newErrors = {
      email: "",
      password: "",
    };

    if (!loginData.email) {
      newErrors.email = "Email is required";
      isValid = false;
    } else if (!/\S+@\S+\.\S+/.test(loginData.email)) {
      newErrors.email = "Email is invalid";
      isValid = false;
    }

    if (loginData.password.length < 6) {
      newErrors.password = "Password must be at least 6 characters";
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (validateForm()) {
      try {
        const userCredential = await signInWithEmailAndPassword(
          auth,
          loginData.email,
          loginData.password,
        );
        console.log("User signed in:", userCredential);
        const token = await userCredential.user.getIdToken();
        const userId = userCredential.user.uid;
        console.log("Token:", token);

        const userRole = await fetchUserRole(userId);
        console.log("User Role:", userRole);

        localStorage.setItem("token", token);

        await sendTokenToBackend();

        if (userRole === "customer") {
          navigate("/home");
        } else if (userRole === "manager") {
          navigate("/supplier");
        } else {
          throw new Error("Invalid user role");
        }
      } catch (error: any) {
        console.error("Error in user login:", error.message);
        setErrors({ ...errors, password: error.message });
      }
    }
  };

  const fetchUserRole = async (uid: string | null) => {
    if (!uid) throw new Error("No email found for the user");

    const userDocRef = doc(db, "users", uid);
    const userDoc = await getDoc(userDocRef);

    if (userDoc.exists()) {
      const userData = userDoc.data();
      return userData.role;
    } else {
      throw new Error("User document does not exist");
    }
  };

//   const sendTokenToBackend = async () => {
//     const token = localStorage.getItem("token");
//     if (!token) {
//       console.error("No token found in local storage");
//       return;
//     }

//     try {
//       const response = await fetch("http://localhost:8080/api/getitems", {
//         method: "POST",
//         headers: {
//           "Content-Type": "application/json",
//           Authorization: `Bearer ${token}`,
//         },
//         body: JSON.stringify({ additionalData: "yourData" }),
//       });

//       if (!response.ok) {
//         throw new Error("Failed to fetch items from backend");
//       }

//       const responseData = await response.json();
      
//       // Map response data to Item instances
//       const items = responseData.map((itemData: { category: any; description: any; id: any; manufacturer: any; name: any; price: any; stock: any; }) => new Item(
//         itemData.category,
//         itemData.description,
//         itemData.id,
//         itemData.manufacturer,
//         itemData.name,
//         itemData.price,
//         itemData.stock
//       ));
      
//       console.log("Items:", items);
//       return items; // Return the items if needed elsewhere
//     } catch (error) {
//       console.error("Error fetching items from backend:", error);
//     }
// };

  const sendTokenToBackend = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      console.error("No token found in local storage");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/usertest", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ additionalData: "yourData" }),
      });

      if (!response.ok) {
        throw new Error("Failed to send token to backend");
      }

      const responseData = await response.json();
      console.log("Response data:", responseData);
    } catch (error) {
      console.error("Error sending token to backend:", error);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLoginData({ ...loginData, [e.target.name]: e.target.value });
  };

  return (
    <div className="login-register-background">
      <div className="login-container">
        <img src={logo} alt="MediTrade Logo" className="logo-img" />
        <h1 className="login-header">Sign In</h1>
        <div className="login-mode-toggle">
          <button
            className={`toggle-button ${mode === "customer" ? "active" : ""}`}
            onClick={() => setMode("customer")}
          >
            Customer
          </button>
          <button
            className={`toggle-button ${mode === "supplier" ? "active" : ""}`}
            onClick={() => setMode("supplier")}
          >
            Supplier
          </button>
        </div>
        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={loginData.email}
              onChange={handleChange}
              className={errors.email ? "input-error" : ""}
            />
            {errors.email && <p className="error-message">{errors.email}</p>}
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={loginData.password}
              onChange={handleChange}
              className={errors.password ? "input-error" : ""}
            />
            {errors.password && (
              <p className="error-message">{errors.password}</p>
            )}
          </div>
          <button type="submit" className="login-button">
            SIGN IN
          </button>
        </form>
        <div className="switch-to-register">
          <a
            href="/register"
            onClick={(e) => {
              e.preventDefault();
              navigate("/register");
            }}
          >
            Don't have an account? Register!
          </a>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default Login;
