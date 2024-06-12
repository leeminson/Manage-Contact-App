package com.g1.contactapp.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.adapter.SuggestContactAdapter;
import com.g1.contactapp.dao.AppointmentDao;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.model.Appointment;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.utils.AppDatabase;
import com.g1.contactapp.utils.AppointmentManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditAppointmentActivity extends AppCompatActivity {

    private AutoCompleteTextView edtContactName;
    private EditText edtDate, edtTime, edtLocation, edtNote;
    private Button btnSaveEdit;
    ImageButton btnBackToAppointment;
    private long selectedContactId = -1;
    private List<Contact> suggestedContacts;
    private SuggestContactAdapter suggestContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointment);

        edtContactName = findViewById(R.id.edt_contact_name);
        edtDate = findViewById(R.id.edt_date);
        edtTime = findViewById(R.id.edt_time);
        edtLocation = findViewById(R.id.edt_location);
        edtNote = findViewById(R.id.edt_note);
        btnSaveEdit = findViewById(R.id.btn_save_edit);
        btnBackToAppointment = findViewById(R.id.btn_backToAppointment);
        AppDatabase database = MyApp.getMyDatabase(this);
        AppointmentDao appointmentDao = database.getAppointmentDao();
        ContactDao contactDao = database.getContactDao();
        // Nhận dữ liệu extra từ Intent
        long appointmentId = getIntent().getLongExtra("appointment_id", -1);
        Appointment appointment = appointmentDao.getAppointment(appointmentId);


        if (appointment != null) {
            edtContactName.setText(contactDao.getContact(appointment.getContactId()).getName());
            edtLocation.setText(appointment.getLocation());
            edtNote.setText(appointment.getNote());
            String[] dateAndTime = getDateAndTimeFromEditText(appointment.getTime());
            edtDate.setText(dateAndTime[0]);
            edtTime.setText(dateAndTime[1]);
        } else {
            Toast.makeText(EditAppointmentActivity.this, "Fail to load appointment", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Lấy danh sách gợi ý liên hệ một lần khi tạo activity
        suggestedContacts = contactDao.getAllContact();

        suggestContactAdapter = new SuggestContactAdapter(this, suggestedContacts);
        edtContactName.setAdapter(suggestContactAdapter);
        edtContactName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < parent.getAdapter().getCount()) {
                    Contact selectedContact = (Contact) parent.getItemAtPosition(position);
                    if (selectedContact != null) {
                        selectedContactId = selectedContact.getId();
                        Toast.makeText(EditAppointmentActivity.this, "id: " + selectedContactId, Toast.LENGTH_SHORT).show();
                        System.out.println("===================" + selectedContactId + "===================");
                        // Khôi phục lại adapter sau khi chọn liên hệ
                        edtContactName.setAdapter(suggestContactAdapter);
                    }
                }
            }
        });
        edtContactName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Không cần reset selectedContactId ở đây
                // Lọc danh sách liên hệ từ danh sách đã lấy từ cơ sở dữ liệu
                String input = s.toString().toLowerCase();
                List<Contact> filteredContacts = new ArrayList<>();
                for (Contact contact : suggestedContacts) {
                    if (contact.getName().toLowerCase().contains(input)) {
                        filteredContacts.add(contact);
                    }
                }
                suggestContactAdapter.clear();
                suggestContactAdapter.addAll(filteredContacts);
                suggestContactAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            private boolean isDateTimeValid() {
                return !edtDate.getText().toString().isEmpty() && !edtTime.getText().toString().isEmpty();
            }

            private boolean isLocationValid() {
                return !edtLocation.getText().toString().isEmpty();
            }

            private boolean isNoteValid() {
                return edtNote.getText().toString().length() <= 200;
            }

            @Override
            public void onClick(View v) {
                // Hiển thị dialog xác nhận trước khi đóng activity
                AlertDialog.Builder builder = new AlertDialog.Builder(EditAppointmentActivity.this);
                builder.setMessage("Bạn có muốn lưu thay đổi không?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!edtContactName.getText().toString().isEmpty()) {
                                    if (isDateTimeValid() && isLocationValid() && isNoteValid()) {
                                        // Xử lý khi người dùng chọn "Có"
                                        Appointment updateAppointment = new Appointment(
                                                getDataFromEditText(), edtLocation.getText().toString(), edtNote.getText().toString()
                                        );
                                        updateAppointment.setId((int) appointmentId);
                                        if (selectedContactId == -1) {
                                            assert appointment != null;
                                            updateAppointment.setContactId(appointment.getContactId());
                                        } else {
                                            updateAppointment.setContactId(selectedContactId);
                                        }
                                        appointmentDao.updateAppointment(updateAppointment);
                                        // cài đặt thông báo
                                        setNotification(edtDate.getText().toString(),edtTime.getText().toString());
                                        Toast.makeText(EditAppointmentActivity.this, "Cuộc hẹn đã được cập nhật", Toast.LENGTH_SHORT).show();
                                        // Đóng activity và quay lại activity trước đó
                                        Intent intent = new Intent();
                                        intent.putExtra("appointment_updated", true);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }else {
                                        // Hiển thị thông báo lỗi hoặc yêu cầu người dùng nhập lại dữ liệu hợp lệ
                                        Toast.makeText(EditAppointmentActivity.this, "Vui lòng nhập đầy đủ thông tin và đảm bảo ngày và thời gian là hợp lệ.", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(EditAppointmentActivity.this, "Tên liên hệ không hợp lệ", Toast.LENGTH_SHORT).show();
                                }


                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Tạo và hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        btnBackToAppointment.setOnClickListener(new View.OnClickListener() {
            private boolean isDateTimeValid() {
                return !edtDate.getText().toString().isEmpty() && !edtTime.getText().toString().isEmpty();
            }

            private boolean isLocationValid() {
                return !edtLocation.getText().toString().isEmpty();
            }

            private boolean isNoteValid() {
                return edtNote.getText().toString().length() <= 200;
            }

            @Override
            public void onClick(View v) {
                // Hiển thị dialog xác nhận trước khi đóng activity
                AlertDialog.Builder builder = new AlertDialog.Builder(EditAppointmentActivity.this);
                builder.setMessage("Bạn có muốn lưu thay đổi không?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!edtContactName.getText().toString().isEmpty()) {
                                    if (isDateTimeValid() && isLocationValid() && isNoteValid()) {
                                        // Xử lý khi người dùng chọn "Có"
                                        Appointment updateAppointment = new Appointment(
                                                getDataFromEditText(), edtLocation.getText().toString(), edtNote.getText().toString()
                                        );
                                        updateAppointment.setId((int) appointmentId);
                                        if (selectedContactId == -1) {
                                            assert appointment != null;
                                            updateAppointment.setContactId(appointment.getContactId());
                                        } else {
                                            updateAppointment.setContactId(selectedContactId);
                                        }
                                        appointmentDao.updateAppointment(updateAppointment);
                                        // cài đặt thông báo
                                        setNotification(edtDate.getText().toString(),edtTime.getText().toString());
                                        Toast.makeText(EditAppointmentActivity.this, "Cuộc hẹn đã được cập nhật", Toast.LENGTH_SHORT).show();
                                        // Đóng activity và quay lại activity trước đó
                                        Intent intent = new Intent();
                                        intent.putExtra("appointment_updated", true);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }else {
                                        // Hiển thị thông báo lỗi hoặc yêu cầu người dùng nhập lại dữ liệu hợp lệ
                                        Toast.makeText(EditAppointmentActivity.this, "Vui lòng nhập đầy đủ thông tin và đảm bảo ngày và thời gian là hợp lệ.", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(EditAppointmentActivity.this, "Tên liên hệ không hợp lệ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Xử lý khi người dùng chọn "Không"
                                // Không làm gì cả và đóng dialog
                                finish();
                            }
                        });
                // Tạo và hiển thị dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        // Thiết lập sự kiện khi người dùng nhấn vào EditText để chọn ngày
        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        // Thiết lập sự kiện khi người dùng nhấn vào EditText để chọn thời gian
        edtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
    }

    // Phương thức hiển thị DatePickerDialog
    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Xử lý ngày được chọn
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                edtDate.setText(date);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((DatePickerDialog) dialog).getButton(DatePickerDialog.BUTTON_POSITIVE);
                Button negativeButton = ((DatePickerDialog) dialog).getButton(DatePickerDialog.BUTTON_NEGATIVE);
                positiveButton.setTextColor(getResources().getColor(R.color.my_color));
                negativeButton.setTextColor(getResources().getColor(R.color.my_color));
            }
        });
        datePickerDialog.show();
    }

    // Phương thức hiển thị TimePickerDialog
    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, R.style.TimePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Xử lý thời gian được chọn
                String time = hourOfDay + ":" + minute;
                edtTime.setText(time);
            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
        timePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((TimePickerDialog) dialog).getButton(TimePickerDialog.BUTTON_POSITIVE);
                Button negativeButton = ((TimePickerDialog) dialog).getButton(TimePickerDialog.BUTTON_NEGATIVE);
                positiveButton.setTextColor(getResources().getColor(R.color.my_color));
                negativeButton.setTextColor(getResources().getColor(R.color.my_color));
            }
        });
        timePickerDialog.show();
    }

    private String getDataFromEditText() {
        String date = edtDate.getText().toString();
        String time = edtTime.getText().toString();
        return "Ngày: " + date + ", Giờ: " + time;
    }

    private String[] getDateAndTimeFromEditText(String dateTimeString) {
        String[] parts = dateTimeString.split(", "); // Tách chuỗi thành mảng con dựa trên dấu phẩy và khoảng trắng
        String date = parts[0].substring(parts[0].indexOf(":") + 2); // Lấy phần tử đầu tiên từ mảng (ngày) và loại bỏ "Ngày: " từ phía trước
        String time = parts[1].substring(parts[1].indexOf(":") + 2); // Lấy phần tử thứ hai từ mảng (thời gian) và loại bỏ "Thời gian: " từ phía trước
        String[] dateAndTime = {date, time}; // Tạo một mảng chứa ngày và thời gian
        return dateAndTime;
    }
    public static long convertToMillis(String dateString, String timeString) {
        try {
            String dateTimeString = dateString + " " + timeString;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            Date date = sdf.parse(dateTimeString);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Trả về -1 nếu có lỗi xảy ra
        }
    }
    public void setNotification(String dateString,String timeString){
        long appointmentTimeMillis = convertToMillis(dateString,timeString);
        AppointmentManager appointmentManager = new AppointmentManager();
        appointmentManager.scheduleNotification(this, appointmentTimeMillis);
    }
}
