"use client"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import Image from "next/image"
import { Header } from "@/components/header"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { useAuth } from "@/lib/auth-context"
import { apiRequest, API_ENDPOINTS } from "@/lib/api"
import { User, Heart, FileText, Settings, Calendar, ThermometerSun, X } from "lucide-react"
import { Input } from "@/components/ui/input"

interface CheeredAnimal {
  animalId: number
  kind: string
  breed?: string
  imageUrl?: string
  heartCount: number
  temperature: number
}

interface MyFeed {
  feedId: number
  title: string
  category: string
  createdAt: string
}

// Mock data for demo
const mockCheeredAnimals: CheeredAnimal[] = [
  { animalId: 1, kind: "개", breed: "믹스견", imageUrl: "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=200&h=200&fit=crop", heartCount: 5, temperature: 85.0 },
  { animalId: 2, kind: "고양이", breed: "코리안숏헤어", imageUrl: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=200&h=200&fit=crop", heartCount: 3, temperature: 72.0 },
  { animalId: 3, kind: "개", breed: "말티즈", imageUrl: "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=200&h=200&fit=crop", heartCount: 2, temperature: 45.0 },
]

const mockMyFeeds: MyFeed[] = [
  { feedId: 101, title: "우리 강아지 입양 후기", category: "REVIEW", createdAt: "2025-01-15T10:30:00" },
  { feedId: 102, title: "유기동물 봉사활동 후기", category: "VOLUNTEER", createdAt: "2025-01-10T14:20:00" },
  { feedId: 103, title: "이 아이 입양 원해요", category: "PROMOTE", createdAt: "2025-01-05T09:15:00" },
]

const categoryLabels: Record<string, string> = {
  REVIEW: "입양 후기",
  VOLUNTEER: "봉사 후기",
  PROMOTE: "홍보",
  QUESTION: "질문",
  FREE: "자유",
}

export default function ProfilePage() {
  const router = useRouter()
  const { user, isLoading: authLoading, updateUser } = useAuth()
  const [cheeredAnimals, setCheeredAnimals] = useState<CheeredAnimal[]>([])
  const [myFeeds, setMyFeeds] = useState<MyFeed[]>([])
  const [profileStats, setProfileStats] = useState<{ feedCount: number, cheerCount: number, createdAt?: string }>({ feedCount: 0, cheerCount: 0 })
  const [isLoading, setIsLoading] = useState(true)
  const [showNicknameModal, setShowNicknameModal] = useState(false)
  const [showPasswordModal, setShowPasswordModal] = useState(false)
  const [showUsernameModal, setShowUsernameModal] = useState(false)
  const [isWithdrawing, setIsWithdrawing] = useState(false)

  const handleWithdraw = async () => {
    if (isWithdrawing) return

    const confirmed = window.confirm("정말 회원 탈퇴하시겠습니까?")
    if (!confirmed) return

    setIsWithdrawing(true)
    const { error, status } = await apiRequest<void>(API_ENDPOINTS.withdraw, {
      method: "DELETE",
    })
    setIsWithdrawing(false)

    if (error || status !== 204) {
      alert(error || "회원 탈퇴에 실패했습니다.")
      return
    }

    localStorage.removeItem("auth_token")
    localStorage.removeItem("user")
    alert("회원 탈퇴가 완료되었습니다.")
    window.location.href = "/"
  }

  useEffect(() => {
    if (authLoading) return;
    if (!user) {
      router.push("/login")
      return
    }

    const fetchProfileData = async () => {
      setIsLoading(true)

      // Fetch user profile info
      const profileResponse = await apiRequest<{
        username: string, profileImageUrl?: string, nickname?: string, name: string, createdAt: string
      }>(API_ENDPOINTS.myProfile)
      
      if (profileResponse.data) {
        if (!user.createdAt || user.nickname !== profileResponse.data.nickname || user.username !== profileResponse.data.username) {
          updateUser({
            username: profileResponse.data.username,
            nickname: profileResponse.data.nickname,
            name: profileResponse.data.name,
            createdAt: profileResponse.data.createdAt
          })
        }
      }

      // Fetch profile stats overview
      const statsResponse = await apiRequest<{ feedCount: number, cheerCount: number, createdAt?: string }>(
        API_ENDPOINTS.myProfileStats
      )
      if (statsResponse.data) {
        setProfileStats(statsResponse.data)
      }

      // Fetch cheered animals
      const heartsResponse = await apiRequest<{
        totalAnimalCount: number, animals: CheeredAnimal[]
      }>(API_ENDPOINTS.myCheerAnimals)

      if (heartsResponse.data?.animals) {
        setCheeredAnimals(heartsResponse.data.animals)
      } else {
        // Use mock data for demo
        setCheeredAnimals(mockCheeredAnimals)
      }

      // Fetch my feeds
      const feedsResponse = await apiRequest<{
        totalFeedCount: number, feeds: MyFeed[]
      }>(API_ENDPOINTS.myFeeds)

      if (feedsResponse.data?.feeds) {
        setMyFeeds(feedsResponse.data.feeds)
      } else {
        // Use mock data for demo
        setMyFeeds(mockMyFeeds)
      }

      setIsLoading(false)
    }

    fetchProfileData()
  }, [user, router, authLoading])

  if (!user || authLoading) {
    return null
  }

  return (
    <div className="min-h-screen bg-background">
      <Header />

      <main className="max-w-4xl mx-auto px-4 py-8">
        {/* Profile Header */}
        <Card className="mb-8">
          <CardContent className="pt-6">
            <div className="flex flex-col sm:flex-row items-center gap-6">
              {/* Avatar */}
              <div className="w-24 h-24 rounded-full bg-primary/10 flex items-center justify-center">
                <User className="w-12 h-12 text-primary" />
              </div>

              {/* User Info */}
              <div className="flex-1 text-center sm:text-left">
                <h1 className="text-2xl font-bold text-foreground">{user.nickname || user.name}</h1>
                <p className="text-muted-foreground">@{user.username}</p>

                {/* Stats */}
                <div className="flex justify-center sm:justify-start gap-6 mt-4">
                  <div className="text-center">
                    <p className="text-2xl font-bold text-primary">{profileStats.cheerCount}</p>
                    <p className="text-sm text-muted-foreground">보낸 응원</p>
                  </div>
                  <div className="text-center">
                    <p className="text-2xl font-bold text-primary">{cheeredAnimals.length}</p>
                    <p className="text-sm text-muted-foreground">응원한 동물</p>
                  </div>
                  <div className="text-center">
                    <p className="text-2xl font-bold text-primary">{profileStats.feedCount}</p>
                    <p className="text-sm text-muted-foreground">작성한 글</p>
                  </div>
                </div>
              </div>

              {/* Settings Button */}
              <Button variant="outline" className="rounded-xl gap-2">
                <Settings className="w-4 h-4" />
                설정
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Tabs */}
        <Tabs defaultValue="account" className="space-y-6">
          <TabsList className="grid w-full grid-cols-3 rounded-xl">
            <TabsTrigger value="account" className="gap-2 rounded-xl">
              <User className="w-4 h-4" />
              <span className="hidden sm:inline">계정 정보</span>
            </TabsTrigger>
            <TabsTrigger value="hearts" className="gap-2 rounded-xl">
              <Heart className="w-4 h-4" />
              <span className="hidden sm:inline">응원 내역</span>
            </TabsTrigger>
            <TabsTrigger value="posts" className="gap-2 rounded-xl">
              <FileText className="w-4 h-4" />
              <span className="hidden sm:inline">작성한 글</span>
            </TabsTrigger>
          </TabsList>

          {/* Account Info Tab */}
          <TabsContent value="account">
            <Card>
              <CardHeader>
                <CardTitle>계정 정보</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">아이디</p>
                    <p className="font-medium">{user.username}</p>
                  </div>
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">이름</p>
                    <p className="font-medium">{user.name}</p>
                  </div>
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">닉네임</p>
                    <p className="font-medium">{user.nickname}</p>
                  </div>
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">가입일</p>
                    <p className="font-medium">
                      {(profileStats.createdAt || user.createdAt)
                        ? new Date(profileStats.createdAt || user.createdAt!).toLocaleDateString('ko-KR', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric',
                        })
                        : "2025년 1월 1일"}
                    </p>
                  </div>
                </div>

                <div className="pt-4 border-t flex flex-wrap items-center gap-2">
                  <Button variant="outline" className="rounded-xl" onClick={() => setShowUsernameModal(true)}>
                    아이디 변경
                  </Button>
                  <Button variant="outline" className="rounded-xl" onClick={() => setShowPasswordModal(true)}>
                    비밀번호 변경
                  </Button>
                  <Button variant="outline" className="rounded-xl" onClick={() => setShowNicknameModal(true)}>
                    이름(닉네임) 변경
                  </Button>
                </div>
                <div className="flex justify-end pt-3">
                  <Button
                    className="rounded-xl bg-destructive text-white hover:bg-destructive/90"
                    onClick={handleWithdraw}
                    disabled={isWithdrawing}
                  >
                    {isWithdrawing ? "탈퇴 처리 중..." : "회원 탈퇴"}
                  </Button>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          {/* Hearts Tab */}
          <TabsContent value="hearts">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Heart className="w-5 h-5 text-primary" />
                  응원한 동물 ({cheeredAnimals.length})
                </CardTitle>
              </CardHeader>
              <CardContent>
                {isLoading ? (
                  <div className="text-center py-8 text-muted-foreground">
                    로딩 중...
                  </div>
                ) : cheeredAnimals.length === 0 ? (
                  <div className="text-center py-8">
                    <Heart className="w-12 h-12 text-muted-foreground/30 mx-auto mb-4" />
                    <p className="text-muted-foreground">아직 응원한 동물이 없습니다</p>
                    <Link href="/">
                      <Button className="mt-4 rounded-xl">동물 응원하러 가기</Button>
                    </Link>
                  </div>
                ) : (
                  <div className="space-y-3">
                    {cheeredAnimals.map((animal) => (
                      <Link
                        key={animal.animalId}
                        href={`/animals/${animal.animalId}`}
                        className="block"
                      >
                        <div className="flex items-center gap-4 p-3 rounded-xl hover:bg-secondary/50 transition-colors">
                          {/* Animal Image */}
                          <div className="relative w-16 h-16 rounded-xl overflow-hidden bg-secondary flex-shrink-0">
                            {animal.imageUrl ? (
                              <Image
                                src={animal.imageUrl}
                                alt={`${animal.kind} ${animal.breed || ""}`}
                                fill
                                className="object-cover"
                              />
                            ) : (
                              <div className="w-full h-full flex items-center justify-center text-muted-foreground">
                                {animal.kind === "개" ? "🐕" : "🐈"}
                              </div>
                            )}
                          </div>

                          {/* Animal Info */}
                          <div className="flex-1 min-w-0">
                            <p className="font-medium text-foreground">
                              {animal.kind} {animal.breed && `(${animal.breed})`}
                            </p>
                            <div className="flex items-center gap-4 mt-1 text-sm text-muted-foreground">
                              <span className="flex items-center gap-1">
                                <Heart className="w-3.5 h-3.5 fill-primary text-primary" />
                                {animal.heartCount}개 보냄
                              </span>
                              <span className="flex items-center gap-1">
                                <ThermometerSun className="w-3.5 h-3.5" />
                                {animal.temperature.toFixed(1)}C
                              </span>
                            </div>
                          </div>

                          {/* Arrow */}
                          <div className="text-muted-foreground">
                            &rarr;
                          </div>
                        </div>
                      </Link>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          {/* Posts Tab */}
          <TabsContent value="posts">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="w-5 h-5 text-primary" />
                  작성한 글 ({myFeeds.length})
                </CardTitle>
              </CardHeader>
              <CardContent>
                {isLoading ? (
                  <div className="text-center py-8 text-muted-foreground">
                    로딩 중...
                  </div>
                ) : myFeeds.length === 0 ? (
                  <div className="text-center py-8">
                    <FileText className="w-12 h-12 text-muted-foreground/30 mx-auto mb-4" />
                    <p className="text-muted-foreground">아직 작성한 글이 없습니다</p>
                    <Link href="/community">
                      <Button className="mt-4 rounded-xl">커뮤니티 가기</Button>
                    </Link>
                  </div>
                ) : (
                  <div className="space-y-3">
                    {myFeeds.map((feed) => (
                      <Link
                        key={feed.feedId}
                        href={`/community/${feed.feedId}`}
                        className="block"
                      >
                        <div className="flex items-center justify-between p-3 rounded-xl hover:bg-secondary/50 transition-colors">
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2 mb-1">
                              <span className="px-2 py-0.5 text-xs rounded-full bg-primary/10 text-primary">
                                {categoryLabels[feed.category] || feed.category}
                              </span>
                            </div>
                            <p className="font-medium text-foreground truncate">
                              {feed.title}
                            </p>
                            <p className="text-sm text-muted-foreground flex items-center gap-1 mt-1">
                              <Calendar className="w-3.5 h-3.5" />
                              {new Date(feed.createdAt).toLocaleDateString("ko-KR")}
                            </p>
                          </div>

                          {/* Arrow */}
                          <div className="text-muted-foreground">
                            &rarr;
                          </div>
                        </div>
                      </Link>
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </main>

      {showNicknameModal && (
        <UpdateNicknameModal
          currentNickname={user.nickname || user.name}
          onClose={() => setShowNicknameModal(false)}
          onSuccess={(newName) => {
            setShowNicknameModal(false)
            updateUser({ nickname: newName }) // 로컬 닉네임 상태 변경
          }}
        />
      )}

      {showUsernameModal && (
        <UpdateUsernameModal
          currentUsername={user.username}
          onClose={() => setShowUsernameModal(false)}
          onSuccess={(newUsername) => {
            setShowUsernameModal(false)
            updateUser({ username: newUsername }) // 로컬 상태 변경
          }}
        />
      )}

      {showPasswordModal && (
        <UpdatePasswordModal
          onClose={() => setShowPasswordModal(false)}
        />
      )}
    </div>
  )
}

function UpdateUsernameModal({ currentUsername, onClose, onSuccess }: { currentUsername: string, onClose: () => void, onSuccess: (newUsername: string) => void }) {
  const [username, setUsername] = useState(currentUsername)
  const [errorMessage, setErrorMessage] = useState("")

  const handleSubmit = async () => {
    setErrorMessage("")

    // 클라이언트 사이드 유효성 검증 (Regex)
    const usernameRegex = /^[a-zA-Z0-9!@#$%^&*()_+\-={}\[\]:;"'<>,.?/]{5,20}$/;

    if (!usernameRegex.test(username)) {
      setErrorMessage("id는 5~20자 사이의 영문, 숫자, 특수문자만 사용할 수 있습니다.");
      return;
    }

    const { data, error, errorCode } = await apiRequest(API_ENDPOINTS.updateUsername, {
      method: "PATCH",
      body: JSON.stringify({ newUsername: username }) // 'newUsername' 백엔드 필드 규격 준수
    })

    if (error || errorCode) {
      if (errorCode === "U-007" || error?.includes("사용 중인") || error?.includes("중복")) {
        setErrorMessage("이미 사용 중인 아이디입니다.")
      } else {
        setErrorMessage(error || "아이디 변경에 실패했습니다.")
      }
      return
    }

    alert("아이디가 성공적으로 변경되었습니다.")
    onSuccess(username)
  }

  return (
    <div className="fixed inset-0 bg-foreground/50 flex items-center justify-center z-50 p-4">
      <Card className="w-full max-w-sm border-0 shadow-2xl">
        <CardHeader className="flex flex-row items-center justify-between pb-2">
          <CardTitle className="text-lg">아이디 변경</CardTitle>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="w-5 h-5" />
          </Button>
        </CardHeader>
        <CardContent className="space-y-4 pt-4">
          <div className="space-y-2">
            <label className="text-sm font-medium">새 아이디</label>
            <Input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="새로운 아이디를 입력하세요"
              className="rounded-xl"
            />
          </div>
          {errorMessage && (
            <p className="text-sm font-medium text-destructive">{errorMessage}</p>
          )}
          <div className="flex gap-2 w-full pt-4">
            <Button variant="outline" className="flex-1 rounded-xl" onClick={onClose}>취소</Button>
            <Button className="flex-1 rounded-xl bg-primary text-primary-foreground hover:bg-primary/90" onClick={handleSubmit}>변경하기</Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

function UpdateNicknameModal({ currentNickname, onClose, onSuccess }: { currentNickname: string, onClose: () => void, onSuccess: (newNickname: string) => void }) {
  const [nickname, setNickname] = useState(currentNickname)
  const [errorMessage, setErrorMessage] = useState("")

  const handleSubmit = async () => {
    setErrorMessage("")
    if (!nickname.trim()) {
      setErrorMessage("변경할 닉네임을 입력해주세요.")
      return
    }

    const { data, error, errorCode } = await apiRequest(API_ENDPOINTS.updateNickname, {
      method: "PATCH",
      body: JSON.stringify({ nickname })
    })

    if (error || errorCode) {
      if (errorCode === "U-007" || error?.includes("DUPLICATE_NICKNAME") || error?.includes("사용 중인")) {
        setErrorMessage("이미 사용 중인 닉네임입니다.")
      } else {
        setErrorMessage("닉네임 변경에 실패했습니다: " + (error || errorCode))
      }
      return
    }

    alert("닉네임이 성공적으로 변경되었습니다.")
    onSuccess(nickname)
  }

  return (
    <div className="fixed inset-0 bg-foreground/50 flex items-center justify-center z-50 p-4">
      <Card className="w-full max-w-sm border-0 shadow-2xl">
        <CardHeader className="flex flex-row items-center justify-between pb-2">
          <CardTitle className="text-lg">닉네임 변경</CardTitle>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="w-5 h-5" />
          </Button>
        </CardHeader>
        <CardContent className="space-y-4 pt-4">
          <div className="space-y-2">
            <label className="text-sm font-medium">새 닉네임</label>
            <Input
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              placeholder="새로운 닉네임을 입력하세요"
              className="rounded-xl"
            />
          </div>
          {errorMessage && (
            <p className="text-sm font-medium text-destructive">{errorMessage}</p>
          )}
          <div className="flex gap-2 w-full pt-4">
            <Button variant="outline" className="flex-1 rounded-xl" onClick={onClose}>취소</Button>
            <Button className="flex-1 rounded-xl bg-primary text-primary-foreground hover:bg-primary/90" onClick={handleSubmit}>변경하기</Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

function UpdatePasswordModal({ onClose }: { onClose: () => void }) {
  const [currentPassword, setCurrentPassword] = useState("")
  const [newPassword, setNewPassword] = useState("")
  const [errorMessage, setErrorMessage] = useState("")

  const handleSubmit = async () => {
    setErrorMessage("")
    if (!currentPassword || !newPassword) {
      setErrorMessage("현재 비밀번호와 새 비밀번호를 모두 입력해주세요.")
      return
    }

    // 1. 클라이언트 사이드 유효성 검증 (Regex)
    // 로그에 찍힌 백엔드 패턴과 동일하게 설정합니다.
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-={}\[\]:;"'<>,.?/]).{8,16}$/;

    if (!passwordRegex.test(newPassword)) {
      setErrorMessage("비밀번호는 8~16자 사이이며 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다.");
      return;
    }

    // 2. 서버 요청
    const { data, error, errorCode } = await apiRequest(API_ENDPOINTS.updatePassword, {
      method: "PATCH",
      body: JSON.stringify({ currentPassword, newPassword })
    });

    // 3. 응답 처리
    if (error || errorCode) {
      // 백엔드 UserErrorCode 에넘 기준 매핑
      const errorMessages: Record<string, string> = {
        "U-004": "사용자 정보를 찾을 수 없습니다.",
        "U-005": "현재 비밀번호가 일치하지 않습니다.",
        "U-006": "기존 비밀번호와 다른 비밀번호를 입력해주세요.",
        "U-001": "로그인이 만료되었습니다. 다시 로그인해주세요."
      };

      // 1순위: 정의된 에러 코드 확인
      // 2순위: 백엔드 Validation 에러 메시지(error) 사용
      // 3순위: 기본 메시지
      const finalMessage = (errorCode && errorMessages[errorCode]) || error || "비밀번호 변경에 실패했습니다.";

      setErrorMessage(finalMessage);
      return;
    }

    // 성공 처리
    alert("비밀번호가 변경되었습니다.");
    onClose()
  }

  return (
    <div className="fixed inset-0 bg-foreground/50 flex items-center justify-center z-50 p-4">
      <Card className="w-full max-w-sm border-0 shadow-2xl">
        <CardHeader className="flex flex-row items-center justify-between pb-2">
          <CardTitle className="text-lg">비밀번호 변경</CardTitle>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="w-5 h-5" />
          </Button>
        </CardHeader>
        <CardContent className="space-y-4 pt-4">
          <div className="space-y-2">
            <label className="text-sm font-medium">현재 비밀번호</label>
            <Input
              type="password"
              value={currentPassword}
              onChange={(e) => setCurrentPassword(e.target.value)}
              placeholder="현재 비밀번호를 입력하세요"
              className="rounded-xl"
            />
          </div>
          <div className="space-y-2">
            <label className="text-sm font-medium">새 비밀번호</label>
            <Input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="새 비밀번호를 입력하세요"
              className="rounded-xl"
            />
          </div>
          {errorMessage && (
            <p className="text-sm font-medium text-destructive">{errorMessage}</p>
          )}
          <div className="flex gap-2 w-full pt-4">
            <Button variant="outline" className="flex-1 rounded-xl" onClick={onClose}>취소</Button>
            <Button className="flex-1 rounded-xl bg-primary text-primary-foreground hover:bg-primary/90" onClick={handleSubmit}>변경하기</Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
