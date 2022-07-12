package com.akif.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.akif.blooddonationapp.adapter.userAdapter;
import com.akif.blooddonationapp.model.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class category_selectedActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<user> userList;
    private userAdapter userAdapter;
    private String title="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selected);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView=findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        userList=new ArrayList<>();
        userAdapter=new userAdapter(category_selectedActivity.this, userList);
        if(getIntent().getExtras()!=null){
            title=getIntent().getStringExtra("group");
            getSupportActionBar().setTitle("Kan Grubu"+" "+title);


            if(title.equals("Eşleşenler")){
                getCompatibleUser();
                getSupportActionBar().setTitle("Eşleşenler");
            }
            else{
                readUsers();
            }


        }
    }

    private void getCompatibleUser() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result;
                String type=snapshot.child("type").getValue().toString();
                if(type.equals("donor")){
                    result="recipient";
                }
                else{
                    result="donor";
                }
                String bloodgroup=snapshot.child("bloodgroup").getValue().toString();

                DatabaseReference reference =FirebaseDatabase.getInstance().getReference()
                        .child("users");
                Query query=reference.orderByChild("search").equalTo(result+bloodgroup);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            user user=dataSnapshot.getValue(user.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });


    }

    private void readUsers() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String result;
                String type=snapshot.child("type").getValue().toString();
                if(type.equals("donor")){
                    result="recipient";
                }
                else{
                    result="donor";
                }

                DatabaseReference reference =FirebaseDatabase.getInstance().getReference()
                        .child("users");
                Query query=reference.orderByChild("search").equalTo(result+title);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       userList.clear();
                       for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                           user user=dataSnapshot.getValue(user.class);
                           userList.add(user);

                       }

                       userAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}