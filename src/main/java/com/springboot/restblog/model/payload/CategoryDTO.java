package com.springboot.restblog.model.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CategoryDTO {

    private Integer id;

    @NotEmpty
    @Size(min = 2, max = 20, message = "Category must be minium 2 characters and maximun 20 characters")
    private String name;
}
