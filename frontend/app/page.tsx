"use client"

import { useEffect, useState } from "react"
import { FeedCard } from "@/components/feed-card"
import { Header } from "@/components/header"
import { RankingBanner } from "@/components/ranking-banner"
import { Pagination } from "@/components/pagination"
import { Filter, SlidersHorizontal } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useAuth } from "@/lib/auth-context"
import { API_ENDPOINTS, apiRequest } from "@/lib/api"

const MAX_DAILY_HEARTS = 5

// Top 3 Ranking Data
const top3Animals = [
  {
    id: "top1",
    animalId: 101,
    rank: 1,
    name: "구름이",
    imageUrl: "https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=600&h=600&fit=crop",
    cheerTemperature: 97,
    maxCheerTemperature: 100,
  },
  {
    id: "top2",
    animalId: 102,
    rank: 2,
    name: "초코",
    imageUrl: "https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?w=600&h=600&fit=crop",
    cheerTemperature: 92,
    maxCheerTemperature: 100,
  },
  {
    id: "top3",
    animalId: 103,
    rank: 3,
    name: "나비",
    imageUrl: "https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=600&h=600&fit=crop",
    cheerTemperature: 88,
    maxCheerTemperature: 100,
  },
]

// Simulated paginated feed data (10 items per page)
const allFeedData = [
  {
    id: "1",
    animalId: 1,
    processState: "보호중" as const,
    imageUrl: "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=600&h=600&fit=crop",
    animalInfo: "믹스견 · 2023년생 · 수원시동물보호센터",
    cheerTemperature: 85,
    maxCheerTemperature: 100,
    totalHeartCount: 33,
    comments: [
      { id: "c1", author: "동물사랑", text: "너무 귀여워요! 좋은 가족 만나길 바라요" },
      { id: "c2", author: "멍멍이팬", text: "응원합니다!" },
      { id: "c3", author: "착한사람", text: "건강하게 잘 지내길!" },
    ],
  },
  {
    id: "2",
    animalId: 2,
    processState: "종료(입양)" as const,
    imageUrl: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=600&h=600&fit=crop",
    animalInfo: "한국고양이 · 2024년생",
    adopterDiary:
      "우리 집에 온 지 한 달이 되었어요! 처음엔 숨기만 하더니 이제는 제 무릎 위에서 골골송을 불러요. 매일매일 행복합니다.",
    comments: [
      { id: "c4", author: "냥이맘", text: "축하드려요! 행복하세요" },
      { id: "c5", author: "고양이천사", text: "좋은 분께 입양되어서 다행이에요!" },
    ],
  },
  {
    id: "3",
    animalId: 3,
    processState: "보호중" as const,
    imageUrl: "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?w=600&h=600&fit=crop",
    animalInfo: "말티즈 · 2022년생 · 성남시동물보호센터",
    cheerTemperature: 72,
    maxCheerTemperature: 100,
    totalHeartCount: 27,
    comments: [
      { id: "c6", author: "강아지조아", text: "빨리 가족을 찾길!" },
    ],
  },
  {
    id: "4",
    animalId: 4,
    processState: "보호중" as const,
    imageUrl: "https://images.unsplash.com/photo-1592194996308-7b43878e84a6?w=600&h=600&fit=crop",
    animalInfo: "코리안숏헤어 · 2024년생 · 용인시동물보호센터",
    cheerTemperature: 45,
    maxCheerTemperature: 100,
    totalHeartCount: 15,
    comments: [],
  },
  {
    id: "5",
    animalId: 5,
    processState: "종료(입양)" as const,
    imageUrl: "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=600&h=600&fit=crop",
    animalInfo: "리트리버믹스 · 2021년생",
    adopterDiary: "드디어 우리 가족이 되었어요! 산책을 너무 좋아해서 매일 아침 공원에 가요.",
    comments: [
      { id: "c7", author: "산책러버", text: "행복한 모습 보기 좋아요!" },
    ],
  },
  {
    id: "6",
    animalId: 6,
    processState: "보호중" as const,
    imageUrl: "https://images.unsplash.com/photo-1561037404-61cd46aa615b?w=600&h=600&fit=crop",
    animalInfo: "포메라니안 · 2023년생 · 서울시동물보호센터",
    cheerTemperature: 63,
    maxCheerTemperature: 100,
    totalHeartCount: 23,
    comments: [
      { id: "c8", author: "포메사랑", text: "너무 사랑스러워요!" },
      { id: "c9", author: "강아지맘", text: "응원해요!" },
    ],
  },
  {
    id: "7",
    animalId: 7,
    processState: "보호중" as const,
    imageUrl: "https://images.unsplash.com/photo-1574158622682-e40e69881006?w=600&h=600&fit=crop",
    animalInfo: "러시안블루믹스 · 2023년생 · 인천시동물보호센터",
    cheerTemperature: 38,
    maxCheerTemperature: 100,
    totalHeartCount: 11,
    comments: [],
  },
  {
    id: "8",
    animalId: 8,
    processState: "보호중" as const,
    imageUrl: "https://images.unsplash.com/photo-1477884213360-7e9d7dcc1e48?w=600&h=600&fit=crop",
    animalInfo: "진돗개믹스 · 2022년생 · 부산시동물보호센터",
    cheerTemperature: 51,
    maxCheerTemperature: 100,
    totalHeartCount: 17,
    comments: [
      { id: "c10", author: "부산사람", text: "우리 진돗개 화이팅!" },
    ],
  },
  {
    id: "9",
    animalId: 9,
    processState: "종료(입양)" as const,
    imageUrl: "https://images.unsplash.com/photo-1495360010541-f48722b34f7d?w=600&h=600&fit=crop",
    animalInfo: "터키시앙고라 · 2023년생",
    adopterDiary: "처음 만났을 때부터 인연인 것 같았어요. 지금은 집안의 작은 왕자님이 되었답니다!",
    comments: [
      { id: "c11", author: "앙고라팬", text: "정말 예쁘네요!" },
    ],
  },
  {
    id: "10",
    animalId: 10,
    processState: "보호중" as const,
    imageUrl: "https://images.unsplash.com/photo-1587564403621-70d61a73c5b9?w=600&h=600&fit=crop",
    animalInfo: "비글 · 2024년생 · 대전시동물보호센터",
    cheerTemperature: 29,
    maxCheerTemperature: 100,
    totalHeartCount: 8,
    comments: [],
  },
]

// Create additional pages of data by reusing items with different IDs
const generatePageData = (page: number, itemsPerPage: number = 10) => {
  return allFeedData.map((item, idx) => ({
    ...item,
    id: `page${page}-${item.id}`,
    cheerTemperature: item.cheerTemperature
      ? Math.max(10, item.cheerTemperature - page * 5 + idx)
      : undefined,
  }))
}

const ITEMS_PER_PAGE = 10
const TOTAL_PAGES = 10

export default function SocialFeedPage() {
  const { user } = useAuth()
  const [currentPage, setCurrentPage] = useState(1)
  const [dailyHeartsRemaining, setDailyHeartsRemaining] = useState(MAX_DAILY_HEARTS)
  const currentFeedData = generatePageData(currentPage, ITEMS_PER_PAGE)

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

  const fetchDailyHeartsRemaining = async () => {
    if (!user) {
      setDailyHeartsRemaining(MAX_DAILY_HEARTS)
      return
    }

    const { data } = await apiRequest<{ [key: string]: any }>(API_ENDPOINTS.cheersToday)
    const remainingToday = extractRemainingToday(data)
    if (remainingToday === null) {
      return
    }

    setDailyHeartsRemaining(Math.max(0, Math.min(MAX_DAILY_HEARTS, remainingToday)))
  }

  const handleCheerSuccess = () => {
    setDailyHeartsRemaining(prev => Math.max(0, prev - 1))
    fetchDailyHeartsRemaining()
  }

  useEffect(() => {
    fetchDailyHeartsRemaining()
  }, [user])

  const handlePageChange = (page: number) => {
    setCurrentPage(page)
    window.scrollTo({ top: 600, behavior: "smooth" })
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <Header dailyHeartsRemaining={dailyHeartsRemaining} maxDailyHearts={MAX_DAILY_HEARTS} />

      {/* Top 3 Ranking Banner */}
      <RankingBanner animals={top3Animals} />

      {/* Feed Section */}
      <main className="max-w-6xl mx-auto px-6 py-8">
        {/* Section Header */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
          <div>
            <h2 className="text-xl font-bold text-foreground">
              전체 보호동물
            </h2>
            <p className="text-sm text-muted-foreground mt-1">
              총 {TOTAL_PAGES * ITEMS_PER_PAGE}마리의 친구들이 가족을 기다리고 있어요
            </p>
          </div>
          <div className="flex items-center gap-2">
            <Button variant="outline" size="sm" className="gap-2 rounded-xl">
              <Filter className="w-4 h-4" />
              필터
            </Button>
            <Button variant="outline" size="sm" className="gap-2 rounded-xl">
              <SlidersHorizontal className="w-4 h-4" />
              정렬
            </Button>
          </div>
        </div>

        {/* Feed Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {currentFeedData.map((item) => (
            <FeedCard
              key={item.id}
              id={item.id}
              animalId={item.animalId}
              processState={item.processState}
              imageUrl={item.imageUrl}
              animalInfo={item.animalInfo}
              cheerTemperature={item.cheerTemperature}
              maxCheerTemperature={item.maxCheerTemperature}
              totalHeartCount={item.totalHeartCount}
              adopterDiary={item.adopterDiary}
              comments={item.comments}
              dailyHeartsRemaining={dailyHeartsRemaining}
              onCheerSuccess={handleCheerSuccess}
            />
          ))}
        </div>

        {/* Pagination */}
        <div className="py-10">
          <Pagination
            currentPage={currentPage}
            totalPages={TOTAL_PAGES}
            onPageChange={handlePageChange}
          />
          <p className="text-center text-sm text-muted-foreground mt-4">
            {currentPage} / {TOTAL_PAGES} 페이지
          </p>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-card border-t border-border">
        <div className="max-w-6xl mx-auto px-6 py-8">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4">
            <p className="text-sm text-muted-foreground">
              2024 유기동물 응원 피드. 모든 생명은 소중합니다.
            </p>
            <div className="flex items-center gap-6 text-sm text-muted-foreground">
              <a href="#" className="hover:text-foreground transition-colors">이용약관</a>
              <a href="#" className="hover:text-foreground transition-colors">개인정보처리방침</a>
              <a href="#" className="hover:text-foreground transition-colors">문의하기</a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  )
}
