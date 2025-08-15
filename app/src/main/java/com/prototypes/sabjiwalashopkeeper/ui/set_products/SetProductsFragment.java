package com.prototypes.sabjiwalashopkeeper.ui.set_products;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwalashopkeeper.MainActivity;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.classes.Vegetable;

import java.util.Locale;

public class SetProductsFragment extends Fragment {
    
    TabLayout tab_layout;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userId;
    LinearLayout vegetables;
    boolean language;
    
    public SetProductsFragment() {
        // Required empty public constructor
    }
    
    
    public static SetProductsFragment newInstance() {
        SetProductsFragment fragment = new SetProductsFragment();
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_products, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        language = sharedPreferences.getBoolean("language", false);
        if (!sharedPreferences.getBoolean("language", false)) {
            setAppLanguage("hi");
        } else {
            setAppLanguage("en");
        }
        super.onViewCreated(view, savedInstanceState);
        tab_layout = view.findViewById(R.id.tab_layout);
        vegetables = view.findViewById(R.id.vegetables);
        ((MainActivity) getActivity()).setTitle(getString(R.string.set_products));
        populateData();
    }
    
    void populateData() {
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        //userId = fAuth.getCurrentUser().getUid();
        userId = "IUvAYRhZfahjB5mh6a7yL1znfU72";
        fStore.collection("SabjiWale")
                .document(userId)
                .collection("ProductsCategories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            String tabName = documentSnapshot.getString("hindi");
                            String tabId = documentSnapshot.getId();
                            if (!language) {
                                tab_layout.addTab(tab_layout.newTab().setText(tabName).setTag(tabId));
                            }else{
                                tab_layout.addTab(tab_layout.newTab().setText(tabId).setTag(tabId));
                            }
                        }
                    }
                });
        tab_layout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vegetables.removeAllViews();
                fStore.collection("SabjiWale")
                        .document(userId)
                        .collection("ProductsCategories")
                        .document(tab.getTag().toString())
                        .collection("Products")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    Vegetable vegetable = documentSnapshot.toObject(Vegetable.class).withId(documentSnapshot.getId());
                                    View view = getLayoutInflater().inflate(R.layout.item_master, null);
                                    TextView item_unit = view.findViewById(R.id.restaurant_item_unit);
                                    TextView item_name = view.findViewById(R.id.restaurant_item_name);
                                    TextView item_type = view.findViewById(R.id.restaurant_type);
                                    ImageView item_image = view.findViewById(R.id.restaurant_item_image);
                                    EditText item_price = view.findViewById(R.id.restaurant_item_price);
                                    SwitchMaterial item_switch = view.findViewById(R.id.switch1);
                                    if (language){
                                        item_name.setText(vegetable.getName());
                                        item_unit.setText(vegetable.getUnit());
                                        item_type.setText(vegetable.getType());
                                    }else {
                                        item_name.setText(vegetable.getName_hindi());
                                        item_unit.setText(vegetable.getUnit_hindi());
                                        item_type.setText(vegetable.getType_hindi());
                                    }
                                    item_switch.setChecked(vegetable.getIsSelling());
                                    item_price.setText(vegetable.getPrice());
                                    StorageReference itemImage = FirebaseStorage.getInstance().getReference().child("MasterList/" + vegetable.getName() + vegetable.getType() + ".png");
                                    Glide.with(item_image)
                                            .load(itemImage)
                                            .error(R.drawable.vegetable_image_not_available)
                                            .into(item_image);
                                    item_price.addTextChangedListener(new TextWatcher() {
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
                                                    .collection("ProductsCategories")
                                                    .document(tab.getTag().toString())
                                                    .collection("Products")
                                                    .document(vegetable.getId())
                                                    .update("price", s.toString());
                                        }
                                    });
                                    item_switch.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            fStore.collection("SabjiWale")
                                                    .document(userId)
                                                    .collection("ProductsCategories")
                                                    .document(tab.getTag().toString())
                                                    .collection("Products")
                                                    .document(vegetable.getId())
                                                    .update("isSelling", item_switch.isChecked());
                                        }
                                    });
                                    vegetables.addView(view);
                                }
                            }
                        });
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            
            }
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            
            }
        });
    }
    
    
    public void setAppLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = this.getResources().getConfiguration();
        config.setLocale(locale);
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
    }
}