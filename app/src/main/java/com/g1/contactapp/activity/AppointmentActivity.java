package com.g1.contactapp.activity;




import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;


import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.adapter.AppointmentAdapter;
import com.g1.contactapp.dao.AppointmentDao;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.model.Appointment;
import com.g1.contactapp.utils.AppDatabase;
import android.os.Build;
import java.util.ArrayList;

public class AppointmentActivity extends AppCompatActivity {
    private ListView listView;
    private Button addButton;
    ImageButton btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_appointment);

        listView = findViewById(R.id.lv_Appointment);
        addButton = findViewById(R.id.btnAddCalender);
        btn_back = findViewById(R.id.btn_backToMain);
        loadData();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AppointmentActivity.this,AddAppointmentActivity.class);
                startActivityForResult(myIntent,782);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("appointment_updated", false)) {
                loadData();
            }
        }
        if (requestCode == 782 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("appointment_created", false)) {
                loadData();
            }
        }
    }
    private void loadData(){
        AppDatabase database = MyApp.getMyDatabase(this);
        AppointmentDao appointmentDao = database.getAppointmentDao();
        ContactDao contactDao =database.getContactDao();
        ArrayList<Appointment> appointments = (ArrayList<Appointment>) appointmentDao.getAllAppointment();

        // Tạo và thiết lập adapter cho ListView
        AppointmentAdapter adapter = new AppointmentAdapter(this, appointments,contactDao,appointmentDao);
        listView.setAdapter(adapter);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}