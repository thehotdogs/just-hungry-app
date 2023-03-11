package com.example.just_hungry;

public class AssetModel {
    private String imageUrl;
    private String title;

    public AssetModel() {
        //set to default value
        this.imageUrl = "https://www.astronauts.id/blog/wp-content/uploads/2022/08/Makanan-Khas-Daerah-tiap-Provinsi-di-Indonesia-Serta-Daerah-Asalnya.jpg";
    }

    // getters and setters
    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }
}