package com.videostore.repository.search;

import com.videostore.domain.Director;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Director entity.
 */
public interface DirectorSearchRepository extends ElasticsearchRepository<Director, Long> {
}
