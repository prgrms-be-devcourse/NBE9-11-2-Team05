"use client"

import { useState, useEffect, use } from "react"
import Image from "next/image"
import Link from "next/link"
import { ArrowLeft, Heart, Phone, MapPin, Calendar, Info, User, Send, MessageCircle } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Header } from "@/components/header"
import { useAuth } from "@/lib/auth-context"
import { cn } from "@/lib/utils"
import { API_ENDPOINTS, apiRequest, type Animal, type Comment } from "@/lib/api"

// Mock data for demo - replace with API call in production
const mockAnimalData: Animal = {
  animalId: 1,
  noticeNo: "서울-송파-2024-00123",
  kind: "개",
  breed: "믹스견",
  age: "2024(년생)",
  gender: "M",
  neutered: "Y",
  weight: "5.3",
  color: "갈색",
  specialMark: "왼쪽 귀 끝 상처 있음",
  imageUrl: "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=800&h=800&fit=crop",
  shelterName: "송파구 유기동물보호소",
  shelterTel: "02-000-0000",
  shelterAddr: "서울특별시 송파구 위례성대로 12길 100",
  chargeNm: "김담당",
  region: "서울특별시 송파구",
  noticeStartDate: "2024-08-01",
  noticeEndDate: "2024-08-15",
  processState: "보호중",
  heartCount: 23,
  temperature: 46.0,
}

const mockComments: Comment[] = [
  { id: 1, author: "동물사랑", authorId: 1, text: "너무 귀여워요! 좋은 가족 만나길 바라요", createdAt: "2024-08-10T10:30:00" },
  { id: 2, author: "멍멍이팬", authorId: 2, text: "응원합니다! 빨리 좋은 분께 입양되길", createdAt: "2024-08-10T11:15:00" },
  { id: 3, author: "착한사람", authorId: 3, text: "건강하게 잘 지내길 바랍니다", createdAt: "2024-08-11T09:00:00" },
]

export default function AnimalDetailPage({ params }: { params: Promise<{ id: string }> }) {
  const resolvedParams = use(params)
  const { user } = useAuth()
  const [animal, setAnimal] = useState<Animal | null>(null)
  const [comments, setComments] = useState<Comment[]>([])
  const [newComment, setNewComment] = useState("")
  const [totalHearts, setTotalHearts] = useState(0)
  const [currentTemp, setCurrentTemp] = useState(0)
  const [remainingToday, setRemainingToday] = useState<number | null>(null)
  const [isAnimating, setIsAnimating] = useState(false)
  const [isLoading, setIsLoading] = useState(true)

  const extractRemainingToday = (
    payload: { [key: string]: any } | string | number | null
  ): number | null => {
    if (typeof payload === "number") return payload
    if (typeof payload === "string") {
      const parsed = Number(payload)
      return Number.isFinite(parsed) ? parsed : null
    }
    if (!payload || typeof payload !== "object") return null
    if (typeof payload.remainingToday === "number") return payload.remainingToday
    if (typeof payload.remainingToday === "string") {
      const parsed = Number(payload.remainingToday)
      if (Number.isFinite(parsed)) return parsed
    }
    if (typeof payload.remaining === "number") return payload.remaining
    if (typeof payload.remaining === "string") {
      const parsed = Number(payload.remaining)
      if (Number.isFinite(parsed)) return parsed
    }
    if (typeof payload.remainingCheers === "number") return payload.remainingCheers
    if (typeof payload.remainingCheers === "string") {
      const parsed = Number(payload.remainingCheers)
      if (Number.isFinite(parsed)) return parsed
    }
    if (payload.data && typeof payload.data === "object") {
      return extractRemainingToday(payload.data as { [key: string]: any })
    }
    if (payload.result && typeof payload.result === "object") {
      return extractRemainingToday(payload.result as { [key: string]: any })
    }
    return null
  }

  const fetchRemainingToday = async () => {
    if (!user) {
      setRemainingToday(5)
      return
    }

    const { data } = await apiRequest<{ [key: string]: any }>(API_ENDPOINTS.cheersToday)
    const parsedRemaining = extractRemainingToday(data)
    if (parsedRemaining === null) {
      return
    }

    setRemainingToday(Math.max(0, Math.min(5, parsedRemaining)))
  }

  useEffect(() => {
    // In production, fetch from API:
    // const fetchAnimal = async () => {
    //   const { data } = await apiRequest<Animal>(API_ENDPOINTS.animalDetail(Number(resolvedParams.id)))
    //   if (data) setAnimal(data)
    // }
    
    // Mock data for demo
    setAnimal(mockAnimalData)
    setComments(mockComments)
    setTotalHearts(mockAnimalData.heartCount)
    setCurrentTemp(mockAnimalData.temperature)
    fetchRemainingToday()
    setIsLoading(false)
  }, [resolvedParams.id, user])

  const handleCheer = async () => {
    if (!user) {
      alert("로그인이 필요합니다")
      return
    }

    if (!animal) return
    if (remainingToday !== null && remainingToday <= 0) {
      alert("오늘 사용할 수 있는 하트를 모두 사용했습니다. 내일 다시 응원해주세요!")
      return
    }

    const { error } = await apiRequest(API_ENDPOINTS.addCheer(animal.animalId), {
      method: "POST",
    })
    if (error) {
      console.warn("addCheer failed, applying local fallback:", error)
    }

    setTotalHearts(prev => prev + 1)
    setCurrentTemp(prev => Math.min(prev + 0.5, 100))
    setRemainingToday(prev => (typeof prev === "number" ? Math.max(0, prev - 1) : prev))
    setIsAnimating(true)
    setTimeout(() => setIsAnimating(false), 300)
    fetchRemainingToday()
  }

  const handleCommentSubmit = () => {
    if (!user) {
      alert("로그인이 필요합니다")
      return
    }
    if (!newComment.trim()) return

    const newCommentObj: Comment = {
      id: Date.now(),
      author: user.name,
      authorId: user.id,
      text: newComment,
      createdAt: new Date().toISOString(),
    }
    setComments(prev => [...prev, newCommentObj])
    setNewComment("")
    // In production, call API:
    // await apiRequest(API_ENDPOINTS.comments(animal.animalId), { method: "POST", body: JSON.stringify({ text: newComment }) })
  }

  if (isLoading || !animal) {
    return (
      <div className="min-h-screen bg-background">
        <Header />
        <div className="flex items-center justify-center py-20">
          <div className="text-muted-foreground">로딩중...</div>
        </div>
      </div>
    )
  }

  const isProtecting = animal.processState === "보호중"
  const genderLabel = animal.gender === "M" ? "수컷" : animal.gender === "F" ? "암컷" : "미상"
  const neuteredLabel = animal.neutered === "Y" ? "중성화 O" : animal.neutered === "N" ? "중성화 X" : "미상"

  return (
    <div className="min-h-screen bg-background">
      <Header dailyHeartsRemaining={remainingToday ?? 5} maxDailyHearts={5} />
      
      <main className="max-w-4xl mx-auto px-4 py-6">
        {/* Back Button */}
        <Link href="/" className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground transition-colors mb-6">
          <ArrowLeft className="w-4 h-4" />
          <span className="text-sm font-medium">목록으로</span>
        </Link>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Image Section */}
          <div className="space-y-4">
            <div className="relative aspect-square rounded-2xl overflow-hidden bg-secondary">
              <Image
                src={animal.imageUrl}
                alt={`${animal.breed} - ${animal.noticeNo}`}
                fill
                className="object-cover"
                priority
              />
              {!isProtecting && (
                <div className="absolute inset-0 flex items-center justify-center bg-foreground/20">
                  <div className="bg-success/90 text-success-foreground px-8 py-4 rounded-xl rotate-[-12deg] shadow-lg">
                    <span className="text-xl font-bold tracking-wide">입양완료</span>
                  </div>
                </div>
              )}
            </div>

            {/* Status Badge */}
            <div className="flex items-center gap-2">
              {isProtecting ? (
                <span className="inline-flex items-center gap-1.5 px-4 py-2 rounded-full bg-primary/10 text-primary text-sm font-semibold">
                  보호중
                </span>
              ) : (
                <span className="inline-flex items-center gap-1.5 px-4 py-2 rounded-full bg-success/10 text-success text-sm font-semibold">
                  입양 완료
                </span>
              )}
              <span className="text-sm text-muted-foreground">
                공고번호: {animal.noticeNo}
              </span>
            </div>
          </div>

          {/* Info Section */}
          <div className="space-y-6">
            {/* Basic Info */}
            <div>
              <h1 className="text-2xl font-bold text-foreground mb-2">
                {animal.breed}
              </h1>
              <p className="text-muted-foreground">
                {animal.kind} / {animal.age} / {genderLabel}
              </p>
            </div>

            {/* Details Grid */}
            <Card className="border-0 bg-secondary/30">
              <CardContent className="p-4 grid grid-cols-2 gap-4">
                <div className="space-y-1">
                  <p className="text-xs text-muted-foreground">체중</p>
                  <p className="text-sm font-medium text-foreground">{animal.weight}kg</p>
                </div>
                <div className="space-y-1">
                  <p className="text-xs text-muted-foreground">색상</p>
                  <p className="text-sm font-medium text-foreground">{animal.color}</p>
                </div>
                <div className="space-y-1">
                  <p className="text-xs text-muted-foreground">중성화</p>
                  <p className="text-sm font-medium text-foreground">{neuteredLabel}</p>
                </div>
                <div className="space-y-1">
                  <p className="text-xs text-muted-foreground">지역</p>
                  <p className="text-sm font-medium text-foreground">{animal.region}</p>
                </div>
              </CardContent>
            </Card>

            {/* Special Mark */}
            {animal.specialMark && (
              <div className="flex items-start gap-3 p-4 rounded-xl bg-accent/30 border border-accent/50">
                <Info className="w-5 h-5 text-accent-foreground shrink-0 mt-0.5" />
                <div>
                  <p className="text-sm font-medium text-foreground">특이사항</p>
                  <p className="text-sm text-muted-foreground mt-1">{animal.specialMark}</p>
                </div>
              </div>
            )}

            {/* Notice Period */}
            <div className="flex items-center gap-3 text-sm">
              <Calendar className="w-4 h-4 text-muted-foreground" />
              <span className="text-muted-foreground">
                공고기간: {animal.noticeStartDate} ~ {animal.noticeEndDate}
              </span>
            </div>

            {/* Heart/Cheer Section */}
            {isProtecting && (
              <Card className="border-0 bg-primary/5">
                <CardContent className="p-5 space-y-4">
                  {/* Cheer Button - Full Width */}
                  <button
                    onClick={handleCheer}
                    disabled={remainingToday !== null && remainingToday <= 0}
                    className="flex items-center justify-center gap-2 w-full py-3 px-4 rounded-xl transition-all bg-primary/10 hover:bg-primary/20 text-primary disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:bg-primary/10"
                  >
                    <Heart className={cn(
                      "w-5 h-5 transition-transform",
                      isAnimating && "scale-125 fill-primary"
                    )} />
                    <span className="font-medium">응원하기</span>
                  </button>

                  {/* Temperature Bar */}
                  <div className="space-y-2">
                    <div className="flex items-center justify-between text-sm">
                      <span className="font-medium text-foreground">응원 온도</span>
                      <span className="text-primary font-semibold">{currentTemp.toFixed(1)}C / 100C</span>
                    </div>
                    <div className="h-3 bg-secondary rounded-full overflow-hidden">
                      <div
                        className="h-full bg-gradient-to-r from-primary to-accent rounded-full transition-all duration-500"
                        style={{ width: `${Math.min(currentTemp, 100)}%` }}
                      />
                    </div>
                    <p className="text-sm text-muted-foreground text-center">
                      {totalHearts}명이 응원했어요
                    </p>
                    {remainingToday !== null && (
                      <p className="text-xs text-muted-foreground text-center">
                        오늘 남은 하트 {remainingToday}개
                      </p>
                    )}
                  </div>
                </CardContent>
              </Card>
            )}
          </div>
        </div>

        {/* Shelter Info */}
        <Card className="mt-8 border-0 bg-card shadow-sm">
          <CardContent className="p-6">
            <h3 className="font-semibold text-foreground mb-4">보호소 정보</h3>
            <div className="space-y-3">
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-lg bg-secondary flex items-center justify-center">
                  <Info className="w-4 h-4 text-muted-foreground" />
                </div>
                <div>
                  <p className="text-sm font-medium text-foreground">{animal.shelterName}</p>
                  <p className="text-xs text-muted-foreground">담당: {animal.chargeNm}</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-lg bg-secondary flex items-center justify-center">
                  <Phone className="w-4 h-4 text-muted-foreground" />
                </div>
                <a href={`tel:${animal.shelterTel}`} className="text-sm text-primary hover:underline">
                  {animal.shelterTel}
                </a>
              </div>
              <div className="flex items-start gap-3">
                <div className="w-8 h-8 rounded-lg bg-secondary flex items-center justify-center shrink-0">
                  <MapPin className="w-4 h-4 text-muted-foreground" />
                </div>
                <p className="text-sm text-muted-foreground">{animal.shelterAddr}</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Comments Section */}
        <Card className="mt-6 border-0 bg-card shadow-sm">
          <CardContent className="p-6">
            <div className="flex items-center gap-2 mb-4">
              <MessageCircle className="w-5 h-5 text-muted-foreground" />
              <h3 className="font-semibold text-foreground">응원 댓글</h3>
              <span className="text-sm text-muted-foreground">({comments.length})</span>
            </div>

            {/* Comment List */}
            <div className="space-y-4 mb-4">
              {comments.length === 0 ? (
                <p className="text-sm text-muted-foreground text-center py-8">
                  첫 번째 응원 댓글을 남겨주세요!
                </p>
              ) : (
                comments.map((comment) => (
                  <div key={comment.id} className="flex gap-3">
                    <div className="w-8 h-8 rounded-full bg-secondary flex items-center justify-center shrink-0">
                      <User className="w-4 h-4 text-muted-foreground" />
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center gap-2">
                        <span className="text-sm font-semibold text-foreground">{comment.author}</span>
                        <span className="text-xs text-muted-foreground">
                          {new Date(comment.createdAt).toLocaleDateString("ko-KR")}
                        </span>
                      </div>
                      <p className="text-sm text-muted-foreground mt-1">{comment.text}</p>
                    </div>
                  </div>
                ))
              )}
            </div>

            {/* Comment Input */}
            <div className="flex gap-2">
              <Input
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder={user ? "응원 댓글을 남겨주세요..." : "로그인 후 댓글을 남길 수 있습니다"}
                className="flex-1 rounded-xl bg-secondary/50 border-0 h-11"
                disabled={!user}
                onKeyDown={(e) => e.key === "Enter" && handleCommentSubmit()}
              />
              <Button
                onClick={handleCommentSubmit}
                disabled={!user || !newComment.trim()}
                className="rounded-xl bg-primary text-primary-foreground hover:bg-primary/90"
              >
                <Send className="w-4 h-4" />
              </Button>
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  )
}
