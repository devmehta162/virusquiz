package com.dm.virusquiz;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {
    private TextView appName;
    private List<CategoryModel> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//<activity android:name=".MainActivity">

        appName = findViewById(R.id.app_name);
      //  list =new ArrayList<>();
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myanim);
        appName.setAnimation(anim);

        new Thread() {
            public void run() {
               try {
                   sleep(1500);
                   Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                   startActivity(intent);
                   finish();
               }catch (InterruptedException e){
                   e.printStackTrace();
               }

            }



        }.start();

    }

}
