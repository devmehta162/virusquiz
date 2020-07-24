package com.dm.virusquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ScoreActivity extends AppCompatActivity {

    private TextView total;
    private Button doneBtn,shareBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        total=findViewById(R.id.total);
        doneBtn=findViewById(R.id.done_btn);

        shareBtn=findViewById(R.id.share_btn);
        loadAds();

        total.setText(String.valueOf(getIntent().getIntExtra("score",0))+"/"+String.valueOf(getIntent().getIntExtra("total",0)));
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = "I scored " + getIntent().getIntExtra("score",0) + " out of "+
                        getIntent().getIntExtra("total",0)+" in Covid-19 quiz app";

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT,"COVID-19 Challenge");
                shareIntent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(shareIntent,"share via"));
            }
        });
    }
    private void loadAds() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
