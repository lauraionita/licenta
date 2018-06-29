package com.example.licenta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.SearchManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPeople extends AppCompatActivity {

    private EditText mSearchField;
    private ImageButton mSearchBtn;

    private RecyclerView recyclerView;
    private AddPeopleAdapter adapter;
    private List<UserCompany> userCompanyList;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this).setNotificationOpenedHandler(new MyNotificationOpenedHandler(this)).init();

        setContentView(R.layout.activity_add_people);

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mSearchField = (EditText) findViewById(R.id.et_search_field);
       // mSearchBtn = (ImageButton) findViewById(R.id.img_search_btn);

        recyclerView = findViewById(R.id.rvAddPeople);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        userCompanyList = new ArrayList<>();
        adapter = new AddPeopleAdapter(this, userCompanyList);
        recyclerView.setAdapter(adapter);

        String domain = getDomainCompany();
        Query query = FirebaseDatabase.getInstance().getReference("Users"+getDomainCompany()).orderByChild("username");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userCompanyList.clear();
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserCompany userCompany = snapshot.getValue(UserCompany.class);
                        String idUser = snapshot.getKey();
                        String email = userCompany.username;
                        setIdUser(i, idUser, email);
                        userCompanyList.add(userCompany);
                        i = i+1;
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(AddPeople.this, "No room availables", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddPeople.this, ProfileUser.class));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //mSearchField.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener()
//        mSearchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//               String searchText = mSearchField.getText().toString();
//            }
//        });

        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s);

            }
        });
//        mSearchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String text) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String text) {
//                adapter.getFilter().filter(text);
//                adapter.notifyDataSetChanged();
//
//                return true;
//            }
//        });
    }

    private void filter(String text) {

        for (UserCompany userCompany : userCompanyList) {
            if (userCompany.username.toLowerCase().contains(text.toLowerCase())) {
                userCompanyList.clear();
                userCompanyList.add(userCompany);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private String getDomainCompany ()
    {
        SharedPreferences prefs = getSharedPreferences("MainActivity", MODE_PRIVATE);
        String domain = prefs.getString("domain", "No name defined");
        return domain;
    }
    private void setIdUser (int unique, String idUser, String email){
        String idUnique = Integer.toString(unique);
        SharedPreferences.Editor editor = getSharedPreferences("AddPeople", MODE_PRIVATE).edit();
        editor.putString(idUnique, idUser);
        editor.putString(idUser, email);
        editor.apply();
    }


    public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        private AddPeople application;
        private String stringEmail, stringFilter;

        public MyNotificationOpenedHandler(AddPeople application) {
            this.application = application;
        }
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            final JSONObject data = result.notification.payload.additionalData;

           // try {
                stringEmail= data.optString("foo", null);
                stringFilter = data.optString("contents", null);
               // stringEmail = email.toString();
               // stringFilter = filter.toString();
           // } catch (JSONException e) {
                //e.printStackTrace();


            String open;

            if (data != null) {
                open = data.optString("open", null);

                if (open != null && open.equals("id2")) {
                    Log.i("OneSignalExample", "customkey set with value: " + open);
                    Intent intent = new Intent(AddPeople.this, ProfileUser.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    AddPeople.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(AddPeople.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                    AddPeople.this.startActivity(intent);
                }

            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken){
                Log.i("OneSignalExample", "Button pressed with id: " + stringEmail );


                if (result.action.actionID.equals("id1")) {

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBodyText = "hello";
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                    AddPeople.this.startActivity(Intent.createChooser(sharingIntent, "Share via"));

                }else if (result.action.actionID.equals("id2")) {
                    Toast.makeText(AddPeople.this, "ActionTwo Button was pressed"+ stringEmail + stringFilter, Toast.LENGTH_LONG).show();
                   // startApp();
                    confirmMeeting();
                }
            }
        }
        private void startApp() {
            Intent intent = new Intent(application, ProfileUser.class)
                    .setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            application.startActivity(intent);
        }

        private void confirmMeeting(){
            //String idMeeting = getIdMeeting(position);
            //String idUser = getId
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Map<String,Object> updates= new HashMap<String,Object>();
            //updates.put("/RoomsMeeting"+domainCompany+"/"+idMeeting+"/",null);
            updates.put("/AtendeesCentric/-LFNRZ3e44sUO82IMJTV/ajsMBfu3weXCW5DTCOj9mIeVTeG2/respond/", "yes");


            ref.updateChildren(updates, new DatabaseReference.CompletionListener(){
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Error updating data: " + databaseError.getMessage());
                       // Toast.makeText(mCtx, "Item Removed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // FirebaseDatabase.getInstance().getReference(dataBase).child(getIdMeeting(getAdapterPosition()).toString()).removeValue();
        }
    }

    }

