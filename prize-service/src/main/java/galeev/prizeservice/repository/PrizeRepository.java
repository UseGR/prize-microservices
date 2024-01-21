package galeev.prizeservice.repository;

import galeev.prizeservice.entity.Prize;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
@Repository
public interface PrizeRepository extends R2dbcRepository<Prize, UUID> {
    Mono<Prize> findByFileIdNull();

    Flux<Void> deleteByFileIdNull();
}
