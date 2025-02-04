package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CategoryController {
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/api/v1/categories")
    Flux<Category> list() {
        // Returns a REACTive type
        return categoryRepository.findAll();
    }

    @GetMapping("/api/v1/categories/{id}")
    Mono<Category> getById(@PathVariable String id) {
        // Returns a REACTive type
        return categoryRepository.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/v1/categories")
    // Both Mono and Flux implement "Publisher"
    Mono<Void> create(@RequestBody Publisher<Category> categoryStream) {
        // then() --> because we return a void
        return categoryRepository.saveAll(categoryStream).then();
    }

    @PutMapping("/api/v1/categories/{id}")
    Mono<Category> update(@PathVariable String id,
                          @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }

    @PatchMapping("/api/v1/categories/{id}")
    Mono<Category> patch(@PathVariable String id,
                          @RequestBody Category category) {
        Category foundCategory = categoryRepository.findById(id).block();

        // Update the description if it is different
        if (foundCategory.getDescription() != category.getDescription()) {
            foundCategory.setDescription(category.getDescription());

            return categoryRepository.save(foundCategory);
        }

        // Otherwise, just return the category
        return Mono.just(foundCategory);
    }
}
