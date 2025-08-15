package com.prototypes.sabjiwalashopkeeper.classes;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class OrderVegetable extends Model {
    public String vegetable_category;
    public String vegetable_id;
    public String vegetable_name;
    public String vegetable_name_hindi;
    public String vegetable_type_hindi;
    public String vegetable_unit_hindi;
    public String vegetable_price;
    public String vegetable_price_per_unit;
    public String vegetable_quantity;
    public String vegetable_type;
    public String vegetable_status;
    public String vegetable_actual_quantity;
    
    public OrderVegetable() {
        //Empty constructor is necessary
    }
    
    public OrderVegetable(String vegetable_category, String vegetable_id, String vegetable_name, String vegetable_name_hindi, String vegetable_type_hindi, String vegetable_unit_hindi, String vegetable_price,
                          String vegetable_price_per_unit, String vegetable_quantity, String vegetable_type, String vegetable_status, String vegetable_actual_quantity) {
        this.vegetable_category = vegetable_category;
        this.vegetable_id = vegetable_id;
        this.vegetable_name = vegetable_name;
        this.vegetable_name_hindi = vegetable_name_hindi;
        this.vegetable_type_hindi = vegetable_type_hindi;
        this.vegetable_unit_hindi = vegetable_unit_hindi;
        this.vegetable_price = vegetable_price;
        this.vegetable_price_per_unit = vegetable_price_per_unit;
        this.vegetable_quantity = vegetable_quantity;
        this.vegetable_type = vegetable_type;
        this.vegetable_status = vegetable_status;
        this.vegetable_actual_quantity = vegetable_actual_quantity;
    }
    
    public String getVegetable_category() {
        return vegetable_category;
    }
    
    public void setVegetable_category(String vegetable_category) {
        this.vegetable_category = vegetable_category;
    }
    
    public String getVegetable_id() {
        return vegetable_id;
    }
    
    public void setVegetable_id(String vegetable_id) {
        this.vegetable_id = vegetable_id;
    }
    
    public String getVegetable_name() {
        return vegetable_name;
    }
    
    public void setVegetable_name(String vegetable_name) {
        this.vegetable_name = vegetable_name;
    }
    
    public String getVegetable_name_hindi() {
        return vegetable_name_hindi;
    }
    
    public void setVegetable_name_hindi(String vegetable_name_hindi) {
        this.vegetable_name_hindi = vegetable_name_hindi;
    }
    
    public String getVegetable_type_hindi() {
        return vegetable_type_hindi;
    }
    
    public void setVegetable_type_hindi(String vegetable_type_hindi) {
        this.vegetable_type_hindi = vegetable_type_hindi;
    }
    
    public String getVegetable_unit_hindi() {
        return vegetable_unit_hindi;
    }
    
    public void setVegetable_unit_hindi(String vegetable_unit_hindi) {
        this.vegetable_unit_hindi = vegetable_unit_hindi;
    }
    
    public String getVegetable_price() {
        return vegetable_price;
    }
    
    public void setVegetable_price(String vegetable_price) {
        this.vegetable_price = vegetable_price;
    }
    
    public String getVegetable_price_per_unit() {
        return vegetable_price_per_unit;
    }
    
    public void setVegetable_price_per_unit(String vegetable_price_per_unit) {
        this.vegetable_price_per_unit = vegetable_price_per_unit;
    }
    
    public String getVegetable_quantity() {
        return vegetable_quantity;
    }
    
    public void setVegetable_quantity(String vegetable_quantity) {
        this.vegetable_quantity = vegetable_quantity;
    }
    
    public String getVegetable_type() {
        return vegetable_type;
    }
    
    public void setVegetable_type(String vegetable_type) {
        this.vegetable_type = vegetable_type;
    }
    
    public String getVegetable_status() {
        return vegetable_status;
    }
    
    public void setVegetable_status(String vegetable_status) {
        this.vegetable_status = vegetable_status;
    }
    
    public String getVegetable_actual_quantity() {
        return vegetable_actual_quantity;
    }
    
    public void setVegetable_actual_quantity(String vegetable_actual_quantity) {
        this.vegetable_actual_quantity = vegetable_actual_quantity;
    }
}