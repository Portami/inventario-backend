package ch.portami.inventorybackend.product.dto.product;

import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeMapper;
import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeChangeDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantMapper;
import ch.portami.inventorybackend.product.entity.Category;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.entity.ProductAttribute;
import ch.portami.inventorybackend.product.repository.CategoryRepository;
import java.util.LinkedList;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses={ProductVariantMapper.class, ProductAttributeMapper.class})
public interface ProductMapper {

    @Mapping(source = "productVariants", target = "variants")
    @Mapping(source = "productAttributes", target = "attributes")
    ProductDto toProductDto(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "productVariants", ignore = true)
    @Mapping(source = "attributes", target = "productAttributes")
    Product toProduct(CreateProductDto createProductDto, @Context CategoryRepository categoryRepository);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "productVariants", ignore = true)
    @Mapping(source = "attributes", target = "productAttributes", ignore = true)
    void updateProduct(UpdateProductDto updateProductDto, @MappingTarget Product product, @Context CategoryRepository categoryRepository);

    @AfterMapping
    default void setSpecialProperties(CreateProductDto createProductDto, @MappingTarget Product product, @Context CategoryRepository categoryRepository) {
        setCategory(product, createProductDto.categoryId(), categoryRepository);
    }

    @AfterMapping
    default void updateSpecialProperties(UpdateProductDto updateProductDto, @MappingTarget Product product, @Context CategoryRepository categoryRepository) {

        if (updateProductDto.categoryId() != null) {
            setCategory(product, updateProductDto.categoryId(), categoryRepository);
        }

        if(updateProductDto.attributes() != null) {

            LinkedList<ProductAttribute> attributesToRemove = new LinkedList<>(product.getProductAttributes());

            for(ProductAttributeChangeDto attribute : updateProductDto.attributes()) {

                if(attribute.id() != null) {
                    ProductAttribute existingAttribute = attributesToRemove.stream().filter(a -> a.getId().equals(attribute.id())).findFirst().orElseThrow();
                    existingAttribute.setName(attribute.name());
                    attributesToRemove.remove(existingAttribute);
                } else {
                    ProductAttribute newAttribute = new ProductAttribute();
                    newAttribute.setName(attribute.name());
                    product.addProductAttribute(newAttribute);
                }

            }

            for(ProductAttribute attribute : attributesToRemove) {
                product.removeProductAttribute(attribute);
            }

        }

    }

    private static void setCategory(Product product, long categoryId, @Context CategoryRepository categoryRepository) {
        Category category = categoryRepository.findById(categoryId).orElseThrow();
        product.setCategory(category);
    }

}
