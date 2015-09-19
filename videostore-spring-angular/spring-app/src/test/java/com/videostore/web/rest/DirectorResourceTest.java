package com.videostore.web.rest;

import com.videostore.Application;
import com.videostore.domain.Director;
import com.videostore.repository.DirectorRepository;
import com.videostore.repository.search.DirectorSearchRepository;

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
 * Test class for the DirectorResource REST controller.
 *
 * @see DirectorResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class DirectorResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final DateTime DEFAULT_BIRTHDATE = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_BIRTHDATE = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_BIRTHDATE_STR = dateTimeFormatter.print(DEFAULT_BIRTHDATE);

    @Inject
    private DirectorRepository directorRepository;

    @Inject
    private DirectorSearchRepository directorSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restDirectorMockMvc;

    private Director director;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DirectorResource directorResource = new DirectorResource();
        ReflectionTestUtils.setField(directorResource, "directorRepository", directorRepository);
        ReflectionTestUtils.setField(directorResource, "directorSearchRepository", directorSearchRepository);
        this.restDirectorMockMvc = MockMvcBuilders.standaloneSetup(directorResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        director = new Director();
        director.setName(DEFAULT_NAME);
        director.setBirthdate(DEFAULT_BIRTHDATE);
    }

    @Test
    @Transactional
    public void createDirector() throws Exception {
        int databaseSizeBeforeCreate = directorRepository.findAll().size();

        // Create the Director

        restDirectorMockMvc.perform(post("/api/directors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(director)))
                .andExpect(status().isCreated());

        // Validate the Director in the database
        List<Director> directors = directorRepository.findAll();
        assertThat(directors).hasSize(databaseSizeBeforeCreate + 1);
        Director testDirector = directors.get(directors.size() - 1);
        assertThat(testDirector.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDirector.getBirthdate().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_BIRTHDATE);
    }

    @Test
    @Transactional
    public void getAllDirectors() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        // Get all the directors
        restDirectorMockMvc.perform(get("/api/directors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(director.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].birthdate").value(hasItem(DEFAULT_BIRTHDATE_STR)));
    }

    @Test
    @Transactional
    public void getDirector() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

        // Get the director
        restDirectorMockMvc.perform(get("/api/directors/{id}", director.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(director.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.birthdate").value(DEFAULT_BIRTHDATE_STR));
    }

    @Test
    @Transactional
    public void getNonExistingDirector() throws Exception {
        // Get the director
        restDirectorMockMvc.perform(get("/api/directors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDirector() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

		int databaseSizeBeforeUpdate = directorRepository.findAll().size();

        // Update the director
        director.setName(UPDATED_NAME);
        director.setBirthdate(UPDATED_BIRTHDATE);
        

        restDirectorMockMvc.perform(put("/api/directors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(director)))
                .andExpect(status().isOk());

        // Validate the Director in the database
        List<Director> directors = directorRepository.findAll();
        assertThat(directors).hasSize(databaseSizeBeforeUpdate);
        Director testDirector = directors.get(directors.size() - 1);
        assertThat(testDirector.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDirector.getBirthdate().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_BIRTHDATE);
    }

    @Test
    @Transactional
    public void deleteDirector() throws Exception {
        // Initialize the database
        directorRepository.saveAndFlush(director);

		int databaseSizeBeforeDelete = directorRepository.findAll().size();

        // Get the director
        restDirectorMockMvc.perform(delete("/api/directors/{id}", director.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Director> directors = directorRepository.findAll();
        assertThat(directors).hasSize(databaseSizeBeforeDelete - 1);
    }
}
