package ch.portami.inventorybackend.product;

import ch.portami.inventorybackend.product.dto.category.CategoryDto;
import ch.portami.inventorybackend.product.dto.category.CreateCategoryDto;
import ch.portami.inventorybackend.product.dto.category.UpdateCategoryDto;
import ch.portami.inventorybackend.product.entity.Category;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.repository.CategoryRepository;
import ch.portami.inventorybackend.product.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class CategoryControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    private static final String CATEGORIES_URL = "/api/products/categories";

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    private Category createTestCategory(String name) {
        Category category = new Category(name);
        return categoryRepository.save(category);
    }

    @Nested
    @DisplayName("POST /api/products/categories - Create Category")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category with valid data")
        void testCreateCategorySuccess() {
            CreateCategoryDto createCategoryDto = new CreateCategoryDto("Test Category");

            CategoryDto body = restTestClient.post()
                    .uri(CATEGORIES_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createCategoryDto)
                    .exchange()
                    .expectStatus().isCreated()
                    .returnResult(CategoryDto.class)
                    .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("Test Category");
            assertThat(body.id()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should return 400 when name is null")
        void testCreateCategoryMissingName() {
            CreateCategoryDto createCategoryDto = new CreateCategoryDto(null);

            restTestClient.post()
                    .uri(CATEGORIES_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(createCategoryDto)
                    .exchange()
                    .expectStatus().isBadRequest();
        }

    }

    @Nested
    @DisplayName("GET /api/products/categories/{id} - Get Category By ID")
    class GetCategoryByIdTests {

        @Test
        @DisplayName("Should retrieve category by id")
        void testGetCategoryByIdSuccess() {
            Category category = createTestCategory("Test Category");
            Long categoryId = category.getId();

            CategoryDto body = restTestClient.get()
                    .uri(CATEGORIES_URL + "/{id}", categoryId)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(CategoryDto.class)
                    .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isEqualTo(categoryId);
            assertThat(body.name()).isEqualTo("Test Category");
        }

        @Test
        @DisplayName("Should return 404 when category does not exist")
        void testGetCategoryByIdNotFound() {
            restTestClient.get()
                    .uri(CATEGORIES_URL + "/{id}", 99999L)
                    .exchange()
                    .expectStatus().isNotFound();
        }

    }

    @Nested
    @DisplayName("GET /api/products/categories - Get All Categories")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("Should return empty list when no categories exist")
        void testGetAllCategoriesEmpty() {
            List<CategoryDto> body = restTestClient.get()
                    .uri(CATEGORIES_URL)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(new ParameterizedTypeReference<List<CategoryDto>>() {})
                    .getResponseBody();

            assertThat(body).isEmpty();
        }

        @Test
        @DisplayName("Should return list of all categories")
        void testGetAllCategoriesSuccess() {
            createTestCategory("Category 1");
            createTestCategory("Category 2");
            createTestCategory("Category 3");

            List<CategoryDto> body = restTestClient.get()
                    .uri(CATEGORIES_URL)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(new ParameterizedTypeReference<List<CategoryDto>>() {})
                    .getResponseBody();

            assertThat(body).hasSize(3);
            assertThat(body).extracting(CategoryDto::name).contains("Category 1", "Category 2", "Category 3");
        }

    }

    @Nested
    @DisplayName("PATCH /api/products/categories/{id} - Update Category")
    class UpdateCategoryTests {

        private Long categoryId;

        @BeforeEach
        void setup() {
            Category category = createTestCategory("Original Name");
            categoryId = category.getId();
        }

        @Test
        @DisplayName("Should update category name")
        void testUpdateCategoryNameSuccess() {
            UpdateCategoryDto updateCategoryDto = new UpdateCategoryDto("Updated Name");

            CategoryDto body = restTestClient.patch()
                    .uri(CATEGORIES_URL + "/{id}", categoryId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updateCategoryDto)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(CategoryDto.class)
                    .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("Updated Name");
            assertThat(body.id()).isEqualTo(categoryId);
        }

        @Test
        @DisplayName("Should update with null name (no change)")
        void testUpdateCategoryWithNullName() {
            UpdateCategoryDto updateCategoryDto = new UpdateCategoryDto(null);

            CategoryDto body = restTestClient.patch()
                    .uri(CATEGORIES_URL + "/{id}", categoryId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updateCategoryDto)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(CategoryDto.class)
                    .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("Original Name");
        }

        @Test
        @DisplayName("Should return 404 when category does not exist")
        void testUpdateCategoryNotFound() {
            UpdateCategoryDto updateCategoryDto = new UpdateCategoryDto("Updated Name");

            restTestClient.patch()
                    .uri(CATEGORIES_URL + "/{id}", 99999L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(updateCategoryDto)
                    .exchange()
                    .expectStatus().isNotFound();
        }

    }

    @Nested
    @DisplayName("DELETE /api/products/categories/{id} - Delete Category")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete category successfully")
        void testDeleteCategorySuccess() {
            Category category = createTestCategory("Category to Delete");
            Long categoryId = category.getId();

            restTestClient.delete()
                    .uri(CATEGORIES_URL + "/{id}", categoryId)
                    .exchange()
                    .expectStatus().isNoContent();

            assertThat(categoryRepository.existsById(categoryId)).isFalse();
        }

        @Test
        @DisplayName("Should do nothing when deleting non-existent category")
        void testDeleteCategoryNotFound() {
            restTestClient.delete()
                    .uri(CATEGORIES_URL + "/{id}", 99999L)
                    .exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        @DisplayName("Should block deletion of category if products still reference it")
        void testDeleteCategoryWithAssociatedProducts() {
            // Create a category with associated products
            Category category = createTestCategory("Category with Products");
            Product product = new Product(category, "Product 1");
            productRepository.save(product);

            Long categoryId = category.getId();
            Long productId = product.getId();

            assertThat(productRepository.existsById(productId)).isTrue();

            restTestClient.delete()
                    .uri(CATEGORIES_URL + "/{id}", categoryId)
                    .exchange();

            assertThat(categoryRepository.existsById(categoryId)).isTrue();
            assertThat(productRepository.existsById(productId)).isTrue();

        }

    }

}


