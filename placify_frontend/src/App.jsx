import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Auth from './pages/Auth';
import StudentDashboard from './pages/StudentDashboard';
import AdminDashboard from './pages/AdminDashboard';
import './App.css';

function App() {
  const role = localStorage.getItem('role');
  const token = localStorage.getItem('token');

  return (
    <Router>
      <div className="app-container">
        <Navbar />
        <main className="container" style={{ marginTop: '2rem' }}>
          <Routes>
            <Route path="/" element={!token ? <Auth /> : <Navigate to={role === 'ADMIN' ? '/admin' : '/student'} />} />
            <Route path="/student" element={token && role === 'STUDENT' ? <StudentDashboard /> : <Navigate to="/" />} />
            <Route path="/admin" element={token && role === 'ADMIN' ? <AdminDashboard /> : <Navigate to="/" />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
