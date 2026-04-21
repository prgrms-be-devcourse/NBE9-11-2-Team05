"use client"

import { createContext, useContext, useState, useEffect, ReactNode } from "react"
import { API_ENDPOINTS, apiRequest, User, decodeJWT } from "./api"

interface AuthContextType {
  user: User | null
  isLoading: boolean
  login: (username: string, password: string) => Promise<{ success: boolean; error?: string }>
  register: (username: string, password: string, nickname: string, realname: string) => Promise<{ success: boolean; error?: string }>
  /** 이메일 로그인/가입 등에서 받은 accessToken으로 세션 저장 */
  establishSessionFromAccessToken: (
    accessToken: string,
    fallbackIdentifier: string
  ) => Promise<{ success: boolean; error?: string }>
  logout: () => Promise<{ success: boolean; error?: string }>
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

  const establishSessionFromAccessToken = async (accessToken: string, fallbackIdentifier: string) => {
    try {
      const payload = decodeJWT(accessToken)
      const extractedId = Number(payload?.sub) || Number(payload?.userId)

      if (!extractedId) {
        console.error("JWT 페이로드에서 사용자 ID를 찾을 수 없습니다:", payload)
        return { success: false, error: "사용자 정보를 파악할 수 없는 토큰입니다." }
      }

      let loggedInUser: User = {
        id: extractedId,
        username: fallbackIdentifier,
        name: fallbackIdentifier,
        role: payload?.role,
      }

      if (payload?.role) {
        loggedInUser.role = payload.role
      }

      localStorage.setItem("auth_token", accessToken)

      const profileInfo = await apiRequest<any>(API_ENDPOINTS.myProfile)
      if (profileInfo.data) {
        loggedInUser = { ...loggedInUser, ...profileInfo.data }
      }

      setUser(loggedInUser)
      localStorage.setItem("user", JSON.stringify(loggedInUser))
      return { success: true }
    } catch (error) {
      return { success: false, error: error instanceof Error ? error.message : "로그인에 실패했습니다." }
    }
  }

  const login = async (username: string, password: string) => {
    try {
      const { data, error, status } = await apiRequest<{ tokenType: string; accessToken: string }>(
        API_ENDPOINTS.login,
        {
          method: "POST",
          body: JSON.stringify({ username, password }),
        }
      )

      if (error || !data) {
        if (status === 401) {
          return { success: false, error: "id 또는 비밀번호가 잘못되었습니다." }
        }
        return { success: false, error: error || "로그인에 실패했습니다." }
      }

      return establishSessionFromAccessToken(data.accessToken, username)
    } catch (error) {
      return { success: false, error: error instanceof Error ? error.message : "로그인에 실패했습니다." }
    }
  }

  const register = async (username: string, password: string, nickname: string, realname: string) => {
    try {
      const { data, error } = await apiRequest<{ userId: number; username: string; nickname: string }>(
        API_ENDPOINTS.register,
        {
          method: "POST",
          body: JSON.stringify({ username, password, nickname, realname }),
        }
      )

      if (error || !data) {
        return { success: false, error: error || "회원가입에 실패했습니다" }
      }

      return { success: true }
    } catch (error) {
      return { success: false, error: error instanceof Error ? error.message : "회원가입에 실패했습니다" }
    }
  }

  const logout = async () => {
    try {
      const { error, status } = await apiRequest<void>(API_ENDPOINTS.logout, {
        method: "POST",
      })

      if (error || status !== 204) {
        return { success: false, error: error || "로그아웃에 실패했습니다." }
      }

      setUser(null)
      localStorage.removeItem("user")
      localStorage.removeItem("auth_token")
      alert("로그아웃되었습니다.")
      window.location.href = "/"
      return { success: true }
    } catch (error) {
      return { success: false, error: error instanceof Error ? error.message : "로그아웃에 실패했습니다." }
    }
  }

  const updateUser = (updates: Partial<User>) => {
    if (user) {
      const updatedUser = { ...user, ...updates }
      setUser(updatedUser)
      localStorage.setItem("user", JSON.stringify(updatedUser))
    }
  }

  return (
    <AuthContext.Provider
      value={{ user, isLoading, login, register, establishSessionFromAccessToken, logout, updateUser }}
    >
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
