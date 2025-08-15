package com.prototypes.sabjiwalashopkeeper.order_history;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.classes.OrderVegetable;
import com.prototypes.sabjiwalashopkeeper.classes.Orders;

import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    FirebaseFirestore fStore;
    String path;
    ImageView customer_image;
    TextView customer_name, order_status, customer_address, total_amount;
    FloatingActionButton call_button;
    StorageReference fStorage;
    LinearLayout vegetables;
    float sub_total;
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
        setContentView(R.layout.activity_order_history);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.order_history));
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        customer_image = findViewById(R.id.customer_image);
        customer_address = findViewById(R.id.customer_address);
        customer_name = findViewById(R.id.customer_name);
        order_status = findViewById(R.id.order_status);
        vegetables = findViewById(R.id.vegetables);
        total_amount = findViewById(R.id.total_amount);
        call_button = findViewById(R.id.call_button);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        fStorage = FirebaseStorage.getInstance().getReference();
        fStore = FirebaseFirestore.getInstance();

        fStore.collection("Orders")
                .document(path)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Orders orders = task.getResult().toObject(Orders.class);
                            call_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent1 = new Intent(Intent.ACTION_DIAL);
                                    intent1.setData(Uri.parse("tel:" + orders.getCustomer_phone_number()));
                                    startActivity(intent1);
                                }
                            });
                            customer_name.setText(orders.getCustomer_name());
                            customer_address.setText(orders.getCustomer_address());
                            order_status.setText(orders.getOrder_status());
                            StorageReference customerImage = fStorage.child("customer_pictures/" + orders.getCustomer_id());
                            Glide.with(customer_image)
                                    .load(customerImage)
                                    .error(R.drawable.default_profile)
                                    .into(customer_image);
                        }
                    }
                });

        fStore.collection("Orders")
                .document(path)
                .collection("order_items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            OrderVegetable orderVegetable = documentSnapshot.toObject(OrderVegetable.class).withId(documentSnapshot.getId());
                            View view = getLayoutInflater().inflate(R.layout.order_ready_vegetable_layout, null);
                            ImageView vegetable_image = view.findViewById(R.id.vegetable_image);
                            TextView vegetable_name = view.findViewById(R.id.vegetable_name);
                            TextView vegetable_type = view.findViewById(R.id.vegetable_type);
                            TextView vegetable_quantity = view.findViewById(R.id.vegetable_quantity);
                            TextView total_price = view.findViewById(R.id.total_price);
                            StorageReference vegetableImage = fStorage.child("MasterList/" + orderVegetable.getVegetable_name() + orderVegetable.getVegetable_type() + ".png");
                            Glide.with(vegetable_image)
                                    .load(vegetableImage)
                                    .error(R.drawable.vegetable_image_not_available)
                                    .into(vegetable_image);
                            if (language) {
                                vegetable_name.setText(orderVegetable.getVegetable_name());
                                vegetable_type.setText(orderVegetable.getVegetable_type());
                            }else {
                                vegetable_name.setText(orderVegetable.getVegetable_name_hindi());
                                vegetable_type.setText(orderVegetable.getVegetable_type_hindi());
                            }
                            float totalPrice = 0;
                            switch (orderVegetable.getVegetable_price_per_unit()) {
                                case "Per Kg": {
                                    String[] split = orderVegetable.getVegetable_quantity().split(" ");
                                    float quantity = Float.parseFloat(split[0]);
                                    if (split[1].equals("gram")) {
                                        totalPrice = Float.parseFloat(orderVegetable.getVegetable_price()) * quantity/1000f;
                                    }else {
                                        totalPrice = Float.parseFloat(orderVegetable.getVegetable_price()) * quantity;
                                    }
                                    break;
                                }
                                case "Per Dozen": {
                                    String[] split = orderVegetable.getVegetable_quantity().split(" ");
                                    float quantity = Float.parseFloat(split[0]);
                                    totalPrice = Float.parseFloat(orderVegetable.getVegetable_price()) * quantity/12f;
                                    break;
                                }
                                case "Per Piece": {
                                    String[] split = orderVegetable.getVegetable_quantity().split(" ");
                                    float quantity = Float.parseFloat(split[0]);
                                    totalPrice = Float.parseFloat(orderVegetable.getVegetable_price()) * quantity;
                                    break;
                                }
                                case "Per 100 gram": {
                                    String[] split = orderVegetable.getVegetable_quantity().split(" ");
                                    float quantity = Float.parseFloat(split[0]);
                                    totalPrice = Float.parseFloat(orderVegetable.getVegetable_price()) * quantity / 100f;
                                    break;
                                }
                            }
                            total_price.setText(getString(R.string.rupee_symbol) + String.valueOf(totalPrice));
                            sub_total = sub_total + totalPrice;
                            vegetables.addView(view);
                        }
                        total_amount.setText(getString(R.string.rupee_symbol) + String.valueOf(sub_total));
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