package com.g1.contactapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.adapter.CustomSpinnerAdapter;
import com.g1.contactapp.dao.CategoryDao;
import com.g1.contactapp.dao.ContactCategoryDao;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.dao.EmailDao;
import com.g1.contactapp.dao.PhoneNumberDao;
import com.g1.contactapp.model.Category;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.model.ContactCategory;
import com.g1.contactapp.model.Email;
import com.g1.contactapp.model.PhoneNumber;
import com.g1.contactapp.utils.AppDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddContactActivity extends AppCompatActivity {
    LinearLayout lnPhone,lnEmail,lnCate;
    Button btnAddPhoto,btnInsert;
    ImageButton btnAddFPhone,btnAddFEmail,btnAddFCate;
    EditText name_input,Pnum_input,email_input;
    Spinner cateSpinner;
    List<Category> categoryList;
    ImageView imgvAvatar;
    byte[] avatar;
    private static int MAX_FIELD_COUNT;
    private int addedFieldCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 120 * 1280 * 960); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_add_contact);
        init();
        AppDatabase database= MyApp.getMyDatabase(this);
        ContactDao contactDao=database.getContactDao();
        PhoneNumberDao phoneNumberDao=database.getPhoneNumberDao();
        EmailDao emailDao=database.getEmailDao();
        CategoryDao categoryDao=database.getCategoryDao();
        ContactCategoryDao contactCategoryDao=database.getContactCategoryDao();
        categoryList=categoryDao.getAllCategory();
        MAX_FIELD_COUNT=categoryList.size();
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.spinner_dropdown_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cateSpinner.setAdapter(adapter);
        btnAddFPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view=getLayoutInflater().inflate(R.layout.add_txt_layout,null,false);
                ImageButton btnDeleteF=(ImageButton) view.findViewById(R.id.btnDeleteF);
                EditText phone_add=(EditText) view.findViewById(R.id.txt_add);
                phone_add.setHint("Số điện thoại");
                phone_add.setInputType(InputType.TYPE_CLASS_PHONE);
                btnDeleteF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lnPhone.removeView(view);
                    }
                });
                lnPhone.addView(view);
            }
        });
        btnAddFEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view=getLayoutInflater().inflate(R.layout.add_txt_layout,null,false);
                ImageButton btnDeleteF=(ImageButton) view.findViewById(R.id.btnDeleteF);
                EditText email_add=(EditText) view.findViewById(R.id.txt_add);
                email_add.setHint("Email");
                email_add.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                btnDeleteF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lnEmail.removeView(view);
                    }
                });
                lnEmail.addView(view);
            }
        });

        btnAddFCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addedFieldCount < MAX_FIELD_COUNT-1) {
                    // Inflate the layout for the new Spinner field
                    View view = getLayoutInflater().inflate(R.layout.category_spinner, null, false);
                    Spinner category =(Spinner) view.findViewById(R.id.spinner_add);
                    ImageButton btnDeleteF =(ImageButton) view.findViewById(R.id.btnDeleteFcate);

                    // Set click listener to delete button
                    btnDeleteF.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Remove the Spinner field view
                            lnCate.removeView(view);
                            // Decrease the counter when field is deleted
                            addedFieldCount--;
                            // Enable the "Add Field" button
                            btnAddFCate.setEnabled(true);
                        }
                    });

                    // Set adapter to Spinner
                    category.setAdapter(adapter);

                    // Add the new Spinner field view to the layout
                    lnCate.addView(view);

                    // Increase the counter
                    addedFieldCount++;

                    // If maximum count is reached, disable the button
                    if (addedFieldCount >= MAX_FIELD_COUNT-1) {
                        btnAddFCate.setEnabled(false);
                    }
                } else {
                    // Show alert or message indicating maximum limit reached
//                    Toast.makeText(AddContactActivity.this, "Maximum limit reached", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = name_input.getText().toString().trim();
                if (name.isEmpty()) {
                    showAlert("Vui lòng nhập tên liên hệ");
                    return;
                }

                byte[] image = avatar;

                // Perform validation for email field
                String emailText = email_input.getText().toString().trim();
                if (emailText.isEmpty()) {
                    showAlert("Vui lòng nhập địa chỉ email");
                    return;
                } else if (!isValidEmail(emailText)) {
                    showAlert("Địa chỉ email không hợp lệ");
                    return;
                }

                // Perform validation for phone number field
                String phoneNumberText = Pnum_input.getText().toString().trim();
                if (phoneNumberText.isEmpty()) {
                    showAlert("Vui lòng nhập số điện thoại");
                    return;
                } else if (!isValidPhoneNumber(phoneNumberText)) {
                    showAlert("Số điện thoại không hợp lệ");
                    return;
                }
                // Proceed with adding contact
                Contact c = new Contact(name);
                c.setImage(image);
                long contactId = contactDao.addContact(c);
                Email email = new Email(emailText);
                email.setContactId(contactId);
                emailDao.addEmail(email);
                PhoneNumber phoneNumber=new PhoneNumber(Pnum_input.getText().toString());
                phoneNumber.setContactId(contactId);
                Category category=(Category) cateSpinner.getSelectedItem();
                ContactCategory contactCategory=new ContactCategory();
                contactCategory.setContactId(contactId);
                contactCategory.setCategoryId(category.getId());
                contactCategoryDao.add(contactCategory);
                phoneNumberDao.addPhoneNumber(phoneNumber);
                if (!addAdditionalEmails(contactId, lnEmail,emailDao)) {
                    showAlert("Vui lòng điền đủ thông tin email");
                    return; // Stop execution if any additional email is empty
                }
                // Add additional phone numbers
                if (!addAdditionalPhoneNumbers(contactId, lnPhone,phoneNumberDao)) {
                    showAlert("Vui lòng điền đủ thông tin số điện thoại");
                    return; // Stop execution if any additional phone number is empty
                }
                if(!addAdditionalCate(contactId,lnCate,contactCategoryDao))
                {
                    showAlert("Mối liên hệ bị trùng,vui lòng chọn lại");
                    return; // Stop execution if any additional phone number is empty
                }
                Toast.makeText(AddContactActivity.this,"Liên hệ mới đã được thêm",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(AddContactActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1&&resultCode == RESULT_OK && data != null) {
            Uri avatarUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), avatarUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                avatar=stream.toByteArray();
                // Load the Bitmap into the ImageView using Glide
                Glide.with(AddContactActivity.this)
                        .load(bitmap)
                        .into(imgvAvatar);

            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }
    private boolean addAdditionalEmails(long contactId, LinearLayout layout,EmailDao emailDao) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            String additionalEmail = ((EditText) view.findViewById(R.id.txt_add)).getText().toString().trim();

            if (!additionalEmail.isEmpty()) {
                Email email=new Email(additionalEmail);
                email.setContactId(contactId);
                emailDao.addEmail(email);
                System.out.println(additionalEmail);
            } else {
                return false; // Return false if any additional email is empty
            }
        }
        return true; // Return true if all additional emails are non-empty
    }
    private boolean addAdditionalCate(long contactId, LinearLayout layout, ContactCategoryDao contactCategoryDao) {
        HashSet<Integer> categoryIds = new HashSet<>(); // HashSet to keep track of Category IDs
        Category cate=(Category) cateSpinner.getSelectedItem();
        categoryIds.add(cate.getId());
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            Category category = (Category) (((Spinner) view.findViewById(R.id.spinner_add)).getSelectedItem());
            System.out.println(categoryIds.contains(category.getId()));
            if (category != null) {
                // Check if the Category ID has already been added
                if (categoryIds.contains(category.getId())) {
                    // Duplicate found, return false
                    return false;
                } else {
                    // Add Category ID to HashSet
                    categoryIds.add(category.getId());
                    // Add ContactCategory to database
                    ContactCategory contactCategory = new ContactCategory();
                    contactCategory.setContactId(contactId);
                    contactCategory.setCategoryId(category.getId());
                    contactCategoryDao.add(contactCategory);
                }
            } else {
                // Category is null, return false
                return false;
            }
        }
        // No duplicates found, return true
        return true;
    }
    private boolean addAdditionalPhoneNumbers(long contactId, LinearLayout layout,PhoneNumberDao phoneNumberDao) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            String additionalPhoneNumber = ((EditText) view.findViewById(R.id.txt_add)).getText().toString().trim();
            if (!additionalPhoneNumber.isEmpty()) {
                PhoneNumber phoneNumber=new PhoneNumber(additionalPhoneNumber);
                phoneNumber.setContactId(contactId);
                phoneNumberDao.addPhoneNumber(phoneNumber);
                System.out.println(additionalPhoneNumber);
            } else {
                return false; // Return false if any additional phone number is empty
            }
        }
        return true; // Return true if all additional phone numbers are non-empty
    }
    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    public void init()
    {
        lnPhone=findViewById(R.id.lnPhone);
        lnEmail=findViewById(R.id.lnEmail);
        lnCate=findViewById(R.id.lnCate);
        btnAddPhoto=findViewById(R.id.btnAddPhoto);
        btnAddFPhone=findViewById(R.id.btnAddFPhone);
        btnAddFEmail=findViewById(R.id.btnAddFEmail);
        btnAddFCate=findViewById(R.id.btnAddFCategory);
        cateSpinner=findViewById(R.id.cateSpinner);
        name_input=findViewById(R.id.name_input);
        Pnum_input=findViewById(R.id.Pnum_input);
        email_input=findViewById(R.id.email_input);
        imgvAvatar=findViewById(R.id.imgvAvatar);
        btnInsert=findViewById(R.id.btnInsert);
    }
    public void setToolbar(){

    }
    public boolean isValidPhoneNumber(String phoneNumber) {
        // Define the pattern for a phone number (modify as needed)
        String regex = "^^\\+?(?:84|0)(?:\\d){9,10}$"; // Example: +91-1234567890 or 1234567890

        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher with the input phone number
        Matcher matcher = pattern.matcher(phoneNumber);

        // Check if the input matches the pattern
        return matcher.matches();
    }
    public boolean isValidEmail(String email) {
        // Define the pattern for an email address (modify as needed)
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; // Example: example@example.com

        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);

        // Create a matcher with the input email address
        Matcher matcher = pattern.matcher(email);

        // Check if the input matches the pattern
        return matcher.matches();
    }


}