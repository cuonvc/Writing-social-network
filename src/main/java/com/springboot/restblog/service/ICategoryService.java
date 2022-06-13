package com.springboot.restblog.service;

import com.springboot.restblog.model.payload.CategoryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ICategoryService {
    CategoryDTO saveCategory(CategoryDTO categoryDTO);
    List<CategoryDTO> getAll();
    CategoryDTO findById(Integer id);
    void deleteById(Integer id);
}
