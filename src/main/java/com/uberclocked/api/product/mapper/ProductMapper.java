package com.uberclocked.api.product.mapper;

import com.uberclocked.api.product.model.dto.ProductDataDto;
import com.uberclocked.api.product.model.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  Product toEntity(ProductDataDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void update(ProductDataDto dto, @MappingTarget Product entity) throws IOException;

  default byte[] map(MultipartFile file) throws IOException {
    if (file == null || file.isEmpty())
      return null;
    return file.getBytes();
  }
}
