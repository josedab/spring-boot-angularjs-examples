package com.videostore.repository.search;

import com.videostore.domain.Movie;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Movie entity.
 */
public interface MovieSearchRepository extends ElasticsearchRepository<Movie, Long> {
}
