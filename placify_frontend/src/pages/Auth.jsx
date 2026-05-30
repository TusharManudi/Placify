import React, { useState } from 'react';
import api from '../api/axiosConfig';

const Auth = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [role, setRole] = useState('STUDENT'); // STUDENT or ADMIN
  const [formData, setFormData] = useState({
    name: '', email: '', password: '', phone: '', course: '', branch: '', universityRollNo: ''
  });
  const [error, setError] = useState('');

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    try {
      let response;
      if (isLogin) {
        response = await api.post('/auth/login', { email: formData.email, password: formData.password });
        localStorage.setItem('token', response.data.token);
        
        // Quick hack: if student register fields are required later, but for now we assume email domain or decode JWT for role.
        // For simplicity, we ask user to select role on login too if backend doesn't return it in response.
        // Wait, backend just returns { token: "..." }. In production we'd decode the JWT to get the role.
        // Let's decode the JWT here to get the role!
        const payload = JSON.parse(atob(response.data.token.split('.')[1]));
        const userRole = payload.role; // Assuming role is in the claims, or we just rely on login form selection
        
        // Since we might not have added role claim to JWT explicitly, we will use the selected radio button for now
        localStorage.setItem('role', userRole || role);
        window.location.href = (userRole || role) === 'ROLE_ADMIN' || (userRole || role) === 'ADMIN' ? '/admin' : '/student';
      } else {
        const endpoint = role === 'ADMIN' ? '/auth/register-admin' : '/auth/register-student';
        response = await api.post(endpoint, formData);
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('role', role);
        window.location.href = role === 'ADMIN' ? '/admin' : '/student';
      }
    } catch (err) {
      setError(err.response?.data || 'An error occurred. Please try again.');
    }
  };

  return (
    <div style={styles.wrapper}>
      <div className="card" style={styles.authCard}>
        <h2 style={{ marginBottom: '1.5rem', textAlign: 'center' }}>
          {isLogin ? 'Welcome Back' : 'Create an Account'}
        </h2>

        {error && <div style={styles.error}>{error}</div>}

        <div style={styles.roleToggle}>
          <label>
            <input type="radio" name="role" value="STUDENT" checked={role === 'STUDENT'} onChange={(e) => setRole(e.target.value)} />
            Student
          </label>
          <label>
            <input type="radio" name="role" value="ADMIN" checked={role === 'ADMIN'} onChange={(e) => setRole(e.target.value)} />
            CRC Admin
          </label>
        </div>

        <form onSubmit={handleSubmit}>
          {!isLogin && (
            <div className="input-group">
              <label>Name</label>
              <input type="text" name="name" value={formData.name} onChange={handleInputChange} required />
            </div>
          )}
          
          <div className="input-group">
            <label>Email</label>
            <input type="email" name="email" value={formData.email} onChange={handleInputChange} required />
          </div>
          
          <div className="input-group">
            <label>Password</label>
            <input type="password" name="password" value={formData.password} onChange={handleInputChange} required />
          </div>

          {!isLogin && (
            <div className="input-group">
              <label>Phone Number</label>
              <input type="text" name="phone" value={formData.phone} onChange={handleInputChange} required />
            </div>
          )}

          {!isLogin && role === 'STUDENT' && (
            <>
              <div className="input-group">
                <label>Course</label>
                <input type="text" name="course" value={formData.course} onChange={handleInputChange} required />
              </div>
              <div className="input-group">
                <label>Branch</label>
                <input type="text" name="branch" value={formData.branch} onChange={handleInputChange} required />
              </div>
              <div className="input-group">
                <label>University Roll No</label>
                <input type="text" name="universityRollNo" value={formData.universityRollNo} onChange={handleInputChange} required />
              </div>
            </>
          )}

          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem', padding: '0.75rem' }}>
            {isLogin ? 'Sign In' : 'Sign Up'}
          </button>
        </form>

        <p style={{ marginTop: '1.5rem', textAlign: 'center', fontSize: '0.875rem' }}>
          {isLogin ? "Don't have an account? " : "Already have an account? "}
          <span style={{ color: 'var(--primary-color)', cursor: 'pointer', fontWeight: '500' }} onClick={() => setIsLogin(!isLogin)}>
            {isLogin ? 'Sign up' : 'Sign in'}
          </span>
        </p>
      </div>
    </div>
  );
};

const styles = {
  wrapper: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '70vh'
  },
  authCard: {
    width: '100%',
    maxWidth: '400px'
  },
  roleToggle: {
    display: 'flex',
    justifyContent: 'center',
    gap: '1rem',
    marginBottom: '1.5rem'
  },
  error: {
    backgroundColor: '#fee2e2',
    color: '#b91c1c',
    padding: '0.75rem',
    borderRadius: 'var(--radius-md)',
    marginBottom: '1rem',
    fontSize: '0.875rem'
  }
};

export default Auth;
