"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { Heart, Eye, EyeOff } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { useAuth } from "@/lib/auth-context"

const USERNAME_PATTERN = /^[a-zA-Z0-9!@#$%^&*()_+\-={}\[\]:;"'<>,.?/]{5,20}$/
const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-={}\[\]:;"'<>,.?/]).+$/

type RegisterFieldErrors = {
  username: string
  password: string
  nickname: string
  realname: string
  confirmPassword: string
}

export default function RegisterPage() {
  const router = useRouter()
  const { register } = useAuth()
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")
  const [nickname, setNickname] = useState("")
  const [realname, setRealname] = useState("")
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const [error, setError] = useState("")
  const [fieldErrors, setFieldErrors] = useState<RegisterFieldErrors>({
    username: "",
    password: "",
    nickname: "",
    realname: "",
    confirmPassword: "",
  })
  const [isLoading, setIsLoading] = useState(false)

  const validateUsername = (value: string) => {
    if (!value.trim()) return "id는 필수 입력값입니다."
    if (value.length < 5 || value.length > 20) return "id는 5~20자 사이여야 합니다."
    if (!USERNAME_PATTERN.test(value)) return "id는 영문, 숫자, 특수문자만 사용할 수 있습니다."
    return ""
  }

  const validatePassword = (value: string) => {
    if (!value.trim()) return "password는 필수 입력값입니다."
    if (value.length < 8 || value.length > 16) return "password는 8~16자 사이여야 합니다."
    if (!PASSWORD_PATTERN.test(value)) {
      return "password는 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    }
    return ""
  }

  const validateNickname = (value: string) => {
    if (!value.trim()) return "nickname은 필수 입력값입니다."
    return ""
  }

  const validateRealname = (value: string) => {
    if (!value.trim()) return "realname은 필수 입력값입니다."
    return ""
  }

  const validateConfirmPassword = (pwd: string, confirmPwd: string) => {
    if (!confirmPwd.trim()) return "비밀번호 확인은 필수 입력값입니다."
    if (pwd !== confirmPwd) return "비밀번호가 일치하지 않습니다."
    return ""
  }

  const validateForm = () => {
    const nextErrors: RegisterFieldErrors = {
      username: validateUsername(username),
      password: validatePassword(password),
      nickname: validateNickname(nickname),
      realname: validateRealname(realname),
      confirmPassword: validateConfirmPassword(password, confirmPassword),
    }

    setFieldErrors(nextErrors)
    return Object.values(nextErrors).every((fieldError) => !fieldError)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError("")
    if (!validateForm()) {
      setError("입력값 제한 조건을 확인해주세요.")
      setIsLoading(false)
      return
    }
    setIsLoading(true)

    const result = await register(username, password, nickname, realname)
    
    if (result.success) {
      router.push("/login")
    } else {
      setError(result.error || "회원가입에 실패했습니다")
    }
    
    setIsLoading(false)
  }

  return (
    <div className="min-h-screen bg-background flex items-center justify-center px-4 py-8">
      <Card className="w-full max-w-md border-0 shadow-xl">
        <CardHeader className="text-center pb-2">
          <Link href="/" className="inline-flex items-center justify-center gap-2 mb-4">
            <div className="flex items-center justify-center w-12 h-12 rounded-xl bg-primary text-primary-foreground">
              <Heart className="w-6 h-6 fill-current" />
            </div>
          </Link>
          <h1 className="text-2xl font-bold text-foreground">회원가입</h1>
          <p className="text-sm text-muted-foreground mt-1">
            함께 유기동물을 응원해요
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
              <label htmlFor="nickname" className="text-sm font-medium text-foreground">
                닉네임
              </label>
              <Input
                id="nickname"
                type="text"
                value={nickname}
                onChange={(e) => {
                  const value = e.target.value
                  setNickname(value)
                  setFieldErrors((prev) => ({ ...prev, nickname: validateNickname(value) }))
                }}
                placeholder="닉네임을 입력하세요"
                className="h-12 rounded-xl bg-secondary/50 border-0"
                disabled={isLoading}
              />
              {fieldErrors.nickname && <p className="text-sm text-destructive">{fieldErrors.nickname}</p>}
            </div>
            <div className="space-y-2">
              <label htmlFor="realname" className="text-sm font-medium text-foreground">
                이름
              </label>
              <Input
                id="realname"
                type="text"
                value={realname}
                onChange={(e) => {
                  const value = e.target.value
                  setRealname(value)
                  setFieldErrors((prev) => ({ ...prev, realname: validateRealname(value) }))
                }}
                placeholder="이름을 입력하세요"
                className="h-12 rounded-xl bg-secondary/50 border-0"
                disabled={isLoading}
              />
              {fieldErrors.realname && <p className="text-sm text-destructive">{fieldErrors.realname}</p>}
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
                    setFieldErrors((prev) => ({
                      ...prev,
                      password: validatePassword(value),
                      confirmPassword: validateConfirmPassword(value, confirmPassword),
                    }))
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
            <div className="space-y-2">
              <label htmlFor="confirmPassword" className="text-sm font-medium text-foreground">
                비밀번호 확인
              </label>
              <div className="relative">
                <Input
                  id="confirmPassword"
                  type={showConfirmPassword ? "text" : "password"}
                  value={confirmPassword}
                  onChange={(e) => {
                    const value = e.target.value
                    setConfirmPassword(value)
                    setFieldErrors((prev) => ({
                      ...prev,
                      confirmPassword: validateConfirmPassword(password, value),
                    }))
                  }}
                  placeholder="비밀번호를 다시 입력하세요"
                  className="h-12 rounded-xl bg-secondary/50 border-0 pr-12"
                  disabled={isLoading}
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="absolute right-4 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                >
                  {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
              {fieldErrors.confirmPassword && <p className="text-sm text-destructive">{fieldErrors.confirmPassword}</p>}
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
              {isLoading ? "가입 중..." : "회원가입"}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-muted-foreground">
              이미 계정이 있으신가요?{" "}
              <Link href="/login" className="text-primary font-medium hover:underline">
                로그인
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
