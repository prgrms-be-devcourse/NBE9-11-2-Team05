// API Configuration for Spring Boot Backend
// Update this BASE_URL to point to your Spring Boot server
export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1"

// API Endpoints
export const API_ENDPOINTS = {
  // Auth
  login: `${API_BASE_URL}/auth/login`,
  register: `${API_BASE_URL}/auth/signup`,
  logout: `${API_BASE_URL}/auth/logout`,

  // Animals
  animals: `${API_BASE_URL}/animals`,
  animalDetail: (id: number) => `${API_BASE_URL}/animals/${id}`,
  animalRanking: `${API_BASE_URL}/animals/ranking`,

  // Hearts/Cheer
  addHeart: (animalId: number) => `${API_BASE_URL}/animals/${animalId}/hearts`,

  // Comments
  comments: (animalId: number) => `${API_BASE_URL}/animals/${animalId}/comments`,
  deleteComment: (animalId: number, commentId: number) => `${API_BASE_URL}/animals/${animalId}/comments/${commentId}`,

  // Community
  communityPosts: `${API_BASE_URL}/community`,
  communityPostDetail: (id: number) => `${API_BASE_URL}/community/${id}`,
  communityPostComments: (postId: number) => `${API_BASE_URL}/community/${postId}/comments`,

  // User Profile
  myProfile: `${API_BASE_URL}/me/profile`,
  myProfileStats: `${API_BASE_URL}/me`,
  myFeeds: `${API_BASE_URL}/me/feeds`,
  myCheerAnimals: `${API_BASE_URL}/me/cheer-animals`,
  updateUsername: `${API_BASE_URL}/me/username`,
  updateProfileImg: `${API_BASE_URL}/me/profileImg`,
  updatePassword: `${API_BASE_URL}/me/password`,
  updateNickname: `${API_BASE_URL}/me/nickname`,

  // Cheers
  addCheer: (animalId: number) => `${API_BASE_URL}/animals/${animalId}/cheers`,
  cheersToday: `${API_BASE_URL}/cheers/today`,

  // Feeds
  feeds: `${API_BASE_URL}/feeds`,
  feedDetail: (feedId: number) => `${API_BASE_URL}/feeds/${feedId}`,
  feedComments: (feedId: number) => `${API_BASE_URL}/feeds/${feedId}/comments`,
  feedCommentDetail: (feedId: number, commentId: number) => `${API_BASE_URL}/feeds/${feedId}/comments/${commentId}`,

  // Animal Sync
  animalSync: `${API_BASE_URL}/animals/sync`,
}

// API Helper Functions
export async function apiRequest<T>(
  url: string,
  options: RequestInit = {}
): Promise<{ data: T | null; error: string | null; errorCode?: string }> {
  try {
    const token = typeof window !== "undefined" ? localStorage.getItem("auth_token") : null

    const headers: Record<string, string> = {
      "Content-Type": "application/json",
      ...(options.headers as Record<string, string>),
    }

    if (token) {
      headers["Authorization"] = `Bearer ${token}`
    }

    const response = await fetch(url, {
      ...options,
      headers,
    })

    if (!response.ok) {
      const errorText = await response.text().catch(() => "")
      let errorData: Record<string, any> = {}
      if (errorText) {
        try {
          errorData = JSON.parse(errorText)
        } catch {
          errorData = {}
        }
      }
      return { 
        data: null, 
        error: errorData.message || `Error: ${response.status}`,
        errorCode: errorData.code || errorData.errorCode || errorData.status
      }
    }

    // Some endpoints can return 200/204 with empty body.
    const responseText = await response.text()
    if (!responseText) {
      return { data: null, error: null }
    }

    try {
      const data = JSON.parse(responseText) as T
      return { data, error: null }
    } catch {
      // Some APIs respond with plain text/number instead of JSON.
      return { data: responseText as T, error: null }
    }

  } catch (error) {
    return { data: null, error: error instanceof Error ? error.message : "Network error" }
  }
}

// Type definitions for API responses
export interface User {
  id: number
  username: string
  name: string
  role?: string
  nickname?: string
  createdAt?: string
}

export interface JwtPayload {
  userId: number
  role?: string
  sub?: string
  exp?: number
  iat?: number
  [key: string]: any
}

export function decodeJWT(token?: string): JwtPayload | null {
  if (!token) return null;
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')

    // Fallback for node environment (like SSR)
    if (typeof window === 'undefined') {
      return JSON.parse(Buffer.from(base64, 'base64').toString('utf-8'))
    }

    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error('Failed to decode JWT', error)
    return null
  }
}

export interface Animal {
  animalId: number
  noticeNo: string
  kind: string
  breed: string
  age: string
  gender: string
  neutered: string
  weight: string
  color: string
  specialMark: string
  imageUrl: string
  shelterName: string
  shelterTel: string
  shelterAddr: string
  chargeNm: string
  region: string
  noticeStartDate: string
  noticeEndDate: string
  processState: string
  heartCount: number
  temperature: number
}

export interface Comment {
  id: number
  author: string
  authorId: number
  text: string
  createdAt: string
}

export interface CommunityPost {
  feedId: number
  title: string
  content: string
  nickname: string
  userId: number
  imageUrl?: string
  likeCount: number
  commentCount: number
  createdAt: string
}

export interface PaginatedResponse<T> {
  content: T[]
  totalPages: number
  totalElements: number
  currentPage: number
  size: number
}

// Feeds
export interface FeedPayload {
  category: "ADOPTION_REVIEW" | "VOLUNTEER" | "FREE" | String;
  title: string;
  content: string;
  imageUrl?: string;
}

export interface Feed {
  feedId: number;
  userId: number;
  nickname?: string;
  category: string;
  title: string;
  content: string;
  imageUrl?: string;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  updatedAt: string;
}

export const getFeeds = async (page = 0, size = 20) => {
  const query = new URLSearchParams({
    page: String(page),
    size: String(size),
  }).toString();
  return await apiRequest<PaginatedResponse<Feed>>(`${API_ENDPOINTS.feeds}?${query}`);
};

export const createFeed = async (payload: FeedPayload) => {
  return await apiRequest<Feed>(API_ENDPOINTS.feeds, { // POST /api/v1/feeds
    method: "POST",
    body: JSON.stringify(payload)
  });
};

export const updateFeed = async (feedId: number, payload: FeedPayload) => {
  return await apiRequest<Feed>(API_ENDPOINTS.feedDetail(feedId), { // PUT /api/v1/feeds/{feedId}
    method: "PUT",
    body: JSON.stringify(payload)
  });
};
// 피드 삭제
export const deleteFeed = async (feedId: number) => {
  return await apiRequest<void>(API_ENDPOINTS.feedDetail(feedId), {
    method: "DELETE",
  })
  ;
};
// 피드 좋아요 토글
export const toggleFeedLike = async (feedId: number) => {
  return await apiRequest<{ likeCount: number; isLiked: boolean }>(
    `${API_ENDPOINTS.feedDetail(feedId)}/likes`,
    { method: "POST" }
  );
};