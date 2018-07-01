package com.example.licenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userUsername, userPassword, userDomain, userCode;
    private CardView regCardView;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ImageView userNewAcc;
    private ProgressBar progressBar;
    //private static Boolean result_domain = true;
    public static int result_domain = 1;

    String username, domain, password, code, domain_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();
        firebaseAuth = FirebaseAuth.getInstance();

        regCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    //Upload data to the database
                    String user_name = userUsername.getText().toString().trim();
                    String user_password = userPassword.getText().toString().trim();
                    progressBar.setVisibility(View.VISIBLE);
//
                    firebaseAuth.createUserWithEmailAndPassword(user_name, user_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {

                                        User user = new User(
                                                username
                                        );

                                        FirebaseDatabase.getInstance().getReference("Users"+domain)
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressBar.setVisibility(View.GONE);
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegistrationActivity.this, "Your account was created sucsessfully!", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(RegistrationActivity.this, "Temporarily we have a problem. Please try again later", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    } else {
                                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }
        });
        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });
    }

    private void setupUIViews () {
        userUsername = (EditText) findViewById(R.id.etUsername);
        userPassword = (EditText) findViewById(R.id.etPass);
        userDomain = (EditText) findViewById(R.id.etCompDomain);
        regCardView = (CardView) findViewById(R.id.cvReg);
        userLogin = (TextView) findViewById(R.id.tvLog);
        userCode = (EditText) findViewById(R.id.etCode);
        userNewAcc = (ImageView) findViewById(R.id.ivNewAcc);
        progressBar = findViewById(R.id.pbLoad);
        progressBar.setVisibility(View.GONE);
    }


    private Boolean validate () {
        Boolean result = true;

        username = userUsername.getText().toString();
        domain = userDomain.getText().toString();
        password = userPassword.getText().toString();
        code = userCode.getText().toString();
        domain_code = domain + "_" + code;
        validateDomain();

        if (username.isEmpty() || domain.isEmpty() || password.isEmpty() || code.isEmpty()) {
            result=false;
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches() && !username.isEmpty()) {
            Toast.makeText(this, "Incorect adress email!", Toast.LENGTH_SHORT).show();
            result = false;
        }

        if (password.length() < 8 && !password.isEmpty()) {
            Toast.makeText(this, "The password must contain at least 8 characters!", Toast.LENGTH_SHORT).show();
            result = false;
        }

        if(com.example.licenta.RegistrationActivity.result_domain == 0){
            Toast.makeText(this, "Company domain and code doesn't exist", Toast.LENGTH_SHORT).show();
            result = false;

        }

        return result;

    }
    private void validateDomain(){
        domain = userDomain.getText().toString();
        code = userCode.getText().toString();
        domain_code = domain + "_" + code;

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("DomainCompanies");
        ref.orderByChild("domain_code").equalTo(domain_code)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            //this.setVar(false);
                            com.example.licenta.RegistrationActivity.result_domain = 0;
                            userDomain.setError("Company domain and code doesn't exist");
                            userCode.setError("Company domain and code doesn't exist");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

    }
    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() != null) {
            Toast.makeText(RegistrationActivity.this, "You have already signed up on InviteMe", Toast.LENGTH_LONG).show();
        }
    }


}
