package com.app.placify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyDto {
    @NotEmpty(message = "The Job Id field cannot be empty")
    @NotNull(message = "The Job Id field cannot be null")
    @NotBlank(message = "The Job Id field cannot be blank")
    private Long jobId;
    @NotEmpty(message = "The Student Id field cannot be empty")
    @NotNull(message = "The Student Id field cannot be null")
    @NotBlank(message = "The Student Id field cannot be blank")
    private Long studentId;
}
