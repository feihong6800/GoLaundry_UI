package com.example.wanjing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wanjing.Model.NotInfo;
import com.example.wanjing.Model.NotificationInfo;
import com.example.wanjing.ViewHolder.NotViewHolder;
import com.example.wanjing.ViewHolder.NotificationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class NotificationActivity extends AppCompatActivity {

    private Toolbar notificationToolbar;
    private DatabaseReference notifyRef, databaseRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth;
    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationToolbar = findViewById(R.id.notification_toolbar);
        recyclerView = findViewById(R.id.notification_recycler);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        //Toolbar
        setSupportActionBar(notificationToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        notificationToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        notificationToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_gold));

        notifyRef = FirebaseDatabase.getInstance().getReference().child("laundryNotification");
        databaseRef = FirebaseDatabase.getInstance().getReference();


        //Notification Recycler View
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(NotificationActivity.this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<NotificationInfo> options = new FirebaseRecyclerOptions.Builder<NotificationInfo>().setQuery(notifyRef, NotificationInfo.class).build();
        FirebaseRecyclerAdapter<NotificationInfo, NotificationViewHolder> adapter = new FirebaseRecyclerAdapter<NotificationInfo, NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int i, @NonNull final NotificationInfo model) {
                holder.notificationTitle.setText(model.getNotificationTitle());
                holder.notificationContent.setText(model.getNotificationContent());
                holder.notificationDate.setText(model.getNotificationDate());
                String status = model.getNotificationStatus();
                Boolean isRead = model.getNotificationRead();

                if (isRead) {
                    holder.notificationLayout.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                    if (status.equals("notification")) {
                        holder.img_icon.setImageResource(R.drawable.ic_notification);

                    } else {
                        holder.img_icon.setImageResource(R.drawable.ic_left_gold);

                    }
                } else {
                    holder.notificationLayout.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_blue));
                    if (status.equals("notification")) {
                        holder.img_icon.setImageResource(R.drawable.ic_notification);
                    } else {
                        holder.img_icon.setImageResource(R.drawable.ic_left_gold);
                    }
                }

                final String notID = getSnapshots().getSnapshot(i).getKey();
                System.out.println(notID);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseRef.child("laundryNotification").child(notID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    HashMap<String, Object> updates = new HashMap<>();
                                    updates.put("notificationRead", true);
                                    databaseRef.child("laundryNotification").child(notID).updateChildren(updates, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                // Handle the error, e.g., log or display an error message
                                                Toast.makeText(NotificationActivity.this, "Error updating data", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Data updated successfully
                                                holder.notificationLayout.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(NotificationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list, parent, false);
                return new NotificationViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}