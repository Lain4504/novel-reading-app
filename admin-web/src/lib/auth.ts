import { User, UserRole } from '@/types';

export const setAuth = (token: string, user: User) => {
  if (typeof window === 'undefined') return;
  localStorage.setItem('token', token);
  localStorage.setItem('user', JSON.stringify(user));
};

export const clearAuth = () => {
  if (typeof window === 'undefined') return;
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};

export const getToken = (): string | null => {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('token');
};

export const getUser = (): User | null => {
  if (typeof window === 'undefined') return null;
  const userStr = localStorage.getItem('user');
  if (!userStr) return null;
  try {
    return JSON.parse(userStr);
  } catch {
    return null;
  }
};

export const isAuthenticated = (): boolean => {
  if (typeof window === 'undefined') return false;
  return !!getToken();
};

export const isAdmin = (): boolean => {
  if (typeof window === 'undefined') return false;
  const user = getUser();
  return user?.roles?.includes(UserRole.ADMIN) || false;
};

export const requireAuth = (redirectUrl = '/login') => {
  if (typeof window !== 'undefined' && !isAuthenticated()) {
    window.location.href = redirectUrl;
    return false;
  }
  return true;
};

export const requireAdmin = (redirectUrl = '/login') => {
  if (typeof window !== 'undefined') {
    if (!isAuthenticated()) {
      window.location.href = redirectUrl;
      return false;
    }
    if (!isAdmin()) {
      alert('Bạn không có quyền truy cập trang này. Chỉ Admin mới có thể truy cập.');
      window.location.href = '/';
      return false;
    }
  }
  return true;
};

