'use client';

import { useEffect, useState } from 'react';
import { FiBook, FiTrendingUp, FiUsers } from 'react-icons/fi';
import { novelApi, userApi } from '@/lib/api';
import { getUser } from '@/lib/auth';

export default function DashboardPage() {
  const [stats, setStats] = useState({
    totalNovels: 0,
    totalUsers: 0,
    loading: true
  });
  const user = getUser();

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const [novelsResponse, usersResponse] = await Promise.all([
        novelApi.getAllNovels(0, 1),
        userApi.getAllUsers(0, 1)
      ]);

      setStats({
        totalNovels: novelsResponse.success && novelsResponse.data ? novelsResponse.data.totalElements : 0,
        totalUsers: usersResponse.success && usersResponse.data ? usersResponse.data.totalElements : 0,
        loading: false
      });
    } catch (error) {
      console.error('Error loading stats:', error);
      setStats({ totalNovels: 0, totalUsers: 0, loading: false });
    }
  };

  const statCards = [
    {
      title: 'Tổng số User',
      value: stats.loading ? '...' : stats.totalUsers,
      icon: FiUsers,
      color: '#3b82f6',
      bgColor: '#dbeafe'
    },
    {
      title: 'Tổng số Novel',
      value: stats.loading ? '...' : stats.totalNovels,
      icon: FiBook,
      color: '#8b5cf6',
      bgColor: '#ede9fe'
    },
    {
      title: 'Hoạt động hệ thống',
      value: 'Online',
      icon: FiTrendingUp,
      color: '#10b981',
      bgColor: '#d1fae5'
    }
  ];

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.875rem', fontWeight: 'bold', marginBottom: '0.5rem' }}>
          Dashboard
        </h1>
        <p style={{ color: 'var(--gray)' }}>
          Chào mừng trở lại, {user?.displayName || user?.username}!
        </p>
      </div>

      {/* Stats Cards */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
        gap: '1.5rem',
        marginBottom: '2rem'
      }}>
        {statCards.map((stat, index) => {
          const Icon = stat.icon;
          return (
            <div key={index} className="card" style={{
              borderLeft: `4px solid ${stat.color}`
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                <div>
                  <p style={{ color: 'var(--gray)', fontSize: '0.875rem', marginBottom: '0.5rem' }}>
                    {stat.title}
                  </p>
                  <h3 style={{ fontSize: '1.875rem', fontWeight: 'bold', color: 'var(--dark)' }}>
                    {stat.value}
                  </h3>
                </div>
                <div style={{
                  background: stat.bgColor,
                  color: stat.color,
                  padding: '0.75rem',
                  borderRadius: '0.5rem'
                }}>
                  <Icon size={24} />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Quick Links */}
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Chức năng chính</h2>
        </div>
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
          gap: '1rem'
        }}>
          <a
            href="/dashboard/users"
            style={{
              padding: '1.5rem',
              background: 'var(--light-gray)',
              borderRadius: '0.5rem',
              textDecoration: 'none',
              color: 'var(--dark)',
              transition: 'all 0.2s'
            }}
            onMouseOver={(e) => {
              e.currentTarget.style.background = 'var(--primary)';
              e.currentTarget.style.color = 'white';
            }}
            onMouseOut={(e) => {
              e.currentTarget.style.background = 'var(--light-gray)';
              e.currentTarget.style.color = 'var(--dark)';
            }}
          >
            <FiUsers size={24} style={{ marginBottom: '0.5rem' }} />
            <h3 style={{ fontWeight: '600', fontSize: '1.125rem' }}>Quản lý User</h3>
            <p style={{ fontSize: '0.875rem', marginTop: '0.25rem', opacity: 0.8 }}>
              Xem và quản lý người dùng
            </p>
          </a>

          <a
            href="/dashboard/novels"
            style={{
              padding: '1.5rem',
              background: 'var(--light-gray)',
              borderRadius: '0.5rem',
              textDecoration: 'none',
              color: 'var(--dark)',
              transition: 'all 0.2s'
            }}
            onMouseOver={(e) => {
              e.currentTarget.style.background = 'var(--secondary)';
              e.currentTarget.style.color = 'white';
            }}
            onMouseOut={(e) => {
              e.currentTarget.style.background = 'var(--light-gray)';
              e.currentTarget.style.color = 'var(--dark)';
            }}
          >
            <FiBook size={24} style={{ marginBottom: '0.5rem' }} />
            <h3 style={{ fontWeight: '600', fontSize: '1.125rem' }}>Quản lý Novel</h3>
            <p style={{ fontSize: '0.875rem', marginTop: '0.25rem', opacity: 0.8 }}>
              Xem và quản lý tiểu thuyết
            </p>
          </a>
        </div>
      </div>
    </div>
  );
}
