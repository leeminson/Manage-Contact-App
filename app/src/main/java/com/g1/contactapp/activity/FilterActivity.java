package com.g1.contactapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.dao.CategoryDao;
import com.g1.contactapp.dao.ContactCategoryDao;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.model.Category;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.model.ContactCategory;
import com.g1.contactapp.utils.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    ImageButton btn_back;

    private CheckBox checkBoxAll,  checkBoxFamily, checkBoxFriends, checkBoxCustomer, checkBoxWork, checkBoxColleague;
    private Button btnApply2;
    private Button btnUnselect2;
    private ContactDao contactDao;
    private CategoryDao categoryDao;
    private ContactCategory contactCategory;
    private List<Contact> contactList;
    private Category category;
    private ContactCategoryDao contactCategoryDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

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



        // Ánh xạ views
        checkBoxAll= findViewById(R.id.checkBoxAll);
        checkBoxFamily = findViewById(R.id.checkBoxFamily);
        checkBoxFriends = findViewById(R.id.checkBoxFriends);
        checkBoxCustomer = findViewById(R.id.checkBoxCustomer);
        checkBoxWork = findViewById(R.id.checkBoxWork);
        checkBoxColleague = findViewById(R.id.checkBoxColleague);
        btnApply2 = findViewById(R.id.btnApply2);
        btnUnselect2= findViewById(R.id.btnUnselect2);


//         Xử lý sự kiện khi nhấn nút "Áp dụng"
        btnApply2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter();
            }
        });

        // Unselect button click listener
        btnUnselect2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxAll.setChecked(false);
                checkBoxFamily.setChecked(false);
                checkBoxFriends.setChecked(false);
                checkBoxCustomer.setChecked(false);
                checkBoxWork.setChecked(false);
                checkBoxColleague.setChecked(false);
                Toast.makeText(getApplicationContext(), "Unchecked", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void applyFilter() {
        List<Contact> filteredContacts = new ArrayList<>();
        AppDatabase database = MyApp.getMyDatabase(this);
        contactCategoryDao = database.getContactCategoryDao();

        if (checkBoxAll.isChecked()) {
            filteredContacts.addAll(contactList);
        } else {
            for (Contact contact : contactList) {
                boolean shouldAddContact = false;
                List<Category> categories = contactCategoryDao.getCategoriesByContactId(contact.getId());
                for (Category category : categories) {
                    if ((checkBoxFamily.isChecked() && category.getCategory().equals("Gia đình")) ||
                            (checkBoxFriends.isChecked() && category.getCategory().equals("Bạn bè")) ||
                            (checkBoxCustomer.isChecked() && category.getCategory().equals("Khách hàng")) ||
                            (checkBoxWork.isChecked() && category.getCategory().equals("Công việc")) ||
                            (checkBoxColleague.isChecked() && category.getCategory().equals("Đồng nghiệp"))) {
                        shouldAddContact = true;
                        break; // Thoát khỏi vòng lặp nếu contact thỏa mãn ít nhất một category
                    }
                }
                if (shouldAddContact) {
                    filteredContacts.add(contact);
                }
            }
        }

        if (!filteredContacts.isEmpty()) {
            // Tạo một mảng long[] chứa ID của các contact đã lọc
            long[] filteredContactIdsArray = new long[filteredContacts.size()];
            for (int i = 0; i < filteredContacts.size(); i++) {
                filteredContactIdsArray[i] = filteredContacts.get(i).getId();
            }

            // Gửi danh sách ID đã lọc qua Intent
            Intent intent = new Intent(FilterActivity.this, MainActivity.class);
            intent.putExtra("filteredContactIds",filteredContactIdsArray );
//            startActivity(intent);
            setResult(Activity.RESULT_OK, intent);
            finish(); // Kết thúc FilterActivity và trở về MainActivity
        } else {
            // Xử lý khi không có contact nào được lọc
            Toast.makeText(this, "Không tìm thấy contact nào phù hợp", Toast.LENGTH_SHORT).show();
        }
    }
}