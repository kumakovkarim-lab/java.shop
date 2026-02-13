package com.warehouse.model;

public class Address {
    private final String city;
    private final String street;
    private final String house;
    private final String phone;

    public Address(String city, String street, String house, String phone) {
        this.city = city;
        this.street = street;
        this.house = house;
        this.phone = phone;
    }

    public String getCity() { return city; }
    public String getStreet() { return street; }
    public String getHouse() { return house; }
    public String getPhone() { return phone; }

    @Override
    public String toString() {
        return city + ", " + street + " " + house + " | Phone: " + phone;
    }
}
