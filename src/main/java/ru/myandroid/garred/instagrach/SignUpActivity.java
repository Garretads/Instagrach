package ru.myandroid.garred.instagrach;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity implements View.OnKeyListener{
EditText usernameView;
EditText emailView;
EditText passwordView;
EditText passwordRepeatView;
private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        usernameView = findViewById(R.id.usernameSignup);
        emailView = findViewById(R.id.emailSignup);
        passwordView = findViewById(R.id.passwordSignup);
        passwordRepeatView = findViewById(R.id.passwordSignupRepeat);
        passwordRepeatView.setOnKeyListener(this);
        mAuth = FirebaseAuth.getInstance();

    }

    public void signup(View view) {
        MainActivity.hideKeyboard(this);
        String[] userCredential = new String[4];
        userCredential[0] = usernameView.getText().toString();
        userCredential[1] = emailView.getText().toString();
        userCredential[2] = passwordView.getText().toString();
        userCredential[3] = passwordRepeatView.getText().toString();
        if (MainActivity.hasNetworkConnection(getApplicationContext())) {
            if (!userCredential[0].equals("") && !userCredential[1].equals("") && !userCredential[2].equals("")) {
               // if (!ParseUtils.isUserExists(userCredential[0])) {
                    if (userCredential[2].equals(userCredential[3])) {
                        //ParseUser user = new ParseUser();
                        mAuth.createUserWithEmailAndPassword(userCredential[1], userCredential[2])
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("SignUp", "createUserWithEmail:success");
                                            //FirebaseUser user = mAuth.getCurrentUser();
                                            //updateUI(user);
                                            Toast.makeText(getApplicationContext(), "User " + mAuth.getCurrentUser().getEmail() + " registered successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                            //updateUI(null);
                                        }
                                    }
                                });
                    } else
                        Toast.makeText(getApplicationContext(), "Password didn't match", Toast.LENGTH_SHORT).show();
               // } else
              //      Toast.makeText(getApplicationContext(), "User " + userCredential[0] + " already exist", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
            signup(view);
        return false;
    }
}
