'use client';

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { FiUsers, FiBook, FiLogOut, FiHome } from 'react-icons/fi';
import { clearAuth, getUser } from '@/lib/auth';

export default function Sidebar() {
  const pathname = usePathname();
  const router = useRouter();
  const user = getUser();

  const handleLogout = () => {
    if (confirm('Bạn có chắc muốn đăng xuất?')) {
      clearAuth();
      router.push('/login');
    }
  };

  const menuItems = [
    { href: '/dashboard', icon: FiHome, label: 'Dashboard' },
    { href: '/dashboard/users', icon: FiUsers, label: 'Quản lý User' },
    { href: '/dashboard/novels', icon: FiBook, label: 'Quản lý Novel' },
  ];

  return (
    <aside style={{
      width: '250px',
      background: 'white',
      borderRight: '1px solid var(--border)',
      height: '100vh',
      position: 'fixed',
      left: 0,
      top: 0,
      display: 'flex',
      flexDirection: 'column'
    }}>
      <div style={{
        padding: '1.5rem',
        borderBottom: '1px solid var(--border)'
      }}>
        <h2 style={{
          fontSize: '1.25rem',
          fontWeight: 'bold',
          color: 'var(--primary)',
          marginBottom: '0.5rem'
        }}>
          Admin Panel
        </h2>
        <p style={{
          fontSize: '0.75rem',
          color: 'var(--gray)'
        }}>
          {user?.displayName || user?.username}
        </p>
      </div>

      <nav style={{ flex: 1, padding: '1rem' }}>
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = pathname === item.href;
          
          return (
            <Link
              key={item.href}
              href={item.href}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.75rem',
                padding: '0.75rem 1rem',
                borderRadius: '0.5rem',
                color: isActive ? 'var(--primary)' : 'var(--dark)',
                background: isActive ? 'rgba(59, 130, 246, 0.1)' : 'transparent',
                textDecoration: 'none',
                marginBottom: '0.5rem',
                fontWeight: isActive ? '600' : '400',
                transition: 'all 0.2s'
              }}
            >
              <Icon size={20} />
              <span style={{ fontSize: '0.875rem' }}>{item.label}</span>
            </Link>
          );
        })}
      </nav>

      <div style={{
        padding: '1rem',
        borderTop: '1px solid var(--border)'
      }}>
        <button
          onClick={handleLogout}
          className="btn btn-danger"
          style={{ width: '100%' }}
        >
          <FiLogOut size={16} />
          <span>Đăng xuất</span>
        </button>
      </div>
    </aside>
  );
}

