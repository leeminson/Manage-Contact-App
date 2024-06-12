package com.g1.contactapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import org.jetbrains.annotations.Contract;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateContactActivity extends AppCompatActivity {
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
    List<PhoneNumber> phoneNumbers;
    List<Email> emails;
    long contactId;

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
        AppDatabase database= MyApp.getMyDatabase(this);
        Intent intent = getIntent();
        contactId = intent.getLongExtra("contact",-1);
        init();
        ContactDao contactDao=database.getContactDao();
        PhoneNumberDao phoneNumberDao=database.getPhoneNumberDao();
        CategoryDao categoryDao=database.getCategoryDao();
        ContactCategoryDao contactCategoryDao=database.getContactCategoryDao();
        EmailDao emailDao=database.getEmailDao();
        categoryList=contactCategoryDao.getCategoriesByContactId(contactId);
        Contact c=contactDao.getContact(contactId);

        avatar= c.getImage();
        if (avatar != null) {
            // Convert byte array to Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);

            // Load the Bitmap into the ImageView using Glide
            Glide.with(this)
                    .load(bitmap)
                    .into(imgvAvatar);
        } else {
            // Handle case where no image is found in the database (e.g., set a placeholder image)
            imgvAvatar.setImageResource(R.drawable.account);
        }
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, R.layout.spinner_dropdown_item, categoryDao.getAllCategory());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cateSpinner.setAdapter(adapter);
        cateSpinner.setSelection(categoryList.get(0).getId()-1);
        phoneNumbers=phoneNumberDao.getPhoneNumberbyContact(contactId);
        emails= emailDao.getEmailbyContact(contactId);
        name_input.setText(c.getName());
        Pnum_input.setText(phoneNumbers.get(0).getNumber());
        email_input.setText(emails.get(0).getEmail());
        for(int i=1;i<phoneNumbers.size();i++)
        {
            PhoneNumber p=phoneNumbers.get(i);
            View view=getLayoutInflater().inflate(R.layout.add_txt_layout,null,false);
            ImageButton btnDeleteF=(ImageButton) view.findViewById(R.id.btnDeleteF);
            EditText phone_add=(EditText) view.findViewById(R.id.txt_add);
            phone_add.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone_call,0, 0, 0);
// Optionally, set padding for the drawable
            phone_add.setCompoundDrawablePadding(15);
            phone_add.setText(p.getNumber());
            phone_add.setInputType(InputType.TYPE_CLASS_PHONE);;
            btnDeleteF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmationDialog("Xác nhận", "Bạn có chắc chắn muốn xóa?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    phoneNumbers.remove(p);
                                    phoneNumberDao.deletePhoneNumber(p);
                                    lnPhone.removeView(view);
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            });
                }
            });
            lnPhone.addView(view);
        }

        for(int i=1;i<emails.size();i++){
            Email e=emails.get(i);
            View view=getLayoutInflater().inflate(R.layout.add_txt_layout,null,false);
            ImageButton btnDeleteF=(ImageButton) view.findViewById(R.id.btnDeleteF);
            EditText email_add=(EditText) view.findViewById(R.id.txt_add);
            email_add.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone_call,0, 0, 0);
// Optionally, set padding for the drawable
            email_add.setCompoundDrawablePadding(15);
            email_add.setText(e.getEmail());
            email_add.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            btnDeleteF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmationDialog("Xác nhận", "Bạn có chắc chắn muốn xóa?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    emails.remove(e);
                                    emailDao.deleteEmail(e);
                                    lnEmail.removeView(view);
                                    // Handle positive action (e.g., continue with the operation)
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Handle negative action (e.g., cancel the operation)
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            });
                }
            });
            lnEmail.addView(view);
        }
        for(int i=1;i<categoryList.size();i++)
        {
            Category cate=categoryList.get(i);
            View view = getLayoutInflater().inflate(R.layout.category_spinner, null, false);
            Spinner category =(Spinner) view.findViewById(R.id.spinner_add);
            ImageButton btnDeleteF =(ImageButton) view.findViewById(R.id.btnDeleteFcate);
            category.setAdapter(adapter);
            category.setSelection(categoryList.get(i).getId()-1);
            btnDeleteF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmationDialog("Xác nhận", "Bạn có chắc chắn muốn xóa?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ContactCategory contactCategory=new ContactCategory();
                                    contactCategory.setContactId(contactId);
                                    contactCategory.setCategoryId(cate.getId());
                                    contactCategoryDao.delete(contactCategory);
                                    lnCate.removeView(view);
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            });

                }
            });
            lnCate.addView(view);
        }
        btnAddFCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addedFieldCount < categoryDao.getAllCategory().size()-1) {
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
                    if (addedFieldCount >= categoryDao.getAllCategory().size()-1) {
                        btnAddFCate.setEnabled(false);
                    }
                } else {
                    // Show alert or message indicating maximum limit reached
//                    Toast.makeText(AddContactActivity.this, "Maximum limit reached", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact c=contactDao.getContact(contactId);
                c.setImage(avatar);
                c.setName(name_input.getText().toString());
                contactDao.updateContact(c);
                Email e=emails.get(0);
                e.setEmail(email_input.getText().toString());
                PhoneNumber p=phoneNumbers.get(0);
                p.setNumber(Pnum_input.getText().toString());
                phoneNumberDao.updatePhoneNumber(phoneNumbers.get(0));
                emailDao.updateEmail(e);
                ContactCategory cc=contactCategoryDao.getContactCategoryByCategoryIdAndContactId(contactId,categoryList.get(0).getId());
                contactCategoryDao.delete(cc);
                cc.setCategoryId(((Category) cateSpinner.getSelectedItem()).getId());
                contactCategoryDao.add(cc);
                System.out.println("Phone"+lnPhone.getChildCount());
                if (!addAdditionalPhoneNumbers(contactId, lnPhone,phoneNumberDao)) {
                    showAlert("Số điện thoại trống hoặc không hợp lệ");
                    return; // Stop execution if any additional phone number is empty
                }
                if (!addAdditionalEmails(contactId, lnEmail,emailDao)) {
                    showAlert("Email trống hoặc không hợp lệ");
                    return; // Stop execution if any additional phone number is empty
                }
                if(!addAdditionalCate(contactId,lnCate,contactCategoryDao))
                {
                    showAlert("Mối liên hệ bị trùng,vui lòng chọn lại");
                    return; // Stop execution if any additional phone number is empty
                }
                finish();
                Intent intent1=new Intent(UpdateContactActivity.this,ContactActivity.class);
                intent1.putExtra("contact",contactId);
                startActivity(intent1);
                Toast.makeText(UpdateContactActivity.this,"Liên hệ đã được cập nhật",Toast.LENGTH_SHORT).show();

            }
        });
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }
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
                Glide.with(UpdateContactActivity.this)
                        .load(bitmap)
                        .into(imgvAvatar);

            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();


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
    private void showAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    public void showConfirmationDialog(String title, String message,
                                       DialogInterface.OnClickListener positiveClickListener,
                                       DialogInterface.OnClickListener negativeClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Xác nhận", positiveClickListener);
        builder.setNegativeButton("Hủy", negativeClickListener);
        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public boolean isValidPhoneNumber(String phoneNumber) {
        // Define the pattern for a phone number (modify as needed)
        String regex = "^\\+?(?:84|0)(?:\\d){9,10}$"; // Example: +91-1234567890 or 1234567890

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
    private boolean addAdditionalPhoneNumbers(long contactId, LinearLayout layout,PhoneNumberDao phoneNumberDao) {
        int size=phoneNumbers.size();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            String additionalPhoneNumber = ((EditText) view.findViewById(R.id.txt_add)).getText().toString().trim();
            if(i<size-1)
            {
                if (!additionalPhoneNumber.isEmpty()&&isValidPhoneNumber(additionalPhoneNumber)) {
                    PhoneNumber phoneNumber=phoneNumbers.get(i+1);
                    phoneNumber.setNumber(additionalPhoneNumber);
                    phoneNumberDao.updatePhoneNumber(phoneNumber);
                    System.out.println(additionalPhoneNumber);
                } else {
                    return false; // Return false if any additional phone number is empty
                }
            }
            else if (!additionalPhoneNumber.isEmpty()&&isValidPhoneNumber(additionalPhoneNumber)) {
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
    private boolean addAdditionalCate(long contactId, LinearLayout layout, ContactCategoryDao contactCategoryDao) {
        HashSet<Integer> categoryIds = new HashSet<>(); // HashSet to keep track of Category IDs
        Category cate=(Category) cateSpinner.getSelectedItem();
        categoryIds.add(cate.getId());
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            Category category = (Category) (((Spinner) view.findViewById(R.id.spinner_add)).getSelectedItem());
            System.out.println(categoryIds.contains(category.getId()));
            if(i>=categoryList.size()-1)
            {
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
            else{
                if (category != null) {
                    // Check if the Category ID has already been added
                    if (categoryIds.contains(category.getId())) {
                        // Duplicate found, return false
                        return false;
                    } else {
                        // Add Category ID to HashSet
                        categoryIds.add(category.getId());
                        // Add ContactCategory to database
                        ContactCategory cc=contactCategoryDao.getContactCategoryByCategoryIdAndContactId(contactId,categoryList.get(i+1).getId());
                        contactCategoryDao.delete(cc);
                        cc.setCategoryId(category.getId());
                        contactCategoryDao.add(cc);
                    }
                } else {
                    // Category is null, return false
                    return false;
                }
            }

        }
        // No duplicates found, return true
        return true;
    }
    private boolean addAdditionalEmails(long contactId, LinearLayout layout,EmailDao emailDao) {
        int size=emails.size();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            String additionalEmail = ((EditText) view.findViewById(R.id.txt_add)).getText().toString().trim();
            if(i<size-1)
            {
                if (!additionalEmail.isEmpty()&&isValidEmail(additionalEmail)) {
                    Email e=emails.get(i+1);
                    e.setEmail(additionalEmail);
                    emailDao.updateEmail(e);
                    System.out.println(additionalEmail);
                } else {
                    return false; // Return false if any additional phone number is empty
                }
            }
            else if (!additionalEmail.isEmpty()&&isValidEmail(additionalEmail)) {
                Email e=new Email(additionalEmail);
                e.setContactId(contactId);
                emailDao.addEmail(e);
                System.out.println(additionalEmail);
            } else {
                return false; // Return false if any additional phone number is empty
            }

        }
        return true; // Return true if all additional phone numbers are non-empty
    }



}
