'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { isAuthenticated, isAdmin } from '@/lib/auth';

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    if (isAuthenticated() && isAdmin()) {
      router.push('/dashboard');
    } else {
      router.push('/login');
    }
  }, [router]);

  return (
    <div className="loading">
      <div className="spinner"></div>
    </div>
  );
}

