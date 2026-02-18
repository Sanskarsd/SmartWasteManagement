package com.example.smartwastemanagement;

public class Product {
    private String id;
    private String name;
    private String mobile;
    private String price;
    private String location;
    private String state;
    private String image1;
    private String image2;
    private String image3;
    public Product() {}

    public Product(String name, String mobile, String price, String location, String state, String image1, String image2, String image3) {
        this.name = name;
        this.mobile = mobile;
        this.price = price;
        this.location = location;
        this.state = state;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
    }

    // Getter and Setter methods
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public String getMobile() { return mobile; }
    public String getPrice() { return price; }
    public String getLocation() { return location; }
    public String getState() { return state; }
    public String getImage1() { return image1; }
    public String getImage2() { return image2; }
    public String getImage3() { return image3; }
}
