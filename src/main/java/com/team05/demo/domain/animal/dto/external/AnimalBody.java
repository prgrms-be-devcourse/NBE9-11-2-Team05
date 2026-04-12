package com.team05.demo.domain.animal.dto.external;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AnimalBody {
    private AnimalItems items;
    private Integer numOfRows;
    private Integer pageNo;
    private Integer totalCount;
}
