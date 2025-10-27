// User types
export enum UserRole {
  USER = 'USER',
  AUTHOR = 'AUTHOR',
  MODERATOR = 'MODERATOR',
  ADMIN = 'ADMIN'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  BANNED = 'BANNED',
  SUSPENDED = 'SUSPENDED'
}

export interface User {
  id: string;
  username: string;
  email: string;
  roles: UserRole[];
  status: UserStatus;
  avatarUrl?: string;
  backgroundUrl?: string;
  authorName?: string;
  bio?: string;
  displayName?: string;
  createdAt: string;
  updatedAt: string;
}

// Novel types
export enum NovelStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  COMPLETED = 'COMPLETED',
  HIATUS = 'HIATUS',
  DROPPED = 'DROPPED'
}

export enum Category {
  ACTION = 'ACTION',
  ADVENTURE = 'ADVENTURE',
  COMEDY = 'COMEDY',
  DRAMA = 'DRAMA',
  FANTASY = 'FANTASY',
  HORROR = 'HORROR',
  MYSTERY = 'MYSTERY',
  ROMANCE = 'ROMANCE',
  SCIFI = 'SCIFI',
  SLICE_OF_LIFE = 'SLICE_OF_LIFE',
  SUPERNATURAL = 'SUPERNATURAL',
  THRILLER = 'THRILLER',
  TRAGEDY = 'TRAGEDY',
  MARTIAL_ARTS = 'MARTIAL_ARTS',
  XUANHUAN = 'XUANHUAN',
  XIANXIA = 'XIANXIA',
  WUXIA = 'WUXIA',
  HISTORICAL = 'HISTORICAL',
  SCHOOL_LIFE = 'SCHOOL_LIFE',
  PSYCHOLOGICAL = 'PSYCHOLOGICAL'
}

export interface Novel {
  id: string;
  title: string;
  description: string;
  authorName: string;
  authorId?: string;
  categories: Category[];
  coverImage?: string;
  rating: number;
  viewCount: number;
  followCount: number;
  wordCount: number;
  chapterCount: number;
  status: NovelStatus;
  isR18: boolean;
  createdAt: string;
  updatedAt: string;
}

// API Response types
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

// Auth types
export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: User;
}

// Request types
export interface UserCreateRequest {
  username: string;
  email: string;
  password: string;
  roles?: UserRole[];
  status?: UserStatus;
  avatarUrl?: string;
  backgroundUrl?: string;
  authorName?: string;
  bio?: string;
  displayName?: string;
}

export interface UserUpdateRequest {
  username?: string;
  email?: string;
  password?: string;
  roles?: UserRole[];
  status?: UserStatus;
  avatarUrl?: string;
  backgroundUrl?: string;
  authorName?: string;
  bio?: string;
  displayName?: string;
}

export interface NovelUpdateRequest {
  title?: string;
  description?: string;
  authorName?: string;
  authorId?: string;
  categories?: string;
  rating?: number;
  wordCount?: number;
  chapterCount?: number;
  status?: NovelStatus;
  isR18?: boolean;
  coverImage?: File;
}

