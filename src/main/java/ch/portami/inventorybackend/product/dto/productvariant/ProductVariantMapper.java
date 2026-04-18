package ch.portami.inventorybackend.product.dto.productvariant;

import ch.portami.inventorybackend.product.dto.productattributevalue.CreateProductAttributeValueDto;
import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueChangeDto;
import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueMapper;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.entity.ProductAttribute;
import ch.portami.inventorybackend.product.entity.ProductAttributeValue;
import ch.portami.inventorybackend.product.entity.ProductVariant;
import ch.portami.inventorybackend.product.exception.ProductAttributeNotFound;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {ProductAttributeValueMapper.class})
public interface ProductVariantMapper {

    @Mapping(source = "productAttributeValues", target = "attributes")
    ProductVariantDto toProductVariantDto(ProductVariant productVariant);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "productAttributeValues", ignore = true)
    @Mapping(target = "productInventories", ignore = true)
    ProductVariant toProductVariant(CreateProductVariantDto createProductVariantDto, @Context Product product);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "productAttributeValues", ignore = true)
    @Mapping(target = "productInventories", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductVariant(
            UpdateProductVariantDto updateProductVariantDto,
            @MappingTarget ProductVariant productVariant,
            @Context Product product
    );

    @AfterMapping
    default void setSpecialProperties(
            CreateProductVariantDto createProductVariantDto,
            @MappingTarget ProductVariant productVariant,
            @Context Product product
    ) {

        productVariant.setProduct(product);

        if(createProductVariantDto.attributes() == null) {
            return;
        }

        for (CreateProductAttributeValueDto attributeDto : createProductVariantDto.attributes()) {
            ProductAttributeValue attributeValue = new ProductAttributeValue(
                    productVariant,
                    product.getProductAttributeById(attributeDto.attributeId())
                            .orElseThrow(() -> new ProductAttributeNotFound(product.getId(), attributeDto.attributeId())),
                    attributeDto.value()
            );

            productVariant.addProductAttributeValue(attributeValue);
        }

    }

    @AfterMapping
    default void updateSpecialProperties(
            UpdateProductVariantDto updateProductVariantDto,
            @MappingTarget ProductVariant productVariant,
            @Context Product product
    ) {

        if (updateProductVariantDto.attributes() != null) {

            Map<Long, ProductAttributeValue> untouchedAttributeValues = productVariant.getProductAttributeValues().stream()
                    .collect(Collectors.toMap(av -> av.getProductAttribute().getId(), Function.identity()));

            for (ProductAttributeValueChangeDto attributeValueDto : updateProductVariantDto.attributes()) {

                ProductAttributeValue existingAttributeValue = untouchedAttributeValues.remove(attributeValueDto.attributeId());

                if(existingAttributeValue == null) {
                    existingAttributeValue = productVariant.getProductAttributeValueByAttributeId(attributeValueDto.attributeId()).orElse(null);
                }

                if (existingAttributeValue != null) {
                    existingAttributeValue.setValue(attributeValueDto.value());
                } else {
                    ProductAttribute attribute = product.getProductAttributeById(attributeValueDto.attributeId())
                            .orElseThrow(() -> new ProductAttributeNotFound(product.getId(), attributeValueDto.attributeId()));

                    ProductAttributeValue newAttributeValue = new ProductAttributeValue(
                            productVariant,
                            attribute,
                            attributeValueDto.value()
                    );
                    productVariant.addProductAttributeValue(newAttributeValue);
                }

            }

            untouchedAttributeValues.values().forEach(productVariant::removeProductAttributeValue);

        }

    }

}
