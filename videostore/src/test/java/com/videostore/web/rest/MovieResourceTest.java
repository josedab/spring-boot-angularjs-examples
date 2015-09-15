package com.videostore.web.rest;

import com.videostore.Application;
import com.videostore.domain.Movie;
import com.videostore.repository.MovieRepository;
import com.videostore.repository.search.MovieSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the MovieResource REST controller.
 *
 * @see MovieResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class MovieResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final DateTime DEFAULT_RELEASED_DATE = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_RELEASED_DATE = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_RELEASED_DATE_STR = dateTimeFormatter.print(DEFAULT_RELEASED_DATE);
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    @Inject
    private MovieRepository movieRepository;

    @Inject
    private MovieSearchRepository movieSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restMovieMockMvc;

    private Movie movie;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MovieResource movieResource = new MovieResource();
        ReflectionTestUtils.setField(movieResource, "movieRepository", movieRepository);
        ReflectionTestUtils.setField(movieResource, "movieSearchRepository", movieSearchRepository);
        this.restMovieMockMvc = MockMvcBuilders.standaloneSetup(movieResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        movie = new Movie();
        movie.setName(DEFAULT_NAME);
        movie.setReleasedDate(DEFAULT_RELEASED_DATE);
        movie.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createMovie() throws Exception {
        int databaseSizeBeforeCreate = movieRepository.findAll().size();

        // Create the Movie

        restMovieMockMvc.perform(post("/api/movies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(movie)))
                .andExpect(status().isCreated());

        // Validate the Movie in the database
        List<Movie> movies = movieRepository.findAll();
        assertThat(movies).hasSize(databaseSizeBeforeCreate + 1);
        Movie testMovie = movies.get(movies.size() - 1);
        assertThat(testMovie.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMovie.getReleasedDate().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_RELEASED_DATE);
        assertThat(testMovie.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllMovies() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get all the movies
        restMovieMockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(movie.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].releasedDate").value(hasItem(DEFAULT_RELEASED_DATE_STR)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getMovie() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

        // Get the movie
        restMovieMockMvc.perform(get("/api/movies/{id}", movie.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(movie.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.releasedDate").value(DEFAULT_RELEASED_DATE_STR))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMovie() throws Exception {
        // Get the movie
        restMovieMockMvc.perform(get("/api/movies/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMovie() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

		int databaseSizeBeforeUpdate = movieRepository.findAll().size();

        // Update the movie
        movie.setName(UPDATED_NAME);
        movie.setReleasedDate(UPDATED_RELEASED_DATE);
        movie.setDescription(UPDATED_DESCRIPTION);
        

        restMovieMockMvc.perform(put("/api/movies")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(movie)))
                .andExpect(status().isOk());

        // Validate the Movie in the database
        List<Movie> movies = movieRepository.findAll();
        assertThat(movies).hasSize(databaseSizeBeforeUpdate);
        Movie testMovie = movies.get(movies.size() - 1);
        assertThat(testMovie.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMovie.getReleasedDate().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_RELEASED_DATE);
        assertThat(testMovie.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteMovie() throws Exception {
        // Initialize the database
        movieRepository.saveAndFlush(movie);

		int databaseSizeBeforeDelete = movieRepository.findAll().size();

        // Get the movie
        restMovieMockMvc.perform(delete("/api/movies/{id}", movie.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Movie> movies = movieRepository.findAll();
        assertThat(movies).hasSize(databaseSizeBeforeDelete - 1);
    }
}
