package com.g1.contactapp.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import com.bumptech.glide.Glide;
import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.dao.NoteDao;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.model.Note;
import com.g1.contactapp.utils.AppDatabase;

public class NoteDetailActivity extends AppCompatActivity {
    Toolbar tb;
    ImageButton btnEmail;
    EditText label,noteDetail;
    Button btnSave;
    TextView c_nameNote;
    ImageView avatarNote;
    Note note;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        init();
        AppDatabase database= MyApp.getMyDatabase(this);
        NoteDao noteDao=database.getNoteDao();
        ContactDao contactDao=database.getContactDao();
        Intent intent = getIntent();
        note = (Note) intent.getSerializableExtra("note");
        Contact c=contactDao.getContact(note.getContactId());
        label.setText(note.getLabel());
        noteDetail.setText(note.getContent());
        c_nameNote.setText(c.getName());
        byte[] imageBytes = c.getImage();
        if(imageBytes!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Glide.with(this)
                    .load(bitmap)
                    .into(avatarNote);
        }
        else avatarNote.setImageResource(R.drawable.account);
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
                    AlertDialog.Builder emptyFieldsDialog = new AlertDialog.Builder(NoteDetailActivity.this);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoteDetailActivity.this);
                    builder.setMessage("Bạn có muốn lưu ghi chú này").setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            note.setContent(noteDetailValue);
                            note.setLabel(labelValue);
                            noteDao.updateNote(note);
                            Intent intent1 = new Intent(NoteDetailActivity.this, NoteActivity.class);
                            intent1.putExtra("contact", note.getContactId());
                            dialog.dismiss();
                            finish();
                            startActivity(intent1);
                            Toast.makeText(NoteDetailActivity.this,"Ghi chú "+note.getLabel()+"cho liên hệ "+c.getName()+" cập nhật thành công",Toast.LENGTH_SHORT).show();
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
}