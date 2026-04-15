"use client"

import Link from "next/link"
import { Search, Bell, Heart, LogOut, User } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { useAuth } from "@/lib/auth-context"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"

interface HeaderProps {
  dailyHeartsRemaining?: number
  maxDailyHearts?: number
}

export function Header({ dailyHeartsRemaining = 5, maxDailyHearts = 5 }: HeaderProps) {
  const { user, logout } = useAuth()

  return (
    <header className="sticky top-0 z-50 bg-card/95 backdrop-blur-lg border-b border-border">
      <div className="max-w-6xl mx-auto px-6 py-4">
        <div className="flex items-center justify-between gap-8">
          {/* Logo */}
          <Link href="/" className="flex items-center gap-2">
            <div className="flex items-center justify-center w-10 h-10 rounded-xl bg-primary text-primary-foreground">
              <Heart className="w-5 h-5 fill-current" />
            </div>
            <span className="text-xl font-bold text-foreground">펫미팅</span>
          </Link>

          {/* Navigation */}
          <nav className="hidden md:flex items-center gap-1">
            <Link href="/">
              <Button variant="ghost" className="text-foreground font-medium">
                홈
              </Button>
            </Link>
            <Link href="/community">
              <Button variant="ghost" className="text-muted-foreground font-medium">
                커뮤니티
              </Button>
            </Link>
          </nav>

          {/* Search & Actions */}
          <div className="flex items-center gap-3">
            <div className="relative hidden sm:block">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="검색어를 입력하세요"
                className="w-64 pl-10 bg-secondary/50 border-0 rounded-xl"
              />
            </div>
            
            {user ? (
              <>
                {/* Daily Hearts Indicator */}
                <div className="hidden sm:flex items-center gap-1.5 px-3 py-1.5 bg-primary/10 rounded-xl">
                  <div className="flex items-center gap-0.5">
                    {Array.from({ length: maxDailyHearts }).map((_, index) => (
                      <Heart 
                        key={index}
                        className={`w-4 h-4 transition-all ${
                          index < dailyHeartsRemaining 
                            ? "fill-primary text-primary" 
                            : "text-primary/30"
                        }`} 
                      />
                    ))}
                  </div>
                  <span className="text-xs font-medium text-primary">
                    {dailyHeartsRemaining}/{maxDailyHearts}
                  </span>
                </div>
                
                <Button variant="ghost" size="icon" className="text-muted-foreground">
                  <Bell className="w-5 h-5" />
                </Button>
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="ghost" size="icon" className="text-muted-foreground">
                      <User className="w-5 h-5" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end" className="w-48">
                    <div className="px-2 py-1.5">
                      <p className="text-sm font-medium text-foreground">{user.nickname || user.name}</p>
                      <p className="text-xs text-muted-foreground">@{user.username}</p>
                    </div>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem asChild>
                      <Link href="/profile" className="cursor-pointer">
                        <User className="w-4 h-4 mr-2" />
                        프로필
                      </Link>
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem 
                      className="text-destructive cursor-pointer"
                      onClick={logout}
                    >
                      <LogOut className="w-4 h-4 mr-2" />
                      로그아웃
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </>
            ) : (
              <div className="flex items-center gap-2">
                <Link href="/login">
                  <Button variant="ghost" className="text-muted-foreground font-medium">
                    로그인
                  </Button>
                </Link>
                <Link href="/register">
                  <Button className="hidden sm:flex bg-primary text-primary-foreground hover:bg-primary/90 rounded-xl">
                    회원가입
                  </Button>
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  )
}
