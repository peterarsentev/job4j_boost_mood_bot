package ru.job4j.bmb.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.job4j.bmb.model.MoodLog;
import ru.job4j.bmb.model.User;

import java.util.List;
import java.util.stream.Stream;

public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findByUserId(Long userId);
    Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT u FROM User u WHERE NOT EXISTS (" +
            "SELECT ml FROM MoodLog ml WHERE ml.user = u AND ml.createdAt BETWEEN :startOfDay AND :endOfDay)")
    List<User> findUsersWhoDidNotVoteToday(@Param("startOfDay") long startOfDay,
                                           @Param("endOfDay") long endOfDay);

    @Query("SELECT ml FROM MoodLog ml WHERE ml.user.id = :userId AND ml.createdAt >= :weekStart")
    List<MoodLog> findMoodLogsForWeek(@Param("userId") Long userId, @Param("weekStart") long weekStart);

    @Query("SELECT ml FROM MoodLog ml WHERE ml.user.id = :userId AND ml.createdAt >= :monthStart")
    List<MoodLog> findMoodLogsForMonth(@Param("userId") Long userId, @Param("monthStart") long monthStart);
}
