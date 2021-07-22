package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;

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
        BDDMockito.given(vendorRepository.findAll())
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
        BDDMockito.given(vendorRepository.findById("RandomId"))
                .willReturn(Mono.just(Vendor.builder().lastName("Dupond").firstName("Marc").build()));

        webTestClient.get().uri("/api/v1/vendors/RandomId")
                .exchange()
                .expectBody(Vendor.class);
    }
}