package com.mehedi.prismweather.repository;

import com.mehedi.prismweather.model.Location;
import com.mehedi.prismweather.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Page<Location> findByUser(User user, Pageable pageable);
    Optional<Location> findByIdAndUser(Long id, User user);
}
