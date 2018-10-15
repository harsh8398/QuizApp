package io.github.harsh8398.quizapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SelectNoQue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_no_que);
    }

    public void startQuiz(View view) {
        String lang = getIntent().getExtras().getString("lang");

        RadioGroup diffGroup = (RadioGroup) findViewById(R.id.difficulty);
        int diffButtonID = diffGroup.getCheckedRadioButtonId();
        RadioButton diffButton = (RadioButton) diffGroup.findViewById(diffButtonID);
        String difficulty = (String) diffButton.getText();

        RadioGroup queGroup = (RadioGroup) findViewById(R.id.noque);
        int queButtonID = queGroup.getCheckedRadioButtonId();
        RadioButton queButton = (RadioButton) queGroup.findViewById(queButtonID);
        String noque = (String) queButton.getText();

        Intent intent = new Intent(this, QuizStart.class);
        intent.putExtra("lang", lang);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("noque", noque);
        startActivity(intent);
    }
}
