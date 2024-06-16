import React, { useState } from "react";
import logo from "../../assets/images/logo.jpeg";
import { signInWithEmailAndPassword } from "firebase/auth";
import { auth, db } from "../../firebase-config";
import { doc, getDoc } from "firebase/firestore";
import { useNavigate } from "react-router-dom";

interface LoginProps {
  onLogin: (email: string, password: string) => void;
   onSwitchMode: () => void;
}

const Login: React.FC<LoginProps> = ({ onSwitchMode }) => {
  const navigate = useNavigate();
  const [loginData, setLoginData] = useState({ email: "", password: "" });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLoginData({ ...loginData, [e.target.name]: e.target.value });
  };

  const handleLogin = async (email: string, password: string) => {
    try {
      const userCredential = await signInWithEmailAndPassword(auth, email, password);
      console.log("User signed in:", userCredential);
      const token = await userCredential.user.getIdToken();
      const userId = userCredential.user.uid;

      // Fetch user role from Firestore
      const userRole = await fetchUserRole(userId);
      console.log("User Role:", userRole);

      localStorage.setItem("token", token);

      if (userRole === "customer") {
        navigate("/home");
      } else if (userRole === "manager") {
        navigate("/supplier");
      } else {
        throw new Error("Invalid user role");
      }
    } catch (error: any) {
      console.error("Error in user login:", error.message);
      // Handle login error
    }
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    handleLogin(loginData.email, loginData.password);
  };

  const fetchUserRole = async (uid: string | null) => {
    if (!uid) throw new Error("No user ID found");

    const userDocRef = doc(db, "users", uid);
    const userDoc = await getDoc(userDocRef);

    if (userDoc.exists()) {
      const userData = userDoc.data();
      return userData?.role;
    } else {
      throw new Error("User document does not exist");
    }
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
      </form>
      <button onClick={onSwitchMode}>Register</button>
    </div>
  );
};

export default Login;
