package com.videostore.repository;

import com.videostore.domain.Movie;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Movie entity.
 */
public interface MovieRepository extends JpaRepository<Movie,Long> {

}
