package com.team05.petmeeting.domain.shelter.service;

import com.team05.petmeeting.domain.shelter.dto.ShelterCommand;
import com.team05.petmeeting.domain.shelter.entity.Shelter;
import com.team05.petmeeting.domain.shelter.repository.ShelterRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ShelterService {

    private final ShelterRepository shelterRepository;

    /*
    * 외부 API 데이터 예시
    * "careRegNo" : "343447202600001",
    * "careNm" : "음성군 동물보호센터",
    * "careTel" : "043-877-3081",
    * "careAddr" : "충청북도 음성군 삼성면 대금로 715-5",
    * "careOwnerNm" : "음성군수",
    * "orgNm" : "충청북도 음성군",
    * "updTm" : "2026-04-15 14:19:49.0"
    * updTm 비교해서 갱신 필요하면 보호소 정보도 갱신
    * cmd 1개 -> DB 조회 여러번
    */
    public Shelter createOrUpdateShelter(ShelterCommand cmd) {
        return shelterRepository.findById(cmd.careRegNo())
                .map(existing -> {
                    if (existing.getUpdTm().isBefore(cmd.updTm())) {
                        existing.updateFrom(cmd);
                    }
                    return existing;
                } )
            .orElseGet(() -> shelterRepository.save(
                    new Shelter(
                            cmd.careRegNo(),
                            cmd.careNm(),
                            cmd.careTel(),
                            cmd.careAddr(),
                            cmd.careOwnerNm(),
                            cmd.orgNm(),
                            cmd.updTm()
                            )
            ));
    }

    /*
    * n개 cmd -> DB 조회 한번
    */
    public void createOrUpdateShelters(List<ShelterCommand> cmds) {
        Set<String> ids = cmds.stream()
                .map(ShelterCommand::careRegNo)
                .collect(Collectors.toSet());

        // 모든 careRegNo 한번에 다 조회해서 map에 저장
        Map<String, Shelter> map = shelterRepository.findByCareRegNoIn(ids)
                .stream()
                .collect(Collectors.toMap(Shelter::getCareRegNo, s -> s));

        for (ShelterCommand cmd : cmds) {
            Shelter existing = map.get(cmd.careRegNo());  // map에 저장해둔 Shelter

            if (existing != null) {
                if (existing.getUpdTm().isBefore(cmd.updTm())) {
                    existing.updateFrom(cmd);
                }
            } else {
                Shelter newShelter = new Shelter(
                        cmd.careRegNo(),
                        cmd.careNm(),
                        cmd.careTel(),
                        cmd.careAddr(),
                        cmd.careOwnerNm(),
                        cmd.orgNm(),
                        cmd.updTm()
                );
                shelterRepository.save(newShelter);
                map.put(cmd.careRegNo(), newShelter);
            }
        }
    }

}
