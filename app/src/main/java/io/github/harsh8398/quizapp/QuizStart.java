package io.github.harsh8398.quizapp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuizStart extends AppCompatActivity {

    List<Question> allQues = new ArrayList<Question>();
    int quid = 0;
    int score = 0;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lang = getIntent().getExtras().getString("lang");
        getSupportActionBar().setTitle(getString(getStringIdentifier(this, lang)));

        String filename = lang + ".json";
        getQuestions(filename);
        currentQuestion = allQues.get(quid);

        questionNo = (TextView)findViewById(R.id.question_no);

        txtQuestion = (TextView)findViewById(R.id.question);
        txtQuestion.setMovementMethod(new ScrollingMovementMethod());
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        // You want size to be 50% per EditText, so divide available height by 2.
        // Note: this is absolute height, does not take into consideration window decoration!
        int textHeight = size.y / 3;
        txtQuestion.setMaxHeight(textHeight);

        rda = (RadioButton)findViewById(R.id.opta);
        rdb = (RadioButton)findViewById(R.id.optb);
        rdc = (RadioButton)findViewById(R.id.optc);
        rdd = (RadioButton)findViewById(R.id.optd);
        butNext = (Button)findViewById(R.id.nxtbtn);
        butSubmit = (Button)findViewById(R.id.submitbtn);
        queProgress = (NumberProgressBar) findViewById(R.id.progress_question);

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
        if(currentQuestion.isAnswered() == -1) {
//            Toast.makeText(this, Integer.toString(currentQuestion.getId()), Toast.LENGTH_SHORT).show();
            questionNo.setText("Question " + Integer.toString(quid + 1) + " out of 30:");
            txtQuestion.setText(currentQuestion.getQuestion());
            rda.setText(currentQuestion.getOptA());
            rdb.setText(currentQuestion.getOptB());
            rdc.setText(currentQuestion.getOptC());
            rdd.setText(currentQuestion.getOptD());

            RadioGroup grp = (RadioGroup) findViewById(R.id.options);
            for (int i = 0; i < grp.getChildCount(); i++) {
                ((RadioButton) grp.getChildAt(i)).setTextColor(getResources().getColor(R.color.black));
            }
            grp.clearCheck();
            butSubmit.setEnabled(true);
            quid++;
        } else {
            questionNo.setText("Question " + Integer.toString(quid + 1) + " out of 30:");
            txtQuestion.setText(currentQuestion.getQuestion());
            rda.setText(currentQuestion.getOptA());
            rdb.setText(currentQuestion.getOptB());
            rdc.setText(currentQuestion.getOptC());
            rdd.setText(currentQuestion.getOptD());

            butSubmit.setEnabled(false);
            RadioGroup grp = (RadioGroup) findViewById(R.id.options);
            for (int i = 0; i < grp.getChildCount(); i++) {
                if(currentQuestion.getAnswer().equals(((RadioButton) grp.getChildAt(i)).getText())) {
                    ((RadioButton) grp.getChildAt(i)).setTextColor(getResources().getColor(R.color.green));
                } else {
                    ((RadioButton) grp.getChildAt(i)).setTextColor(getResources().getColor(R.color.black));
                }
            }
            grp.clearCheck();
            RadioButton answer = (RadioButton) grp.getChildAt(currentQuestion.isAnswered());
            answer.setChecked(true);
            if(!currentQuestion.getAnswer().equals(answer.getText())){
                answer.setTextColor(getResources().getColor(R.color.red));
            }
            quid = currentQuestion.getId() + 1;
        }
    }

    public String loadJSONFromAsset(String filename) {
        String json = null;
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
        score = (int) (((float) score/30) * 5);
        Intent intent = new Intent(this, ResultActivity.class);
        Bundle b = new Bundle();
        b.putInt("score", score);
        b.putInt("correct", correct);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public void submitClick(View view) {
        RadioGroup grp = (RadioGroup)findViewById(R.id.options);
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
                if(currentQuestion.getAnswer().equals(((RadioButton) grp.getChildAt(i)).getText())) {
                    ((RadioButton) grp.getChildAt(i)).setTextColor(getResources().getColor(R.color.green));
                }
            }
            if(currentQuestion.getAnswer().equals(answer.getText())){
                score++;
                correct++;
                Log.d("Score", "Your score: "+score);
            } else {
                answer.setTextColor(getResources().getColor(R.color.red));
            }
        }

    }

    public void prevClick(View view) {
        this.onBackPressed();
    }
}