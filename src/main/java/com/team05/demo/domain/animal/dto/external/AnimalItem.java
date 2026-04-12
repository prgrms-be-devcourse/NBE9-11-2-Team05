package com.team05.demo.domain.animal.dto.external;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnimalItem {
    private String desertionNo;
    private String processState;
    private String noticeNo;
    private String noticeEdt;

    private String upKindNm;
    private String kindFullNm;
    private String colorCd;
    private String age;
    private String weight;
    private String sexCd;

    private String popfile1;
    private String popfile2;

    private String careNm;
    private String careTel;
}
