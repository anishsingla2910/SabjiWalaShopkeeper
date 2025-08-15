package com.prototypes.sabjiwalashopkeeper.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.prototypes.sabjiwalashopkeeper.R;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    private EditText editText;
    private Button btnContinue;
    ImageView tick_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("language", false)) {
            setAppLanguage("hi");
        }else {
            setAppLanguage("en");
        }
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();

        // spinner = findViewById(R.id.spinnerCountries);
        //spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        editText = findViewById(R.id.editTextMobile);
        tick_image = findViewById(R.id.check_image);
        tick_image.setVisibility(View.INVISIBLE);
        btnContinue = findViewById(R.id.buttonContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
                String number = editText.getText().toString().trim();
                if (number.isEmpty() || number.length() < 10) {
                    editText.setError("Please enter Valid number");
                    editText.requestFocus();
                    return;
                }
                String phoneNumber = "+91" + number;
                Intent intent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("phonenumber", phoneNumber);
                startActivity(intent);
                finish();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    btnContinue.setEnabled(true);
                    tick_image.setVisibility(View.VISIBLE);
                } else {
                    btnContinue.setEnabled(false);
                    tick_image.setVisibility(View.INVISIBLE);
                }
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