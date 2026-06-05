package ch.portami.inventorybackend.product;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.product.dto.category.CategoryDto;
import ch.portami.inventorybackend.product.dto.category.CategoryFieldDto;
import ch.portami.inventorybackend.product.dto.category.CreateCategoryDto;
import ch.portami.inventorybackend.product.dto.category.UpdateCategoryDto;
import ch.portami.inventorybackend.product.entity.Category;
import ch.portami.inventorybackend.product.entity.CategoryField;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.repository.CategoryRepository;
import ch.portami.inventorybackend.product.repository.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryControllerIntegrationTest extends BaseIntegrationTest {

    private static final String CATEGORIES_URL = "/api/products/categories";

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    private Category createTestCategory(String name) {
        return categoryRepository.save(new Category(name));
    }

    private Category createTestCategoryWithFields(String name, String... fieldNames) {
        Category category = new Category(name);
        for (String fieldName : fieldNames) {
            category.addField(new CategoryField(category, fieldName));
        }
        return categoryRepository.save(category);
    }

    @Nested
    @DisplayName("POST /api/products/categories - Create Category")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category without fields")
        void testCreateCategorySuccess() {
            CreateCategoryDto dto = new CreateCategoryDto("Test Category", null);

            CategoryDto body = restTestClient.post()
                                             .uri(CATEGORIES_URL)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .body(dto)
                                             .exchange()
                                             .expectStatus()
                                             .isCreated()
                                             .returnResult(CategoryDto.class)
                                             .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("Test Category");
            assertThat(body.id()).isGreaterThan(0);
            assertThat(body.fields()).isEmpty();
        }

        @Test
        @DisplayName("Should create category with initial fields")
        void testCreateCategoryWithFields() {
            CreateCategoryDto dto = new CreateCategoryDto("Taschen", List.of("Farbe", "Grösse"));

            CategoryDto body = restTestClient.post()
                                             .uri(CATEGORIES_URL)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .body(dto)
                                             .exchange()
                                             .expectStatus()
                                             .isCreated()
                                             .returnResult(CategoryDto.class)
                                             .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.fields()).hasSize(2);
            assertThat(body.fields()).extracting(CategoryFieldDto::name)
                                    .containsExactly("Farbe", "Grösse");
            assertThat(body.fields()).extracting(CategoryFieldDto::id)
                                    .allMatch(id -> id > 0);
        }

        @Test
        @DisplayName("Should return 400 when name is null")
        void testCreateCategoryMissingName() {
            CreateCategoryDto dto = new CreateCategoryDto(null, null);

            restTestClient.post()
                          .uri(CATEGORIES_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(dto)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when a field name is blank")
        void testCreateCategoryWithBlankFieldName() {
            CreateCategoryDto dto = new CreateCategoryDto("Valid Name", List.of("Farbe", "   "));

            restTestClient.post()
                          .uri(CATEGORIES_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(dto)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

    }

    @Nested
    @DisplayName("GET /api/products/categories/{id} - Get Category By ID")
    class GetCategoryByIdTests {

        @Test
        @DisplayName("Should return category without fields")
        void testGetCategoryByIdNoFields() {
            Category category = createTestCategory("Test Category");

            CategoryDto body = restTestClient.get()
                                             .uri(CATEGORIES_URL + "/{id}", category.getId())
                                             .exchange()
                                             .expectStatus()
                                             .isOk()
                                             .returnResult(CategoryDto.class)
                                             .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isEqualTo(category.getId());
            assertThat(body.name()).isEqualTo("Test Category");
            assertThat(body.fields()).isEmpty();
        }

        @Test
        @DisplayName("Should return category with its fields")
        void testGetCategoryByIdWithFields() {
            Category category = createTestCategoryWithFields("Taschen", "Farbe", "Grösse");

            CategoryDto body = restTestClient.get()
                                             .uri(CATEGORIES_URL + "/{id}", category.getId())
                                             .exchange()
                                             .expectStatus()
                                             .isOk()
                                             .returnResult(CategoryDto.class)
                                             .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.fields()).hasSize(2);
            assertThat(body.fields()).extracting(CategoryFieldDto::name)
                                    .containsExactlyInAnyOrder("Farbe", "Grösse");
        }

        @Test
        @DisplayName("Should return 404 when category does not exist")
        void testGetCategoryByIdNotFound() {
            restTestClient.get()
                          .uri(CATEGORIES_URL + "/{id}", 99999L)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
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
                                                   .expectStatus()
                                                   .isOk()
                                                   .returnResult(new ParameterizedTypeReference<List<CategoryDto>>() {
                                                   })
                                                   .getResponseBody();

            assertThat(body).isEmpty();
        }

        @Test
        @DisplayName("Should return all categories with their fields")
        void testGetAllCategoriesSuccess() {
            createTestCategoryWithFields("Category A", "Farbe");
            createTestCategory("Category B");

            List<CategoryDto> body = restTestClient.get()
                                                   .uri(CATEGORIES_URL)
                                                   .exchange()
                                                   .expectStatus()
                                                   .isOk()
                                                   .returnResult(new ParameterizedTypeReference<List<CategoryDto>>() {
                                                   })
                                                   .getResponseBody();

            assertThat(body).hasSize(2);
            CategoryDto catA = body.stream().filter(c -> c.name().equals("Category A")).findFirst().orElseThrow();
            CategoryDto catB = body.stream().filter(c -> c.name().equals("Category B")).findFirst().orElseThrow();
            assertThat(catA.fields()).extracting(CategoryFieldDto::name).containsExactly("Farbe");
            assertThat(catB.fields()).isEmpty();
        }

    }

    @Nested
    @DisplayName("PATCH /api/products/categories/{id} - Update Category")
    class UpdateCategoryTests {

        private Long categoryId;

        @BeforeEach
        void setup() {
            categoryId = createTestCategory("Original Name").getId();
        }

        @Test
        @DisplayName("Should update category name, leaving fields unchanged (null fieldNames)")
        void testUpdateCategoryNameOnly() {
            UpdateCategoryDto dto = new UpdateCategoryDto("Updated Name", null);

            CategoryDto body = restTestClient.patch()
                                             .uri(CATEGORIES_URL + "/{id}", categoryId)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .body(dto)
                                             .exchange()
                                             .expectStatus()
                                             .isOk()
                                             .returnResult(CategoryDto.class)
                                             .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("Updated Name");
            assertThat(body.id()).isEqualTo(categoryId);
        }

        @Test
        @DisplayName("Should leave name unchanged when null")
        void testUpdateCategoryWithNullName() {
            UpdateCategoryDto dto = new UpdateCategoryDto(null, null);

            CategoryDto body = restTestClient.patch()
                                             .uri(CATEGORIES_URL + "/{id}", categoryId)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .body(dto)
                                             .exchange()
                                             .expectStatus()
                                             .isOk()
                                             .returnResult(CategoryDto.class)
                                             .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("Original Name");
        }

        @Test
        @DisplayName("Should add fields when category had none")
        void testUpdateAddsFields() {
            UpdateCategoryDto dto = new UpdateCategoryDto(null, List.of("Farbe", "Grösse"));

            CategoryDto body = restTestClient.patch()
                                             .uri(CATEGORIES_URL + "/{id}", categoryId)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .body(dto)
                                             .exchange()
                                             .expectStatus()
                                             .isOk()
                                             .returnResult(CategoryDto.class)
                                             .getResponseBody();

            assertThat(body.fields()).hasSize(2);
            assertThat(body.fields()).extracting(CategoryFieldDto::name)
                                    .containsExactly("Farbe", "Grösse");
        }

        @Test
        @DisplayName("Should remove fields not present in the new list")
        void testUpdateRemovesFields() {
            categoryId = createTestCategoryWithFields("Cat", "Farbe", "Grösse", "Material").getId();

            UpdateCategoryDto dto = new UpdateCategoryDto(null, List.of("Farbe"));

            CategoryDto body = restTestClient.patch()
                                             .uri(CATEGORIES_URL + "/{id}", categoryId)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .body(dto)
                                             .exchange()
                                             .expectStatus()
                                             .isOk()
                                             .returnResult(CategoryDto.class)
                                             .getResponseBody();

            assertThat(body.fields()).hasSize(1);
            assertThat(body.fields().get(0).name()).isEqualTo("Farbe");
        }

        @Test
        @DisplayName("Should retain field ID when name is unchanged (smart sync)")
        void testUpdateRetainsFieldId() {
            categoryId = createTestCategoryWithFields("Cat", "Farbe").getId();

            // First GET to capture the existing field ID
            CategoryDto before = restTestClient.get()
                                               .uri(CATEGORIES_URL + "/{id}", categoryId)
                                               .exchange()
                                               .expectStatus()
                                               .isOk()
                                               .returnResult(CategoryDto.class)
                                               .getResponseBody();
            assertThat(before).isNotNull();
            Long existingFieldId = before.fields().get(0).id();

            // PATCH: keep "Farbe", add "Grösse"
            UpdateCategoryDto dto = new UpdateCategoryDto(null, List.of("Farbe", "Grösse"));
            CategoryDto after = restTestClient.patch()
                                              .uri(CATEGORIES_URL + "/{id}", categoryId)
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .body(dto)
                                              .exchange()
                                              .expectStatus()
                                              .isOk()
                                              .returnResult(CategoryDto.class)
                                              .getResponseBody();

            assertThat(after).isNotNull();
            assertThat(after.fields()).hasSize(2);
            CategoryFieldDto retainedFarbe = after.fields().stream()
                                                   .filter(f -> f.name().equals("Farbe"))
                                                   .findFirst()
                                                   .orElseThrow();
            assertThat(retainedFarbe.id()).as("Field ID should be stable when name is unchanged")
                                         .isEqualTo(existingFieldId);
        }

        @Test
        @DisplayName("Should remove all fields when fieldNames is empty list")
        void testUpdateClearsAllFields() {
            categoryId = createTestCategoryWithFields("Cat", "Farbe", "Grösse").getId();

            UpdateCategoryDto dto = new UpdateCategoryDto(null, List.of());

            CategoryDto body = restTestClient.patch()
                                             .uri(CATEGORIES_URL + "/{id}", categoryId)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .body(dto)
                                             .exchange()
                                             .expectStatus()
                                             .isOk()
                                             .returnResult(CategoryDto.class)
                                             .getResponseBody();

            assertThat(body.fields()).isEmpty();
        }

        @Test
        @DisplayName("Should return 400 when a field name is blank")
        void testUpdateWithBlankFieldName() {
            UpdateCategoryDto dto = new UpdateCategoryDto(null, List.of("Farbe", ""));

            restTestClient.patch()
                          .uri(CATEGORIES_URL + "/{id}", categoryId)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(dto)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should return 404 when category does not exist")
        void testUpdateCategoryNotFound() {
            UpdateCategoryDto dto = new UpdateCategoryDto("Updated Name", null);

            restTestClient.patch()
                          .uri(CATEGORIES_URL + "/{id}", 99999L)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(dto)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

    }

    @Nested
    @DisplayName("DELETE /api/products/categories/{id} - Delete Category")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete category successfully")
        void testDeleteCategorySuccess() {
            Category category = createTestCategory("Category to Delete");

            restTestClient.delete()
                          .uri(CATEGORIES_URL + "/{id}", category.getId())
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            assertThat(categoryRepository.existsById(category.getId())).isFalse();
        }

        @Test
        @DisplayName("Should cascade-delete fields when category is deleted")
        void testDeleteCategoryCascadesFields() {
            Category category = createTestCategoryWithFields("Cat with Fields", "Farbe", "Grösse");

            restTestClient.delete()
                          .uri(CATEGORIES_URL + "/{id}", category.getId())
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            assertThat(categoryRepository.existsById(category.getId())).isFalse();
            int remainingFields = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM category_field WHERE category_id = ?",
                    Integer.class, category.getId());
            assertThat(remainingFields).as("Fields should be deleted with the category").isZero();
        }

        @Test
        @DisplayName("Should do nothing when deleting non-existent category")
        void testDeleteCategoryNotFound() {
            restTestClient.delete()
                          .uri(CATEGORIES_URL + "/{id}", 99999L)
                          .exchange()
                          .expectStatus()
                          .isNoContent();
        }

        @Test
        @DisplayName("Should block deletion of category that still has products")
        void testDeleteCategoryWithAssociatedProducts() {
            Category category = createTestCategory("Category with Products");
            productRepository.save(new Product(category, "Product 1"));

            restTestClient.delete()
                          .uri(CATEGORIES_URL + "/{id}", category.getId())
                          .exchange();

            assertThat(categoryRepository.existsById(category.getId())).isTrue();
        }

    }

}
