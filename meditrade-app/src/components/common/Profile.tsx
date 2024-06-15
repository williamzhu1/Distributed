import React from "react";
import { useUser } from "../../contexts/UserContext";
import Header from "../common/Header";
import Footer from "../common/Footer";
import "./profile.css";

const Profile: React.FC = () => {
  const { user } = useUser();

  if (!user) {
    return <p>Loading...</p>;
  }

  return (
    <div className="profile-page">
      <Header />
      <div className="profile-container">
        <h1>Profile</h1>
        <div className="profile-field">
          <label>Email:</label>
          <input type="text" value={user.email} readOnly />
        </div>
        <div className="profile-field">
          <label>Username:</label>
          <input type="text" value={user.username || ""} readOnly />
        </div>
        {user.role === "customer" && (
          <div className="profile-field">
            <label>Address:</label>
            <input type="text" value={user.address || ""} readOnly />
          </div>
        )}
        {user.role === "manager" && (
          <div className="profile-field">
            <label>Company Name:</label>
            <input type="text" value={user.company || ""} readOnly />
          </div>
        )}
      </div>
      <Footer />
    </div>
  );
};

export default Profile;

