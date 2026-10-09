package com.janvo.salon.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalonServiceRequest {

    @NotBlank
    private String name;

    private String description;

    private String icon;

    private Integer displayOrder;
}