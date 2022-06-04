package com.springboot.restblog.service.impl;

import com.springboot.restblog.exception.APIException;
import com.springboot.restblog.exception.ResourceNotFoundException;
import com.springboot.restblog.model.converter.CategoryConverter;
import com.springboot.restblog.model.entity.CategoryEntity;
import com.springboot.restblog.model.entity.PostEntity;
import com.springboot.restblog.model.payload.CategoryDTO;
import com.springboot.restblog.repository.CategoryRepository;
import com.springboot.restblog.repository.PostRepository;
import com.springboot.restblog.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.Convert;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryConverter converter;

    @Override
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        CategoryEntity categoryEntity;

        if (categoryDTO.getId() == null) {
            categoryEntity = converter.toEntity(categoryDTO);
        } else {
            CategoryEntity oldCategory = categoryRepository.findById(categoryDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryDTO.getId()));
            categoryEntity = converter.toEntity(oldCategory, categoryDTO);
        }

        CategoryEntity newCategory = categoryRepository.save(categoryEntity);
        CategoryDTO responseCategory = converter.toDTO(newCategory);

        return responseCategory;
    }

    @Override
    public List<CategoryDTO> getAll() {
        List<CategoryEntity> entityList = categoryRepository.findAll();
        List<CategoryDTO> response = new ArrayList<>();
        for (CategoryEntity entity : entityList) {
            response.add(converter.toDTO(entity));
        }

        return response;
    }

    @Override
    public CategoryDTO findById(Integer id) {
        CategoryEntity response = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return converter.toDTO(response);
    }

    @Override
    public void deleteById(Integer id) {
        CategoryEntity categoryById = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        List<PostEntity> postList = postRepository.findAll();

        for (PostEntity post : postList) {
            Set<CategoryEntity> categoriesInPost = post.getCategoryEntities();

            for (CategoryEntity category : categoriesInPost) {
                if (category.getId() == id) {
                    throw new APIException(HttpStatus.BAD_REQUEST, "This category can't be deleted because there are Post that apply");
                }
            }
        }


        categoryRepository.delete(categoryById);
    }
}
