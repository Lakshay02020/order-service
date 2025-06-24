package com.ecom.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DeliveryDto {

    private String fullName;
    @JsonProperty("street")
    private String streetAddress;
    private String city;
    private String state;

    @JsonProperty("deliveryZip")
    private String deliveryZip;
    private String country;
    private String phone;
    private String email;
}
