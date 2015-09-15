package com.videostore.repository;

import com.videostore.domain.Director;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Director entity.
 */
public interface DirectorRepository extends JpaRepository<Director,Long> {

}
