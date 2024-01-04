package galeev.prizeservice.controller;

import galeev.prizeservice.dto.PrizeRequestDto;
import galeev.prizeservice.dto.PrizeResponseDto;
import galeev.prizeservice.mapper.PrizeMapper;
import galeev.prizeservice.service.PrizeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/v1.0/prizes")
public class PrizeController {
    private final PrizeService prizeService;
    private final PrizeMapper prizeMapper;

    @GetMapping("/{id}")
    public Mono<PrizeResponseDto> getPrizeById(@PathVariable UUID id) {
        return prizeService.findById(id)
                .flatMap(prize -> Mono.just(prizeMapper.map(prize)));
    }

    @PostMapping
    public Mono<PrizeResponseDto> createOrUpdatePrize(@RequestBody PrizeRequestDto prizeRequestDto) {
        return prizeService.createNewPrize(prizeMapper.map(prizeRequestDto))
                .flatMap(prize -> Mono.just(prizeMapper.map(prize)));
    }
}