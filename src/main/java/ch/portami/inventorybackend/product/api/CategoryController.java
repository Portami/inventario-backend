package ch.portami.inventorybackend.product.api;

import ch.portami.inventorybackend.product.dto.category.CategoryDto;
import ch.portami.inventorybackend.product.dto.category.CreateCategoryDto;
import ch.portami.inventorybackend.product.dto.category.UpdateCategoryDto;
import ch.portami.inventorybackend.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product Categories", description = "Manage product categories.")
@RestController
@RequestMapping("/api/products/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Create a product category")
    @ApiResponse(responseCode = "201", description = "Category successfully created")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CreateCategoryDto createCategoryDto) {
        CategoryDto categoryDto = categoryService.createCategory(createCategoryDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(categoryDto);
    }

    @Operation(summary = "Get a product category by ID")
    @ApiResponse(responseCode = "200", description = "Category found and returned in the response body")
    @ApiResponse(responseCode = "404", description = "No category exists with the given ID")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable long id) {
        CategoryDto categoryDto = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryDto);
    }

    @Operation(summary = "List all product categories")
    @ApiResponse(responseCode = "200", description = "List of all categories (may be empty)")
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @Operation(summary = "Update a product category", description = "Updates all provided fields of a product category. Omitted fields are not updated.")
    @ApiResponse(responseCode = "200", description = "Category successfully updated. Response body contains the updated category.")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "No category exists with the given ID")
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable long id,
            @RequestBody @Valid UpdateCategoryDto updateCategoryDto) {
        CategoryDto categoryDto = categoryService.updateCategory(id, updateCategoryDto);
        return ResponseEntity.ok(categoryDto);
    }

    @Operation(summary = "Delete a product category")
    @ApiResponse(responseCode = "204", description = "Category successfully deleted or is not existing (anymore)")
    @ApiResponse(responseCode = "409", description = "Category still has products attached and cannot be deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent()
                             .build();
    }

}
