package com.janvo.salon.dtos;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SubServiceResponse {

    private Long id;

    private Long serviceId;

    private String name;

    private String description;

    private BigDecimal price;

    private BigDecimal offerPrice;

    private Integer durationMinutes;

    private Integer displayOrder;

    private Boolean active;
}
