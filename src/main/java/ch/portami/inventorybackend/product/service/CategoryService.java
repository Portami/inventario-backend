package ch.portami.inventorybackend.product.service;

import ch.portami.inventorybackend.product.dto.category.CategoryDto;
import ch.portami.inventorybackend.product.dto.category.CreateCategoryDto;
import ch.portami.inventorybackend.product.dto.category.UpdateCategoryDto;
import ch.portami.inventorybackend.product.entity.Category;
import ch.portami.inventorybackend.product.exception.CategoryNotFoundException;
import ch.portami.inventorybackend.product.mapper.CategoryMapper;
import ch.portami.inventorybackend.product.repository.CategoryRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for retrieving and managing product categories.
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Creates a new CategoryService with the given dependencies.
     *
     * @param categoryRepository the repository for accessing category data
     * @param categoryMapper     the mapper for converting between Category entities and DTOs
     */
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Creates a new category based on the provided DTO.
     *
     * @param createCategoryDto the DTO containing the data for the new category
     * @return the DTO of the created category
     */
    @Transactional
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        Category category = categoryMapper.toCategory(createCategoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(savedCategory);
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category to retrieve
     * @return the DTO of the retrieved category
     * @throws CategoryNotFoundException if no category with the given ID exists
     */
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = getCategory(id);
        return categoryMapper.toCategoryDto(category);
    }

    /**
     * Retrieves all categories.
     *
     * @return a list of DTOs for all categories
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                                 .stream()
                                 .map(categoryMapper::toCategoryDto)
                                 .toList();
    }

    /**
     * Updates an existing category with the provided data.
     *
     * @param id                the ID of the category to update
     * @param updateCategoryDto the DTO containing the requested updates for the category. Only fields with non-null
     *                          values are updated.
     * @return the DTO of the updated category
     * @throws CategoryNotFoundException if no category with the given ID exists
     */
    @Transactional
    public CategoryDto updateCategory(Long id, UpdateCategoryDto updateCategoryDto) {
        Category category = getCategory(id);
        categoryMapper.updateCategory(updateCategoryDto, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    /**
     * Deletes a category by its ID. If no category with the given ID exists, this method does nothing (i.e. no
     * exception is thrown).
     *
     * @param id the ID of the category to delete
     * @throws DataIntegrityViolationException if the category cannot be deleted because it is still referenced by
     *                                         products
     */
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private Category getCategory(Long id) {
        return categoryRepository.findById(id)
                                 .orElseThrow(() -> new CategoryNotFoundException(id));
    }

}
