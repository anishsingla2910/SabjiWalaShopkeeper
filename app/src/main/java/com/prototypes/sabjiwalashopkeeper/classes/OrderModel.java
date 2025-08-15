package com.prototypes.sabjiwalashopkeeper.classes;


import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class OrderModel {
    @Exclude
    public String id;

    public <T extends OrderModel> T withId(@NonNull final String id) {
        this.id = id;
        return (T) this;
    }
}
