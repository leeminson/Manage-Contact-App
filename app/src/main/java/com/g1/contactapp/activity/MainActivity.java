package com.g1.contactapp.activity;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorWindow;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.adapter.ContactAdapter;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.utils.AppDatabase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ContactDao contactDao;
    private SearchView searchView;
    private ContactAdapter adapter;
    static List<Contact> contacts = new ArrayList<>();

    private static final int FILTER_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 120 * 1280 * 960); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppDatabase database = MyApp.getMyDatabase(this);
        contactDao = database.getContactDao();
        ListView listView = findViewById(R.id.lv_contacts);
        // Tạo dữ liệu demo

        contacts = contactDao.getAllContact();

        // Tạo Adapter và gắn với ListView
        adapter = new ContactAdapter(this, (ArrayList<Contact>) contacts);
        listView.setAdapter(adapter);
        // Sắp xếp liên hệ
        // Kiểm tra xem có dữ liệu được gửi từ SortActivity không
        if (getIntent() != null && getIntent().hasExtra("sortedContactList")) {
            // Nhận danh sách đã sắp xếp từ Intent
            long[] sortedContactIdsArray = getIntent().getLongArrayExtra("sortedContactList");

            // Chuyển đổi mảng sang ArrayList nếu cần thiết
            ArrayList<Long> filteredContactIds = new ArrayList<>();
            if (sortedContactIdsArray != null) {
                for (long id : sortedContactIdsArray) {
                    filteredContactIds.add(id);
                }
            }
            List<Contact> sortedContactList = new ArrayList<>();
            for (Long contactId : filteredContactIds) {
                Contact contact = contactDao.getContact(contactId);
                System.out.println(contact);
                sortedContactList.add(contact);
            }
            // Cập nhật danh sách liên hệ trong MainActivity với danh sách đã sắp xếp
            updateContactList(sortedContactList);
        }


        // tìm kiếm liên hệ
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter adapter khi người dùng nhập văn bản vào SearchView
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        ImageButton imageButton = findViewById(R.id.imageButton);


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.subnavigation_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_add_contact) {
                            Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                            startActivity(intent);
                            // Xử lí khi click vào "Thêm danh bạ"
                            return true;
                        } else if (itemId == R.id.sort_menu_item) {
                            // Chuyển đến activity quản lí lịch hẹn
                            Intent intent = new Intent(MainActivity.this, SortActivity.class);
                            startActivity(intent);
                            return true;
                        } else if (itemId == R.id.filter_menu_item) {
                            // Chuyển đến activity quản lí lịch hẹn
                            Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                            startActivityForResult(intent, FILTER_REQUEST_CODE);
                            return true;
                        } else if (itemId == R.id.menu_appointment) {
                            // Chuyển đến activity quản lí lịch hẹn
                            Intent intent = new Intent(MainActivity.this, AppointmentActivity.class);
                            startActivity(intent);
                            return true;
                        } else {
                            // Xử lí các option khác nếu cần
                            return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });
    }

    // hàm comfirm khi thoát ứng dụng
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận thoát");
        builder.setMessage("Bạn có chắc muốn thoát ứng dụng?");

        builder.setPositiveButton("có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    // Phương thức cập nhật danh sách liên hệ
    public void updateContactList(List<Contact> sortedContactList) {
        contacts.clear(); // Xóa danh sách hiện tại trước khi cập nhật mới
        contacts.addAll(sortedContactList); // Thêm danh sách đã sắp xếp vào danh sách liên hệ
        adapter.notifyDataSetChanged(); // Thông báo cho adapter biết dữ liệu đã thay đổi
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_REQUEST_CODE && resultCode == RESULT_OK) {
            // Nhận danh sách đã lọc từ Intent
            long[] filteredContactIdsArray = data.getLongArrayExtra("filteredContactIds");
            // Xử lý danh sách đã lọc ở đây
            // Chuyển đổi mảng sang ArrayList nếu cần thiết
            ArrayList<Long> filteredContactIds = new ArrayList<>();
            if (filteredContactIdsArray != null) {
                for (long id : filteredContactIdsArray) {
                    filteredContactIds.add(id);
                }
            }
            List<Contact> filteredContacts = new ArrayList<>();
            for (Long contactId : filteredContactIds) {
                Contact contact = contactDao.getContact(contactId);
                System.out.println(contact);
                filteredContacts.add(contact);
            }
            System.out.println("hello ");
            // Cập nhật danh sách liên hệ trong MainActivity với danh sách đã sắp xếp
            updateContactList(filteredContacts);
        }
    }
}
