package com.dm.virusquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.dm.virusquiz.R.drawable.rounded_button;

public class QuestionsActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    public static final String FILE_NAME ="COVID";
    public static final String KEY_NAME ="QUESTIONS";

    private InterstitialAd interstitialAd;
    private TextView question , noIndicator;
    private FloatingActionButton bookmarkBtn;
    private LinearLayout optionsContainer;
   private List<QuestionModel> list;
    private Button shareBtn,nextBtn;
    private int count=0;
    private int position=0;
    private int score=0;
    private String category;
    private int setNo;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private List<QuestionModel> bookmarkList;
    private int matchedQuestionPosition;




    private Dialog loadingDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);


        Toolbar toolbar = findViewById(R.id.set_toolbar);
        setSupportActionBar(toolbar);



        question = findViewById(R.id.question);
        noIndicator = findViewById(R.id.no_indicator);
        bookmarkBtn = findViewById(R.id.bookmark_btn);

        loadAds();

        optionsContainer = findViewById(R.id.options_container);
        shareBtn = findViewById(R.id.share_btn);
        nextBtn = findViewById(R.id.next_btn);

        preferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
        gson = new Gson();

        getBookmarks();

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMatch()){

                    bookmarkList.remove(matchedQuestionPosition);
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));

                }else {
                    bookmarkList.add(list.get(position));
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });
        category = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setNo", 1);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);


        list = new ArrayList<>();
        loadingDialog.show();
        myRef.child("SETS").child(category).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list.add(snapshot.getValue(QuestionModel.class));
                }
                if (list.size() > 0) {
                    for (int i = 0; i < 4; i++) {
                        optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                checkAnswer((Button)v);
                            }
                        });
                    }
                    playAnim(question, 0, list.get(position).getQuestion());

                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.7f);
                            position++;
                            enableOption(true);
                            if (position == list.size()) {
                                if (interstitialAd.isLoaded()){
                                    interstitialAd.show();
                                    return;
                                }
                                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                                intent.putExtra("score", score);
                                intent.putExtra("total", list.size());
                                startActivity(intent);
                                finish();
                                return;
                            }
                            //  position++;
                            count = 0;
                            playAnim(question, 0, list.get(position).getQuestion());
                        }
                    });
                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String body = "Question)"+list.get(position).getQuestion() + "\n"+
                                    "Option A:-"+ list.get(position).getOptionA() + "\n"+
                                    "Option B:-"+ list.get(position).getOptionB() + "\n"+
                                    "Option C:-"+ list.get(position).getOptionC() + "\n"+
                                    "Option D:-"+ list.get(position).getOptionD();

                                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"COVID-19 Challenge");
                            shareIntent.putExtra(Intent.EXTRA_TEXT,body);
                            startActivity(Intent.createChooser(shareIntent,"share via"));

                        }
                    });
                } else {
                    finish();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                loadingDialog.dismiss();
                finish();
            }
        });
    }
        @Override
        protected void onPause() {
            super.onPause();
            storeBookmarks();
        }


    private void playAnim(final View view, final int value, final String data){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                if (value==0 && count<4){
                    String option="";
                    if (count==0){
                        option= list.get(position).getOptionA();
                    }else if (count==1){
                        option= list.get(position).getOptionB();
                    }else if (count==2){
                        option= list.get(position).getOptionC();
                    }else if (count==3){
                        option= list.get(position).getOptionD();
                    }
                    playAnim(optionsContainer.getChildAt(count),0,option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            if (value==0){

                try {

                    ((TextView)view).setText(data);
                    noIndicator.setText(position+1+"/"+list.size());
                    if (modelMatch()){
                        bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                    }else {
                        bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                    }
                }catch (ClassCastException ex){
                    ((Button)view).setText(data);

                }

                view.setTag(data);
                    playAnim(view,1,data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    private void checkAnswer(Button selectedoption){

        enableOption(false);
        nextBtn.setEnabled(true);
        nextBtn.setAlpha(1);

        if (selectedoption.getText().toString().equals(list.get(position).getCorrectAns())){
            //correct
            score++;
            selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

        }else {
            //incorret
            selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));

           Button correctoption = optionsContainer.findViewWithTag(list.get(position).getCorrectAns());
            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

        }

    }
    private void enableOption(boolean enable){
        for (int i =0;i<4;i++){

            optionsContainer.getChildAt(i).setEnabled(enable);
            if (enable){
            //    optionsContainer.getChildAt(i).setBackgroundColor(Integer.parseInt("#fff"));

               optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFAFA")));
            }
        }
    }
    private void getBookmarks(){
        String json = preferences.getString(KEY_NAME,"");

        Type type = new TypeToken<List<QuestionModel>>(){}.getType();

        bookmarkList = gson.fromJson(json,type);
        if (bookmarkList==null){
            bookmarkList =new ArrayList<>();
        }

    }
    private Boolean modelMatch(){
        boolean matched= false;
        int i=0;
        for (QuestionModel model : bookmarkList){

            if(model.getQuestion().equals(list.get(position).getQuestion()) &&
                    model.getCorrectAns().equals(list.get(position).getCorrectAns()) &&
                    model.getSetNo() == list.get(position).getSetNo()){
                matched=true;
                matchedQuestionPosition=i;
            }
            i++;
        }
        return matched;
    }
    private void storeBookmarks(){

        String json = gson.toJson(bookmarkList);
        editor.putString(KEY_NAME,json);
        editor.commit();
    }
    private void loadAds() {
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial_Ad));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(QuestionsActivity.this, ScoreActivity.class);
                intent.putExtra("score", score);
                intent.putExtra("total", list.size());
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to quit the current quiz ?");
        builder.setCancelable(false);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(QuestionsActivity.this, CategoriesActivity.class);

                finish();
                startActivity(intent);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
