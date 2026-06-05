package ch.portami.inventorybackend.product.mapper;

import ch.portami.inventorybackend.product.dto.category.CategoryDto;
import ch.portami.inventorybackend.product.dto.category.CategoryFieldDto;
import ch.portami.inventorybackend.product.dto.category.CreateCategoryDto;
import ch.portami.inventorybackend.product.dto.category.UpdateCategoryDto;
import ch.portami.inventorybackend.product.entity.Category;
import ch.portami.inventorybackend.product.entity.CategoryField;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    CategoryFieldDto toCategoryFieldDto(CategoryField categoryField);

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "fields", ignore = true)
    Category toCategory(CreateCategoryDto createCategoryDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "fields", ignore = true)
    void updateCategory(UpdateCategoryDto updateCategoryDto, @MappingTarget Category category);

}
