package com.janvo.salon.dtos;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SalonServiceResponse {

    private Long id;

    private String name;

    private String description;

    private String icon;

    private Integer displayOrder;

    private Boolean active;

    private List<SubServiceResponse> subServices;
}
