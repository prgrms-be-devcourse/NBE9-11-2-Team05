package com.team05.demo.domain.animal.entity;

import com.team05.demo.domain.animal.dto.external.AnimalItem;
import com.team05.demo.domain.comment.entity.AnimalComment;
import com.team05.demo.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "animals")
@NoArgsConstructor
public class Animal extends BaseEntity {

    @Column(name = "desertion_no", nullable = false, length = 50, unique = true)
    private String desertionNo; // 유기번호

    @Column(name = "process_state", nullable = false, length = 30)
    private String processState; // 상태 (보호중, 입양가능, 입양대기, 파양, 종결 등)

    @Column(name = "notice_no", nullable = false, length = 50)
    private String noticeNo; // 공고번호

    @Column(name = "notice_edt", nullable = false)
    private LocalDate noticeEdt; // 공고 종료일

    @Column(name = "up_kind_nm", nullable = false, length = 30)
    private String upKindNm; // 종 (개, 고양이 등)

    @Column(name = "kind_full_name", nullable = false, length = 100)
    private String kindFullNm; // 품종 (예: 믹스견, 시바견 등)

    @Column(name = "color_cd", nullable = false, length = 100)
    private String colorCd; // 색상

    @Column(name = "age", nullable = false)
    private String age; // 나이

    @Column(name = "weight", nullable = false, length = 30)
    private String weight; // 몸무게 (예: 5kg, 10kg 등)

    @Column(name = "sex_cd", nullable = false, length = 10)
    private String sexCd; // 성별

    @Column(name = "popfile1", nullable = false, length = 500)
    private String popfile1; // 사진 URL

    @Column(name = "popfile2", nullable = true, length = 500)
    private String popfile2; // 사진 URL

    @Column(name = "care_nm")
    private String careNm; // 보호소 이름

    @Column(name = "care_tel")
    private String careTel; // 보호소 전화번호

    @Column(name = "total_cheer_count", nullable = false)
    private Integer totalCheerCount; // 응원 수

    @OneToMany (mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnimalComment> comments = new ArrayList<>();

    private Animal(
            String desertionNo,
            String processState,
            String noticeNo,
            LocalDate noticeEdt,
            String upKindNm,
            String kindFullName,
            String colorCd,
            String age,
            String weight,
            String sexCd,
            String popfile1,
            String popfile2,
            String careNm,
            String careTel,
            Integer totalCheerCount
    ) {
        this.desertionNo = desertionNo;
        this.processState = processState;
        this.noticeNo = noticeNo;
        this.noticeEdt = noticeEdt;
        this.upKindNm = upKindNm;
        this.kindFullNm = kindFullName;
        this.colorCd = colorCd;
        this.age = age;
        this.weight = weight;
        this.sexCd = sexCd;
        this.popfile1 = popfile1;
        this.popfile2 = popfile2;
        this.careNm = careNm;
        this.careTel = careTel;
        this.totalCheerCount = totalCheerCount;
    }

    public static Animal from(AnimalItem item) {
        return new Animal(
                item.getDesertionNo(),
                item.getProcessState(),
                item.getNoticeNo(),
                parseNoticeEdt(item.getNoticeEdt()),
                item.getUpKindNm(),
                item.getKindFullNm(),
                item.getColorCd(),
                item.getAge(),
                item.getWeight(),
                item.getSexCd(),
                item.getPopfile1(),
                item.getPopfile2(),
                item.getCareNm(),
                item.getCareTel(),
                0
        );
    }

    private static LocalDate parseNoticeEdt(String noticeEdt) {
        return LocalDate.parse(noticeEdt, DateTimeFormatter.BASIC_ISO_DATE);
    }


}
