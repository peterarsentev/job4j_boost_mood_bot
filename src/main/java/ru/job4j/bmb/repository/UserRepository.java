package ru.job4j.bmb.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    private final Map<Long, User> mem = new HashMap<>();

    public void add(User user) {
        mem.put(user.getClientId(), user);
    }

    public List<User> findAll() {
        return new ArrayList<>(mem.values());
    }
}
