// API Configuration for Spring Boot Backend
// Update this BASE_URL to point to your Spring Boot server
export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api"

// API Endpoints
export const API_ENDPOINTS = {
  // Auth
  login: `${API_BASE_URL}/auth/login`,
  register: `${API_BASE_URL}/auth/register`,
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
  myHearts: `${API_BASE_URL}/v1/users/me/hearts`,
  myFeeds: `${API_BASE_URL}/v1/users/me/feeds`,
}

// API Helper Functions
export async function apiRequest<T>(
  url: string,
  options: RequestInit = {}
): Promise<{ data: T | null; error: string | null }> {
  try {
    const token = typeof window !== "undefined" ? localStorage.getItem("auth_token") : null
    
    const headers: HeadersInit = {
      "Content-Type": "application/json",
      ...options.headers,
    }
    
    if (token) {
      headers["Authorization"] = `Bearer ${token}`
    }
    
    const response = await fetch(url, {
      ...options,
      headers,
    })
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}))
      return { data: null, error: errorData.message || `Error: ${response.status}` }
    }
    
    const data = await response.json()
    return { data, error: null }
  } catch (error) {
    return { data: null, error: error instanceof Error ? error.message : "Network error" }
  }
}

// Type definitions for API responses
export interface User {
  id: number
  username: string
  name: string
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
  id: number
  title: string
  content: string
  author: string
  authorId: number
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
