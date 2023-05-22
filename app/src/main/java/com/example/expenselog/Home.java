package com.example.expenselog;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity {

    private EditText newExpText;
    private EditText newExpVal;
    private Button newExpSaveButton;

    private RadioGroup radioGroup;

    private RadioButton radioButton;
    private Button viewExpenseButton;
    private TextView totalExpView;
    private DatabaseReference databaseRef;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Calendar cal;

    Dialog bdialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        setContentView(R.layout.activity_home);
        newExpVal=findViewById(R.id.expenseAmountEditText);
        newExpText=findViewById(R.id.expenseDescriptionEditText);
        viewExpenseButton=findViewById(R.id.view_expenses);
        radioGroup=findViewById(R.id.radio_group);

        totalExpView=findViewById(R.id.totalExpensesTextView);
        newExpSaveButton=findViewById(R.id.addExpenseButton);

//         Get reference to Firebase Realtime Database

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = currentUser.getEmail();

        rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userEmail.replace(".",","));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        String currentDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat dFormat = new SimpleDateFormat("MMMM d");
        String cDate = dFormat.format(calendar.getTime());

        databaseRef = rootRef.child("expense").child(currentDate);


        newExpSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String expensetext=newExpText.getText().toString().trim();
                String expenseval=newExpVal.getText().toString().trim();

                if (expenseval.isEmpty() ){
                    newExpVal.setError("Please enter expense amount");
                    return;
                }
                if (expensetext.isEmpty() ){
                    newExpText.setError("Please enter expense description");
                    return;
                }
                int expval=Integer.parseInt(expenseval);


                int ID=radioGroup.getCheckedRadioButtonId();
                if(ID==-1){
                    Toast.makeText(Home.this,"Select Expanse Category ! ", Toast.LENGTH_SHORT).show();
                    return;
                }
                radioButton=findViewById(ID);
                String exp_type= (String) radioButton.getText();

                //adding new task
                Exp_Model exp=new Exp_Model();
                exp.setDes(expensetext);
                exp.setValue(expval);
                exp.setDate(cDate);
                exp.setExp_type(exp_type);

                String id=databaseRef.push().getKey();
                exp.setId(id);

                databaseRef.child(id).setValue(exp);
                Toast.makeText(Home.this, "Expense Added Successfully!!✅", Toast.LENGTH_SHORT).show();

                newExpVal.getText().clear();
                newExpText.getText().clear();

            }
        });

        viewExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home.this,Expense_View.class));
            }
        });


        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value=snapshot.child("total_exp").getValue().toString();
                totalExpView.setText("Total expense : ₹"+value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference myRef = rootRef.child("expense");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total=0;
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    for(DataSnapshot snap: singleSnapshot.getChildren()){
                        if(snap.hasChild("id")) {
                            Exp_Model Exp = snap.getValue(Exp_Model.class);
                            total = total + Exp.getValue();
                        }
                    }
                }
                rootRef.child("total_exp").setValue(total);
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
                    bdialog=new Dialog(Home.this);
                    if (total >= budget) {
//                        Toast.makeText(Home.this, "Expenditure Exceeded Budget", Toast.LENGTH_SHORT).show();
                        openbudgetDialog("Budget Exceeded!!!",R.color.red);
                    }
                    else if (total>=.8*budget){
                        openbudgetDialog("Budget Exceeded over 80%",R.color.purple_200);
                    }
                    else if (total>=.5*budget && total<=.8*budget){
                        openbudgetDialog("Budget Exceeded over 50%",R.color.yellow);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                System.out.println(currentDate);
                if(!snapshot.child("expense").hasChild(currentDate)){
                    setAlarm();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setAlarm() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 22); // 10pm
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);

        if (Calendar.getInstance().after(cal)) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        System.out.println("step1");
        Intent intent = new Intent(Home.this, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        System.out.println("step2");

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
        }
        System.out.println("step3");

        Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        System.out.println("step0");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Reminders";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channel_id",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    private void openbudgetDialog(String info,int color){
        bdialog.setContentView(R.layout.budget_exceeded);
        bdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView budget_info=bdialog.findViewById(R.id.textView4);
        ImageView status_img=bdialog.findViewById(R.id.imageView2);
        budget_info.setTextColor(color);
        status_img.setColorFilter(status_img.getContext().getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
        budget_info.setText(info);
        bdialog.show();

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                bdialog.dismiss();
                timer.cancel(); //this will cancel the timer of the system
            }
        }, 3000);
    }

}