package com.akif.blooddonationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akif.blooddonationapp.adapter.userAdapter;
import com.akif.blooddonationapp.model.user;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerlayout;
    private Toolbar toolbar;
    private NavigationView nav_view;
    private CircleImageView nav_profile_image;
    private TextView nav_fullnmae,nav_email,nav_bloodGroup,nav_type;
    private DatabaseReference userReferance;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<user> userList;
    private userAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Kan Bağış Uygulaması");
        drawerlayout=findViewById(R.id.drawerlayout);
        nav_view=findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(MainActivity.this,drawerlayout,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        recyclerView=findViewById(R.id.recyclerView);
        progressBar=findViewById(R.id.progressBar);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        userList=new ArrayList<>();
        userAdapter=new userAdapter(MainActivity.this,userList);
        recyclerView.setAdapter(userAdapter);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference()
                .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type=snapshot.child("type").getValue().toString();
                if(type.equals("donor")){
                    readRecipients();
                }
                else{
                    readDonors();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        nav_profile_image=nav_view.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_fullnmae=nav_view.getHeaderView(0).findViewById(R.id.nav_user_fullname);
        nav_email=nav_view.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_bloodGroup=nav_view.getHeaderView(0).findViewById(R.id.nav_user_bloodGroup);
        nav_type=nav_view.getHeaderView(0).findViewById(R.id.nav_user_type);
        userReferance= FirebaseDatabase.getInstance().getReference().child("users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userReferance.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name=snapshot.child("name").getValue().toString();
                    nav_fullnmae.setText(name);
                    String email=snapshot.child("email").getValue().toString();
                    nav_email.setText(email);
                    String bloodGroup=snapshot.child("bloodgroup").getValue().toString();
                    nav_bloodGroup.setText(bloodGroup);
                    String type=snapshot.child("type").getValue().toString();
                    nav_type.setText(type);
                }
            }
            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
            }
        });
    }
    private void readDonors() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("users");
        Query query=reference.orderByChild("type").equalTo("donor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    user user=dataSnapshot.getValue(user.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if(userList.isEmpty()){
                    Toast.makeText(MainActivity.this,"no donor",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull   DatabaseError error) {
            }
        });
    }
    private void readRecipients() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("users");
        Query query=reference.orderByChild("type").equalTo("recipient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    user user=dataSnapshot.getValue(user.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if(userList.isEmpty()){
                    Toast.makeText(MainActivity.this,"no recipient",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull   DatabaseError error) {
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
        switch (item.getItemId()){
            case R.id.aplus:
                Intent intent3=new Intent(MainActivity.this,category_selectedActivity.class);
                intent3.putExtra("group","A+");
                startActivity(intent3);
                break;
            case R.id.logout:
                Intent intent2=new Intent(MainActivity.this,loginActivity.class);
              //  startActivity(intent2);
                finish();
                break;
            case R.id.aminus:
                Intent intent4=new Intent(MainActivity.this,category_selectedActivity.class);
                intent4.putExtra("group","A-");
                startActivity(intent4);
                break;
            case R.id.bplus:
                Intent intent5=new Intent(MainActivity.this,category_selectedActivity.class);
                intent5.putExtra("group","B+");
                startActivity(intent5);
                break;
            case R.id.bminuse:
                Intent intent6=new Intent(MainActivity.this,category_selectedActivity.class);
                intent6.putExtra("group","B-");
                startActivity(intent6);
                break;
            case R.id.abplus:
                Intent intent7=new Intent(MainActivity.this,category_selectedActivity.class);
                intent7.putExtra("group","AB+");
                startActivity(intent7);
                break;
            case R.id.abminuse:
                Intent intent8=new Intent(MainActivity.this,category_selectedActivity.class);
                intent8.putExtra("group","AB-");
                startActivity(intent8);
                break;
            case R.id.oplus:
                Intent intent9=new Intent(MainActivity.this,category_selectedActivity.class);
                intent9.putExtra("group","O+");
                startActivity(intent9);
                break;
            case R.id.ominuse:
                Intent intent10=new Intent(MainActivity.this,category_selectedActivity.class);
                intent10.putExtra("group","O-");
                startActivity(intent10);
                break;
            case R.id.compatible:
                Intent intent11=new Intent(MainActivity.this,category_selectedActivity.class);
                intent11.putExtra("group","Eşleşenler");
                startActivity(intent11);
                break;
            case R.id.profile:
                Intent intent=new Intent(MainActivity.this,profileActivity.class);
                startActivity(intent);
                break;
            case R.id.about:
                Intent intent12=new Intent(MainActivity.this,aboutActivity.class);
                startActivity(intent12);
                break;
            default:
                System.out.println("hata");
        }
        drawerlayout.closeDrawer(GravityCompat.START);
        return true ;
    }
}