package com.g1.contactapp.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.dao.NoteDao;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.model.Note;
import com.g1.contactapp.utils.AppDatabase;

public class AddNoteActivity extends AppCompatActivity {
    Toolbar tb;
    ImageButton btnEmail;
    EditText label,noteDetail;
    TextView c_nameNote;
    ImageView avatarNote;
    Button btnSave;
    long contactId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        init();
        Intent intent = getIntent();
        contactId = intent.getLongExtra("contact",-1);
        AppDatabase database= MyApp.getMyDatabase(this);
        ContactDao contactDao=database.getContactDao();
        NoteDao noteDao=database.getNoteDao();
        label.setHint("Nhãn ghi chú");
        noteDetail.setHint("Nội dung ghi chú");
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
//        Intent intent=getIntent();
//        Bundle bundle=intent.getBundleExtra("ghichu");
//        String nhan=bundle.getString("nhan");
//        String noidung=bundle.getString("noidung");
//        note.setText(nhan);
//        noteDetail.setText(noidung);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String labelValue = label.getText().toString().trim();
                String noteDetailValue = noteDetail.getText().toString().trim();

                if (labelValue.isEmpty() || noteDetailValue.isEmpty()) {
                    // Hiển thị thông báo khi có trường trống
                    AlertDialog.Builder emptyFieldsDialog = new AlertDialog.Builder(AddNoteActivity.this);
                    emptyFieldsDialog.setTitle("Thông báo");
                    emptyFieldsDialog.setMessage("Vui lòng điền đầy đủ thông tin vào các trường.");
                    emptyFieldsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    emptyFieldsDialog.show();
                } else {
                    // Nếu các trường đều được điền đầy đủ, thực hiện thêm ghi chú vào cơ sở dữ liệu
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNoteActivity.this);
                    builder.setMessage("Bạn có muốn lưu ghi chú này").setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Note note = new Note(labelValue, noteDetailValue);
                            note.setContactId(contactId);
                            noteDao.addNote(note);
                            Intent intent1 = new Intent(AddNoteActivity.this, NoteActivity.class);
                            intent1.putExtra("contact", contactId);
                            dialog.dismiss();
                            finish();
                            startActivity(intent1);
                            Toast.makeText(AddNoteActivity.this,"Ghi chú mới cho liên hệ "+c.getName()+" đã được thêm",Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    Dialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }
    public void init()
    {
        tb=findViewById(R.id.tbNoteDetail);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("Ghi chú");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        label=findViewById(R.id.noteTxt);
        noteDetail=findViewById(R.id.noteDetailTxt);
        btnSave=findViewById(R.id.btnSave);
        c_nameNote=findViewById(R.id.c_nameNote);
        avatarNote=findViewById(R.id.avatarNote);
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(AddNoteActivity.this, NoteActivity.class);
        intent1.putExtra("contact", contactId);
        finish();
        startActivity(intent1);
        super.onBackPressed();
    }
}
