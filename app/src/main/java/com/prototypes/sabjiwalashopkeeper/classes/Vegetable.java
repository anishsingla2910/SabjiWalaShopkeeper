package com.prototypes.sabjiwalashopkeeper.classes;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Vegetable extends Model {
    
    private String name;
    private String name_hindi;
    private String type_hindi;
    private String category;
    private String photo_url;
    private String price;
    private String type;
    private String unit;
    private String unit_hindi;
    private String code;
    private boolean isSelling;
    private List<String> quantity;
    
    public Vegetable() {
        //empty constructor needed
    }
    
    public Vegetable(String name, String name_hindi, String type_hindi, String category, String photo_url,
                     String price, String type, String unit, String unit_hindi, String code, boolean isSelling, List<String> quantity) {
        this.name = name;
        this.name_hindi = name_hindi;
        this.type_hindi = type_hindi;
        this.category = category;
        this.photo_url = photo_url;
        this.price = price;
        this.type = type;
        this.unit = unit;
        this.unit_hindi = unit_hindi;
        this.code = code;
        this.isSelling = isSelling;
        this.quantity = quantity;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName_hindi() {
        return name_hindi;
    }
    
    public void setName_hindi(String name_hindi) {
        this.name_hindi = name_hindi;
    }
    
    public String getType_hindi() {
        return type_hindi;
    }
    
    public void setType_hindi(String type_hindi) {
        this.type_hindi = type_hindi;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getPhoto_url() {
        return photo_url;
    }
    
    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getUnit_hindi() {
        return unit_hindi;
    }
    
    public void setUnit_hindi(String unit_hindi) {
        this.unit_hindi = unit_hindi;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public boolean getIsSelling() {
        return isSelling;
    }
    
    public void setSelling(boolean selling) {
        isSelling = selling;
    }
    
    public List<String> getQuantity() {
        return quantity;
    }
    
    public void setQuantity(List<String> quantity) {
        this.quantity = quantity;
    }
}