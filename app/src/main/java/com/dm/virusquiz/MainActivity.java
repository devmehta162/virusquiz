package com.dm.virusquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    private Toast backToast;
    private long backPressedTime;
    private Button StartBtn,bookmarkBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartBtn=findViewById(R.id.start_btn);
        bookmarkBtn=findViewById(R.id.bookmarks_btn);

       // MobileAds.initialize(this);
        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    Intent intent = new Intent(MainActivity.this,CategoriesActivity.class);
                    startActivity(intent);


                } else {

                    Toast.makeText(MainActivity.this,"No Internet Connection!",Toast.LENGTH_LONG).show();



                }


            }
        });

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BookmarkActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime +2000> System.currentTimeMillis()){
            finishAffinity();
            backToast.cancel();
            super.onBackPressed();
            return;
        }else {
            backToast= Toast.makeText(MainActivity.this,"Press back again to exit",Toast.LENGTH_LONG);
            backToast.show();
        }
        backPressedTime  = System.currentTimeMillis();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Are you sure you want to exit the app?");
//        builder.setCancelable(false);
//
//        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                finishAffinity();
//
//            }
//        });
//        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
   }
    }

