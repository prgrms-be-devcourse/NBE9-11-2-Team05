// API Configuration for Spring Boot Backend
// Update this BASE_URL to point to your Spring Boot server
export const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080/api/v1"

/** Spring 서버 루트 (OAuth2 등 /api/v1 바깥 경로용) */
export function getApiServerOrigin(): string {
  return API_BASE_URL.replace(/\/api\/v1\/?$/, "")
}

export function oauthAuthorizationUrl(provider: "google" | "naver"): string {
  return `${getApiServerOrigin()}/oauth2/authorization/${provider}`
}

// API Endpoints
export const API_ENDPOINTS = {
  // Auth
  login: `${API_BASE_URL}/auth/login`,
  register: `${API_BASE_URL}/auth/signup`,
  emailStart: `${API_BASE_URL}/auth/email/start`,
  emailSendOtp: `${API_BASE_URL}/auth/email/send-otp`,
  emailVerify: `${API_BASE_URL}/auth/email/verify`,
  emailSignup: `${API_BASE_URL}/auth/email/signup`,
  emailLogin: `${API_BASE_URL}/auth/email/login`,
  refresh: `${API_BASE_URL}/auth/refresh`,
  logout: `${API_BASE_URL}/auth/logout`,
  withdraw: `${API_BASE_URL}/auth/withdraw`,

  // Animals
  animals: `${API_BASE_URL}/animals`,
  animalDetail: (id: number) => `${API_BASE_URL}/animals/${id}`,
  animalRanking: `${API_BASE_URL}/animals/ranking`,

  // Comments
  comments: (animalId: number) => `${API_BASE_URL}/animals/${animalId}/comments`,
  deleteComment: (animalId: number, commentId: number) => `${API_BASE_URL}/animals/${animalId}/comments/${commentId}`,
  updateComment: (animalId: number, commentId: number) => `${API_BASE_URL}/animals/${animalId}/comments/${commentId}`,

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
  myFeedComments: `${API_BASE_URL}/me/comments/feeds`,
  myAnimalComments: `${API_BASE_URL}/me/comments/animals`,

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

  // Campaigns
  campaigns: `${API_BASE_URL}/campaigns`,
  shelterCampaign: (shelterId: string) => `${API_BASE_URL}/shelters/${shelterId}/campaign`,

  // Shelters
  shelterDetail: (shelterId: string) => `${API_BASE_URL}/shelters/${shelterId}`,
}

type ApiResult<T> = { data: T | null; error: string | null; errorCode?: string; status?: number }

let refreshPromise: Promise<boolean> | null = null

async function executeRequest<T>(url: string, options: RequestInit = {}): Promise<ApiResult<T>> {
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
    credentials: options.credentials ?? "include",
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
      errorCode: errorData.code || errorData.errorCode || errorData.status,
      status: response.status,
    }
  }

  const responseText = await response.text()
  if (!responseText) {
    return { data: null, error: null, status: response.status }
  }

  try {
    const data = JSON.parse(responseText) as T
    return { data, error: null, status: response.status }
  } catch {
    return { data: responseText as T, error: null, status: response.status }
  }
}

async function refreshAccessToken(): Promise<boolean> {
  if (typeof window === "undefined") return false

  if (!refreshPromise) {
    refreshPromise = (async () => {
      const response = await executeRequest<{ tokenType: string; accessToken: string }>(API_ENDPOINTS.refresh, {
        method: "POST",
      })

      if (response.error || !response.data?.accessToken) {
        localStorage.removeItem("auth_token")
        return false
      }

      localStorage.setItem("auth_token", response.data.accessToken)
      return true
    })().finally(() => {
      refreshPromise = null
    })
  }

  return refreshPromise
}

// API Helper Functions
export async function apiRequest<T>(
  url: string,
  options: RequestInit = {}
): Promise<ApiResult<T>> {
  try {
    const initialResponse = await executeRequest<T>(url, options)
    const shouldTryRefresh =
      initialResponse.status === 401 &&
      initialResponse.errorCode === "S-001" &&
      url !== API_ENDPOINTS.login &&
      url !== API_ENDPOINTS.register &&
      url !== API_ENDPOINTS.emailLogin &&
      url !== API_ENDPOINTS.emailSignup &&
      url !== API_ENDPOINTS.refresh

    if (shouldTryRefresh) {
      const refreshed = await refreshAccessToken()
      if (refreshed) {
        return await executeRequest<T>(url, options)
      }
    }

    return initialResponse
  } catch (error) {
    return { data: null, error: error instanceof Error ? error.message : "Network error" }
  }
}

// Type definitions for API responses
export interface User {
  id: number
  username: string
  email?: string
  name: string
  role?: string
  nickname?: string
  profileImageUrl?: string
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

/** 동물 목록/상세/응원 API와 동일: 0~1 비율이면 0~100으로, 그 외는 0~100으로 클램프 */
export function normalizeAnimalTemperatureDisplay(value: number, max = 100): number {
  if (typeof value !== "number" || Number.isNaN(value)) return 0
  const scaled = value <= 1 ? value * 100 : value
  return Math.max(0, Math.min(max, scaled))
}

/** POST /animals/{id}/cheers 응답 (CheerRes) — 래핑된 바디도 허용 */
export function parseAddCheerResponse(data: unknown): {
  animalId: number
  cheerCount: number
  temperature: number
  remaingCheersToday: number
} | null {
  if (data == null || typeof data !== "object") return null
  const root = data as Record<string, unknown>
  const raw =
    root.data && typeof root.data === "object"
      ? (root.data as Record<string, unknown>)
      : root.result && typeof root.result === "object"
        ? (root.result as Record<string, unknown>)
        : root

  const animalId = Number(raw.animalId)
  const cheerCount = Number(raw.cheerCount)
  const temperature = Number(raw.temperature)
  const remainingRaw =
    raw.remaingCheersToday ?? raw.remainingCheersToday ?? raw.remainingToday ?? raw.remaining
  const remaingCheersToday =
    typeof remainingRaw === "number" ? remainingRaw : Number(remainingRaw)

  if (!Number.isFinite(animalId) || !Number.isFinite(cheerCount) || !Number.isFinite(temperature)) {
    return null
  }

  return {
    animalId,
    cheerCount,
    temperature,
    remaingCheersToday: Number.isFinite(remaingCheersToday) ? remaingCheersToday : 0,
  }
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

export interface AnimalDropdownItem {
  animalId: number
  noticeNo: string
  kindFillName: string
  careNm: string
}

export const getAnimals = async () => {
  return await apiRequest<PaginatedResponse<AnimalDropdownItem>>(API_ENDPOINTS.animals)
}

export const getAnimalDetail = async (animalId: number) => {
  return await apiRequest<AnimalDropdownItem>(API_ENDPOINTS.animalDetail(animalId))
}

export interface Comment {
  id: number
  author: string
  authorId: number
  profileImageUrl?: string
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
  animalId?: number;
}

export interface FeedComment {
  userId: number
  nickname: string
  profileImageUrl?: string
  commentId: number
  content: string
  feedId: number
  createdAt: string
}

export interface FeedDetail {
  feedId: number
  userId: number
  nickname?: string
  profileImageUrl?: string
  animalId?: number
  category: string
  title: string
  content: string
  imageUrl?: string
  likeCount: number
  commentCount: number
  comments: FeedComment[]
  createdAt: string
  updatedAt: string
}

export interface Feed {
  updatedAt: string;
}

export interface Campaign {
  id: number;
  title: string;
  targetAmount: number;
  currentAmount: number;
  status: "ACTIVE" | string;
  shelterId?: string;
}

export interface CampaignsResponse {
  totalCampaigns: number;
  campaigns: Campaign[];
}

export interface ShelterCampaignResponse {
  campaignCount: number;
  campaigns: Campaign[];
}

export interface Shelter {
  shelterId: string;
  careNm: string;
  careTel: string;
  careAddr: string;
  orgNm: string;
}

export interface MyFeedComment {
  feedId: number;
  category: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export interface MyAnimalComment {
  feedId?: number;
  animalId?: number;
  desertionNo?: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

export type FeedCategoryFilter = "ADOPTION_REVIEW" | "VOLUNTEER" | "FREE"

export const getFeeds = async (page = 0, size = 20, category?: FeedCategoryFilter) => {
  const queryParams: Record<string, string> = {
    page: String(page),
    size: String(size),
  }

  if (category) {
    queryParams.category = category;
  }

  const query = new URLSearchParams(queryParams).toString();
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

// Campaigns
export const getCampaigns = async () => {
  return await apiRequest<CampaignsResponse>(API_ENDPOINTS.campaigns);
};

export const getShelterCampaign = async (shelterId: string) => {
  return await apiRequest<ShelterCampaignResponse>(API_ENDPOINTS.shelterCampaign(shelterId));
};

// Shelters
export const getShelterDetail = async (shelterId: string) => {
  return await apiRequest<Shelter>(API_ENDPOINTS.shelterDetail(shelterId));
};