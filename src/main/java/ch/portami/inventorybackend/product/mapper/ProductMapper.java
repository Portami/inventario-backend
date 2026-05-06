package ch.portami.inventorybackend.product.mapper;

import ch.portami.inventorybackend.product.dto.product.CreateProductDto;
import ch.portami.inventorybackend.product.dto.product.ProductDto;
import ch.portami.inventorybackend.product.dto.product.UpdateProductDto;
import ch.portami.inventorybackend.product.dto.productattribute.CreateProductAttributeDto;
import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeChangeDto;
import ch.portami.inventorybackend.product.entity.Category;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.entity.ProductAttribute;
import ch.portami.inventorybackend.product.exception.InvalidCategoryReferenceException;
import ch.portami.inventorybackend.product.exception.InvalidProductAttributeReferenceException;
import ch.portami.inventorybackend.product.repository.CategoryRepository;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class, ProductAttributeMapper.class})
public interface ProductMapper {

    @Mapping(source = "productVariants", target = "variants")
    @Mapping(source = "productAttributes", target = "attributes")
    ProductDto toProductDto(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "productVariants", ignore = true)
    @Mapping(target = "productAttributes", ignore = true)
    Product toProduct(CreateProductDto createProductDto, @Context CategoryRepository categoryRepository);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "productVariants", ignore = true)
    @Mapping(target = "productAttributes", ignore = true)
    void updateProduct(UpdateProductDto updateProductDto, @MappingTarget Product product,
            @Context CategoryRepository categoryRepository);

    @AfterMapping
    default void setSpecialProperties(CreateProductDto createProductDto, @MappingTarget Product product,
            @Context CategoryRepository categoryRepository) {
        setCategory(product, createProductDto.categoryId(), categoryRepository);

        if (createProductDto.attributes() == null) {
            return;
        }

        for (CreateProductAttributeDto attributeDto : createProductDto.attributes()) {
            ProductAttribute attribute = new ProductAttribute(product, attributeDto.name());
            product.addProductAttribute(attribute);
        }
    }

    @AfterMapping
    default void updateSpecialProperties(UpdateProductDto updateProductDto, @MappingTarget Product product,
            @Context CategoryRepository categoryRepository) {

        if (updateProductDto.categoryId() != null && !product.getCategory()
                                                             .getId()
                                                             .equals(updateProductDto.categoryId())) {
            setCategory(product, updateProductDto.categoryId(), categoryRepository);
        }

        if (updateProductDto.attributes() != null) {

            Map<Long, ProductAttribute> untouchedAttributes = product.getProductAttributes()
                                                                     .stream()
                                                                     .collect(Collectors.toMap(ProductAttribute::getId,
                                                                             Function.identity()));

            for (ProductAttributeChangeDto attribute : updateProductDto.attributes()) {

                if (attribute.id() != null) {
                    ProductAttribute existingAttribute = untouchedAttributes.remove(attribute.id());

                    if (existingAttribute == null) {
                        existingAttribute = product.getProductAttributeById(attribute.id())
                                                   .orElseThrow(() -> new InvalidProductAttributeReferenceException(
                                                           product.getId(),
                                                           attribute.id()));
                    }

                    existingAttribute.setName(attribute.name());
                } else {
                    ProductAttribute newAttribute = new ProductAttribute(product, attribute.name());
                    product.addProductAttribute(newAttribute);
                }

            }

            untouchedAttributes.values()
                               .forEach(product::removeProductAttribute);

        }

    }

    private static void setCategory(Product product, Long categoryId, @Context CategoryRepository categoryRepository) {
        Category category = categoryRepository.findById(categoryId)
                                              .orElseThrow(() -> new InvalidCategoryReferenceException(categoryId));
        product.setCategory(category);
    }

}
