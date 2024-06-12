package com.g1.contactapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.dao.ContactCategoryDao;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.dao.EmailDao;
import com.g1.contactapp.dao.PhoneNumberDao;
import com.g1.contactapp.model.Category;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.model.Email;
import com.g1.contactapp.model.PhoneNumber;
import com.g1.contactapp.utils.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {
    Toolbar tb1;
    Button btnNote;
    ImageButton btnEmail;
    ImageButton btnCall;
    LinearLayout lnPhoneContact,lnEmailContact;
    TextView nametxt,relation;
    ImageView imgAvatarContact;
    long contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        AppDatabase database= MyApp.getMyDatabase(this);
        init();
        Intent intent = getIntent();
        contactId = intent.getLongExtra("contact",-1);
        PhoneNumberDao phoneNumberDao=database.getPhoneNumberDao();
        ContactDao contactDao=database.getContactDao();
        ContactCategoryDao contactCategoryDao=database.getContactCategoryDao();
        EmailDao emailDAo=database.getEmailDao();
        Contact c=contactDao.getContact(contactId);
        tb1.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.modify){
                    Intent intent = new Intent(ContactActivity.this, UpdateContactActivity.class);
                    intent.putExtra("contact",contactId);
                    startActivity(intent);
                    finish();
                    return true;
                }
                else if(id==R.id.delete)
                {
                    showConfirmationDialog("Xác nhận", "Bạn có chắc chắn muốn xóa?",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    contactDao.deleteContact(c);
                                    finish();
                                    Intent intent = new Intent(ContactActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    dialog.dismiss(); // Dismiss the dialog
                                    Toast.makeText(ContactActivity.this,"Xóa liên hệ thành công",Toast.LENGTH_SHORT).show();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            });
                    return true;
                }
                return false;
            }
        });
        List<Category> categories=contactCategoryDao.getCategoriesByContactId(contactId);
        String relate="";
        for (int i = 0; i < categories.size(); i++) {
            relate += categories.get(i).getCategory();
            if (i < categories.size() - 1) {
                relate += " , ";
            }
        }
        relation.setText(relate);
        relation.setMovementMethod(new ScrollingMovementMethod());
        nametxt.setText(c.getName());
        byte[] imageBytes = c.getImage();
        if(imageBytes!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Glide.with(this)
                    .load(bitmap)
                    .into(imgAvatarContact);
        }
        else imgAvatarContact.setImageResource(R.drawable.account);
// Convert the byte array to a Bitmap

        List<PhoneNumber> phoneNumbers= phoneNumberDao.getPhoneNumberbyContact( contactId);
        System.out.println(phoneNumbers.size());
        for (PhoneNumber p:phoneNumbers)
        {
            View view=getLayoutInflater().inflate(R.layout.list_item,null,false);
            Button btn=view.findViewById(R.id.btnF);
            btn.setText(p.getNumber());
            btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.phone_call,0, 0, 0);
// Optionally, set padding for the drawable
            btn.setCompoundDrawablePadding(15);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+btn.getText()));
                    if (ContextCompat.checkSelfPermission(ContactActivity.this,
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ContactActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                11);
                    }
                    startActivity(intent);
                }
            });
            lnPhoneContact.addView(view);
        }
        List<Email> emails=emailDAo.getEmailbyContact(contactId);
        System.out.println(emails.size());
        for(Email e:emails)
        {
            View view=getLayoutInflater().inflate(R.layout.list_item,null,false);
            Button btn=view.findViewById(R.id.btnF);
            System.out.println(e.getEmail());
            btn.setText(e.getEmail());
            btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mail,0, 0, 0);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL,new String[]{e.getEmail()});
                    intent.putExtra(Intent.EXTRA_SUBJECT,"");
                    intent.putExtra(Intent.EXTRA_TEXT,"");
                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent,"Choose app"));
                }
            });
            lnEmailContact.addView(view);
        }
        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ContactActivity.this,NoteActivity.class);
                intent.putExtra("contact",contactId);
                startActivity(intent);
            }
        });

        btnEmail=findViewById(R.id.imgBtnEmail);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{emails.get(0).getEmail()});
                intent.putExtra(Intent.EXTRA_SUBJECT,"");
                intent.putExtra(Intent.EXTRA_TEXT,"");
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent,"Choose app"));
            }
        });
        btnCall=findViewById(R.id.imgBtnCall);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phoneNumbers.get(0).getNumber()));
                if (ContextCompat.checkSelfPermission(ContactActivity.this,
                        Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ContactActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            11);
                }
                startActivity(intent);
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutoolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void init(){
        tb1=(Toolbar) findViewById(R.id.tb1);
        setSupportActionBar(tb1);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnNote=findViewById(R.id.btnNote);
        lnPhoneContact=findViewById(R.id.lnPhoneContact);
        nametxt=findViewById(R.id.nameTxt);
        imgAvatarContact=findViewById(R.id.imgAvatarContact);
        lnEmailContact=findViewById(R.id.lnEmailContact);
        relation=findViewById(R.id.relation);
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
}