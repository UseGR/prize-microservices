package galeev.prizeservice.service;

import galeev.prizeservice.entity.Prize;
import galeev.prizeservice.repository.PrizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrizeService {
    private final PrizeRepository prizeRepository;

    public Mono<Prize> createNewPrize(Prize prize) {
        return prizeRepository.save(prize);
    }

    public Mono<Prize> findById(UUID id) {
        return prizeRepository.findById(id);
    }
}
