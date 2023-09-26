package com.example.wanjing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MonthSpendingActivity extends AppCompatActivity {

    private Toolbar monthToolbar;
    private TextView tv_month;
    private ImageView iv_left, iv_right;
    private LineChart montly_lineChart;
    private DatabaseReference dataRef;
    private String userID, currentMonthName, monthNumber= "";
    private FirebaseAuth mAuth;
    private int currentMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_spending);

        Intent passIntent = getIntent();
        String statusID = passIntent.getStringExtra("status");

        mAuth = FirebaseAuth.getInstance();
        monthToolbar = findViewById(R.id.monthly_toolbar);
        montly_lineChart = findViewById(R.id.month_chart);
        tv_month = findViewById(R.id.tv_month);
        iv_left = findViewById(R.id.iv_left);
        iv_right = findViewById(R.id.iv_right);

        // toolbar
        setSupportActionBar(monthToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        monthToolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        monthToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_gold));

        LocalDate currentDate = LocalDate.now();
        currentMonth = currentDate.getMonthValue();
        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonth--;
                updateMonthTextAndButtons();
            }
        });

        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonth++;
                updateMonthTextAndButtons();
            }
        });

        updateMonthTextAndButtons();

        //Firebase Real-Time Database
        userID = mAuth.getCurrentUser().getUid();
        dataRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(userID).child("monthly").child("2023");

        currentMonthName = tv_month.getText().toString();
        // Retrieve and display data
        retrieveAndDisplayData();

    }

    private void updateMonthTextAndButtons() {
        // Update the TextView text based on the currentMonth value
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        if (currentMonth < 1) currentMonth = 12;
        if (currentMonth > 12) currentMonth = 1;
        tv_month.setText(monthNames[currentMonth - 1]);

//        SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM", Locale.US);
//        SimpleDateFormat outputFormat = new SimpleDateFormat("MM", Locale.US);
//
//        try {
//            // Parse the month name
//            Date date = inputFormat.parse(currentMonthName);
//
//            // Format the date to obtain the two-digit month
//            monthNumber = outputFormat.format(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            // Handle the case where parsing fails (e.g., invalid month name)
//        }


        // Check if currentMonth is equal to the current month (e.g., September)
        Calendar calendar = Calendar.getInstance();
        int currentMonthInCalendar = calendar.get(Calendar.MONTH) + 1; // Adding 1 to match the month numbering
        if (currentMonth == currentMonthInCalendar) {
            iv_right.setImageResource(R.drawable.ic_right_grey);
            iv_right.setEnabled(false);
        } else if (currentMonth == 1){
            iv_left.setImageResource(R.drawable.ic_left_grey);
            iv_left.setEnabled(false);
        }else {
            iv_right.setEnabled(true);
            iv_left.setEnabled(true);
            iv_left.setImageResource(R.drawable.ic_left);
            iv_right.setImageResource(R.drawable.ic_right_black);

        }

    }

    private void retrieveAndDisplayData() {
        // Initialize ArrayLists to store day values and corresponding data
        ArrayList<Integer> days = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();

        Query query = dataRef.child(currentMonthName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                        // Get the day (as a string) and value (as an integer) for each day
                        String dayStr = daySnapshot.getKey();
                        int day = Integer.parseInt(dayStr); // Convert the day string to an integer
                        int value = daySnapshot.getValue(Integer.class);

                        // Add day and value to the ArrayLists
                        days.add(day);
                        values.add(value);
                    }
                    // Call a method to display the data in the LineChart
                    displayDataInLineChart(days, values);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors here
            }
        });
    }

    private void displayDataInLineChart(ArrayList<Integer> days, ArrayList<Integer> values) {
        ArrayList<Entry> entries = new ArrayList<>();

        // Populate the entries list with day values and corresponding values
        for (int i = 0; i < days.size(); i++) {
            entries.add(new Entry(days.get(i), values.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Spending Amount");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);

        LineData lineData = new LineData(dataSet);

        // Configure X-axis (days)
        XAxis xAxis = montly_lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(convertDaysToLabels(days)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        YAxis yAxis = montly_lineChart.getAxisLeft(); // Get the left Y-axis
        yAxis.setAxisMinimum(0f); // Set the minimum value to 0

        // If you want to apply the same setting to the right Y-axis (if you have it):
        YAxis rightYAxis = montly_lineChart.getAxisRight();
        rightYAxis.setAxisMinimum(0f);

        // Set the LineData to the LineChart
        montly_lineChart.setData(lineData);
        montly_lineChart.getDescription().setEnabled(false);
        montly_lineChart.setTouchEnabled(false);

        // Refresh the chart
        montly_lineChart.invalidate();
    }

    private ArrayList<String> convertDaysToLabels(ArrayList<Integer> days) {
        ArrayList<String> labels = new ArrayList<>();

        // Customize label format as needed, e.g., "Day 1", "Day 2", ...
        for (int day : days) {
            String label = "Day " + day;
            labels.add(label);
        }

        return labels;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}