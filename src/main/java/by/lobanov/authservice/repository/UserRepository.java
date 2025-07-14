package by.lobanov.authservice.repository;

import by.lobanov.authservice.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmailAndConfirmed(String username, boolean confirmed);
    User save (User user);
}
