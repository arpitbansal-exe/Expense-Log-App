package com.example.expenselog;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Analyse_Screen extends AppCompatActivity {

    private DatabaseReference todayRef;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser;

    ArrayList<PieEntry> PieArraylist;

    ArrayList<PieEntry> BudgetArraylist;
    ArrayList<PieEntry> CategoryArraylist;

    ArrayList<Entry> DailyArrayList;

    private PieChart pieChart;
    private PieChart budgetpieChart;

    private PieChart categoy_piechart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();
        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userEmail.replace(".", ","));

        PieArraylist = new ArrayList<>();
        BudgetArraylist = new ArrayList<>();
        CategoryArraylist = new ArrayList<>();
        DailyArrayList = new ArrayList<>();

        categoy_piechart = findViewById(R.id.categoy_piechart);
//        pieChart = findViewById(R.id.piechart);
        budgetpieChart = findViewById(R.id.budgetpiechart);
        LineChart chart = findViewById(R.id.linechart);


        DatabaseReference myRef = rootRef.child("expense");
        // daily total calculator

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    int day_total = 0;
                    for (DataSnapshot snap : singleSnapshot.getChildren()) {
                        if (snap.hasChild("id")) {
                            Exp_Model Exp = snap.getValue(Exp_Model.class);
                            day_total = day_total + Exp.getValue();
                        }
                    }
                    singleSnapshot.child("day_total").getRef().setValue(day_total);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int foodtotal = 0;
//                int traveltotal = 0;
//                int studytotal = 0;
//                int othertotal = 0;
//                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
//                    for (DataSnapshot snap : singleSnapshot.getChildren()) {
//                        if (snap.hasChild("id")) {
//                            Exp_Model Exp = snap.getValue(Exp_Model.class);
//                            if (Exp.getExp_type().equals("Food"))
//                                foodtotal = foodtotal + Exp.getValue();
//                            if (Exp.getExp_type().equals("Travel"))
//                                traveltotal = traveltotal + Exp.getValue();
//                            if (Exp.getExp_type().equals("Study"))
//                                studytotal = studytotal + Exp.getValue();
//                            if (Exp.getExp_type().equals("Other"))
//                                othertotal = othertotal + Exp.getValue();
//
//                        }
//                    }
//                }
//                rootRef.child("foodtotal").setValue(foodtotal);
//                rootRef.child("traveltotal").setValue(traveltotal);
//                rootRef.child("studytotal").setValue(studytotal);
//                rootRef.child("othertotal").setValue(othertotal);
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


//         daily Graph
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                PieArraylist.clear();
//                for (DataSnapshot daySnap : snapshot.getChildren()) {
//                    String date = daySnap.getKey();
//                    int total = daySnap.child("day_total").getValue(Integer.class);
//
////                    System.out.println(date+" "+total);
//                    PieArraylist.add(new PieEntry(total, date));
//                }
//                PieDataSet dataSet = new PieDataSet(PieArraylist, "");
//                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//                PieData data = new PieData(dataSet);
//                data.setValueTextColor(Color.BLACK);
//                data.setValueTextSize(20f);
//
//                pieChart.setData(data);
//                pieChart.getDescription().setEnabled(false);
//                pieChart.setDrawEntryLabels(true);
//                pieChart.setDrawHoleEnabled(true);
//
//                pieChart.animateY(1000);
//                pieChart.getLegend().setEnabled(false);
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        //Budget Graph
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BudgetArraylist.clear();

                int budget = snapshot.child("budget").getValue(Integer.class);

                int total_exp = snapshot.child("total_exp").getValue(Integer.class);
                if(budget>=total_exp) {

                    BudgetArraylist.add(new PieEntry((budget - total_exp), "Available"));
                    BudgetArraylist.add(new PieEntry(total_exp, "Expend"));

                    PieDataSet b_dataSet = new PieDataSet(BudgetArraylist, "");
                    b_dataSet.setColors(Color.GREEN, Color.RED);
                    PieData b_data = new PieData(b_dataSet);
                    b_data.setValueTextColor(Color.BLACK);
                    b_data.setValueTextSize(20f);
                    budgetpieChart.setData(b_data);
                    budgetpieChart.getDescription().setEnabled(false);
                    budgetpieChart.setDrawEntryLabels(true);
                    budgetpieChart.setEntryLabelColor(Color.BLACK);
                    budgetpieChart.setDrawHoleEnabled(true);
                    budgetpieChart.animateY(1000);
                    budgetpieChart.setRotationEnabled(false);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //  Category Graph
        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CategoryArraylist.clear();

                int foodtotal = snapshot.child("foodtotal").getValue(Integer.class);
                int traveltotal = snapshot.child("traveltotal").getValue(Integer.class);
                int studytotal = snapshot.child("studytotal").getValue(Integer.class);
                int othertotal = snapshot.child("othertotal").getValue(Integer.class);

                CategoryArraylist.add(new PieEntry(foodtotal, "Food"));
                CategoryArraylist.add(new PieEntry(traveltotal, "Travel"));
                CategoryArraylist.add(new PieEntry(studytotal, "Study"));
                CategoryArraylist.add(new PieEntry(othertotal, "Other"));


                PieDataSet dataSet = new PieDataSet(CategoryArraylist, "");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                PieData data = new PieData(dataSet);
                data.setValueTextColor(Color.BLACK);
                data.setValueTextSize(20f);
                categoy_piechart.setData(data);
                categoy_piechart.getDescription().setEnabled(false);
                categoy_piechart.setDrawEntryLabels(true);
                categoy_piechart.setEntryLabelColor(Color.BLACK);
                categoy_piechart.setDrawHoleEnabled(true);
                categoy_piechart.animateY(1000);
                categoy_piechart.setRotationEnabled(false);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Float> d_exp = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DailyArrayList.clear();
                dates.clear();
                d_exp.clear();
                for (DataSnapshot daySnap : snapshot.getChildren()) {
                    String date = daySnap.getKey();
                    int total = daySnap.child("day_total").getValue(Integer.class);

                    SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM d, yyyy");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM");

                    Date o_date = null;
                    String n_date = null;
                    try {
                        o_date = inputFormat.parse(date);
                        n_date = outputFormat.format(o_date);
                    }catch (ParseException e) {
                        e.printStackTrace();
                    }

                    dates.add(n_date);
                    d_exp.add((float) total);

//                    System.out.println(getDate(date));

                }
                for(int i=0;i<d_exp.size();i++){
                    DailyArrayList.add(new Entry(i, d_exp.get(i)));
                }

                chart.setAutoScaleMinMaxEnabled(false);
                chart.getDescription().setEnabled(false); // hide the description label
                chart.setDrawGridBackground(false); // hide the background grid lines
                chart.setTouchEnabled(true); // enable touch gestures (e.g. scrolling and zooming)
                chart.setDragEnabled(true); // enable dragging the chart
                chart.setScaleEnabled(false); // enable scaling the chart
                chart.setPinchZoom(true); // enable zooming via pinch gestures
                chart.getAxisRight().setEnabled(false);
                chart.getLegend().setEnabled(false);
                XAxis xAxis = chart.getXAxis();
                xAxis.setGranularityEnabled(true);
                xAxis.setGranularity(1f);
                xAxis.setCenterAxisLabels(false);

                chart.setScrollContainer(true);
                chart.setHorizontalScrollBarEnabled(true);
                chart.setScaleXEnabled(true);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
                chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                LineDataSet dataSet = new LineDataSet(DailyArrayList, "Daily Expenses"); // create a LineDataSet object with your data
                dataSet.setColor(Color.BLUE); // set the line color
                dataSet.setLineWidth(2f); // set the line width
                dataSet.setDrawCircles(true); // hide the data point circles
                dataSet.setValueTextSize(8f);
//                dataSet.setCircleRadius(3f);
                dataSet.setCircleHoleRadius(5f);
                dataSet.setDrawValues(true); // hide the data point values
                LineData lineData = new LineData(dataSet); // create a LineData object with your LineDataSet
                chart.setData(lineData); // set the LineData object to your chart
                chart.invalidate(); // refresh the chart

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}