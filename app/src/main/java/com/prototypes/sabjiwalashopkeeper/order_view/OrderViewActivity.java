package com.prototypes.sabjiwalashopkeeper.order_view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.classes.OrderVegetable;
import com.prototypes.sabjiwalashopkeeper.classes.Orders;
import com.prototypes.sabjiwalashopkeeper.database_classes.UpdateOrderDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderViewActivity extends AppCompatActivity {

    private static final String TAG = "OrderViewActivity";
    TextView customer_name, delivery_address, order_status, total_amount;
    LinearLayout vegetables;
    FirebaseFirestore fStore;
    String path;
    Toolbar toolbar;
    float sub_total;
    Button order_ready_button, order_cannot_be_delivered;
    FloatingActionButton call_button;
    UpdateOrderDatabase database;
    ArrayList<OrderVegetable> orderVegetableArrayList;
    boolean language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        language = sharedPreferences.getBoolean("language", false);
        if (!sharedPreferences.getBoolean("language", false)) {
            setAppLanguage("hi");
        }else {
            setAppLanguage("en");
        }
        setContentView(R.layout.activity_order_view);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        fStore = FirebaseFirestore.getInstance();

        vegetables = findViewById(R.id.vegetables);
        customer_name = findViewById(R.id.customer_name);
        delivery_address = findViewById(R.id.delivery_address);
        order_status = findViewById(R.id.order_status);
        total_amount = findViewById(R.id.total_amount);
        order_ready_button = findViewById(R.id.order_ready_button);
        call_button = findViewById(R.id.call_button);
        order_cannot_be_delivered = findViewById(R.id.order_cannot_be_delivered);
        //deleteDatabase();
        order_ready_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("Orders")
                        .document(path)
                        .update("order_status", "Order ready");
                for (OrderVegetable orderVegetable : orderVegetableArrayList) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("vegetable_category", orderVegetable.getVegetable_category());
                    map.put("vegetable_id", orderVegetable.getVegetable_id());
                    map.put("vegetable_name", orderVegetable.getVegetable_name());
                    map.put("vegetable_name_hindi", orderVegetable.getVegetable_name_hindi());
                    map.put("vegetable_type", orderVegetable.getVegetable_type());
                    map.put("vegetable_type_hindi", orderVegetable.getVegetable_type_hindi());
                    map.put("vegetable_unit", orderVegetable.getVegetable_price_per_unit());
                    map.put("vegetable_unit_hindi", orderVegetable.getVegetable_unit_hindi());
                    map.put("vegetable_price", orderVegetable.getVegetable_price());
                    map.put("vegetable_actual_quantity", orderVegetable.getVegetable_actual_quantity());
                    map.put("vegetable_quantity", orderVegetable.getVegetable_quantity());
                    map.put("vegetable_status", orderVegetable.getVegetable_status());
                    fStore.collection("Orders")
                            .document(path)
                            .collection("order_items")
                            .add(map)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    finish();
                                }
                            });
                }
                fStore.collection("Orders")
                        .document(path)
                        .collection("order_items")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    fStore.collection("Orders")
                                            .document(path)
                                            .collection("order_items")
                                            .document(documentSnapshot.getId())
                                            .delete();
                                }
                            }
                        });
            }
        });
        order_cannot_be_delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("Orders")
                        .document(path)
                        .update("order_status", "Order cannot be delivered")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
            }
        });
        orderVegetableArrayList = new ArrayList<>();
        getDataFromDatabase();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fStore.collection("Orders")
                .document(path)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Orders orders = task.getResult().toObject(Orders.class).withId(task.getResult().getId());
                        String customerName = orders.getCustomer_name();
                        String deliveryAddress = orders.getCustomer_address();
                        String orderStatus = orders.getOrder_status();
                        customer_name.setText(customerName);
                        delivery_address.setText(deliveryAddress);
                        call_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                                intent1.setData(Uri.parse("tel:" + orders.getCustomer_phone_number()));
                                startActivity(intent1);
                            }
                        });
                        order_status.setText(orderStatus);
                    }
                });
    }

    private void getDataFromDatabase() {
        database = new UpdateOrderDatabase(this, path);
        Cursor cursor = database.getData();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            OrderVegetable orderVegetable = new OrderVegetable(cursor.getString(cursor.getColumnIndex("VEGETABLE_CATEGORY")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_ID")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_NAME")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_NAME_HINDI")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_TYPE_HINDI")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_UNIT_HINDI")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_PRICE")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_PRICE_PER_UNIT")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_QUANTITY")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_TYPE")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_STATUS")),
                    cursor.getString(cursor.getColumnIndex("VEGETABLE_ACTUAL_QUANTITY")));
            orderVegetableArrayList.add(orderVegetable);
            cursor.moveToNext();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        vegetables.removeAllViews();
        fStore.collection("Orders")
                .document(path)
                .collection("order_items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            OrderVegetable orderVegetable = documentSnapshot.toObject(OrderVegetable.class)
                                    .withId(documentSnapshot.getId());
                            database.addData(orderVegetable.getVegetable_id(), orderVegetable.getVegetable_name(),
                                    orderVegetable.getVegetable_category(),
                                    orderVegetable.getVegetable_price(), orderVegetable.getVegetable_price_per_unit(),
                                    orderVegetable.getVegetable_quantity(), orderVegetable.getVegetable_type(),
                                    "Available", orderVegetable.getVegetable_quantity(),
                                    orderVegetable.getVegetable_name_hindi(), orderVegetable.getVegetable_type_hindi(),
                                    orderVegetable.getVegetable_unit_hindi());
                            orderVegetableArrayList = new ArrayList<>();
                            getDataFromDatabase();
                            for (OrderVegetable orderVegetable1 : orderVegetableArrayList) {
                                if (orderVegetable1.getVegetable_id().equals(orderVegetable.getVegetable_id())) {
                                    View view = getLayoutInflater().inflate(R.layout.order_vegetable_layout, null);
                                    ImageView vegetable_image = view.findViewById(R.id.customer_image);
                                    TextView textView = view.findViewById(R.id.text4);
                                    TextView vegetable_type = view.findViewById(R.id.vegetable_type);
                                    TextView vegetable_name = view.findViewById(R.id.customer_name);
                                    TextView vegetable_price = view.findViewById(R.id.vegetable_price);
                                    EditText vegetable_quantity = view.findViewById(R.id.vegetable_quantity);
                                    ImageButton delete_button = view.findViewById(R.id.delete_button);
                                    delete_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            vegetables.removeView(view);
                                            database.updateData(orderVegetable.getVegetable_id(), orderVegetable.getVegetable_name(),
                                                    orderVegetable.getVegetable_category(),
                                                    orderVegetable.getVegetable_price(), orderVegetable.getVegetable_price_per_unit(),
                                                    orderVegetable.getVegetable_quantity(), orderVegetable.getVegetable_type(),
                                                    "Not Available", orderVegetable.getVegetable_actual_quantity(),
                                                    orderVegetable.getVegetable_name_hindi(), orderVegetable.getVegetable_type_hindi(),
                                                    orderVegetable.getVegetable_unit_hindi());
                                        }
                                    });
                                    StorageReference vegetableImage = FirebaseStorage.getInstance().getReference().child("MasterList/" +
                                            orderVegetable1.getVegetable_name() + orderVegetable1.getVegetable_type() + ".png");
                                    Glide.with(vegetable_image)
                                            .load(vegetableImage)
                                            .error(R.drawable.vegetable_image_not_available)
                                            .into(vegetable_image);
                                    if (language) {
                                        vegetable_name.setText(orderVegetable1.getVegetable_name());
                                        vegetable_type.setText(orderVegetable1.getVegetable_type());
                                    }else {
                                        vegetable_name.setText(orderVegetable1.getVegetable_name_hindi());
                                        vegetable_type.setText(orderVegetable1.getVegetable_type_hindi());
                                    }
                                    String[] split = orderVegetable.getVegetable_quantity().split(" ");
                                    float price = 0;
                                    switch (orderVegetable.getVegetable_price_per_unit()){
                                        case "Per Kg": {
                                            if (split[1].equals("gram")) {
                                                price = Float.parseFloat(orderVegetable.getVegetable_price()) * Float.parseFloat(split[0]) / 1000f;
                                            }else {
                                                price = Float.parseFloat(orderVegetable.getVegetable_price()) * Float.parseFloat(split[0]);
                                            }
                                            break;
                                        }
                                        case "Per Dozen": {
                                            price = Float.parseFloat(orderVegetable.getVegetable_price()) * Float.parseFloat(split[0])/12f;
                                            break;
                                        }
                                        case "Per Piece": {
                                            price = Float.parseFloat(orderVegetable.getVegetable_price()) * Float.parseFloat(split[0]);
                                            break;
                                        }
                                        case "Per 100 gram": {
                                            price = Float.parseFloat(orderVegetable.getVegetable_price()) * Float.parseFloat(split[0])/100f;
                                            break;
                                        }
                                    }
                                    vegetable_quantity.setText(split[0]);
                                    textView.setText(split[1]);
                                    vegetable_quantity.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                            if (!s.toString().isEmpty()) {
                                                float oldPrice = Float.parseFloat(vegetable_price.getText().toString());
                                                float newQuantity = Float.parseFloat(s.toString());
                                                float vegetablePrice = 0;
                                                switch (orderVegetable.getVegetable_price_per_unit()) {
                                                    case "Per Kg": {
                                                        if (textView.getText().toString().equals(" kg")) {
                                                            vegetablePrice = Float.parseFloat(orderVegetable1.getVegetable_price()) * newQuantity;
                                                        } else {
                                                            vegetablePrice = Float.parseFloat(orderVegetable1.getVegetable_price()) * newQuantity / 1000f;
                                                        }
                                                        break;
                                                    }
                                                    case "Per Dozen": {
                                                        vegetablePrice = Float.parseFloat(orderVegetable1.getVegetable_price()) * newQuantity / 12f;
                                                        break;
                                                    }
                                                    case "Per Piece": {
                                                        vegetablePrice = Float.parseFloat(orderVegetable1.getVegetable_price()) * newQuantity;
                                                        break;
                                                    }
                                                    case "Per 100 gram": {
                                                        vegetablePrice = Float.parseFloat(orderVegetable1.getVegetable_price()) * newQuantity/100f;
                                                    }
                                                }
                                                sub_total = sub_total - oldPrice + vegetablePrice;
                                                database.updateData(orderVegetable1.getVegetable_id(), orderVegetable1.getVegetable_name(),
                                                        orderVegetable1.getVegetable_category(),
                                                        orderVegetable1.getVegetable_price(), orderVegetable1.getVegetable_price_per_unit(),
                                                        s.toString() + " " + textView.getText().toString(), orderVegetable1.getVegetable_type(),
                                                        "Available", orderVegetable.getVegetable_actual_quantity(),
                                                        orderVegetable.getVegetable_name_hindi(), orderVegetable.getVegetable_type_hindi(),
                                                        orderVegetable.getVegetable_unit_hindi());
                                                Toast.makeText(OrderViewActivity.this, s.toString() + textView.getText().toString(), Toast.LENGTH_SHORT).show();
                                                vegetable_price.setText(String.format("%.2f", vegetablePrice));
                                                total_amount.setText(getString(R.string.rupee_symbol) + String.format("%.2f", sub_total));
                                            }
                                        }
                                    });
                                    sub_total = sub_total + price;
                                    vegetable_price.setText(String.format("%.2f", price));
                                    vegetables.addView(view);
                                }
                            }
                        }
                        total_amount.setText(getString(R.string.rupee_symbol) + String.format("%.2f", sub_total));
                    }
                });
    }

    public void setAppLanguage(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = this.getResources().getConfiguration();
        config.setLocale(locale);
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
    }
}