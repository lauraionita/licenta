package com.example.licenta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;;import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BlankFragment extends Fragment {
    int nrSection;
    private Context mCtx;
    private List<UserCompany> userCompanyList, firstList;
    private AtendeesAdapter adapter;
    //protected UserCompany userCompany = new UserCompany();
    public BlankFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public int getPage (){
        return this.nrSection;
    }
    public void setPage (int i){
        this.nrSection = i;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        mCtx = getContext();
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_atendees);
        rv.setHasFixedSize(true);
        userCompanyList = new ArrayList<>();
        firstList = new ArrayList<>();
        adapter = new AtendeesAdapter(getContext(),userCompanyList);
        rv.setAdapter(adapter);

       LinearLayoutManager llm = new LinearLayoutManager(getActivity());
       // LinearLayoutManager llm = new LinearLayoutManager(BlankFragment.this);
        rv.setLayoutManager(llm);

        if (getPage()==0){
            getAtendees("yes");

        }else if(getPage()==1) {
            getAtendees("no");
        }

        return rootView;
    }

    private void getAtendees (final String response){
        String idMeeting = getIdMeeting();
        String domain = getDomainCompany();
        domain = domain.substring(0, 1).toUpperCase() + domain.substring(1).toLowerCase();
        Query query = FirebaseDatabase.getInstance().getReference("Atendees" + domain).child(idMeeting);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userCompanyList.clear();
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserCompany userCompany = snapshot.getValue(UserCompany.class);
                        String idUser = snapshot.getKey();
                        setIdUser(i, idUser);
                        //String response = userCompany.respond;
                        if(userCompany.respond.trim().equals(response)){
                            userCompanyList.add(userCompany);
                            i = i+1;
                        }
                    }
                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(mCtx, "No room availables", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(mCtx, ProfileUser.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getIdMeeting ()
    {
        SharedPreferences prefs = mCtx.getSharedPreferences("ViewMeetingAdapter", MODE_PRIVATE);
        String adapterPosition= prefs.getString("adapterPosition", "No name defined");

        SharedPreferences pref = mCtx.getSharedPreferences("ViewMeeting", MODE_PRIVATE);
        String idMeeting= pref.getString(adapterPosition, "No name defined");

        return idMeeting;
    }


    private String getDomainCompany ()
    {
        SharedPreferences prefs = mCtx.getSharedPreferences("MainActivity", MODE_PRIVATE);
        String domain = prefs.getString("domain", "No name defined");

        return domain;
    }

    private void setIdUser (int unique, String idUser){
        String idUnique = Integer.toString(unique);
        SharedPreferences.Editor editor = mCtx.getSharedPreferences("BlankFragment", MODE_PRIVATE).edit();
        editor.putString(idUnique, idUser);
        editor.apply();
    }

}