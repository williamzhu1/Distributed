import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signInWithEmailAndPassword } from "firebase/auth";
import { auth } from '../../firebase-config'; // Import the auth instance from firebase-config
import "./login_register.css";
import logo from "../../assets/images/logo.jpeg";
import Footer from "../common/Footer";

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
        const userCredential = await signInWithEmailAndPassword(auth, loginData.email, loginData.password);
        console.log("User signed in:", userCredential);
        // Navigate to the home page or dashboard after successful login
        navigate("/home"); // Replace with your actual home or dashboard page
      } catch (error: any) {
        console.error("Error in user login:", error.message);
        setErrors({ ...errors, password: error.message });
      }
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
            {errors.email && (
              <p className="error-message">{errors.email}</p>
            )}
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

