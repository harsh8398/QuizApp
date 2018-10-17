package io.github.harsh8398.quizapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class QuizStart extends AppCompatActivity {

    List<Question> allQues = new ArrayList<>();
    int quid = 0;
    float score = 0;
    int correct = 0;
    int que_submitted = 0;
    Question currentQuestion;

    TextView txtQuestion;
    TextView questionNo;
    RadioButton rda,rdb,rdc, rdd;
    Button butNext;
    String lang = "";
    Button butSubmit;
    NumberProgressBar queProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_start);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        lang = Objects.requireNonNull(getIntent().getExtras()).getString("lang");
        getSupportActionBar().setTitle(getString(getStringIdentifier(this, lang)));

        String filename = lang + ".json";
        getQuestions(filename);
        currentQuestion = allQues.get(quid);

        questionNo = findViewById(R.id.question_no);

        txtQuestion = findViewById(R.id.question);
        txtQuestion.setMovementMethod(new ScrollingMovementMethod());
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        // You want size to be 50% per EditText, so divide available height by 2.
        // Note: this is absolute height, does not take into consideration window decoration!
        int textHeight = size.y / 3;
        txtQuestion.setMaxHeight(textHeight);

        rda = findViewById(R.id.opta);
        rdb = findViewById(R.id.optb);
        rdc = findViewById(R.id.optc);
        rdd = findViewById(R.id.optd);
        butNext = findViewById(R.id.nxtbtn);
        butSubmit = findViewById(R.id.submitbtn);
        queProgress = findViewById(R.id.progress_question);

        setQuestionView();
    }

    public static int getStringIdentifier(Context context, String name) {
        return context.getResources().getIdentifier(name, "string", context.getPackageName());
    }

    public boolean onOptionsItemSelected(final MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuizStart.this);

                alertDialog.setTitle("Confirm quit?");

                alertDialog.setMessage("You will lose your progress by pressing YES.");
                alertDialog.setIcon(R.drawable.alert);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        finish();
                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {
        if(currentQuestion.getId() == 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuizStart.this);

            // Setting Dialog Title
            alertDialog.setTitle("Confirm quit?");

            // Setting Dialog Message
            alertDialog.setMessage("You will lose your progress by pressing YES.");

            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.alert);

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    QuizStart.super.onBackPressed();
                }
            });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            // Showing Alert Message
            alertDialog.show();

        } else {
            quid = currentQuestion.getId() - 1;
            currentQuestion = allQues.get(quid);
//            Toast.makeText(this, "quid: " + quid, Toast.LENGTH_SHORT).show();
            setQuestionView();
        }
    }

    private void setQuestionView() {
        txtQuestion.scrollTo(0, 0);
        String qnostr = "Question " + Integer.toString(quid + 1) + " out of 30:";
        if(currentQuestion.isAnswered() == -1) {
            questionNo.setText(qnostr);
            txtQuestion.setText(currentQuestion.getQuestion());
            rda.setText(currentQuestion.getOptA());
            rdb.setText(currentQuestion.getOptB());
            rdc.setText(currentQuestion.getOptC());
            rdd.setText(currentQuestion.getOptD());

            RadioGroup grp = findViewById(R.id.options);
            for (int i = 0; i < grp.getChildCount(); i++) {
                ((RadioButton) grp.getChildAt(i)).setTextColor(Color.BLACK);
            }
            grp.clearCheck();
            butSubmit.setEnabled(true);
            quid++;
        } else {
            questionNo.setText(qnostr);
            txtQuestion.setText(currentQuestion.getQuestion());
            rda.setText(currentQuestion.getOptA());
            rdb.setText(currentQuestion.getOptB());
            rdc.setText(currentQuestion.getOptC());
            rdd.setText(currentQuestion.getOptD());

            butSubmit.setEnabled(false);
            RadioGroup grp = findViewById(R.id.options);
            for (int i = 0; i < grp.getChildCount(); i++) {
                if(currentQuestion.getAnswer().contentEquals(((RadioButton) grp.getChildAt(i)).getText())) {
                    ((RadioButton) grp.getChildAt(i)).setTextColor(Color.GREEN);
                } else {
                    ((RadioButton) grp.getChildAt(i)).setTextColor(Color.BLACK);
                }
            }
            grp.clearCheck();
            RadioButton answer = (RadioButton) grp.getChildAt(currentQuestion.isAnswered());
            answer.setChecked(true);
            if(!currentQuestion.getAnswer().contentEquals(answer.getText())){
                answer.setTextColor(Color.RED);
            }
            quid = currentQuestion.getId() + 1;
        }
    }

    public String loadJSONFromAsset(String filename) {
        String json;
        try {
            InputStream is = this.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void getQuestions(String filename) {
        try {
            JSONArray m_jArry = new JSONArray(loadJSONFromAsset(filename));

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Log.d("Details-->", jo_inside.getString("question"));
                Question q = new Question();
                q.setId(jo_inside.getInt("id"));
                q.setQuestion(jo_inside.getString("question"));
                q.setOptA(jo_inside.getString("a"));
                q.setOptB(jo_inside.getString("b"));
                q.setOptC(jo_inside.getString("c"));
                q.setOptD(jo_inside.getString("d"));
                q.setAnswer(jo_inside.getString("answer"));
                allQues.add(q);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void nxtClick(View view) {
        if(quid<30){
            currentQuestion = allQues.get(quid);
            setQuestionView();
        }else{
            if(que_submitted < 30) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(QuizStart.this);

                // Setting Dialog Title
                alertDialog.setTitle("Are you sure you want to end the quiz?");

                // Setting Dialog Message
                alertDialog.setMessage("You forgot to submit one or more questions.");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.alert);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        endQuiz();
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            } else {
                endQuiz();
            }
        }
    }

    private void endQuiz() {
        score = (score/30) * 5;
        Intent intent = new Intent(this, ResultActivity.class);
        Bundle b = new Bundle();
        b.putFloat("score", score);
        b.putInt("correct", correct);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public void submitClick(View view) {
        RadioGroup grp = findViewById(R.id.options);
        if (grp.getCheckedRadioButtonId() == -1)
        {
            Toast.makeText(this, "Please select one of the above options", Toast.LENGTH_SHORT).show();
        }
        else
        {
            butSubmit.setEnabled(false);
            queProgress.setProgress(++que_submitted);
            RadioButton answer = findViewById(grp.getCheckedRadioButtonId());
            currentQuestion.setAnswered(grp.indexOfChild(answer));

            for(int i = 0; i < grp.getChildCount(); i++){
                if(currentQuestion.getAnswer().contentEquals(((RadioButton) grp.getChildAt(i)).getText())) {
                    ((RadioButton) grp.getChildAt(i)).setTextColor(Color.GREEN);
                }
            }
            if(currentQuestion.getAnswer().contentEquals(answer.getText())){
                score++;
                correct++;
                Log.d("Score", "Your score: "+score);
            } else {
                answer.setTextColor(Color.RED);
            }
        }

    }

    public void prevClick(View view) {
        this.onBackPressed();
    }
}