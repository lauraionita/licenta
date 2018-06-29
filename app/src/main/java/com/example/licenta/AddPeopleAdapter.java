package com.example.licenta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.example.licenta.MainActivity.domain;


public class AddPeopleAdapter extends RecyclerView.Adapter<AddPeopleAdapter.AddPeopleViewHolder> implements Filterable {

    private Context mCtx;
    private List<UserCompany> userCompanyList;
    protected List<UserCompany> originalList;
    protected List<String> userCompanyDup;
    protected Boolean result = false;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    private String message, idMeeting, idUser;
    private String username;
    private List <String> userInfo;
    protected Meeting meeting;
    //private final boolean[] mCheckedState = new boolean[userCompanyList.size()];

    public AddPeopleAdapter(Context mCtx, List<UserCompany> userCompanyList) {
        this.originalList=userCompanyList;
        this.mCtx = mCtx;
        this.userCompanyList = userCompanyList;
        this.userCompanyDup = new ArrayList<String>();
        this.userInfo = new ArrayList<>();
        this.meeting = new Meeting();
    }

    @NonNull
    @Override
    public AddPeopleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_add_people, parent, false);
        return new AddPeopleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddPeopleViewHolder holder, final int position) {

        final UserCompany userCompany = userCompanyList.get(position);
        holder.textViewUsername.setText("Username: "+ userCompany.username);
        holder.cardViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendNotification();
                view.startAnimation(buttonClick);

                idMeeting = getIdMeeting();
                final String idUser = getIdUser(position).get(0);
                final String email = getIdUser(position).get(1);

                String get_domain = getDomainCompany();
                get_domain = get_domain.substring(0,1).toUpperCase() + get_domain.substring(1).toLowerCase();
                final String domain = get_domain;
                requestMeetingDetails(idMeeting, email);


                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Atendees"+domain);
                ref.child(idMeeting).child(idUser)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Toast.makeText(mCtx, "User already added to the meeting", Toast.LENGTH_SHORT).show();
                                    //insertAtendee(idUser, domain1, idMeeting, userCompany);
                                } else {
                                    insertAtendee(idUser, domain, idMeeting, userCompany);
                                    Toast.makeText(mCtx, "User added to the meeting", Toast.LENGTH_SHORT).show();

                                    String body_message = "You've been invited to a meeting on "+ meeting.date  +" at " + meeting.startMeeting;
                                    sendNotification(email, body_message);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }

        });

    }
    @Override
    public int getItemCount() {
        return userCompanyList.size();
    }

    class AddPeopleViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername;
        CardView cardViewAdd;

        public AddPeopleViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUsername = itemView.findViewById(R.id.et_people_username);
            cardViewAdd = itemView.findViewById(R.id.cvAdd);
        }
    }

    private void insertAtendee (String idUser, String domain, String idMeeting, UserCompany userCompany){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Atendees"+domain);
        ref.child(idMeeting).child(idUser).setValue(userCompany);
    }

    private void setMeetingDetails(String name, String date, String startMeeting, String endMeeting, String idUser ) {
        this.meeting.name = name;
        this.meeting.date = date;
        this.meeting.startMeeting = startMeeting;
        this.meeting.endMeeting = endMeeting;
        this.meeting.idUser = idUser;
    }




    private void requestMeetingDetails(String idMeeting, final String email) {
        domain = domain.substring(0, 1).toUpperCase() + domain.substring(1).toLowerCase();

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("RoomsMeeting"+domain);
        ref.child(idMeeting).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        meeting = dataSnapshot.getValue(Meeting.class);
                        setMeetingDetails(meeting.name, meeting.date, meeting.startMeeting, meeting.endMeeting, meeting.idUser);
                        String date = meeting.date;
                        String id = dataSnapshot.getKey();


                } else {
                    Toast.makeText(mCtx, "No details", Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userCompanyList = (List<UserCompany>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<UserCompany> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = originalList;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected List<UserCompany> getFilteredResults(String constraint) {
        List<UserCompany> results = new ArrayList<>();

        for (UserCompany item : userCompanyList) {
            if (item.username.toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }

    private String getIdMeeting ()
    {
        SharedPreferences prefs = mCtx.getSharedPreferences("ViewMeetingAdapter", MODE_PRIVATE);
        String adapterPosition= prefs.getString("adapterPosition", "No name defined");

        SharedPreferences pref = mCtx.getSharedPreferences("ViewMeeting", MODE_PRIVATE);
        String idMeeting= pref.getString(adapterPosition, "No name defined");

        return idMeeting;
    }

    private List<String> getIdUser (int i)
    {
        String idUnique = Integer.toString(i);
        SharedPreferences prefs = mCtx.getSharedPreferences("AddPeople", MODE_PRIVATE);
        String idUser= prefs.getString(idUnique, "No name defined");
        String email = prefs.getString(idUser, "No name defined");
        userInfo.add(idUser);
        userInfo.add(email);

        return userInfo;
    }

    private String getDomainCompany ()
    {
        SharedPreferences prefs = mCtx.getSharedPreferences("MainActivity", MODE_PRIVATE);
        String domain = prefs.getString("domain", "No name defined");

        return domain;
    }

    private void sendNotification(final String email, final String message )
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String send_email;

                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    if (MainActivity.LoggedIn_User_Email.equals("ana@gmail.com")) {
                        send_email = "ana@gmail.com";
                        //exit();
                    } else {
                        send_email = "ana@gmail.com";
                    }

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic YWI3MDNmYzUtYzllYS00ZWUxLWEwYjUtYmM3ZDI1NzliYjkx");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"010e6132-c8d8-4ee2-a389-c974f85218a2\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_id\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \""+ message +"\"},"
                                + "\"buttons\": [{\"id\": \"id1\", \"text\":\"button1\", \"icon\":\"ic_launcher\"}, {\"id\": \"id2\", \"text\":\"button2\", \"icon\":\"ic_launcher\"}]"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }



}


