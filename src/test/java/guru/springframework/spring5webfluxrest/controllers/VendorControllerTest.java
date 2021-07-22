package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class VendorControllerTest {
    WebTestClient webTestClient;
    VendorRepository vendorRepository;
    VendorController vendorController;

    @Before
    public void setUp() throws Exception {
        vendorRepository = Mockito.mock(VendorRepository.class); // Mock a new VendorRepository instance
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void listTest() throws Exception {
        given(vendorRepository.findAll())
                .willReturn(Flux.just(
                        Vendor.builder().lastName("Dupond").firstName("Marc").build(),
                        Vendor.builder().lastName("Lefevre").firstName("Jean").build()));

        webTestClient.get().uri("/api/v1/vendors/")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getByIdTest() throws Exception {
        given(vendorRepository.findById("RandomId"))
                .willReturn(Mono.just(Vendor.builder().lastName("Dupond").firstName("Marc").build()));

        webTestClient.get().uri("/api/v1/vendors/RandomId")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    public void createVendorTest() throws Exception {
        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().lastName("Dupont").firstName("Laurent").build());

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void updateVendorTest() throws Exception {
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToUpdateMono = Mono.just(Vendor.builder().lastName("Dupont").firstName("Laurent").build());

        webTestClient.put()
                .uri("/api/v1/vendors/myRandomId")
                .body(vendorToUpdateMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void patchVendorWithChangesTest() {
        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().build()));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToPatchMono = Mono.just(Vendor.builder().lastName("Dupont").firstName("Laurent").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/myRandomId")
                .body(vendorToPatchMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Verify that that save operation is called at least 1 time
        verify(vendorRepository).save(any());
    }

    @Test
    public void patchVendorWithNoChangesTest() {
        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().build()));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToPatchMono = Mono.just(Vendor.builder().build());

        webTestClient.patch()
                .uri("/api/v1/vendors/myRandomId")
                .body(vendorToPatchMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        // Verify that that save operation is never called
        verify(vendorRepository, never()).save(any());
    }
}