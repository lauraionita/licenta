package com.example.licenta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mUsername, mPassword, mDomain;
    private TextView mRegister, mForgotPass, mPleaseWait;
    private CardView mLogCv;
    private static final String TAG = "LoginActivity";
    private Context mContext;
    private ProgressBar mProgressBar;
    public static int result_user = 1;
    //public static String user_id;
    public static String domain;
    static String LoggedIn_User_Email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mProgressBar = (ProgressBar) findViewById(R.id.pbWait);
        mPleaseWait = (TextView) findViewById(R.id.tvWait);
        mUsername = (EditText) findViewById(R.id.etUser);
        mPassword = (EditText) findViewById(R.id.etPass);
        mDomain = (EditText) findViewById(R.id.etDomain);
        mRegister = (TextView) findViewById(R.id.tvReg);
        mForgotPass = (TextView) findViewById(R.id.tvForgotPass);
        mLogCv = (CardView) findViewById(R.id.cvReg);

        mContext = MainActivity.this;
        Log.d(TAG, "onCreate: started.");

        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        //setupFirebaseAuth();
        init();


    }
    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null.");

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void init(){

        //initialize the button for logging in
        mLogCv = (CardView) findViewById(R.id.cvReg);
        mLogCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in.");

                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                com.example.licenta.MainActivity.domain = mDomain.getText().toString();

                if(isStringNull(username) && isStringNull(password) && isStringNull(domain)){
                    Toast.makeText(mContext, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);



                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "signInWithEmail:failed", task.getException());

                                        Toast.makeText(MainActivity.this, "user inexistent",
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        mPleaseWait.setVisibility(View.GONE);
                                    }
                                    else{
                                        // aici trec pe pag userului
                                        try {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            final String user_id = user.getUid();

                                            LoggedIn_User_Email = user.getEmail().trim();
                                            OneSignal.sendTag("User_id", LoggedIn_User_Email);

                                            SharedPreferences.Editor editor = getSharedPreferences("MainActivity", MODE_PRIVATE).edit();
                                            editor.putString("idUser", user_id);
                                            editor.putString("domain", com.example.licenta.MainActivity.domain);
                                            editor.apply();

                                            final String users = "Users" + com.example.licenta.MainActivity.domain;

                                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
                                            ref.child(users).child(user_id)
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            if (!dataSnapshot.exists()) {
                                                                //this.setVar(false);
                                                                com.example.licenta.MainActivity.result_user = 0;
                                                                Toast.makeText(mContext, "domeniul nu exista " + user_id + " " +users, Toast.LENGTH_SHORT).show();
                                                                //Toast.makeText(mContext, "acum nu merge"+com.example.licenta.MainActivity.domain, Toast.LENGTH_SHORT).show();
                                                                mProgressBar.setVisibility(View.GONE);
                                                                mPleaseWait.setVisibility(View.GONE);
                                                                mAuth.signOut();

                                                            }else {
                                                                Log.d(TAG, "onComplete: success. email is verified.");
                                                                Toast.makeText(mContext, "acum merge"+com.example.licenta.MainActivity.domain, Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(MainActivity.this, ProfileUser.class);
                                                                startActivity(intent);
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {}
                                                    });
                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                        }







                                    }


                                }
                            });
                }

            }
        });

        TextView mReg = (TextView) findViewById(R.id.tvReg);
        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to register screen");
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

         /*
         If the user is logged in then navigate to HomeActivity and call 'finish()'
          */
//        if(mAuth.getCurrentUser() != null){
//            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }
//    private void setupFirebaseAuth(){
//        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
//
//        mAuth = FirebaseAuth.getInstance();
//
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
////                com.example.licenta.MainActivity.user_id = user.getUid();
//
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                }
//                // ...
//            }
//        };
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }



}