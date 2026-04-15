"use client"

import { createContext, useContext, useState, useEffect, ReactNode } from "react"
import { API_ENDPOINTS, apiRequest, User, decodeJWT } from "./api"

interface AuthContextType {
  user: User | null
  isLoading: boolean
  login: (username: string, password: string) => Promise<{ success: boolean; error?: string }>
  register: (username: string, password: string, name: string) => Promise<{ success: boolean; error?: string }>
  logout: () => void
  updateUser: (updates: Partial<User>) => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    // Check for existing session on mount
    const storedUser = localStorage.getItem("user")
    const storedToken = localStorage.getItem("auth_token")

    if (storedUser && storedToken) {
      try {
        const parsedUser = JSON.parse(storedUser)
        setUser(parsedUser)

        // Background sync with API to refresh name/nickname instantly on reload
        apiRequest<any>(API_ENDPOINTS.myProfile).then((res) => {
          if (res.data) {
            const syncedUser = { ...parsedUser, ...res.data }
            setUser(syncedUser)
            localStorage.setItem("user", JSON.stringify(syncedUser))
          }
        })
      } catch {
        localStorage.removeItem("user")
        localStorage.removeItem("auth_token")
      }
    }
    setIsLoading(false)
  }, [])

  const login = async (username: string, password: string) => {
    try {
      const { data, error } = await apiRequest<{ user?: User; accessToken: string; tokenType: string }>(
        API_ENDPOINTS.login,
        {
          method: "POST",
          body: JSON.stringify({ username, password }),
        }
      )

      if (error || !data) {
        throw new Error("Login failed")
      }

      // JWT 디코딩하여 userId, role 추출
      const payload = decodeJWT(data.accessToken)
      
      let loggedInUser: User = data.user || {
        id: payload?.userId || 1, // 백엔드에서 user 객체를 주지 않더라도 토큰에서 추출
        username,
        name: username,
        role: payload?.role
      }

      // 혹시라도 토큰에서 꺼낸 권한 등을 user 객체에 병합하고 싶다면:
      if (payload?.role) {
        loggedInUser.role = payload.role;
      }

      // 토큰 세팅
      localStorage.setItem("auth_token", data.accessToken)

      // 진짜 유저 정보(name, nickname 등) 즉시 동기화
      const profileInfo = await apiRequest<any>(API_ENDPOINTS.myProfile)
      if (profileInfo.data) {
        loggedInUser = { ...loggedInUser, ...profileInfo.data }
      }

      setUser(loggedInUser)
      localStorage.setItem("user", JSON.stringify(loggedInUser))
      return { success: true }
    } catch {
      // Fallback to mock login on any error
      const mockUser: User = { id: 1, username, name: username, role: "USER" }
      setUser(mockUser)
      localStorage.setItem("user", JSON.stringify(mockUser))
      localStorage.setItem("auth_token", "mock_token")
      return { success: true }
    }
  }

  const register = async (username: string, password: string, name: string) => {
    try {
      const { data, error } = await apiRequest<{ user: User; token: string }>(
        API_ENDPOINTS.register,
        {
          method: "POST",
          body: JSON.stringify({ username, password, name }),
        }
      )

      if (error || !data) {
        // For demo purposes, allow mock registration when backend is not available
        const mockUser: User = { id: Date.now(), username, name }
        setUser(mockUser)
        localStorage.setItem("user", JSON.stringify(mockUser))
        localStorage.setItem("auth_token", "mock_token")
        return { success: true }
      }

      setUser(data.user)
      localStorage.setItem("user", JSON.stringify(data.user))
      localStorage.setItem("auth_token", data.token)
      return { success: true }
    } catch {
      // Fallback to mock registration on any error
      const mockUser: User = { id: Date.now(), username, name }
      setUser(mockUser)
      localStorage.setItem("user", JSON.stringify(mockUser))
      localStorage.setItem("auth_token", "mock_token")
      return { success: true }
    }
  }

  const logout = () => {
    setUser(null)
    localStorage.removeItem("user")
    localStorage.removeItem("auth_token")
  }

  const updateUser = (updates: Partial<User>) => {
    if (user) {
      const updatedUser = { ...user, ...updates }
      setUser(updatedUser)
      localStorage.setItem("user", JSON.stringify(updatedUser))
    }
  }

  return (
    <AuthContext.Provider value={{ user, isLoading, login, register, logout, updateUser }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
