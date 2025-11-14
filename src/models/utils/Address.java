package models.utils;

import validation.NotBlank;
import validation.NotNull;

public class Address {
    @NotBlank
    private String city;
    @NotBlank
    private String street;
    @NotBlank
    private String building;
    @NotNull
    private long postCode;

    public Address(String city, String street, String building, long postCode) {
        this.city = city;
        this.street = street;
        this.building = building;
        this.postCode = postCode;
    }
}
