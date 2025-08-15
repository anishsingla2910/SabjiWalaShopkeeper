package com.prototypes.sabjiwalashopkeeper.account_activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwalashopkeeper.MainActivity;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.classes.Shop;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class AccountActivity extends AppCompatActivity {
    
    public static final int PROFILE_IMAGE_REQUEST_PERMISSION = 101;
    public static final int PROFILE_IMAGE_REQUEST = 102;
    public static final int SHOP_IMAGE_PERMISSION_REQUEST = 201;
    public static final int SHOP_IMAGE_REQUEST = 202;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 2000;
    Toolbar toolbar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference fStorage;
    String userId;
    TextInputEditText shop_name, owner_name, shop_address;
    ImageView shop_image, customer_image;
    ImageButton shop_image_button, profile_image_button;
    NumberPicker radius_supplied;
    StorageReference shopImage, ownerImage;
    ToggleButton language_button;
    FloatingActionButton location_button;
    double latitude, longitude;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("language", false)) {
            setAppLanguage("hi");
        } else {
            setAppLanguage("en");
        }
        setContentView(R.layout.activity_account);
        
        language_button = findViewById(R.id.language_button);
        location_button = findViewById(R.id.location_button);
        language_button.setChecked(sharedPreferences.getBoolean("language", false));
        shop_name = findViewById(R.id.shop_name);
        owner_name = findViewById(R.id.owner_name);
        shop_address = findViewById(R.id.shop_address);
        radius_supplied = findViewById(R.id.radius_supplied);
        shop_image = findViewById(R.id.shop_image);
        shop_image_button = findViewById(R.id.shop_image_button);
        profile_image_button = findViewById(R.id.profile_image_button);
        customer_image = findViewById(R.id.customer_image);
        
        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    getLocation();
                }
            }
        });
        
        language_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("language", isChecked);
                editor.apply();
                boolean language = sharedPreferences.getBoolean("language", false);
                language_button.setChecked(language);
                finish();
                Intent intent = getIntent();
                startActivity(intent);
            }
        });
        
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.account_settings));
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance().getReference();
        
        //userId = fAuth.getCurrentUser().getUid();
        userId = "IUvAYRhZfahjB5mh6a7yL1znfU72";
        
        shop_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.CAMERA}, SHOP_IMAGE_PERMISSION_REQUEST);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, SHOP_IMAGE_REQUEST);
                }
            }
        });
        profile_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.CAMERA}, PROFILE_IMAGE_REQUEST_PERMISSION);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, PROFILE_IMAGE_REQUEST);
                }
            }
        });
        fStore.collection("SabjiWale")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Shop shop = task.getResult().toObject(Shop.class).withId(task.getResult().getId());
                        shop_name.setText(shop.getShopName());
                        owner_name.setText(shop.getOwnerName());
                        shop_address.setText(shop.getShopAddress());
                        radius_supplied.setMinValue(0);
                        radius_supplied.setMaxValue(4000);
                        radius_supplied.setValue(shop.getRadiusSupplied());
                        shopImage = fStorage.child("shoppictures/" + userId);
                        ownerImage = fStorage.child("shop_owner_pictures/" + userId);
                        Glide.with(shop_image)
                                .load(shopImage)
                                .error(R.drawable.sabji_wala_logo_shopkeeper)
                                .into(shop_image);
                        Glide.with(customer_image)
                                .load(ownerImage)
                                .error(R.drawable.default_profile)
                                .into(customer_image);
                    }
                });
        shop_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                fStore.collection("SabjiWale")
                        .document(userId)
                        .update("shopName", s.toString());
            }
        });
        owner_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                fStore.collection("SabjiWale")
                        .document(userId)
                        .update("ownerName", s.toString());
            }
        });
        shop_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                fStore.collection("SabjiWale")
                        .document(userId)
                        .update("shopAddress", s.toString());
            }
        });
        radius_supplied.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int roundedValue = ((newVal + 99) / 100) * 100;
                fStore.collection("SabjiWale")
                        .document(userId)
                        .update("radiusSupplied", roundedValue);
            }
        });
    }
    
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        });
        fStore.collection("SabjiWale")
                .document(userId)
                .update("location", new GeoPoint(latitude, longitude))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AccountActivity.this, "Location updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PROFILE_IMAGE_REQUEST_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, PROFILE_IMAGE_REQUEST);
                }
            }
        }
        if (requestCode == SHOP_IMAGE_PERMISSION_REQUEST) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, SHOP_IMAGE_REQUEST);
                }
            }
        }if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            for (int grantResult : grantResults){
                if (grantResult == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                }
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE_IMAGE_REQUEST) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            customer_image.setImageBitmap(image);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();
            ownerImage.putBytes(imageData);
        }
        if (requestCode == SHOP_IMAGE_REQUEST) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            shop_image.setImageBitmap(image);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();
            shopImage.putBytes(imageData);
        }
    }
    
    void setAppLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = this.getResources().getConfiguration();
        config.setLocale(locale);
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
    }
}