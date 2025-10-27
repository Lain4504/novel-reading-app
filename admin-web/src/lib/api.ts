import axios, { AxiosError } from 'axios';
import type {
  ApiResponse,
  LoginRequest,
  LoginResponse,
  User,
  UserCreateRequest,
  UserUpdateRequest,
  Novel,
  PageResponse,
  NovelUpdateRequest,
} from '@/types';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Redirect to login if unauthorized
      if (typeof window !== 'undefined') {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authApi = {
  login: async (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    const response = await api.post<ApiResponse<LoginResponse>>('/users/login', data);
    return response.data;
  },
};

// User API
export const userApi = {
  getAllUsers: async (page = 0, size = 20): Promise<ApiResponse<PageResponse<User>>> => {
    const response = await api.get<ApiResponse<PageResponse<User>>>('/users', {
      params: { page, size },
    });
    return response.data;
  },

  getUserById: async (id: string): Promise<ApiResponse<User>> => {
    const response = await api.get<ApiResponse<User>>(`/users/${id}`);
    return response.data;
  },

  getUserByUsername: async (username: string): Promise<ApiResponse<User>> => {
    const response = await api.get<ApiResponse<User>>(`/users/by-username/${username}`);
    return response.data;
  },

  getUserByEmail: async (email: string): Promise<ApiResponse<User>> => {
    const response = await api.get<ApiResponse<User>>(`/users/by-email/${email}`);
    return response.data;
  },

  createUser: async (data: UserCreateRequest): Promise<ApiResponse<User>> => {
    const response = await api.post<ApiResponse<User>>('/users', data);
    return response.data;
  },

  updateUser: async (id: string, data: UserUpdateRequest): Promise<ApiResponse<User>> => {
    const response = await api.put<ApiResponse<User>>(`/users/${id}`, data);
    return response.data;
  },

  deleteUser: async (id: string): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/users/${id}`);
    return response.data;
  },
};

// Novel API
export const novelApi = {
  getAllNovels: async (page = 0, size = 20): Promise<ApiResponse<PageResponse<Novel>>> => {
    const response = await api.get<ApiResponse<PageResponse<Novel>>>('/novels', {
      params: { page, size },
    });
    return response.data;
  },

  getNovelById: async (id: string): Promise<ApiResponse<Novel>> => {
    const response = await api.get<ApiResponse<Novel>>(`/novels/${id}`);
    return response.data;
  },

  updateNovel: async (id: string, data: NovelUpdateRequest): Promise<ApiResponse<Novel>> => {
    const formData = new FormData();
    
    if (data.title) formData.append('title', data.title);
    if (data.description) formData.append('description', data.description);
    if (data.authorName) formData.append('authorName', data.authorName);
    if (data.authorId) formData.append('authorId', data.authorId);
    if (data.categories) formData.append('categories', data.categories);
    if (data.rating !== undefined) formData.append('rating', data.rating.toString());
    if (data.wordCount !== undefined) formData.append('wordCount', data.wordCount.toString());
    if (data.chapterCount !== undefined) formData.append('chapterCount', data.chapterCount.toString());
    if (data.status) formData.append('status', data.status);
    if (data.isR18 !== undefined) formData.append('isR18', data.isR18.toString());
    if (data.coverImage) formData.append('coverImage', data.coverImage);

    const response = await api.put<ApiResponse<Novel>>(`/novels/${id}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  deleteNovel: async (id: string): Promise<ApiResponse<void>> => {
    const response = await api.delete<ApiResponse<void>>(`/novels/${id}`);
    return response.data;
  },
};

export default api;

