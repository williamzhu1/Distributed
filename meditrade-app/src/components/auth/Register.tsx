import React, { useState } from "react";
import logo from "../../assets/images/logo.jpeg";

interface RegisterProps {
  onRegister: (registerData: any) => void;
  onSwitchMode: () => void;
}

const Register: React.FC<RegisterProps> = ({ onRegister, onSwitchMode }) => {
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

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setRegisterData({ ...registerData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    onRegister(registerData);
  };

  return (
    <div className="register-container">
      <img src={logo} alt="Logo" className="logo-img" />
      <h1 className="header">Register</h1>
      <form onSubmit={handleSubmit} className="form">
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
      <button onClick={onSwitchMode}>Sign In</button>
    </div>
  );
};

export default Register;
