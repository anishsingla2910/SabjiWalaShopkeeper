package com.prototypes.sabjiwalashopkeeper.login;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.BuildConfig;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwalashopkeeper.MainActivity;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.update_activity.UpdateActivity;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    double latitude;
    double longitude;
    FloatingActionButton location_button;
    FirebaseAuth fAuth;
    String userId = "";
    FirebaseFirestore fStore;
    TextInputEditText etOwnerName;
    TextInputEditText etShopName, etAddress;
    StorageReference mStorageRef;
    private ImageButton ibCamera;
    private ImageView ivShopPicture;
    private Bitmap bShopPicture;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("language", false)) {
            setAppLanguage("hi");
        }else {
            setAppLanguage("en");
        }
        setContentView(R.layout.activity_sign_up);

        fStore = FirebaseFirestore.getInstance();

        fStore.collection("App")
                .document("app-details-sabji-wala-shopkeeper")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (Double.parseDouble(task.getResult().getString("version")) > Double.parseDouble(BuildConfig.VERSION_NAME)) {
                            Intent intent = new Intent(SignUpActivity.this, UpdateActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        ibCamera = findViewById(R.id.ibCamera);
        ivShopPicture = findViewById(R.id.ivShopPicture);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);
        location_button = findViewById(R.id.location_button);
        etOwnerName = findViewById(R.id.etOwnerName);
        etShopName = findViewById(R.id.etShopName);

        fAuth = FirebaseAuth.getInstance();
        //userId = fAuth.getCurrentUser().getUid();
        userId = "IUvAYRhZfahjB5mh6a7yL1znfU72";
        mStorageRef = FirebaseStorage.getInstance().getReference();

        ibCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.CAMERA}, 101);
                } else {
                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent1, 102);
                }
            }
        });

        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    getLocation();
                }
            }
        });
        
        btnRegister.setEnabled(false);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
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
        btnRegister.setEnabled(true);
    }

    private void register() {
        if (!userId.equals("")) {
            Date date = Calendar.getInstance().getTime();
            Timestamp timestamp = new Timestamp(date);
            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("ownerName", etOwnerName.getText().toString());
            userInfo.put("shopName", etShopName.getText().toString());
            userInfo.put("shopAddress", etAddress.getText().toString());
            userInfo.put("createdOn", timestamp);
            userInfo.put("shopStatus", false);
            userInfo.put("shopNumber", "+91 9810165395");//fAuth.getCurrentUser().getPhoneNumber());
            userInfo.put("radiusSupplied", 0);
            userInfo.put("location", geoPoint);
            
            fStore = FirebaseFirestore.getInstance();
            DocumentReference df = fStore.collection("SabjiWale").document(userId);

            fStore.collection("MasterList")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                fStore.collection("MasterList")
                                        .document(documentSnapshot.getId())
                                        .collection("Products")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot documentSnapshot1 : task.getResult()) {
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("hindi", documentSnapshot.getString("hindi"));
                                                    fStore.collection("SabjiWale")
                                                            .document(userId)
                                                            .collection("ProductsCategories")
                                                            .document(documentSnapshot.getId())
                                                            .set(map)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    fStore.collection("SabjiWale")
                                                                            .document(userId)
                                                                            .collection("ProductsCategories")
                                                                            .document(documentSnapshot.getId())
                                                                            .collection("Products")
                                                                            .add(documentSnapshot1.getData());
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        }
                    });

            df.set(userInfo).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    upload(userId);
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent1, 102);
                }
            }
        }
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            bShopPicture = (Bitmap) data.getExtras().get("data");
            ivShopPicture.setImageBitmap(bShopPicture);
        }
    }

    public void upload(String userId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bShopPicture.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] imageData = baos.toByteArray();
        StorageReference shopImageReference = FirebaseStorage.getInstance().getReference().child("shoppictures/" + userId);
        shopImageReference.putBytes(imageData);
    }

    public void setAppLanguage(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = this.getResources().getConfiguration();
        config.setLocale(locale);
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
    }
}