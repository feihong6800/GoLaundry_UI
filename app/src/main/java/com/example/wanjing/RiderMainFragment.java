package com.example.wanjing;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RiderMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RiderMainFragment extends Fragment {
    private TextView tv_laundry_rider_name, tv_spending, tv_number_pending,
            tv_number_pending_collection, tv_pending_process,
            tv_pending_collection, tv_monthly_order, tv_rating_number, tv_view_all;
    private String userID = "";
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef, pendingRef, dataRef, lineRef;
    private String userName = "";
    private BarChart barChart;
    private Toolbar riderToolbar;
    private LineChart lineChart;
    private RatingBar ratingBar;

    public RiderMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_rider_main, container, false);
        mAuth = FirebaseAuth.getInstance();
        tv_laundry_rider_name = view.findViewById(R.id.tv_rider_name);
        tv_spending = view.findViewById(R.id.tv_spending);
        tv_number_pending = view.findViewById(R.id.tv_number_pending_collection);
        tv_number_pending_collection = view.findViewById(R.id.tv_number_pending_receiving);
        tv_pending_process = view.findViewById(R.id.tv_pending_collection);
        tv_pending_collection = view.findViewById(R.id.tv_pending_receiving);
        tv_view_all = view.findViewById(R.id.tv_spending_viewMore);
        barChart = view.findViewById(R.id.chart2);
        tv_monthly_order = view.findViewById(R.id.tv_monthly_orders);
        riderToolbar = view.findViewById(R.id.home_toolbar);
        lineChart = view.findViewById(R.id.chart1);
        ratingBar = view.findViewById(R.id.rating_star);
        tv_rating_number = view.findViewById(R.id.tv_rating_number);

        userID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference("USERS").child(userID);
        pendingRef = FirebaseDatabase.getInstance().getReference("Pending");

        //Get Current Year
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String currentYear = sdf.format(new Date());

        SimpleDateFormat month = new SimpleDateFormat("MMMM");
        String currentMonth = month.format(new Date());
        System.out.println(currentMonth);

        dataRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(userID).child("Month Order").child(currentYear);
        lineRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(userID).child("Earning").child(currentYear);

        //Toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(riderToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("name").getValue().toString();
                tv_laundry_rider_name.setText(userName);
                String status = snapshot.child("status").getValue().toString();
                double ratingStart = snapshot.child("rating").getValue(Double.class);
                String ratingNumber = snapshot.child("ratingRate").getValue().toString();

                if(status.equals("laundry")){
                    tv_spending.setText(R.string.tv_earning);
                    tv_pending_process.setText(R.string.laundry_tv_pending_process);
                    tv_pending_collection.setText(R.string.laundry_tv_pending_confirm);
                    tv_monthly_order.setText(R.string.laundry_rider_monthy_order);
                    ratingBar.setRating((float) ratingStart);
                    tv_rating_number.setText(ratingNumber);
                    tv_view_all.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), MonthSpendingActivity.class);
                            intent.putExtra("status", status);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });

                    // Retrieve Bar Chart Data
                    dataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                ArrayList<String> months = new ArrayList<>();
                                ArrayList<Integer> values = new ArrayList<>();

                                // Define your custom order for months
                                String[] monthOrder = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

                                // Iterate through the custom order of months
                                for (String monthName : monthOrder) {
                                    DataSnapshot monthSnapshot = dataSnapshot.child(monthName);
                                    if (monthSnapshot.exists()) {
                                        String month = monthSnapshot.getKey();
                                        int spendingAmount = monthSnapshot.getValue(Integer.class);

                                        // Add month names to the months list
                                        months.add(month);

                                        // Add spending amounts to the values list
                                        values.add(spendingAmount);
                                    }
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

                    // Earning Chart
                    lineRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                ArrayList<String> months = new ArrayList<>();
                                ArrayList<Integer> values = new ArrayList<>();

                                // Define your custom order for months
                                String[] monthOrder = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

                                // Iterate through the custom order of months
                                for (String monthName : monthOrder) {
                                    DataSnapshot monthSnapshot = dataSnapshot.child(monthName);
                                    if (monthSnapshot.exists()) {
                                        int spendingAmount = monthSnapshot.getValue(Integer.class);

                                        // Add month names to the months list
                                        months.add(monthName);

                                        // Add spending amounts to the values list
                                        values.add(spendingAmount);
                                    }
                                }

                                // Call a method to display the data in a LineChart
                                displayDataInLineChart(months, values);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors here
                        }
                    });

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
        return view;
    }
    private void displayDataInLineChart(ArrayList<String> months, ArrayList<Integer> values) {

        // Populate the entries list with values
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < months.size(); i++) {
            entries.add(new Entry(i, values.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(Color.RED);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Configure X-axis (months)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // Configure Y-axis (spending amounts)
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        // Refresh the chart
        lineChart.invalidate();
        lineChart.setTouchEnabled(false);
        lineChart.getDescription().setEnabled(false);
    }

    //Display Bar Chart
    private void displayDataInBarChart(ArrayList<String> months, ArrayList<Integer> values) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        // Populate the entries list with values and label them with months
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < months.size(); i++) {
            barEntries.add(new BarEntry(i, values.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.invalidate();
        barChart.setTouchEnabled(false);
        barChart.getDescription().setEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // First clear current all the menu items
        menu.clear();

        // Add the new menu items
        inflater.inflate(R.menu.top_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_notification) {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }
}