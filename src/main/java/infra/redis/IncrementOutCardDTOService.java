package infra.redis;

import infra.redis.dto.CardsIncrementDTO;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import rest.dtos.card.OutCardDTO;

import java.util.List;

@ApplicationScoped
public class IncrementOutCardDTOService {
    private final ReactiveKeyCommands<String> keyCommands;
    private final ValueCommands<String, CardsIncrementDTO> countCommands;

    public IncrementOutCardDTOService(RedisDataSource ds, ReactiveRedisDataSource reactive) {
        countCommands = ds.value(CardsIncrementDTO.class);
        keyCommands = reactive.key();
    }

    public CardsIncrementDTO get(String key) {
        return countCommands.get(key);
    }

    public void set(String key, CardsIncrementDTO value) {
        countCommands.set(key, value);
    }

    public void increment(String key, long incrementBy) {
        countCommands.incrby(key, incrementBy);
    }

    public Uni<Void> del(String key) {
        return keyCommands.del(key)
                .replaceWithVoid();
    }

    public Uni<List<String>> keys() {
        return keyCommands.keys("*");
    }
}