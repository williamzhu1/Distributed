import React, { useState } from "react";
import logo from "../../assets/images/logo.jpeg";

interface LoginProps {
  onLogin: (email: string, password: string) => void;
  onSwitchMode: () => void;
}

const Login: React.FC<LoginProps> = ({ onLogin, onSwitchMode }) => {
  const [loginData, setLoginData] = useState({ email: "", password: "" });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLoginData({ ...loginData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    onLogin(loginData.email, loginData.password);
  };

  return (
    <div className="login-container">
      <img src={logo} alt="Logo" className="logo-img" />
      <h1 className="header">Sign In</h1>
      <form onSubmit={handleSubmit} className="form">
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
        <div className="switch-to-register">
          <a href="/register" onClick={(e) => {e.preventDefault(); onSwitchMode();}}>
            Don't have an account yet? Register
          </a>
        </div>
      </form>
    </div>
  );
};

export default Login;
