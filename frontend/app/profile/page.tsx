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
import { User, Heart, FileText, Settings, Calendar, ThermometerSun } from "lucide-react"

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
  const { user } = useAuth()
  const [cheeredAnimals, setCheeredAnimals] = useState<CheeredAnimal[]>([])
  const [myFeeds, setMyFeeds] = useState<MyFeed[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    if (!user) {
      router.push("/login")
      return
    }

    const fetchProfileData = async () => {
      setIsLoading(true)
      
      // Fetch cheered animals
      const heartsResponse = await apiRequest<{
        data: { content: CheeredAnimal[] }
      }>(API_ENDPOINTS.myHearts)
      
      if (heartsResponse.data?.data?.content) {
        setCheeredAnimals(heartsResponse.data.data.content)
      } else {
        // Use mock data for demo
        setCheeredAnimals(mockCheeredAnimals)
      }

      // Fetch my feeds
      const feedsResponse = await apiRequest<{
        data: { content: MyFeed[] }
      }>(API_ENDPOINTS.myFeeds)
      
      if (feedsResponse.data?.data?.content) {
        setMyFeeds(feedsResponse.data.data.content)
      } else {
        // Use mock data for demo
        setMyFeeds(mockMyFeeds)
      }

      setIsLoading(false)
    }

    fetchProfileData()
  }, [user, router])

  if (!user) {
    return null
  }

  const totalHeartsSent = cheeredAnimals.reduce((sum, animal) => sum + animal.heartCount, 0)

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
                <h1 className="text-2xl font-bold text-foreground">{user.name}</h1>
                <p className="text-muted-foreground">@{user.username}</p>
                
                {/* Stats */}
                <div className="flex justify-center sm:justify-start gap-6 mt-4">
                  <div className="text-center">
                    <p className="text-2xl font-bold text-primary">{totalHeartsSent}</p>
                    <p className="text-sm text-muted-foreground">보낸 하트</p>
                  </div>
                  <div className="text-center">
                    <p className="text-2xl font-bold text-primary">{cheeredAnimals.length}</p>
                    <p className="text-sm text-muted-foreground">응원한 동물</p>
                  </div>
                  <div className="text-center">
                    <p className="text-2xl font-bold text-primary">{myFeeds.length}</p>
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
                    <p className="text-sm text-muted-foreground">회원 번호</p>
                    <p className="font-medium">#{user.id}</p>
                  </div>
                  <div className="space-y-1">
                    <p className="text-sm text-muted-foreground">가입일</p>
                    <p className="font-medium">2025년 1월 1일</p>
                  </div>
                </div>
                
                <div className="pt-4 border-t">
                  <Button variant="outline" className="rounded-xl">
                    비밀번호 변경
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
    </div>
  )
}
