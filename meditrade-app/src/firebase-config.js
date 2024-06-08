// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import { getAuth } from "firebase/auth";
import { getFirestore } from 'firebase/firestore';

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyDMzHbMz68MIN_IMQxRJhUlEFg45Buo45o",
  authDomain: "meditrade-c7526.firebaseapp.com",
  projectId: "meditrade-c7526",
  storageBucket: "meditrade-c7526.appspot.com",
  messagingSenderId: "74852326524",
  appId: "1:74852326524:web:36ed444b7b04f9d4487df9",
  measurementId: "G-DMBZY0JH85"
};


// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const auth = getAuth(app);
const db = getFirestore(app);

export { app, analytics, auth, db };
