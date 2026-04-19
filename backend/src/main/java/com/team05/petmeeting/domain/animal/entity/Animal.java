package com.team05.petmeeting.domain.animal.entity;

import com.team05.petmeeting.domain.animal.dto.external.AnimalItem;
import com.team05.petmeeting.domain.comment.entity.AnimalComment;
import com.team05.petmeeting.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Table(name = "animals")
@NoArgsConstructor
public class Animal extends BaseEntity {

    @Column(name = "desertion_no", nullable = false, length = 50, unique = true)
    private String desertionNo; // 유기번호

    @Column(name = "process_state", length = 30)
    private String processState; // 상태 (보호중, 입양가능, 입양대기, 파양, 종결 등)

    @Column(name = "notice_no", length = 50)
    private String noticeNo; // 공고번호

    @Column(name = "notice_edt")
    private LocalDate noticeEdt; // 공고 종료일

    @Column(name ="happen_place", length = 100)
    private String happenPlace; // 발견 장소

    @Column(name = "up_kind_nm", length = 30)
    private String upKindNm; // 종 (개, 고양이 등)

    @Column(name = "kind_full_name", length = 100)
    private String kindFullNm; // 품종 (예: 믹스견, 시바견 등)

    @Column(name = "color_cd", length = 100)
    private String colorCd; // 색상

    @Column(name = "age")
    private String age; // 나이

    @Column(name = "weight", length = 30)
    private String weight; // 몸무게 (예: 5kg, 10kg 등)

    @Column(name = "sex_cd", length = 10)
    private String sexCd; // 성별

    @Column(name = "popfile1", nullable = true, length = 500)
    private String popfile1; // 사진 URL

    @Column(name = "popfile2", nullable = true, length = 500)
    private String popfile2; // 사진 URL

    @Column(name = "special_mark", nullable = true, length = 500)
    private String specialMark; // 사진 URL

    @Column(name = "care_ower_nm", nullable = true, length = 50)
    private String careOwerNm; // 보호자 이름

    @Column(name = "care_nm")
    private String careNm; // 보호소 이름

    @Column(name = "care_addr", length = 255)
    private String careAddr; // 보호소 주소

    @Column(name = "care_tel")
    private String careTel; // 보호소 전화번호

    @Column(name = "total_cheer_count", nullable = false)
    private Integer totalCheerCount; // 응원 수

    @Column(name = "api_updated_at")
    private LocalDateTime apiUpdatedAt;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnimalComment> comments = new ArrayList<>();

    public void updateProcessState(AnimalItem item) {
        this.processState = item.getProcessState();
        this.apiUpdatedAt = parseUpdTm(item.getUpdTm());
    }

    public void updateFrom(AnimalItem item) {
        this.processState = item.getProcessState();
        this.noticeNo = item.getNoticeNo();
        this.noticeEdt = parseNoticeEdt(item.getNoticeEdt());
        this.happenPlace = item.getHappenPlace();
        this.upKindNm = item.getUpKindNm();
        this.kindFullNm = item.getKindFullNm();
        this.colorCd = item.getColorCd();
        this.age = item.getAge();
        this.weight = item.getWeight();
        this.sexCd = item.getSexCd();
        this.popfile1 = item.getPopfile1();
        this.popfile2 = item.getPopfile2();
        this.specialMark = item.getSpecialMark();
        this.careOwerNm = item.getCareOwnerNm();
        this.careNm = item.getCareNm();
        this.careAddr = item.getCareAddr();
        this.careTel = item.getCareTel();
        this.apiUpdatedAt = parseUpdTm(item.getUpdTm());
    }

    public boolean needsProcessStateUpdate(AnimalItem item) {
        LocalDateTime incomingUpdatedAt = parseUpdTm(item.getUpdTm()); // API에서 제공하는 업데이트 시각을 파싱한다.

        if (this.apiUpdatedAt != null && incomingUpdatedAt != null) {
            return incomingUpdatedAt.isAfter(this.apiUpdatedAt);
            //apiUpdateAt은 데이터베이스에 이미 지정된 값
        }

        return !Objects.equals(this.processState, item.getProcessState());
    }

    private static LocalDateTime parseUpdTm(String updTm) {
        if (updTm == null || updTm.isBlank()) {
            return null;
        }

        return LocalDateTime.parse(
                updTm,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
        );
    }

    private Animal(
            String desertionNo,
            String processState,
            String noticeNo,
            String happenPlace,
            LocalDate noticeEdt,
            String upKindNm,
            String kindFullName,
            String colorCd,
            String age,
            String weight,
            String sexCd,
            String popfile1,
            String popfile2,
            String specialMark,
            String careOwerNm,
            String careNm,
            String careAddr,
            String careTel,
            Integer totalCheerCount
    ) {
        this.desertionNo = desertionNo;
        this.processState = processState;
        this.noticeNo = noticeNo;
        this.happenPlace = happenPlace;
        this.noticeEdt = noticeEdt;
        this.upKindNm = upKindNm;
        this.kindFullNm = kindFullName;
        this.colorCd = colorCd;
        this.age = age;
        this.weight = weight;
        this.sexCd = sexCd;
        this.popfile1 = popfile1;
        this.popfile2 = popfile2;
        this.specialMark = specialMark;
        this.careOwerNm = careOwerNm;
        this.careNm = careNm;
        this.careAddr = careAddr;
        this.careTel = careTel;
        this.totalCheerCount = totalCheerCount;
    }


    public static Animal from(AnimalItem item) {
        Animal animal = new Animal(
                item.getDesertionNo(),
                item.getProcessState(),
                item.getNoticeNo(),
                item.getHappenPlace(),
                parseNoticeEdt(item.getNoticeEdt()),
                item.getUpKindNm(),
                item.getKindFullNm(),
                item.getColorCd(),
                item.getAge(),
                item.getWeight(),
                item.getSexCd(),
                item.getPopfile1(),
                item.getPopfile2(),
                item.getSpecialMark(),
                item.getCareOwnerNm(),
                item.getCareNm(),
                item.getCareAddr(),
                item.getCareTel(),
                0
        );

        animal.apiUpdatedAt = parseUpdTm(item.getUpdTm());
        return animal;
    }

    // API에서 제공하는 noticeEdt는 "yyyyMMdd" 형식이므로, 이를 LocalDate로 파싱하는 헬퍼 메서드
    private static LocalDate parseNoticeEdt(String noticeEdt) {
        if (noticeEdt == null || noticeEdt.isBlank()) {
            return null;
        }

        return LocalDate.parse(noticeEdt, DateTimeFormatter.BASIC_ISO_DATE);
    }

    public double getTemperature() {
        double cheerGoal = 50.0; // todo 통합 후 수정
        if (this.totalCheerCount == null) {
            return 0;
        }
        return this.totalCheerCount / cheerGoal * 100;
    }
}
