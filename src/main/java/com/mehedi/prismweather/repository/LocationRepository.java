package com.mehedi.prismweather.repository;

import com.mehedi.prismweather.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

}
