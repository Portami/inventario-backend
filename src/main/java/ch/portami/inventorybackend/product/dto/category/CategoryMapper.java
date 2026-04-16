package ch.portami.inventorybackend.product.dto.category;

import ch.portami.inventorybackend.product.entity.Category;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toCategoryDto(Category category);

    @Mapping(target = "products", ignore = true)
    Category toCategory(CategoryRequest categoryRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "products", ignore = true)
    void updateCategoryFromPatchRequest(CategoryPatchRequest categoryPatchRequest, @MappingTarget Category category);

}
