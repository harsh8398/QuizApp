package io.github.harsh8398.quizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setTitle("Result");

        TextView scoreTxtView = (TextView) findViewById(R.id.score);
        RatingBar ratingBar = (RatingBar)findViewById(R.id.score_rating);
        TextView textmoji = (TextView)findViewById(R.id.text_emoji);
        TextView detailResult = (TextView) findViewById(R.id.detail_result);


        Bundle b = getIntent().getExtras();
        float score = Float.parseFloat(String.format("%.1f", b.getFloat("score")));
        int correct = b.getInt("correct");
        String detailres = "Correctly Answered: " + correct + "/30";
        ratingBar.setRating(score);
        scoreTxtView.setText(String.valueOf(score));
        detailResult.setText(detailres);

        if(score <= 1){
            textmoji.setText(R.string.textmoji_throwing_table);
        }else if(score <= 2){
            textmoji.setText(R.string.textmoji_angry);
        }else if(score <= 3){
            textmoji.setText(R.string.textmoji_meh);
        }else if(score <= 4){
            textmoji.setText(R.string.textmoji_average);
        }else if(score <= 5){
            textmoji.setText(R.string.textmoji_yeah);
        }
    }
}
