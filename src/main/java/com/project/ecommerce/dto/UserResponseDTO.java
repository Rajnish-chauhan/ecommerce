package com.project.ecommerce.dto;

public class UserResponseDTO {
    private String id;
    private String name;
    private String email;
    private String role;
    private String profileImageUrl;
    private String dob;
    private String address;

    public UserResponseDTO(String id, String name, String email, String role, String profileImageUrl, String dob, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.dob = dob;
        this.address = address;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public String getDob() { return dob; }
    public String getAddress() { return address; }
}