package com.example.just_hungry.browse_order;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostQueryBuilder {
    private Query query;

    public PostQueryBuilder() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        this.query = db.collection("posts");
    }

    public PostQueryBuilder isHalal(boolean isHalal) {
        if (isHalal) {
            this.query = this.query.whereEqualTo("isHalal", true);
        }
        return this;
    }

    public PostQueryBuilder filterByCuisine(String cuisine) {
        if (cuisine != null && !cuisine.isEmpty()) {
            this.query = this.query.whereEqualTo("cuisine", cuisine);
        }
        return this;
    }

    public Query build() {
        return this.query;
    }
}
