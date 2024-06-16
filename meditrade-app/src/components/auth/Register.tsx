import { auth, db } from "../../firebase-config"; // Import the auth instance from firebase-config
import { createUserWithEmailAndPassword } from "firebase/auth";
import { doc, setDoc } from "firebase/firestore";
import React, { useState, useEffect } from "react";
import logo from "../../assets/images/logo.jpeg";
import "./login_register.css";

interface RegisterProps {
  onRegister: (registerData: any) => void;
  onSwitchMode: () => void;
}

const Register: React.FC<RegisterProps> = ({ onRegister, onSwitchMode }) => {
  const [mode, setMode] = useState<"customer" | "supplier">("customer");
  const [formData, setFormData] = useState({
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
    firstName: "",
    lastName: "",
    address: "", // For customer
    companyName: "", // For supplier
    apiKey: "", // For supplier
    endpoint: "", // For supplier
  });
  const [errors, setErrors] = useState({
    email: "",
    username: "",
    password: "",
    confirmPassword: "",
    firstName: "",
    lastName: "",
    address: "",
    companyName: "",
    apiKey: "",
    endpoint: "",
  });
  const [isFormValid, setIsFormValid] = useState(false);

  const validateForm = () => {
    let isValid = true;
    const newErrors = {
      email: "",
      username: "",
      password: "",
      confirmPassword: "",
      firstName: "",
      lastName: "",
      address: "",
      companyName: "",
      apiKey: "",
      endpoint: "",
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

    if (mode === "customer" && !formData.firstName) {
      newErrors.firstName = "First name is required";
      isValid = false;
    }

    if (mode === "customer" && !formData.lastName) {
      newErrors.lastName = "Last name is required";
      isValid = false;
    }

    if (mode === "customer" && !formData.address) {
      newErrors.address = "Address is required";
      isValid = false;
    }

    if (mode === "supplier") {
      if (!formData.companyName) {
        newErrors.companyName = "Company name is required";
        isValid = false;
      }
      if (!formData.apiKey) {
        newErrors.apiKey = "API key is required";
        isValid = false;
      }
      if (!formData.endpoint) {
        newErrors.endpoint = "Endpoint is required";
        isValid = false;
      }
    }

    setErrors(newErrors);
    setIsFormValid(isValid);
  };

  useEffect(() => {
    validateForm();
  }, [formData, mode]);

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (isFormValid) {
      onRegister({ ...formData, role: mode });
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
            <>
              <div className="form-group">
                <label htmlFor="firstName">First Name</label>
                <input
                  type="text"
                  id="firstName"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  className={errors.firstName ? "input-error" : ""}
                />
                {errors.firstName && (
                  <p className="error-message">{errors.firstName}</p>
                )}
              </div>
              <div className="form-group">
                <label htmlFor="lastName">Last Name</label>
                <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  className={errors.lastName ? "input-error" : ""}
                />
                {errors.lastName && (
                  <p className="error-message">{errors.lastName}</p>
                )}
              </div>
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
            </>
          )}
          {mode === "supplier" && (
            <>
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
              <div className="form-group">
                <label htmlFor="apiKey">API Key</label>
                <input
                  type="text"
                  id="apiKey"
                  name="apiKey"
                  value={formData.apiKey}
                  onChange={handleChange}
                  className={errors.apiKey ? "input-error" : ""}
                />
                {errors.apiKey && (
                  <p className="error-message">{errors.apiKey}</p>
                )}
              </div>
              <div className="form-group">
                <label htmlFor="endpoint">Endpoint</label>
                <input
                  type="text"
                  id="endpoint"
                  name="endpoint"
                  value={formData.endpoint}
                  onChange={handleChange}
                  className={errors.endpoint ? "input-error" : ""}
                />
                                {errors.endpoint && (
                                  <p className="error-message">{errors.endpoint}</p>
                                )}
                              </div>
                            </>
                          )}
                          <button
                            type="submit"
                            className="register-button"
                            disabled={!isFormValid}
                          >
                            REGISTER
                          </button>
                          <div className="switch-to-login">
                            <a
                              href="/login"
                              onClick={(e) => {
                                e.preventDefault();
                                onSwitchMode();
                              }}
                            >
                              Already have an account? Sign in!
                            </a>
                          </div>
                        </form>
                      </div>
                    </div>
                  );
                };

                export default Register;