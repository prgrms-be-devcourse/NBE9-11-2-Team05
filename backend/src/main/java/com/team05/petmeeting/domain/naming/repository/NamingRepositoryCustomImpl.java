package com.team05.petmeeting.domain.naming.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team05.petmeeting.domain.animal.entity.Animal;
import com.team05.petmeeting.domain.animal.entity.QAnimal;
import com.team05.petmeeting.domain.animal.errorCode.AnimalErrorCode;
import com.team05.petmeeting.domain.naming.dto.NameCandidateRes;
import com.team05.petmeeting.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.team05.petmeeting.domain.naming.entity.QAnimalNameCandidate.animalNameCandidate;
import static com.team05.petmeeting.domain.naming.entity.QNameVoteHistory.nameVoteHistory;
import static com.team05.petmeeting.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class NamingRepositoryCustomImpl implements NamingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public NameCandidateRes getCandidates(Long animalId, Long userId) {

        // 동물 정보 조회
        Animal animal = queryFactory
                .selectFrom(QAnimal.animal)
                .where(QAnimal.animal.id.eq(animalId))
                .fetchOne();// 단건 조회

        if (animal == null) {
            throw new BusinessException(AnimalErrorCode.ANIMAL_NOT_FOUND);
        }

        // 후보 리스트 조회 (Projection 사용)
        List<NameCandidateRes.CandidateDto> candidateDtoList = queryFactory
                .select(Projections.constructor(NameCandidateRes.CandidateDto.class,
                        animalNameCandidate.id,
                        animalNameCandidate.proposedName,
                        animalNameCandidate.user.nickname,
                        animalNameCandidate.voteCount,
                        // 유저가 이 후보에 투표했는지 여부를 판단하는 로직
                        checkIsVoted(animalNameCandidate.id, userId)
                ))
                .from(animalNameCandidate)
                .leftJoin(animalNameCandidate.user, user) // 제안자 닉네임을 위해 조인
                .where(animalNameCandidate.animal.id.eq(animalId)) // 특정 동물의 후보만 조회하는 조건
                .orderBy(
                        animalNameCandidate.voteCount.desc(), // 1순위: 득표수 많은 순
                        animalNameCandidate.createdAt.asc()   // 2순위: 먼저 등록된 순 (동점자 처리)
                )
                .limit(3) // 상위 3개만 조회
                .fetch();

        return new NameCandidateRes(
                animalId,
                animal.getName(),
                candidateDtoList,
                candidateDtoList.size()
        );
    }

    @Override
    public Optional<NameCandidateRes.CandidateDto> getTopQualifiedCandidate(Long animalId, int threshold) {
        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(NameCandidateRes.CandidateDto.class,
                        animalNameCandidate.id,
                        animalNameCandidate.proposedName,
                        animalNameCandidate.user.nickname,
                        animalNameCandidate.voteCount,
                        Expressions.asBoolean(false) // 관리자 조회이므로 투표 여부는 무의미하여 false 처리
                ))
                .from(animalNameCandidate)
                .where(
                        animalNameCandidate.animal.id.eq(animalId),
                        animalNameCandidate.voteCount.goe(threshold) // 1. 일정 득표수(threshold) 이상
                )
                .orderBy(
                        animalNameCandidate.voteCount.desc(), // 2. 득표수 내림차순
                        animalNameCandidate.createdAt.asc()   // 3. 동점 시 먼저 생성된 순
                )
                .limit(1) // 4. 단 1개만 추출
                .fetchOne());
    }


    // 사용자의 투표 여부를 확인하는 서브쿼리 생성 메서드
    private BooleanExpression checkIsVoted(NumberPath<Long> candidateId, Long userId) {
        if (userId == null) {
            // 항상 false 반환
            return Expressions.asBoolean(false);
        }

        // 현재 유저가 해당 후보(candidateId)에 투표한 이력이 있는지 exists 쿼리로 확인
        return JPAExpressions
                .selectOne()
                .from(nameVoteHistory)
                .where(nameVoteHistory.candidate.id.eq(candidateId)
                        .and(nameVoteHistory.user.id.eq(userId)))
                .exists();
    }


}
