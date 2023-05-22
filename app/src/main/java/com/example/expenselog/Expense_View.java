package com.example.expenselog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Expense_View extends AppCompatActivity {

    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<Exp_Model> list;

    ArrayList<Exp_Model> flist;
    ArrayList<Exp_Model> tlist;
    ArrayList<Exp_Model> slist;
    ArrayList<Exp_Model> olist;

    private TextView t_view;

    private RadioGroup radioGroup;
    private RadioButton radioButton;

    Dialog dialog;

    private Button open_set_budget;

    private Button analyse_button;

    TextView status_view;
    private TextView budget_text;
    private TextView safe_to_spend_text;

    private FirebaseUser currentUser;
    private DatabaseReference rootRef;

    private ImageButton delete_all;

    String total;
    String foodtotal ;
    String traveltotal;
    String studytotal;
    String  othertotal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_view);
        getSupportActionBar().setTitle("Expense List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(Expense_View.this));

        budget_text=findViewById(R.id.budget_text);
        safe_to_spend_text=findViewById(R.id.safe_to_spend_text);
        open_set_budget=findViewById(R.id.open_set_budget);
        analyse_button=findViewById(R.id.analyse_button);
        radioGroup=findViewById(R.id.radio_group);
        delete_all=findViewById(R.id.delete_all);
        status_view=findViewById(R.id.status_view);
        t_view=findViewById(R.id.t_view);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();

        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userEmail.replace(".",","));
        DatabaseReference myRef = rootRef.child("expense");

        list=new ArrayList<>();
        flist=new ArrayList<>();
        tlist=new ArrayList<>();
        slist=new ArrayList<>();
        olist=new ArrayList<>();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int ft = 0;
                int tt = 0;
                int st = 0;
                int ot = 0;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snap : singleSnapshot.getChildren()) {
                        if (snap.hasChild("id")) {
                            Exp_Model Exp = snap.getValue(Exp_Model.class);
                            if (Exp.getExp_type().equals("Food"))
                                ft = ft + Exp.getValue();
                            if (Exp.getExp_type().equals("Travel"))
                                tt = tt + Exp.getValue();
                            if (Exp.getExp_type().equals("Study"))
                                st = st + Exp.getValue();
                            if (Exp.getExp_type().equals("Other"))
                                ot = ot + Exp.getValue();

                        }
                    }
                }
                rootRef.child("foodtotal").setValue(ft);
                rootRef.child("traveltotal").setValue(tt);
                rootRef.child("studytotal").setValue(st);
                rootRef.child("othertotal").setValue(ot);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total=snapshot.child("total_exp").getValue().toString();
                t_view.setText("Total: "+total);

                foodtotal=snapshot.child("foodtotal").getValue().toString();
                traveltotal=snapshot.child("traveltotal").getValue().toString();
                studytotal=snapshot.child("studytotal").getValue().toString();
                othertotal=snapshot.child("othertotal").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.radio0:
                        t_view.setText("Total: "+total);
                        myAdapter=new MyAdapter(Expense_View.this,list);
                        recyclerView.setAdapter(myAdapter);
                        break;
                    case R.id.radio1:
                        t_view.setText("Total: "+foodtotal);
                        myAdapter=new MyAdapter(Expense_View.this,flist);
                        recyclerView.setAdapter(myAdapter);
                        break;
                    case R.id.radio2:
                        t_view.setText("Total: "+traveltotal);
                        myAdapter=new MyAdapter(Expense_View.this,tlist);
                        recyclerView.setAdapter(myAdapter);
                        break;
                    case R.id.radio3:
                        t_view.setText("Total: "+studytotal);
                        myAdapter=new MyAdapter(Expense_View.this,slist);
                        recyclerView.setAdapter(myAdapter);
                        break;
                    case R.id.radio4:
                        t_view.setText("Total: "+othertotal);
                        myAdapter=new MyAdapter(Expense_View.this,olist);
                        recyclerView.setAdapter(myAdapter);
                        break;
                    default:
                        break;
                }
            }
        });

        myAdapter=new MyAdapter(Expense_View.this,list);
        recyclerView.setAdapter(myAdapter);




        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                flist.clear();
                tlist.clear();
                olist.clear();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                        for (DataSnapshot snap : singleSnapshot.getChildren()) {
                            if(snap.hasChild("id")){
                                Exp_Model ex = snap.getValue(Exp_Model.class);
                                list.add(ex);
                                if(ex.getExp_type().equals("Food"))
                                    flist.add(ex);
                                else if(ex.getExp_type().equals("Travel"))
                                    tlist.add(ex);
                                else if(ex.getExp_type().equals("Study"))
                                    slist.add(ex);
                                else if(ex.getExp_type().equals("Other"))
                                    olist.add(ex);

                            }
                        }
                }

                myAdapter.notifyDataSetChanged();
                if(list.isEmpty()){
//                    status_view.setText("No data Avaliable");
                    status_view.setVisibility(View.VISIBLE);
                    analyse_button.setEnabled(false);
                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                    params.height = 50;
                    recyclerView.setLayoutParams(params);

                }
                if(!list.isEmpty()){
                    status_view.setVisibility(View.INVISIBLE);
                    analyse_button.setEnabled(true);
                    ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                    params.height = 800;
                    recyclerView.setLayoutParams(params);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total=dataSnapshot.child("total_exp").getValue(Integer.class);
                int budget=dataSnapshot.child("budget").getValue(Integer.class);

                if(budget!=0) {
                    budget_text.setText("Budget Amount: ₹"+budget);
                    if (total >= budget) {
                        safe_to_spend_text.setText("Budget Exceeded by "+ String.valueOf((total - budget)));
                        Toast.makeText(Expense_View.this, "Expenditure Exceeded Budget", Toast.LENGTH_SHORT).show();
                    } else {safe_to_spend_text.setText("Safe to Spend: "+String.valueOf((budget - total)));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog=new Dialog(this);
        open_set_budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Expense_View.this);
                builder.setTitle("Delete Task");
                builder.setMessage("Are you sure you want all expenses?");

                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                rootRef.child("expense").removeValue();
                                Toast.makeText(Expense_View.this, "All expense deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();



//                rootRef.child("expense").removeValue();
            }
        });


        analyse_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Expense_View.this, Analyse_Screen.class));
            }
        });




    }

    private void openDialog() {
        dialog.setContentView(R.layout.budget_add);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button set_budget=dialog.findViewById(R.id.set_budget);
        EditText get_budget_amt=dialog.findViewById(R.id.budget_text);

        set_budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (get_budget_amt.getText().toString().isEmpty() ){
                    get_budget_amt.setError("Enter budget");
                    return;
                }
                int b=Integer.parseInt(get_budget_amt.getText().toString());

                rootRef.child("budget").setValue(b);

                Toast.makeText(Expense_View.this, "Budget set: "+b, Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });
        dialog.show();

    }


}

