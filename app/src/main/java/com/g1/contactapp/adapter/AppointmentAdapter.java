package com.g1.contactapp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.g1.contactapp.R;
import com.g1.contactapp.activity.EditAppointmentActivity;
import com.g1.contactapp.dao.AppointmentDao;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.model.Appointment;

import java.util.ArrayList;


public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    private Context context;
    private ArrayList<Appointment> appointments;
    ContactDao contactDao;
    private AppointmentDao appointmentDao;

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointments, ContactDao contactDao,AppointmentDao appointmentDao) {
        super(context, 0, appointments);
        this.context = context;
        this.appointments = appointments;
        this.contactDao = contactDao;
        this.appointmentDao = appointmentDao;
    }
    @Nullable
    @Override
    public Appointment getItem(int position){
        return appointments.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_appointment, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.contactImageView = convertView.findViewById(R.id.iv_contact_image);
            viewHolder.contactNameTextView = convertView.findViewById(R.id.txt_appointmentContactName);
            viewHolder.timeTextView = convertView.findViewById(R.id.txt_appointmentTime);
            viewHolder.locationTextView = convertView.findViewById(R.id.txt_appointmentAddress);
            viewHolder.noteTextView = convertView.findViewById(R.id.txt_appointmentNote);
            viewHolder.editButton = convertView.findViewById(R.id.btn_edit);
            viewHolder.deleteButton = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        convertView.setBackgroundResource(R.drawable.rounded_corner);
        // Lấy dữ liệu của cuộc hẹn tại vị trí hiện tại
        Appointment appointment = appointments.get(position);

        // Hiển thị thông tin cuộc hẹn trong giao diện
        //chưa có hình nên tạm thời comment
//        viewHolder.contactImageView.setImageResource(appointment.getContactImage());
        viewHolder.contactNameTextView.setText(contactDao.getContact(appointment.getContactId()).getName());
        viewHolder.timeTextView.setText(appointment.getTime());
        viewHolder.locationTextView.setText(appointment.getLocation());
        viewHolder.noteTextView.setText(appointment.getNote());

        // Xử lý sự kiện chỉnh sửa
        viewHolder.editButton.setTag(position);
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                Appointment appointment = appointments.get(pos);
                long appointmentId = appointment.getId();
                Intent myIntent = new Intent(context, EditAppointmentActivity.class);
                myIntent.putExtra("appointment_id",appointmentId);
                ((Activity) context).startActivityForResult(myIntent, 1001);
            }
        });

        // Xử lý sự kiện xóa
        viewHolder.deleteButton.setTag(position);
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị dialog xác nhận xóa
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có chắc chắn muốn xóa cuộc hẹn này?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //lấy vị trí của item trong danh sách
                        int position = (Integer) v.getTag();
                        //xóa cuộc hẹn trong db
                        appointmentDao.deleteAppointment(getItem(position));
                        //xóa cuộc hẹn khỏi danh sách dựa trên vị trí đó
                        appointments.remove(position);
                        // Cập nhật lại ListView
                        notifyDataSetChanged();
                        // Đóng dialog
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return convertView;
    }

    // Lớp ViewHolder để quản lý hiệu suất
    private static class ViewHolder {
        ImageView contactImageView;
        TextView contactNameTextView;
        TextView timeTextView;
        TextView locationTextView;
        TextView noteTextView;
        ImageButton editButton;
        ImageButton deleteButton;
    }
}
