package com.springboot.restblog.model.converter;

import com.springboot.restblog.model.entity.CategoryEntity;
import com.springboot.restblog.model.payload.CategoryDTO;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    public CategoryDTO toDTO(CategoryEntity entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        return dto;
    }

    public CategoryEntity toEntity(CategoryDTO dto) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(dto.getName());

        return entity;
    }

    public CategoryEntity toEntity(CategoryEntity entity, CategoryDTO dto) {
        entity.setName(dto.getName());

        return entity;
    }
}
