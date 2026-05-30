import React, { useEffect, useState } from 'react';
import api from '../api/axiosConfig';

const StudentDashboard = () => {
  const [activeJobs, setActiveJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [profile, setProfile] = useState(null);
  const [activeTab, setActiveTab] = useState('jobs'); // 'jobs', 'applications', 'profile'
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState({ show: false, message: '', type: 'success' });

  // Profile edit state
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({});

  const showToast = (message, type = 'success') => {
    setToast({ show: true, message, type });
    setTimeout(() => setToast({ show: false, message: '', type: 'success' }), 4000);
  };

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [jobsRes, appsRes, profileRes] = await Promise.all([
        api.get('/student/activejobs'),
        api.get('/student/applications'),
        api.get('/student/profile')
      ]);
      setActiveJobs(jobsRes.data);
      setApplications(appsRes.data);
      setProfile(profileRes.data);
      setEditForm(profileRes.data);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const applyForJob = async (jobId) => {
    try {
      await api.post('/student/apply', { jobId });
      showToast('Successfully applied for the job!', 'success');
      fetchData(); // Refresh lists
    } catch (error) {
      showToast(error.response?.data || 'Failed to apply.', 'error');
    }
  };

  const handleProfileChange = (e) => {
    setEditForm({ ...editForm, [e.target.name]: e.target.value });
  };

  const saveProfile = async (e) => {
    e.preventDefault();
    try {
      const res = await api.put('/student/profile', editForm);
      setProfile(res.data);
      setEditForm(res.data);
      setIsEditing(false);
      showToast('Profile updated successfully!', 'success');
    } catch (error) {
      showToast('Failed to update profile.', 'error');
    }
  };

  if (loading) return <div style={{ textAlign: 'center', marginTop: '2rem', color: 'var(--text-muted)' }}>Loading your dashboard...</div>;

  return (
    <div>
      <div style={styles.header}>
        <h2 style={{ fontWeight: '700', fontSize: '1.75rem', letterSpacing: '-0.5px' }}>
          {profile ? `Welcome, ${profile.name.split(' ')[0]}` : 'Student Dashboard'}
        </h2>
        <div style={styles.tabs}>
          <button
            className={`btn ${activeTab === 'jobs' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => setActiveTab('jobs')}
          >
            Active Jobs
          </button>
          <button
            className={`btn ${activeTab === 'applications' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => setActiveTab('applications')}
          >
            My Applications
          </button>
          <button
            className={`btn ${activeTab === 'profile' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => setActiveTab('profile')}
          >
            My Profile
          </button>
        </div>
      </div>

      <div style={activeTab === 'profile' ? {} : styles.grid}>
        {activeTab === 'jobs' && (
          activeJobs.length > 0 ? (
            activeJobs.map(job => {
              const alreadyApplied = applications.some(app => app.jobRole === job.jobRole && app.companyName === job.companyName);
              return (
                <div key={job.id} className="card" style={styles.jobCardWrapper}>
                  <div style={{ marginBottom: '1.25rem' }}>
                    <h3 style={styles.cardTitle}>{job.jobRole}</h3>
                    <p style={styles.cardCompany}>{job.companyName}</p>
                  </div>

                  <div style={styles.badgeContainer}>
                    {job.ctc && <span style={styles.badge}>💰 {job.ctc}</span>}
                    {job.location && <span style={styles.badge}>📍 {job.location}</span>}
                    {job.domain && <span style={styles.badge}>🏢 {job.domain}</span>}
                  </div>

                  <div style={{ marginTop: 'auto', paddingTop: '1rem', borderTop: '1px solid var(--border-color)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
                      <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        Deadline: <span style={{ fontWeight: '600' }}>{job.deadline ? new Date(job.deadline).toLocaleDateString() : 'N/A'}</span>
                      </span>
                    </div>

                    <button
                      className={`btn ${alreadyApplied ? 'btn-outline' : 'btn-primary'}`}
                      disabled={alreadyApplied}
                      onClick={() => applyForJob(job.id)}
                      style={{ width: '100%', padding: '0.6rem', fontWeight: '600' }}
                    >
                      {alreadyApplied ? 'Already Applied' : 'Apply Now'}
                    </button>
                  </div>
                </div>
              );
            })
          ) : (
            <div style={styles.emptyState}>No active jobs available at the moment.</div>
          )
        )}

        {activeTab === 'applications' && (
          applications.length > 0 ? (
            applications.map((app, index) => (
              <div key={index} className="card" style={styles.jobCardWrapper}>
                <div style={{ marginBottom: '1.25rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                    <h3 style={styles.cardTitle}>{app.jobRole}</h3>
                    <span style={styles.statusBadge}>
                      {app.status || 'APPLIED'}
                    </span>
                  </div>
                  <p style={styles.cardCompany}>{app.companyName}</p>
                </div>

                <div style={styles.badgeContainer}>
                  {app.ctc && <span style={styles.badge}>💰 {app.ctc}</span>}
                  {app.location && <span style={styles.badge}>📍 {app.location}</span>}
                </div>

                <div style={{ marginTop: 'auto', paddingTop: '1rem', borderTop: '1px solid var(--border-color)' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    Applied on: <span style={{ fontWeight: '600' }}>{app.applicationDate ? new Date(app.applicationDate).toLocaleDateString() : 'Recently'}</span>
                  </span>
                </div>
              </div>
            ))
          ) : (
            <div style={styles.emptyState}>You haven't applied to any jobs yet. Explore the Active Jobs tab!</div>
          )
        )}

        {activeTab === 'profile' && profile && (
          <div className="card" style={{ maxWidth: '800px', margin: '0 auto', padding: '2rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
              <div>
                <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: '600' }}>Personal Information</h3>
                <p style={{ margin: 0, fontSize: '0.875rem', color: 'var(--text-muted)' }}>Update your academic details and resume</p>
              </div>
              {!isEditing && (
                <button className="btn btn-outline" onClick={() => setIsEditing(true)}>Edit Profile</button>
              )}
            </div>

            <form onSubmit={saveProfile}>
              <div style={styles.formGrid}>
                {/* Read-only / Sensitive fields */}
                <div className="input-group">
                  <label>Email </label>
                  <input type="email" value={profile.email} disabled style={styles.disabledInput} />
                </div>
                <div className="input-group">
                  <label>University Roll No</label>
                  <input type="text" value={profile.universityRollNo} disabled style={styles.disabledInput} />
                </div>

                {/* Editable fields */}
                <div className="input-group">
                  <label>Full Name</label>
                  <input type="text" name="name" value={isEditing ? editForm.name || '' : profile.name || ''} onChange={handleProfileChange} disabled={!isEditing} required />
                </div>
                <div className="input-group">
                  <label>Phone Number</label>
                  <input type="text" name="phone" value={isEditing ? editForm.phone || '' : profile.phone || ''} onChange={handleProfileChange} disabled={!isEditing} required />
                </div>
                <div className="input-group">
                  <label>Course</label>
                  <input type="text" name="course" value={isEditing ? editForm.course || '' : profile.course || ''} onChange={handleProfileChange} disabled={!isEditing} required />
                </div>
                <div className="input-group">
                  <label>Branch</label>
                  <input type="text" name="branch" value={isEditing ? editForm.branch || '' : profile.branch || ''} onChange={handleProfileChange} disabled={!isEditing} required />
                </div>

                {/* Academics */}
                <div className="input-group">
                  <label>10th Percentage</label>
                  <input type="number" step="0.01" name="tenthPercentage" value={isEditing ? editForm.tenthPercentage || '' : profile.tenthPercentage || ''} onChange={handleProfileChange} disabled={!isEditing} />
                </div>
                <div className="input-group">
                  <label>12th Percentage</label>
                  <input type="number" step="0.01" name="twelfthPercentage" value={isEditing ? editForm.twelfthPercentage || '' : profile.twelfthPercentage || ''} onChange={handleProfileChange} disabled={!isEditing} />
                </div>
                <div className="input-group">
                  <label>Graduation CGPA</label>
                  <input type="number" step="0.01" name="graduationCgpa" value={isEditing ? editForm.graduationCgpa || '' : profile.graduationCgpa || ''} onChange={handleProfileChange} disabled={!isEditing} />
                </div>
                <div className="input-group">
                  <label>Post-Graduation CGPA</label>
                  <input type="number" step="0.01" name="postGraduationCgpa" value={isEditing ? editForm.postGraduationCgpa || '' : profile.postGraduationCgpa || ''} onChange={handleProfileChange} disabled={!isEditing} />
                </div>

                <div className="input-group" style={{ gridColumn: '1 / -1' }}>
                  <label>Resume URL</label>
                  <input type="url" name="resumeUrl" value={isEditing ? editForm.resumeUrl || '' : profile.resumeUrl || ''} onChange={handleProfileChange} disabled={!isEditing} />
                </div>
              </div>

              {isEditing && (
                <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end', marginTop: '2rem', paddingTop: '1.5rem', borderTop: '1px solid var(--border-color)' }}>
                  <button type="button" className="btn btn-outline" onClick={() => { setIsEditing(false); setEditForm(profile); }}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Save Changes</button>
                </div>
              )}
            </form>
          </div>
        )}
      </div>

      {/* Professional Toast Notification */}
      {toast.show && (
        <div style={{
          ...styles.toast,
          backgroundColor: toast.type === 'success' ? '#10b981' : '#ef4444',
          transform: toast.show ? 'translateY(0)' : 'translateY(100px)',
          opacity: toast.show ? 1 : 0,
        }}>
          {toast.type === 'success' ? '✅ ' : '⚠️ '} {toast.message}
        </div>
      )}
    </div>
  );
};

const styles = {
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '2.5rem'
  },
  tabs: {
    display: 'flex',
    gap: '0.5rem',
    backgroundColor: 'var(--surface-color)',
    padding: '0.35rem',
    borderRadius: '12px',
    border: '1px solid var(--border-color)',
    boxShadow: 'var(--shadow-sm)'
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
    gap: '1.5rem'
  },
  jobCardWrapper: {
    display: 'flex',
    flexDirection: 'column',
    height: '100%',
    padding: '1.5rem',
    transition: 'transform 0.2s ease, box-shadow 0.2s ease',
  },
  cardTitle: {
    fontSize: '1.125rem',
    color: 'var(--text-main)',
    marginBottom: '0.25rem',
    fontWeight: '600',
    lineHeight: '1.4'
  },
  cardCompany: {
    color: 'var(--primary-color)',
    fontWeight: '500',
    fontSize: '0.875rem'
  },
  badgeContainer: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '0.5rem',
    marginBottom: '1.5rem'
  },
  badge: {
    padding: '0.35rem 0.6rem',
    backgroundColor: '#f3f4f6',
    color: '#4b5563',
    borderRadius: '6px',
    fontSize: '0.75rem',
    fontWeight: '500',
    display: 'inline-flex',
    alignItems: 'center',
    gap: '0.25rem'
  },
  statusBadge: {
    padding: '0.25rem 0.75rem',
    backgroundColor: '#d1fae5',
    color: '#065f46',
    borderRadius: '9999px',
    fontSize: '0.7rem',
    fontWeight: '600',
    textTransform: 'uppercase',
    letterSpacing: '0.5px'
  },
  emptyState: {
    gridColumn: '1 / -1',
    textAlign: 'center',
    padding: '4rem 2rem',
    backgroundColor: 'var(--surface-color)',
    borderRadius: '12px',
    border: '1px dashed var(--border-color)',
    color: 'var(--text-muted)',
    fontSize: '0.95rem'
  },
  formGrid: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '1.5rem'
  },
  disabledInput: {
    backgroundColor: '#f9fafb',
    color: '#6b7280',
    cursor: 'not-allowed',
    borderColor: '#e5e7eb'
  },
  toast: {
    position: 'fixed',
    bottom: '24px',
    right: '24px',
    color: '#ffffff',
    padding: '12px 24px',
    borderRadius: '8px',
    boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
    fontWeight: '600',
    fontSize: '0.95rem',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
    zIndex: 9999
  }
};

export default StudentDashboard;
