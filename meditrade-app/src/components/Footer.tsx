import React from 'react';
import './footer.css'; // Ensure you have a CSS file for styling

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-content">
        <p>1234 Street Address, City, State</p>
        <p>Email: contact@example.com | Tel: (123) 456-7890</p>
        <p>&copy; {new Date().getFullYear()} MediTrade - All Rights Reserved</p>
      </div>
    </footer>
  );
};

export default Footer;
