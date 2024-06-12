package com.g1.contactapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.adapter.NoteAdapter;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.dao.NoteDao;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.model.Note;
import com.g1.contactapp.utils.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity {
    Toolbar tb;
    ImageButton btnDelete;
    Button btnAdd;
    Button btnShowNote;
    ListView lv_note;
    List<Note> noteList;
    long contactId;
    ImageView avatarNote;
    TextView c_nameNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        init();
        Intent intent = getIntent();
        contactId = intent.getLongExtra("contact",-1);
        AppDatabase database= MyApp.getMyDatabase(this);
        NoteDao noteDao=database.getNoteDao();
        ContactDao contactDao=database.getContactDao();
        Contact c=contactDao.getContact(contactId);
        byte[] imageBytes = c.getImage();
        if(imageBytes!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Glide.with(this)
                    .load(bitmap)
                    .into(avatarNote);
        }
        else avatarNote.setImageResource(R.drawable.account);
        c_nameNote.setText(c.getName());
        noteList=new ArrayList<>();
        noteList=noteDao.getNotebyContact(contactId);
        NoteAdapter noteAdapter=new NoteAdapter(this, (ArrayList<Note>) noteList);
        lv_note.setAdapter(noteAdapter);

        btnAdd=findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent=new Intent(NoteActivity.this,AddNoteActivity.class);
                intent.putExtra("contact",contactId);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note_toolbar,menu);
        return super.onCreateOptionsMenu(menu);
    }
    public void init(){
        tb=(Toolbar) findViewById(R.id.tbNote);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        c_nameNote=findViewById(R.id.c_nameNote);
        avatarNote=findViewById(R.id.avatarNote);
        lv_note=findViewById(R.id.lv_note);

    }
}