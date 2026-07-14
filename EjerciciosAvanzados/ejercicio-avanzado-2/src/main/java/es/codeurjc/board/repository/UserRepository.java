package es.codeurjc.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.board.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
}