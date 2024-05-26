import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./login_register.css";
import logo from "../../assets/images/logo.jpeg";
import Footer from "../common/Footer";

const Register: React.FC = () => {
  const navigate = useNavigate();
  const [mode, setMode] = useState<"customer" | "supplier">("customer");
  const [formData, setFormData] = useState({
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
    address: "", // For customer
    companyName: "", // For supplier
  });
  const [errors, setErrors] = useState({
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
    address: "",
    companyName: "",
  });

  // Check if all fields are valid
  const allFieldsValid = () => {
    const baseValid =
      formData.email.match(/\S+@\S+\.\S+/) &&
      formData.username &&
      formData.password.length >= 6 &&
      formData.password === formData.confirmPassword;
    if (mode === "customer") {
      return baseValid && formData.address;
    } else {
      return baseValid && formData.companyName;
    }
  };

  const validateForm = () => {
    let isValid = true;
    const newErrors = {
      email: "",
      username: "",
      password: "",
      confirmPassword: "",
      address: "",
      companyName: "",
    };

    if (!formData.email) {
      newErrors.email = "Email is required";
      isValid = false;
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = "Email is invalid";
      isValid = false;
    }

    if (!formData.username) {
      newErrors.username = "Username is required";
      isValid = false;
    }

    if (formData.password.length < 6) {
      newErrors.password = "Password must be at least 6 characters";
      isValid = false;
    }

    if (formData.password !== formData.confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
      isValid = false;
    }

    if (mode === "customer" && !formData.address) {
      newErrors.address = "Address is required";
      isValid = false;
    }

    if (mode === "supplier" && !formData.companyName) {
      newErrors.companyName = "Company name is required";
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (validateForm()) {
      console.log("Registering:", formData);
      // Submit form logic here or a call to API
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <div className="login-register-background">
      <div className="register-container">
        <img src={logo} alt="MediTrade Logo" className="logo-img" />
        <h1 className="register-header">Register</h1>
        <div className="register-mode-toggle">
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
        <form onSubmit={handleSubmit} className="register-form">
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className={errors.email ? "input-error" : ""}
            />
            {errors.email && <p className="error-message">{errors.email}</p>}
          </div>
          <div className="form-group">
            <label htmlFor="username">Username</label>
            <input
              type="text"
              id="username"
              name="username"
              value={formData.username}
              onChange={handleChange}
              className={errors.username ? "input-error" : ""}
            />
            {errors.username && (
              <p className="error-message">{errors.username}</p>
            )}
          </div>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className={errors.password ? "input-error" : ""}
            />
            {errors.password && (
              <p className="error-message">{errors.password}</p>
            )}
          </div>
          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              className={errors.confirmPassword ? "input-error" : ""}
            />
            {errors.confirmPassword && (
              <p className="error-message">{errors.confirmPassword}</p>
            )}
          </div>
          {mode === "customer" && (
            <div className="form-group">
              <label htmlFor="address">Address</label>
              <input
                type="text"
                id="address"
                name="address"
                value={formData.address}
                onChange={handleChange}
                className={errors.address ? "input-error" : ""}
              />
              {errors.address && (
                <p className="error-message">{errors.address}</p>
              )}
            </div>
          )}
          {mode === "supplier" && (
            <div className="form-group">
              <label htmlFor="companyName">Company Name</label>
              <input
                type="text"
                id="companyName"
                name="companyName"
                value={formData.companyName}
                onChange={handleChange}
                className={errors.companyName ? "input-error" : ""}
              />
              {errors.companyName && (
                <p className="error-message">{errors.companyName}</p>
              )}
            </div>
          )}
          <button
            type="submit"
            className="register-button"
            disabled={!allFieldsValid()}
          >
            REGISTER
          </button>
          <div className="switch-to-login">
            <a
              href="/login"
              onClick={(e) => {
                e.preventDefault();
                navigate("/login");
              }}
            >
              Already have an account? Sign in!
            </a>
          </div>
        </form>
      </div>
      <Footer />
    </div>
  );
};

export default Register;
