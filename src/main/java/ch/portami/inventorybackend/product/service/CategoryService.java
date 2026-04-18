package ch.portami.inventorybackend.product.service;

import ch.portami.inventorybackend.product.dto.category.CategoryDto;
import ch.portami.inventorybackend.product.dto.category.CategoryMapper;
import ch.portami.inventorybackend.product.dto.category.CreateCategoryDto;
import ch.portami.inventorybackend.product.dto.category.UpdateCategoryDto;
import ch.portami.inventorybackend.product.entity.Category;
import ch.portami.inventorybackend.product.exception.CategoryNotFoundException;
import ch.portami.inventorybackend.product.repository.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        Category category = categoryMapper.toCategory(createCategoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(long id) {
        Category category = getCategoryOrThrow(id);
        return categoryMapper.toCategoryDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Transactional
    public CategoryDto updateCategory(long id, UpdateCategoryDto updateCategoryDto) {
        Category category = getCategoryOrThrow(id);
        categoryMapper.updateCategory(updateCategoryDto, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    public void deleteCategory(long id) {
        categoryRepository.deleteById(id);
    }

    private Category getCategoryOrThrow(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

}
