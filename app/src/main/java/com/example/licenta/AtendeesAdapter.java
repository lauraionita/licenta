package com.example.licenta;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class AtendeesAdapter extends RecyclerView.Adapter<AtendeesAdapter.MyViewHolder> {
    private Context mCtx;
    private List<UserCompany> userCompanyList;
    private int position;


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public CardView mCardViewRemove;
        public TextView mTextView;

        private void removeAtendee()

        {   final String idMeeting = getIdMeeting();
            final String idUser = getIdUser(position);
            String domain = getDomainCompany();
            domain = domain.substring(0, 1).toUpperCase() + domain.substring(1).toLowerCase();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Atendees" + domain);
            ref.child(idMeeting).child(idUser).removeValue();
            Toast.makeText(mCtx, "User removed from meeting!", Toast.LENGTH_SHORT).show();


                    //.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    dataSnapshot.getRef().removeValue();
//                    Toast.makeText(mCtx, "User removed from meeting!", Toast.LENGTH_SHORT).show();
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
        }



        public MyViewHolder(View v){
            super(v);
            mTextView = (TextView) v.findViewById(R.id.tv_atendees);
            mCardViewRemove = (CardView)v.findViewById(R.id.cv_Remove);
            mCardViewRemove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    removeAtendee();
                    userCompanyList.remove(position);
                    //notifyItemRemoved( position );
                    notifyItemRemoved(position);
                   // notifyItemRangeChanged(position,userCompanyList.size());
                }
            });
        }

    }

    public AtendeesAdapter (Context mCtx, List<UserCompany> userCompanyList) {
        this.mCtx = mCtx;
        this.userCompanyList = userCompanyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_atendees, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
        final UserCompany userCompany = userCompanyList.get(position);
        holder.mTextView.setText(userCompany.username);

    }

    @Override
    public int getItemCount() { return userCompanyList.size(); }

    private String getIdMeeting ()
    {
        SharedPreferences prefs = mCtx.getSharedPreferences("ViewMeetingAdapter", MODE_PRIVATE);
        String adapterPosition= prefs.getString("adapterPosition", "No name defined");

        SharedPreferences pref = mCtx.getSharedPreferences("ViewMeeting", MODE_PRIVATE);
        String idMeeting= pref.getString(adapterPosition, "No name defined");

        return idMeeting;
    }

    private String getIdUser (int i)
    {
        String idUnique = Integer.toString(i);
        SharedPreferences prefs = mCtx.getSharedPreferences("BlankFragment", MODE_PRIVATE);
        String idUser= prefs.getString(idUnique, "No name defined");

        return idUser;
    }
    private String getDomainCompany ()
    {
        SharedPreferences prefs = mCtx.getSharedPreferences("MainActivity", MODE_PRIVATE);
        String domain = prefs.getString("domain", "No name defined");

        return domain;
    }

}