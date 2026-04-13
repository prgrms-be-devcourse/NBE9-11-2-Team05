package com.team05.demo.domain.animal.entity;

import com.team05.demo.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "animals")
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

    @Column(name = "popfile", nullable = false, length = 500)
    private String popfile; // 사진 URL

    @Column(name = "care_nm")
    private String careNm; // 보호소 이름

    @Column(name = "care_tel")
    private String careTel; // 보호소 전화번호

    @Column(name = "total_cheer_count", nullable = false)
    private Integer totalCheerCount; // 응원 수

}
