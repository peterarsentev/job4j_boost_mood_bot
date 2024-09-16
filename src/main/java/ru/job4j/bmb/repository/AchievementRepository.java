package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.bmb.model.Achievement;

public interface AchievementRepository extends CrudRepository<Achievement, Long> {
}
