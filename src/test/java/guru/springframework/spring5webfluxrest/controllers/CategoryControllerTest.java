package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class CategoryControllerTest {
    WebTestClient webTestClient;
    CategoryController categoryController;
    CategoryRepository categoryRepository;

    @Before
    public void setUp() throws Exception {
        categoryRepository = Mockito.mock(CategoryRepository.class); // Mock the CategoryRepository
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    public void listTest() throws Exception{
        // Set-up Mock for the controller
        // When CategoryRepository is invoked
        // Return a Flux (= REACTive type)
        given(categoryRepository.findAll())
                .willReturn(Flux.just(
                        Category.builder().description("Categ1").build(),
                        Category.builder().description("Categ2").build()));

        webTestClient.get().uri("/api/v1/categories/")
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    public void getByIdTest() throws Exception {
        given(categoryRepository.findById("RandomId"))
                .willReturn(Mono.just(Category.builder().description("Categ").build()));

        webTestClient.get().uri("/api/v1/categories/RandomId")
                .exchange()
                .expectBody(Category.class);
    }

    @Test
    public void createCategoryTest() throws Exception {
        given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().build()));

        Mono<Category> categoryToSaveMono = Mono.just(Category.builder().description("My description").build());

        // isCreated() corresponds to the 201 status.
        webTestClient.post()
                .uri("/api/v1/categories")
                .body(categoryToSaveMono, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void updateCategoryTest() throws Exception {
        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdateMono = Mono.just(Category.builder().description("My descri").build());

        webTestClient.put()
                .uri("/api/v1/categories/someRandomId")
                .body(categoryToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void patchCategoryTest() throws Exception {
        // Need to mock the id
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().build()));

        // Need t o mock the save operation
        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdateMono = Mono.just(Category.builder().description("My descri").build());

        webTestClient.patch()
                .uri("/api/v1/categories/someRandomId")
                .body(categoryToUpdateMono, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Verify that that save operation is called at least 1 time
        verify(categoryRepository).save(any());
    }
}