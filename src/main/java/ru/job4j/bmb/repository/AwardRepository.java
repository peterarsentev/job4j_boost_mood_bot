package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.bmb.model.Award;

import java.util.Optional;

public interface AwardRepository extends CrudRepository<Award, Long> {
    Optional<Award> findFirstByDaysLessThanEqualOrderByDaysDesc(long days);
}
