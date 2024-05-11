import React, { useState } from 'react';
import './login_register.css'; // Using the same CSS file for consistency

const Login: React.FC = () => {
    const [loginData, setLoginData] = useState({
        username: '',
        password: ''
    });
    const [errors, setErrors] = useState({
        username: '',
        password: ''
    });

    // Validate form data
    const validateForm = () => {
        let isValid = true;
        const newErrors = {
            username: '',
            password: ''
        };

        if (!loginData.username) {
            newErrors.username = 'Username is required';
            isValid = false;
        }

        if (loginData.password.length < 6) {
            newErrors.password = 'Password must be at least 6 characters';
            isValid = false;
        }

        setErrors(newErrors);
        return isValid;
    };

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (validateForm()) {
            console.log('Logging in:', loginData);
            // Submit form logic here or a call to API
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setLoginData({ ...loginData, [e.target.name]: e.target.value });
    };

    return (
        <div className="login-container">
            <h1 className="login-header">Log In</h1>
            <form onSubmit={handleSubmit} className="login-form">
                <div className="form-group">
                    <label htmlFor="username">Username</label>
                    <input type="text" id="username" name="username" value={loginData.username} onChange={handleChange} className={errors.username ? 'input-error' : ''} />
                    {errors.username && <p className="error-message">{errors.username}</p>}
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input type="password" id="password" name="password" value={loginData.password} onChange={handleChange} className={errors.password ? 'input-error' : ''} />
                    {errors.password && <p className="error-message">{errors.password}</p>}
                </div>
                <button type="submit" className="login-button">Log In</button>
            </form>
            <div className="switch-to-register">
                <button onClick={() => { /* Handle route change to register */ }}>Don't have an account? Register!</button>
            </div>
        </div>
    );
};

export default Login;
