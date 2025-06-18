package com.example.donorblood;

public class User {
    private String name;
    private String email;
    private String address;
    private String citizenship;
    private String phoneNumber;
    private String dob;
    private String gender;
    private String bloodType;
    private String photoUri;
    private String password;

    public User() {
        // Empty constructor
    }

    // Constructor with all fields (optional)
    public User(String name, String email, String address, String citizenship,
                String phoneNumber, String dob, String gender,
                String bloodType, String photoUri, String password) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.citizenship = citizenship;
        this.phoneNumber = phoneNumber;
        this.dob = dob;
        this.gender = gender;
        this.bloodType = bloodType;
        this.photoUri = photoUri;
        this.password = password;
    }

    public User(String name, String emailStr, String address) {
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getCitizenship() { return citizenship; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getBloodType() { return bloodType; }
    public String getPhotoUri() { return photoUri; }
    public String getPassword() { return password; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setCitizenship(String citizenship) { this.citizenship = citizenship; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDob(String dob) { this.dob = dob; }
    public void setGender(String gender) { this.gender = gender; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public void setPhotoUri(String photoUri) { this.photoUri = photoUri; }
    public void setPassword(String password) { this.password = password; }
}
