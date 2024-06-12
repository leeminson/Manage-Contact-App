package com.g1.contactapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.utils.AppDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortActivity extends AppCompatActivity {

    ImageButton btn_back;
    private ContactDao contactDao;
    private List<Contact> contactList;

    private CheckBox checkBoxSortByName;
    private CheckBox checkBoxSortByLastName;
    private Button btnUnselect;
    private Button btnApply;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        btn_back = findViewById(R.id.btn_backToMain);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AppDatabase database= MyApp.getMyDatabase(this);
        contactDao =database.getContactDao();
        contactList = contactDao.getAllContact();

        // Initialize views
        checkBoxSortByName = findViewById(R.id.checkBoxSortByName);
        checkBoxSortByLastName = findViewById(R.id.checkBoxSortByLastName);
        btnUnselect = findViewById(R.id.btnUnselect);
        btnApply = findViewById(R.id.btnApply);

        // Apply button click listener
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxSortByName.isChecked()) {
                    sortByName();
                } else if (checkBoxSortByLastName.isChecked()) {
                    sortByLastName();
                }
//                for(Contact contact : contactList)
//                {
//                    System.out.println(contact);
//                }

                // Tạo một mảng long[] chứa ID của các contact đã lọc
                long[] sortedContactIdsArray = new long[contactList.size()];
                for (int i = 0; i < contactList.size(); i++) {
                    sortedContactIdsArray[i] = contactList.get(i).getId();
                }

                // Gửi danh sách ID đã lọc qua Intent
                Intent intent = new Intent(SortActivity.this, MainActivity.class);
                intent.putExtra("sortedContactList",sortedContactIdsArray );
                startActivity(intent);

//                // Create an intent to return sorted contact list to MainActivity
//                Intent intent = new Intent(SortActivity.this, MainActivity.class);
//                ArrayList<Contact> sortedContactList = new ArrayList<>(contactList);
//                intent.putParcelableArrayListExtra("sortedContactList", (ArrayList) sortedContactList);
//                startActivity(intent);
            }
        });

        // Unselect button click listener
        btnUnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxSortByName.setChecked(false);
                checkBoxSortByLastName.setChecked(false);
            }
        });
    }
    // Sort by name
    private void sortByName() {
        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact1, Contact contact2) {
                String[] firstName1 = contact1.getName().split(" ");
                String[] firstName2 = contact2.getName().split(" ");
                return firstName1[firstName1.length-1].compareToIgnoreCase(firstName2[firstName2.length-1]);
            }
        });
    }

    // Sort by last name
    private void sortByLastName() {
        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact1, Contact contact2) {
                String[] lastName1 = contact1.getName().split(" ");
                String[] lastName2 = contact2.getName().split(" ");
                return lastName1[0].compareToIgnoreCase(lastName2[0]);
            }
        });
    }
}