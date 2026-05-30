import React, { useEffect, useState } from 'react';
import api from '../api/axiosConfig';

const AdminDashboard = () => {
  const [jobs, setJobs] = useState([]);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [adminName, setAdminName] = useState('');
  const [exportingId, setExportingId] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedJob, setSelectedJob] = useState(null);
  const [applicants, setApplicants] = useState([]);
  const [loadingApplicants, setLoadingApplicants] = useState(false);

  const [formData, setFormData] = useState({
    companyName: '', jobRole: '', deadline: '', ctc: '', location: '', jobDescription: '', domain: ''
  });

  useEffect(() => {
    fetchJobs();
  }, []);

  const fetchJobs = async (search = '') => {
    setLoading(true);
    try {
      const url = `/admin/jobs/getList?page=0&size=50${search ? `&company=${encodeURIComponent(search)}` : ''}`;
      const [jobsRes, profileRes] = await Promise.all([
        api.get(url),
        api.get('/admin/profile')
      ]);
      setJobs(jobsRes.data.content || []);
      setAdminName(profileRes.data.name);
    } catch (error) {
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    fetchJobs(searchQuery);
  };

  const handleViewApplicants = async (job) => {
    setSelectedJob(job);
    setLoadingApplicants(true);
    setApplicants([]);
    try {
      const res = await api.get(`/admin/${job.jobListingId}/applicants`);
      setApplicants(res.data || []);
    } catch (error) {
      alert('Failed to load applicants');
    } finally {
      setLoadingApplicants(false);
    }
  };

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleCreateJob = async (e) => {
    e.preventDefault();
    try {
      await api.post('/admin/createListing', {
        ...formData,
        deadline: new Date(formData.deadline).toISOString() // Ensure correct ISO format if needed
      });
      alert('Job listing created successfully!');
      setShowCreateModal(false);
      setFormData({ companyName: '', jobRole: '', deadline: '', ctc: '', location: '', jobDescription: '', domain: '' });
      fetchJobs();
    } catch (error) {
      alert(error.response?.data || 'Failed to create job listing.');
    }
  };

  const handleExport = async (jobId) => {
    try {
      setExportingId(jobId);
      const response = await api.get(`/admin/${jobId}/export`, { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `applicants_job_${jobId}.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      alert('Failed to export data.');
    } finally {
      setExportingId(null);
    }
  };

  if (loading) return <div style={{ textAlign: 'center', marginTop: '2rem', color: 'var(--text-muted)' }}>Loading dashboard...</div>;

  return (
    <div>
      <div style={styles.header}>
        <h2 style={{ fontWeight: '700', fontSize: '1.75rem', letterSpacing: '-0.5px' }}>
          {adminName ? `Welcome, ${adminName.split(' ')[0]}` : 'CRC Admin Dashboard'}
        </h2>
        <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
          <form onSubmit={handleSearch} style={{ display: 'flex', gap: '0.5rem' }}>
            <input 
              type="text" 
              placeholder="Search by company..." 
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{ padding: '0.5rem 1rem', borderRadius: '6px', border: '1px solid var(--border-color)' }}
            />
            <button type="submit" className="btn btn-outline">Search</button>
          </form>
          <button className="btn btn-primary" style={{ fontWeight: '600' }} onClick={() => setShowCreateModal(true)}>
            + Create Job Listing
          </button>
        </div>
      </div>

      {showCreateModal && (
        <div className="card" style={{ marginBottom: '2.5rem', backgroundColor: 'var(--surface-color)', border: '1px solid var(--primary-color)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: '600' }}>New Job Listing</h3>
            <button className="btn btn-outline" style={{ border: 'none', padding: '0.25rem' }} onClick={() => setShowCreateModal(false)}>✕</button>
          </div>
          <form onSubmit={handleCreateJob} style={styles.formGrid}>
            <div className="input-group"><label>Company Name</label><input type="text" name="companyName" value={formData.companyName} onChange={handleInputChange} required /></div>
            <div className="input-group"><label>Job Role</label><input type="text" name="jobRole" value={formData.jobRole} onChange={handleInputChange} required /></div>
            <div className="input-group"><label>CTC</label><input type="text" name="ctc" placeholder="e.g., 12 LPA" value={formData.ctc} onChange={handleInputChange} required /></div>
            <div className="input-group"><label>Location</label><input type="text" name="location" value={formData.location} onChange={handleInputChange} required /></div>
            <div className="input-group"><label>Domain</label><input type="text" name="domain" placeholder="e.g., IT, Finance" value={formData.domain} onChange={handleInputChange} required /></div>
            <div className="input-group"><label>Application Deadline</label><input type="datetime-local" name="deadline" value={formData.deadline} onChange={handleInputChange} required /></div>
            <div className="input-group" style={{ gridColumn: '1 / -1' }}><label>Job Description</label><textarea name="jobDescription" rows="3" value={formData.jobDescription} onChange={handleInputChange} required /></div>
            <div style={{ gridColumn: '1 / -1', display: 'flex', gap: '1rem', justifyContent: 'flex-end', marginTop: '1rem' }}>
              <button type="button" className="btn btn-outline" onClick={() => setShowCreateModal(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary">Publish Job</button>
            </div>
          </form>
        </div>
      )}

      <div style={styles.grid}>
        {jobs.length > 0 ? (
          jobs.map(job => (
            <div key={job.jobListingId} className="card" style={styles.jobCardWrapper}>
              <div style={{ marginBottom: '1.25rem' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <h3 style={styles.cardTitle}>{job.jobRole}</h3>
                  <span style={styles.idBadge}>ID: {job.jobListingId}</span>
                </div>
                <p style={styles.cardCompany}>{job.companyName}</p>
              </div>
              
              <div style={styles.badgeContainer}>
                {job.ctc && <span style={styles.badge}>💰 {job.ctc}</span>}
                {job.location && <span style={styles.badge}>📍 {job.location}</span>}
                {job.domain && <span style={styles.badge}>🏢 {job.domain}</span>}
              </div>

              <div style={{ marginTop: 'auto', paddingTop: '1rem', borderTop: '1px solid var(--border-color)' }}>
                <div style={{ marginBottom: '1.25rem' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    Deadline: <span style={{ fontWeight: '600' }}>{job.deadline ? new Date(job.deadline).toLocaleDateString() : 'N/A'}</span>
                  </span>
                </div>
                
                <button 
                  className="btn btn-outline" 
                  onClick={() => handleViewApplicants(job)}
                  style={{ width: '100%', padding: '0.6rem', fontWeight: '500' }}
                >
                  👁️ View Applicants
                </button>
              </div>
            </div>
          ))
        ) : (
          <div style={styles.emptyState}>No job listings found. Create one above!</div>
        )}
      </div>

      {selectedJob && (
        <div style={styles.modalOverlay}>
          <div className="card" style={{ ...styles.modalContent, width: '90%', maxWidth: '1000px', maxHeight: '90vh', overflowY: 'auto' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
              <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: '600' }}>Applicants for {selectedJob.companyName} ({selectedJob.jobRole})</h3>
              <div style={{ display: 'flex', gap: '1rem' }}>
                <button 
                  className="btn btn-primary" 
                  onClick={() => handleExport(selectedJob.jobListingId)}
                  disabled={exportingId === selectedJob.jobListingId}
                >
                  {exportingId === selectedJob.jobListingId ? '⏳ Exporting...' : '📥 Export to Excel'}
                </button>
                <button className="btn btn-outline" style={{ border: 'none', padding: '0.25rem' }} onClick={() => setSelectedJob(null)}>✕</button>
              </div>
            </div>
            
            {loadingApplicants ? (
              <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>Loading applicants...</div>
            ) : applicants.length > 0 ? (
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                  <thead>
                    <tr style={{ borderBottom: '2px solid var(--border-color)' }}>
                      <th style={{ padding: '1rem 0.5rem' }}>Roll No</th>
                      <th style={{ padding: '1rem 0.5rem' }}>Name</th>
                      <th style={{ padding: '1rem 0.5rem' }}>Course</th>
                      <th style={{ padding: '1rem 0.5rem' }}>Branch</th>
                      <th style={{ padding: '1rem 0.5rem' }}>Email</th>
                    </tr>
                  </thead>
                  <tbody>
                    {applicants.map((app, idx) => (
                      <tr key={idx} style={{ borderBottom: '1px solid var(--border-color)' }}>
                        <td style={{ padding: '1rem 0.5rem' }}>{app.universityRollNo}</td>
                        <td style={{ padding: '1rem 0.5rem', fontWeight: '500' }}>{app.name}</td>
                        <td style={{ padding: '1rem 0.5rem' }}>{app.course}</td>
                        <td style={{ padding: '1rem 0.5rem' }}>{app.branch}</td>
                        <td style={{ padding: '1rem 0.5rem' }}>{app.email}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div style={styles.emptyState}>No applicants found for this job yet.</div>
            )}
          </div>
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
    marginBottom: '2.5rem',
    flexWrap: 'wrap',
    gap: '1rem'
  },
  modalOverlay: {
    position: 'fixed',
    top: 0, left: 0, right: 0, bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.5)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000
  },
  modalContent: {
    backgroundColor: 'var(--bg-color)',
    padding: '2rem',
    borderRadius: '12px'
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
  idBadge: {
    fontSize: '0.7rem',
    color: 'var(--text-muted)',
    backgroundColor: 'var(--bg-color)',
    padding: '0.2rem 0.5rem',
    borderRadius: '4px',
    border: '1px solid var(--border-color)'
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
    gap: '1.25rem'
  }
};

export default AdminDashboard;
