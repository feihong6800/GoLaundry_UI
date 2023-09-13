package com.example.wanjing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiderMainActivity extends AppCompatActivity {

    private TextView tv_laundry_rider_name, tv_spending, tv_number_pending,
            tv_number_pending_collection, tv_pending_process, tv_pending_collection, tv_monthly_order;
    String userID = "";
    FirebaseAuth mAuth;
    DatabaseReference rootRef, pendingRef;
    String userName = "";

    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_main);

        mAuth = FirebaseAuth.getInstance();
        tv_laundry_rider_name = findViewById(R.id.tv_rider_name);
        tv_spending = findViewById(R.id.tv_spending);
        tv_number_pending = findViewById(R.id.tv_number_pending_collection);
        tv_number_pending_collection = findViewById(R.id.tv_number_pending_receiving);
        tv_pending_process = findViewById(R.id.tv_pending_collection);
        tv_pending_collection = findViewById(R.id.tv_pending_receiving);
        barChart = findViewById(R.id.chart2);
        tv_monthly_order = findViewById(R.id.tv_monthly_orders);

        userID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference("USERS").child(userID);
        pendingRef = FirebaseDatabase.getInstance().getReference("Pending");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(userID).child("Monthly Ordedr");

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Process the data here and store it in data structures
                if (dataSnapshot.exists()) {
                    ArrayList<String> months = new ArrayList<>();
                    ArrayList<Integer> values = new ArrayList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String month = snapshot.getKey();
                        String spending_amount = snapshot.getValue(String.class);
                        int spending_amount_int = Integer.parseInt(spending_amount);

                        // Add month names to the months list
                        months.add(month);

                        // Add numeric values to the values list
                        values.add(spending_amount_int);
                    }

                    // Call a method to display the data in a BarChart
                    displayDataInBarChart(months, values);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });

//        dataRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Process the data here and organize it by month
//                if (dataSnapshot.exists()) {
//                    Map<String, String> monthlyData = new HashMap<>();
//
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        String month = snapshot.getKey();
//                        String data = snapshot.getValue(String.class);
//
//                        // Store the data in the map with month as the key
//                        monthlyData.put(month, data);
//                    }
//
//                    // Call a method to display the data in a BarChart
//                    displayDataInBarChart(monthlyData);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle any errors here
//            }
//        });


        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("name").getValue().toString();
                tv_laundry_rider_name.setText(userName);
                String status = snapshot.child("status").getValue().toString();
                if(status.equals("laundry")){
                    tv_spending.setText(R.string.tv_earning);
                    tv_pending_process.setText(R.string.laundry_tv_pending_process);
                    tv_pending_collection.setText(R.string.laundry_tv_pending_confirm);
                    tv_monthly_order.setText(R.string.laundry_rider_monthy_order);
                    pendingRef.child("Pending Process").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int count = (int) snapshot.getChildrenCount();
                            String numberofPendingProcess = String.valueOf(count);
                            tv_number_pending.setText(numberofPendingProcess);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    pendingRef.child("Pending Collection").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int count  = (int) snapshot.getChildrenCount();
                            String numberofPendingCollection = String.valueOf(count);
                            tv_number_pending_collection.setText(numberofPendingCollection);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else if(status.equals("rider")){
                    tv_spending.setText(R.string.tv_earning);
                    tv_pending_process.setText(R.string.rider_tv_pending_pick_up);
                    tv_pending_collection.setText(R.string.rider_tv_pending_deliver);
                    tv_monthly_order.setText(R.string.laundry_rider_monthy_order);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void displayDataInBarChart(ArrayList<String> months, ArrayList<Integer> values) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        // Populate the entries list with values and label them with months
        for (int i = 0; i < months.size(); i++) {
            entries.add(new BarEntry(i, values.get(i), months.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Order Data");
        BarData barData = new BarData(dataSet);

        barChart.setData(barData);
        barChart.setTouchEnabled(false);
        barChart.invalidate(); // Refresh the chart
    }

//    private void displayDataInBarChart(Map<String, String> monthlyData) {
//
//        ArrayList<BarEntry> entries = new ArrayList<>();
//        ArrayList<String> labels = new ArrayList<>();
//
//        // Populate the entries list and labels
//        int index = 0;
//        for (Map.Entry<String, String> entry : monthlyData.entrySet()) {
//            String month = entry.getKey();
//            String data = entry.getValue();
//            float value = Float.parseFloat(data); // Convert String data to a float
//
//            entries.add(new BarEntry(index, value));
//            labels.add(month);
//
//            index++;
//        }
//
//        BarDataSet dataSet = new BarDataSet(entries, "Monthly Data");
//        BarData barData = new BarData(dataSet);
//
//        // Customize the x-axis labels
//        XAxis xAxis = barChart.getXAxis();
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setGranularity(1f);
//        xAxis.setLabelRotationAngle(45f);
//
//        barChart.setData(barData);
//        barChart.invalidate(); // Refresh the chart
//    }

}