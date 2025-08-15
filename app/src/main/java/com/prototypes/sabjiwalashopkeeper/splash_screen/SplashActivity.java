package com.prototypes.sabjiwalashopkeeper.splash_screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prototypes.sabjiwalashopkeeper.MainActivity;
import com.prototypes.sabjiwalashopkeeper.R;
import com.prototypes.sabjiwalashopkeeper.login.LoginActivity;
import com.prototypes.sabjiwalashopkeeper.login.SignUpActivity;


public class SplashActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        relativeLayout = findViewById(R.id.splashRelative);
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_splash);
        relativeLayout.startAnimation(animation);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        ScreenChange();
    }
    public void ScreenChange(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fAuth.getCurrentUser() != null){
                    String userId = fAuth.getCurrentUser().getUid();
                    fStore.collection("SabjiWale")
                            .document(userId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.getResult().getData() != null){
                                        Intent intent = new Intent(SplashActivity.this,
                                                MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Intent intent = new Intent(SplashActivity.this,
                                                SignUpActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },5000);
    }
}