"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { Heart, Eye, EyeOff } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { useAuth } from "@/lib/auth-context"

type LoginFieldErrors = {
  username: string
  password: string
}

export default function LoginPage() {
  const router = useRouter()
  const { login } = useAuth()
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState("")
  const [fieldErrors, setFieldErrors] = useState<LoginFieldErrors>({
    username: "",
    password: "",
  })
  const [isLoading, setIsLoading] = useState(false)

  const validateUsername = (value: string) => {
    if (!value.trim()) return "id는 필수 입력값입니다."
    return ""
  }

  const validatePassword = (value: string) => {
    if (!value.trim()) return "password는 필수 입력값입니다."
    return ""
  }

  const validateForm = () => {
    const nextErrors: LoginFieldErrors = {
      username: validateUsername(username),
      password: validatePassword(password),
    }
    setFieldErrors(nextErrors)
    return Object.values(nextErrors).every((fieldError) => !fieldError)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    if (!validateForm()) {
      setError("입력값 제한 조건을 확인해주세요.")
      return
    }
    setIsLoading(true)

    const result = await login(username, password)
    
    if (result.success) {
      router.push("/")
    } else {
      setError(result.error || "로그인에 실패했습니다")
    }
    
    setIsLoading(false)
  }

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-4">
      <Card className="w-full max-w-md border-0 shadow-xl">
        <CardHeader className="text-center pb-2">
          <Link href="/" className="inline-flex items-center justify-center gap-2 mb-4">
            <div className="flex items-center justify-center w-12 h-12 rounded-xl bg-primary text-primary-foreground">
              <Heart className="w-6 h-6 fill-current" />
            </div>
          </Link>
          <h1 className="text-2xl font-bold text-foreground">로그인</h1>
          <p className="text-sm text-muted-foreground mt-1">
            유기동물 응원에 참여해주세요
          </p>
        </CardHeader>
        <CardContent className="pt-4">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <label htmlFor="username" className="text-sm font-medium text-foreground">
                아이디
              </label>
              <Input
                id="username"
                type="text"
                value={username}
                onChange={(e) => {
                  const value = e.target.value
                  setUsername(value)
                  setFieldErrors((prev) => ({ ...prev, username: validateUsername(value) }))
                }}
                placeholder="아이디를 입력하세요"
                className="h-12 rounded-xl bg-secondary/50 border-0"
                disabled={isLoading}
              />
              {fieldErrors.username && <p className="text-sm text-destructive">{fieldErrors.username}</p>}
            </div>
            <div className="space-y-2">
              <label htmlFor="password" className="text-sm font-medium text-foreground">
                비밀번호
              </label>
              <div className="relative">
                <Input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  value={password}
                  onChange={(e) => {
                    const value = e.target.value
                    setPassword(value)
                    setFieldErrors((prev) => ({ ...prev, password: validatePassword(value) }))
                  }}
                  placeholder="비밀번호를 입력하세요"
                  className="h-12 rounded-xl bg-secondary/50 border-0 pr-12"
                  disabled={isLoading}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
              {fieldErrors.password && <p className="text-sm text-destructive">{fieldErrors.password}</p>}
            </div>

            {error && (
              <div className="p-3 rounded-xl bg-destructive/10 text-destructive text-sm text-center">
                {error}
              </div>
            )}

            <Button
              type="submit"
              className="w-full h-12 rounded-xl bg-primary text-primary-foreground hover:bg-primary/90 text-base font-semibold"
              disabled={isLoading}
            >
              {isLoading ? "로그인 중..." : "로그인"}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-muted-foreground">
              계정이 없으신가요?{" "}
              <Link href="/register" className="text-primary font-medium hover:underline">
                회원가입
              </Link>
            </p>
          </div>

          <div className="mt-4 text-center">
            <Link href="/" className="text-sm text-muted-foreground hover:text-foreground transition-colors">
              홈으로 돌아가기
            </Link>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
