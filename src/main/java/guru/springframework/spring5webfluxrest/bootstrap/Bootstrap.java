package guru.springframework.spring5webfluxrest.bootstrap;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.CategoryRepository;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {
    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;

    public Bootstrap(CategoryRepository categoryRepository, VendorRepository vendorRepository) {
        this.categoryRepository = categoryRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // The "block" tells to subscribe and invoke the categoryRepository
        if (categoryRepository.count().block() == 0) {
            // Load Data
            System.out.println("---------- Loading Data on Bootsrap ----------");

            loadCategories();
            loadVendors();
        }
    }

    private void loadVendors() {
        vendorRepository.save(Vendor.builder().firstName("Robin").lastName("Ghyselinck").build()).block();
        vendorRepository.save(Vendor.builder().firstName("Caro").lastName("Hazard").build()).block();
        vendorRepository.save(Vendor.builder().firstName("Pierre").lastName("Dupont").build()).block();
        vendorRepository.save(Vendor.builder().firstName("Jean").lastName("LaFable").build()).block();
        vendorRepository.save(Vendor.builder().firstName("Yves").lastName("LeGrand").build()).block();

        System.out.println("Loaded Vendors with " + vendorRepository.count().block() + " records.");
    }

    private void loadCategories() {
        categoryRepository.save(Category.builder().description("Fruits").build()).block();
        categoryRepository.save(Category.builder().description("Vegetables").build()).block();
        categoryRepository.save(Category.builder().description("Nuts").build()).block();
        categoryRepository.save(Category.builder().description("Meats").build()).block();
        categoryRepository.save(Category.builder().description("Eggs").build()).block();

        System.out.println("Loaded Categories with " + categoryRepository.count().block() + " records.");
    }

}
