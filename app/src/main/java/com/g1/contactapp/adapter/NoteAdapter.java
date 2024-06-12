package com.g1.contactapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.g1.contactapp.MyApp;
import com.g1.contactapp.R;
import com.g1.contactapp.activity.NoteActivity;
import com.g1.contactapp.activity.NoteDetailActivity;
import com.g1.contactapp.dao.NoteDao;
import com.g1.contactapp.model.Note;
import com.g1.contactapp.utils.AppDatabase;

import java.util.ArrayList;

public class NoteAdapter extends ArrayAdapter<Note> {
    ArrayList<Note> notes;
    AppDatabase database= MyApp.getMyDatabase(getContext());
    NoteDao noteDao=database.getNoteDao();
    public NoteAdapter(Context context, ArrayList<Note> notes) {

        super(context, 0,notes);
        this.notes=notes;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Note note=getItem(position);
        if(convertView==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_note, parent, false);
        }
        Button btnNote=convertView.findViewById(R.id.btnGetNote);
        btnNote.setText(note.getLabel());
        ImageButton btnDelete=convertView.findViewById(R.id.btnDeleteNote);
        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), NoteDetailActivity.class);
                intent.putExtra("note",note);
                getContext().startActivity(intent);
                ((Activity) getContext()).finish();

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setMessage("Bạn có chắc chắn muốn xóa ghi chú này không").setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noteDao.deleteNote(getItem(position));
                        notes.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(getContext(),"Xóa ghi chú thành công",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });
        return convertView;
    }
    @Override
    public int getCount() {
        return notes.size();
    }

    @Nullable
    @Override
    public Note getItem(int position) {
        return notes.get(position);
    }
}
