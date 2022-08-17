package com.springboot.restblog.anotation.validator;

import com.springboot.restblog.anotation.ValidImage;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    @Override
    public void initialize(ValidImage constraintAnnotation) {

    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

        List<String> validTypes = Arrays.asList("image/png", "image/jpg", "image/jpeg");
        boolean isSupportedContentType = validTypes.contains(multipartFile.getContentType());

        if (!isSupportedContentType) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Only PNG or JPG images are allowed")
                    .addConstraintViolation();
        }

        return isSupportedContentType;
    }
}
