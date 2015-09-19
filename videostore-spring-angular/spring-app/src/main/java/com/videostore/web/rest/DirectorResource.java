package com.videostore.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.videostore.domain.Director;
import com.videostore.repository.DirectorRepository;
import com.videostore.repository.search.DirectorSearchRepository;
import com.videostore.web.rest.util.HeaderUtil;
import com.videostore.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Director.
 */
@RestController
@RequestMapping("/api")
public class DirectorResource {

    private final Logger log = LoggerFactory.getLogger(DirectorResource.class);

    @Inject
    private DirectorRepository directorRepository;

    @Inject
    private DirectorSearchRepository directorSearchRepository;

    /**
     * POST  /directors -> Create a new director.
     */
    @RequestMapping(value = "/directors",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Director> create(@RequestBody Director director) throws URISyntaxException {
        log.debug("REST request to save Director : {}", director);
        if (director.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new director cannot already have an ID").body(null);
        }
        Director result = directorRepository.save(director);
        directorSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/directors/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("director", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /directors -> Updates an existing director.
     */
    @RequestMapping(value = "/directors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Director> update(@RequestBody Director director) throws URISyntaxException {
        log.debug("REST request to update Director : {}", director);
        if (director.getId() == null) {
            return create(director);
        }
        Director result = directorRepository.save(director);
        directorSearchRepository.save(director);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("director", director.getId().toString()))
                .body(result);
    }

    /**
     * GET  /directors -> get all the directors.
     */
    @RequestMapping(value = "/directors",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Director>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Director> page = directorRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/directors", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /directors/:id -> get the "id" director.
     */
    @RequestMapping(value = "/directors/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Director> get(@PathVariable Long id) {
        log.debug("REST request to get Director : {}", id);
        return Optional.ofNullable(directorRepository.findOne(id))
            .map(director -> new ResponseEntity<>(
                director,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /directors/:id -> delete the "id" director.
     */
    @RequestMapping(value = "/directors/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Director : {}", id);
        directorRepository.delete(id);
        directorSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("director", id.toString())).build();
    }

    /**
     * SEARCH  /_search/directors/:query -> search for the director corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/directors/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Director> search(@PathVariable String query) {
        return StreamSupport
            .stream(directorSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
