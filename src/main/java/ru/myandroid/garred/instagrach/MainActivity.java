package ru.myandroid.garred.instagrach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener {
private EditText userEmail;
private EditText password;
RelativeLayout relativeLayout;
private StorageReference mStorageRef;
protected static FirebaseAuth mAuth;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm.isActive())
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        userEmail = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        mAuth = FirebaseAuth.getInstance();
        relativeLayout = findViewById(R.id.relativeLayoutMain);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(MainActivity.this);
            }
        });

        password.setOnKeyListener(this);
        //ParseUser.logOut();
        mAuth.signOut();

       if (mAuth.getCurrentUser() != null) {
           Log.i("Current user", "User "+mAuth.getCurrentUser().getDisplayName()+" logged in");
       }
       else
           Log.i("Current user", "User not logged in");
    }

    public void registration(View view) {
        Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
        startActivity(intent);
    }

    public void login(View view) {
        hideKeyboard(this);
        if (hasNetworkConnection(getApplicationContext())) {
            if (!userEmail.getText().toString().equals("") && !password.getText().toString().equals("")) {

                    //ParseUser.logIn(userEmail.getText().toString(), password.getText().toString());
                    mAuth.signInWithEmailAndPassword(userEmail.getText().toString(),password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                if (task != null)
                                    Toast.makeText(getApplicationContext(), "User Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(MainActivity.this, GalleryFeedActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                    //Toast.makeText(getApplicationContext(),"Current user is "+ParseUser.getCurrentUser().getUsername(),Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)
            login(view);
        return false;
    }

    static boolean hasNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
