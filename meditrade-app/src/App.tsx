import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import SinglePageApp from "./components/SinglePageApp";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/*" element={<SinglePageApp />} />
      </Routes>
    </Router>
  );
}

export default App;
