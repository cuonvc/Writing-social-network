package com.springboot.restblog.controller;

import com.springboot.restblog.model.payload.CategoryDTO;
import com.springboot.restblog.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO categoryResponse = categoryService.saveCategory(categoryDTO);

        return new ResponseEntity<>(categoryResponse, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/category/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @PathVariable(name = "id") Integer id,
                                                      @RequestBody CategoryDTO categoryDTO) {
        categoryDTO.setId(id);
        CategoryDTO categoryResponse = categoryService.saveCategory(categoryDTO);

        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @GetMapping("/categories")
    public List<CategoryDTO> getAllCategory() {
        List<CategoryDTO> listResponse = categoryService.getAll();
        return listResponse;
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable(name = "id") Integer id) {
        CategoryDTO categoryResponse = categoryService.findById(id);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/category/{id}")
    public ResponseEntity<String> deleteById(@PathVariable(name = "id") Integer id) {
        categoryService.deleteById(id);
        return new ResponseEntity<>("Delete successfully", HttpStatus.OK);
    }
}
