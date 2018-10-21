package io.github.harsh8398.quizapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    String personName;
    Uri photoURL;
    TextView pname;
    int imageSize;
    ImageView pphoto;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        personName = getIntent().getExtras().getString("name");

        pname = (TextView) findViewById(R.id.pname);
        pphoto = (ImageView) findViewById(R.id.pphoto);

        imageSize = getDip((int) getResources().getDimension(R.dimen.large_text));
        pname.setText("Hi,\n" + personName);
        if(getIntent().getExtras().getString("photo") != null) {
            photoURL = Uri.parse(getIntent().getExtras().getString("photo"));
            Picasso.get().load(photoURL)
                    .resize(imageSize, imageSize)
                    .transform(new CircleTransform()).into(pphoto);
        }
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, QuizStart.class);
        switch (v.getId()) {
            case R.id.java:
                intent.putExtra("lang", "java");
                break;
            case R.id.cpp:
                intent.putExtra("lang", "cpp");
                break;
            case R.id.python:
                intent.putExtra("lang", "python");
                break;
            case R.id.php:
                intent.putExtra("lang", "php");
                break;
        }
        startActivity(intent);
    }

    public int getDip(int pixel) {
        float scale = getBaseContext().getResources().getDisplayMetrics().density;
        return (int) (pixel * scale + 0.5f);
    }

    public void signOut(View view) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                });

    }
}
