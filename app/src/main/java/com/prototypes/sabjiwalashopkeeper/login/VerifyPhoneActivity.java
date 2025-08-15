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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prototypes.sabjiwalashopkeeper.MainActivity;
import com.prototypes.sabjiwalashopkeeper.R;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    private EditText vcode;
    private Button submit;
    private Button resend;
    private TextView timertext;
    FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("language", false)) {
            setAppLanguage("hi");
        }else {
            setAppLanguage("en");
        }
        setContentView(R.layout.activity_verify_phone);

        vcode = findViewById(R.id.code);
        submit = findViewById(R.id.submit);
        resend=findViewById(R.id.resend);
        timertext=findViewById(R.id.timertext);

        Intent intent = getIntent();
        String mobile = intent.getStringExtra("phonenumber");
    
        SharedPreferences fragment = getSharedPreferences("selectedFragment", MODE_PRIVATE);
        SharedPreferences.Editor editor = fragment.edit();
        editor.putString("MainActivity", "currentOrdersFragment");
        editor.apply();

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        initFireBaseCallbacks();
        sendVerificationCode(mobile);
        starttimer();

        vcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 6) {
                    submit.setEnabled(true);
                } else {
                    submit.setEnabled(false);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = vcode.getText().toString().trim();
                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(mobile,mResendToken);
                starttimer();
            }
        });

    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                50,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    void initFireBaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Toast.makeText(VerifyPhoneActivity.this, "Verification Complete", Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(VerifyPhoneActivity.this, "Verification Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(VerifyPhoneActivity.this, "Code Sent", Toast.LENGTH_LONG).show();
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerifyPhoneActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //String userId = task.getResult().getUser().getUid();
                            String userId = "IUvAYRhZfahjB5mh6a7yL1znfU72";
                            DocumentReference df = fStore.collection("SabjiWale").document(userId);
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.getResult().getData() == null){
                                        Intent intent = new Intent(VerifyPhoneActivity.this, SignUpActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Intent intent = new Intent(VerifyPhoneActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Something is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });


    }
    public void starttimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            int second = 60;

            @Override
            public void run() {
                if (second <= 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timertext.setVisibility(View.INVISIBLE);
                            resend.setEnabled(true);
                            timer.cancel();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timertext.setText("00:" + second--);
                            resend.setEnabled(false);
                        }
                    });
                }

            }
        }, 0, 1000);
    }
    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        signInWithPhoneAuthCredential(credential);
    }

    public void setAppLanguage(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = this.getResources().getConfiguration();
        config.setLocale(locale);
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
    }
}