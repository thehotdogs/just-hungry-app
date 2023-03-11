package com.example.just_hungry;


public class UserModel {
    private String name;
    private String profilePictureUrl;

    public UserModel() {
        //set to default value
        this.name = "nama";
        this.profilePictureUrl = "https://www.astronauts.id/blog/wp-content/uploads/2022/08/Makanan-Khas-Daerah-tiap-Provinsi-di-Indonesia-Serta-Daerah-Asalnya.jpg";
    }

    // getters and setters
    public String getName() {
        return name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
