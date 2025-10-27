'use client';

import { useState, FormEvent } from 'react';
import { useRouter } from 'next/navigation';
import { authApi } from '@/lib/api';
import { setAuth, isAuthenticated, isAdmin } from '@/lib/auth';
import { UserRole } from '@/types';
import { useEffect } from 'react';

export default function LoginPage() {
  const router = useRouter();
  const [usernameOrEmail, setUsernameOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Redirect if already logged in as admin
    if (isAuthenticated() && isAdmin()) {
      router.push('/dashboard');
    }
  }, [router]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await authApi.login({ usernameOrEmail, password });
      
      if (response.success && response.data) {
        const { token, user } = response.data;
        
        // Check if user is admin
        if (!user.roles.includes(UserRole.ADMIN)) {
          setError('Bạn không có quyền truy cập. Chỉ Admin mới có thể đăng nhập.');
          setLoading(false);
          return;
        }

        setAuth(token, user);
        router.push('/dashboard');
      } else {
        setError(response.message || 'Đăng nhập thất bại');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data?.error || 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    }}>
      <div className="card" style={{ maxWidth: '400px', width: '100%', margin: '1rem' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h1 style={{ 
            fontSize: '1.875rem', 
            fontWeight: 'bold', 
            color: 'var(--dark)',
            marginBottom: '0.5rem'
          }}>
            Admin Panel
          </h1>
          <p style={{ color: 'var(--gray)', fontSize: '0.875rem' }}>
            Novel Reading App
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label" htmlFor="usernameOrEmail">
              Tên đăng nhập hoặc Email
            </label>
            <input
              id="usernameOrEmail"
              type="text"
              className="form-input"
              value={usernameOrEmail}
              onChange={(e) => setUsernameOrEmail(e.target.value)}
              placeholder="Nhập tên đăng nhập hoặc email"
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="password">
              Mật khẩu
            </label>
            <input
              id="password"
              type="password"
              className="form-input"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Nhập mật khẩu"
              required
              disabled={loading}
            />
          </div>

          {error && (
            <div style={{ 
              padding: '0.75rem', 
              background: '#fee2e2', 
              color: '#991b1b',
              borderRadius: '0.5rem',
              fontSize: '0.875rem',
              marginBottom: '1rem'
            }}>
              {error}
            </div>
          )}

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%' }}
            disabled={loading}
          >
            {loading ? 'Đang đăng nhập...' : 'Đăng nhập'}
          </button>
        </form>

        <div style={{ 
          marginTop: '1.5rem', 
          padding: '1rem', 
          background: 'var(--light-gray)',
          borderRadius: '0.5rem',
          fontSize: '0.75rem',
          color: 'var(--gray)'
        }}>
          <strong>Lưu ý:</strong> Chỉ tài khoản có quyền Admin mới có thể đăng nhập vào hệ thống quản trị này.
        </div>
      </div>
    </div>
  );
}

