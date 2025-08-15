package com.prototypes.sabjiwalashopkeeper.classes;

import com.google.firebase.Timestamp;

public class Orders extends Model {
    public String customer_address;
    public String customer_id;
    public String customer_name;
    public String customer_phone_number;
    public Timestamp delivery_date;
    public Timestamp order_date;
    public String order_status;
    public String payment_mode;
    public String payment_status;
    public String sabjiwala_id;
    public String total_amount;

    public Orders(){
        //empty constructor is necessary
    }

    public Orders(String customer_address, String customer_id, String customer_name, String customer_phone_number,
                  Timestamp delivery_date, Timestamp order_date, String order_status, String payment_mode,
                  String payment_status, String sabjiwala_id, String total_amount) {
        this.customer_address = customer_address;
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_phone_number = customer_phone_number;
        this.delivery_date = delivery_date;
        this.order_date = order_date;
        this.order_status = order_status;
        this.payment_mode = payment_mode;
        this.payment_status = payment_status;
        this.sabjiwala_id = sabjiwala_id;
        this.total_amount = total_amount;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_phone_number() {
        return customer_phone_number;
    }

    public void setCustomer_phone_number(String customer_phone_number) {
        this.customer_phone_number = customer_phone_number;
    }

    public Timestamp getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(Timestamp delivery_date) {
        this.delivery_date = delivery_date;
    }

    public Timestamp getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Timestamp order_date) {
        this.order_date = order_date;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public void setPayment_status(String payment_status) {
        this.payment_status = payment_status;
    }

    public String getSabjiwala_id() {
        return sabjiwala_id;
    }

    public void setSabjiwala_id(String sabjiwala_id) {
        this.sabjiwala_id = sabjiwala_id;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }
}
