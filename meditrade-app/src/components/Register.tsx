import React, { useState } from 'react';
import './login_register.css'; // CSS for styling
import logo from '../images/logo.jpeg'; // Logo import

const Register: React.FC = () => {
    const [formData, setFormData] = useState({
        email: '',
        username: '',
        password: '',
        confirmPassword: ''
    });
    const [errors, setErrors] = useState({
        email: '',
        username: '',
        password: '',
        confirmPassword: ''
    });

    // Check if all fields are valid
    const allFieldsValid = () => {
        return (
            formData.email.match(/\S+@\S+\.\S+/) &&
            formData.username &&
            formData.password.length >= 6 &&
            formData.password === formData.confirmPassword
        );
    };

    const validateForm = () => {
        let isValid = true;
        const newErrors = {
            email: '',
            username: '',
            password: '',
            confirmPassword: ''
        };

        if (!formData.email) {
            newErrors.email = 'Email is required';
            isValid = false;
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = 'Email is invalid';
            isValid = false;
        }

        if (!formData.username) {
            newErrors.username = 'Username is required';
            isValid = false;
        }

        if (formData.password.length < 6) {
            newErrors.password = 'Password must be at least 6 characters';
            isValid = false;
        }

        if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = 'Passwords do not match';
            isValid = false;
        }

        setErrors(newErrors);
        return isValid;
    };

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (validateForm()) {
            console.log('Registering:', formData);
            // Submit form logic here or a call to API
        }
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    return (
        <div className="register-container">
            <img src={logo} alt="MediTrade Logo" className="logo-img" />
            <h1 className="register-header">Register</h1>
            <form onSubmit={handleSubmit} className="register-form">
                <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input type="email" id="email" name="email" value={formData.email} onChange={handleChange} className={errors.email ? 'input-error' : ''} />
                    {errors.email && <p className="error-message">{errors.email}</p>}
                </div>
                <div className="form-group">
                    <label htmlFor="username">Username</label>
                    <input type="text" id="username" name="username" value={formData.username} onChange={handleChange} className={errors.username ? 'input-error' : ''} />
                    {errors.username && <p className="error-message">{errors.username}</p>}
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input type="password" id="password" name="password" value={formData.password} onChange={handleChange} className={errors.password ? 'input-error' : ''} />
                    {errors.password && <p className="error-message">{errors.password}</p>}
                </div>
                <div className="form-group">
                    <label htmlFor="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" value={formData.confirmPassword} onChange={handleChange} className={errors.confirmPassword ? 'input-error' : ''} />
                    {errors.confirmPassword && <p className="error-message">{errors.confirmPassword}</p>}
                </div>
                <button type="submit" className="register-button" disabled={!allFieldsValid()}>Register</button>
                <div className="switch-to-login">
                    <button onClick={() => { /* Handle route change to login */ }}>Already have an account? Log in!</button>
                </div>
            </form>
        </div>
    );
};

export default Register;
