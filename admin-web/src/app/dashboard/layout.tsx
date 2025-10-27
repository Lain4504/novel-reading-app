'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { requireAdmin } from '@/lib/auth';
import Sidebar from '@/components/Sidebar';

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();

  useEffect(() => {
    if (!requireAdmin()) {
      router.push('/login');
    }
  }, [router]);

  return (
    <div style={{ display: 'flex' }}>
      <Sidebar />
      <main style={{
        marginLeft: '250px',
        flex: 1,
        minHeight: '100vh',
        background: '#f9fafb'
      }}>
        <div style={{ padding: '2rem' }}>
          {children}
        </div>
      </main>
    </div>
  );
}

