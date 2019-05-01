package com.example.braintrainer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.braintrainer.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class LeaderBoardActivity extends AppCompatActivity {

    private ArrayList<User> users = new ArrayList<>();
    private RecyclerView leaderBoardRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    users.add(user);
                }
                Collections.sort(users, Collections.<User>reverseOrder());

                leaderBoardRecyclerView
                        .setAdapter(new LeaderBoardAdapter(LeaderBoardActivity.this, users));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        leaderBoardRecyclerView = findViewById(R.id.rv_leaderboard);

        leaderBoardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
