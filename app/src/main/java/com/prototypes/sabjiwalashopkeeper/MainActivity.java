package com.prototypes.sabjiwalashopkeeper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.BuildConfig;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwalashopkeeper.account_activity.AccountActivity;
import com.prototypes.sabjiwalashopkeeper.login.LoginActivity;
import com.prototypes.sabjiwalashopkeeper.ui.current_orders.CurrentOrdersFragment;
import com.prototypes.sabjiwalashopkeeper.ui.order_history.OrderHistoryFragment;
import com.prototypes.sabjiwalashopkeeper.ui.orders_ready.OrdersReadyFragment;
import com.prototypes.sabjiwalashopkeeper.ui.set_products.SetProductsFragment;
import com.prototypes.sabjiwalashopkeeper.update_activity.UpdateActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DrawerLayout drawer;
    SetProductsFragment setProductsFragment;
    CurrentOrdersFragment currentOrdersFragment;
    OrdersReadyFragment ordersReadyFragment;
    SwitchMaterial shop_status;
    OrderHistoryFragment orderHistoryFragment;
    String selectedFragment;
    SharedPreferences fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("language", false)) {
            setAppLanguage("hi");
        }else {
            setAppLanguage("en");
        }
        fragment = getSharedPreferences("selectedFragment", MODE_PRIVATE);
        selectedFragment = fragment.getString("MainActivity", "loadFragment");
        setContentView(R.layout.activity_main);

        fStore = FirebaseFirestore.getInstance();

        fStore.collection("App")
                .document("app-details-sabji-wala-shopkeeper")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (Double.parseDouble(task.getResult().getString("version")) > Double.parseDouble(BuildConfig.VERSION_NAME)){
                            Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        getUserDetails();
        setProductsFragment = new SetProductsFragment();
        currentOrdersFragment = new CurrentOrdersFragment();
        ordersReadyFragment = new OrdersReadyFragment();
        orderHistoryFragment = new OrderHistoryFragment();
        if (selectedFragment.equals("setProductsFragment")) {
            loadFragment(setProductsFragment);
        }if (selectedFragment.equals("currentOrdersFragment")){
            loadFragment(currentOrdersFragment);
        }if (selectedFragment.equals("ordersReadyFragment")){
            loadFragment(ordersReadyFragment);
        }if (selectedFragment.equals("orderHistoryFragment")){
            loadFragment(orderHistoryFragment);
        }
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(5).getSubMenu().getItem(0).setTitle(BuildConfig.VERSION_NAME);
    }

    void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void getUserDetails() {
        //String userId = fAuth.getCurrentUser().getUid();
        //String mobile = fAuth.getCurrentUser().getPhoneNumber();
        String userId = "IUvAYRhZfahjB5mh6a7yL1znfU72";
        String mobile = "+91 9810165395";
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ImageView ivShopPicture = (ImageView) headerView.findViewById(R.id.ivShopPicture);
        TextView tvName = (TextView) headerView.findViewById(R.id.tvName);
        TextView tvMobile = (TextView) headerView.findViewById(R.id.tvMobile);
        shop_status = headerView.findViewById(R.id.shop_status);
        tvMobile.setText(mobile);
        StorageReference mStorageRef;
        ImageView owner_picture = (ImageView) headerView.findViewById(R.id.owner_picture);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = mStorageRef.child("shoppictures/" + userId);
        StorageReference ownerPicture = mStorageRef.child("shop_owner_pictures/" + userId);
        Glide.with(this)
                .load(imageRef)
                .error(R.drawable.app_bar_main_background)
                .into(ivShopPicture);
        Glide.with(this)
                .load(ownerPicture)
                .error(R.drawable.default_profile)
                .into(owner_picture);
        DocumentReference df = fStore.collection("SabjiWale").document(userId);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tvName.setText(documentSnapshot.getString("ownerName"));
                shop_status.setChecked(documentSnapshot.getBoolean("shopStatus"));
                shop_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        fStore.collection("SabjiWale")
                                .document(userId)
                                .update("shopStatus", isChecked);
                    }
                });
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_current_orders) {
            loadFragment(currentOrdersFragment);
            SharedPreferences.Editor editor = fragment.edit();
            editor.putString("MainActivity", "currentOrdersFragment");
            editor.apply();
        }else if(item.getItemId() == R.id.nav_logout){
            fAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if(item.getItemId() == R.id.nav_set_products){
            loadFragment(setProductsFragment);
            SharedPreferences.Editor editor = fragment.edit();
            editor.putString("MainActivity", "setProductsFragment");
            editor.apply();
        }else if(item.getItemId() == R.id.nav_set_products){
            loadFragment(setProductsFragment);
            SharedPreferences.Editor editor = fragment.edit();
            editor.putString("MainActivity", "setProductsFragment");
            editor.apply();
        }else if(item.getItemId() == R.id.nav_orders_ready){
            loadFragment(ordersReadyFragment);
            SharedPreferences.Editor editor = fragment.edit();
            editor.putString("MainActivity", "ordersReadyFragment");
            editor.apply();
        }else{
            loadFragment(orderHistoryFragment);
            SharedPreferences.Editor editor = fragment.edit();
            editor.putString("MainActivity", "orderHistoryFragment");
            editor.apply();
        }
        /*switch (item.getItemId()) {
            case R.id.nav_current_orders: {
                loadFragment(currentOrdersFragment);
                SharedPreferences.Editor editor = fragment.edit();
                editor.putString("MainActivity", "currentOrdersFragment");
                editor.apply();
                break;
            }
            case R.id.nav_logout: {
                fAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_set_products: {
                loadFragment(setProductsFragment);
                SharedPreferences.Editor editor = fragment.edit();
                editor.putString("MainActivity", "setProductsFragment");
                editor.apply();
                break;
            }
            case R.id.nav_orders_ready: {
                loadFragment(ordersReadyFragment);
                SharedPreferences.Editor editor = fragment.edit();
                editor.putString("MainActivity", "ordersReadyFragment");
                editor.apply();
                break;
            }
            case R.id.nav_order_history: {
                loadFragment(orderHistoryFragment);
                SharedPreferences.Editor editor = fragment.edit();
                editor.putString("MainActivity", "orderHistoryFragment");
                editor.apply();
                break;
            }*/
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadFragment(Fragment someFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContent, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setAppLanguage(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = this.getResources().getConfiguration();
        config.setLocale(locale);
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
    }
}



