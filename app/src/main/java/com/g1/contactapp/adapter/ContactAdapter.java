package com.g1.contactapp.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.g1.contactapp.R;
import com.g1.contactapp.activity.ContactActivity;
import com.g1.contactapp.model.Contact;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> implements Filterable {
    private ArrayList<Contact> contacts; // Danh sách gốc
    private ArrayList<Contact> filteredContacts; // Danh sách đã lọc
    private ContactFilter filter; // Bộ lọc

    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
        this.contacts = contacts;
        this.filteredContacts = contacts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Contact contact = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_contact, parent, false);
        }

        // Lookup view for data population
        TextView textViewName = convertView.findViewById(R.id.textViewName);
//        TextView textViewPhone = convertView.findViewById(R.id.textViewPhone);
        ImageView imageView = convertView.findViewById(R.id.image);
        byte[] image = contact.getImage();
        if (image != null) {
            // Convert byte array to Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

            // Load the Bitmap into the ImageView using Glide
            Glide.with(convertView)
                    .load(bitmap)
                    .into(imageView);
        } else {
            // Handle case where no image is found in the database (e.g., set a placeholder image)
            imageView.setImageResource(R.drawable.account);
        }
        // Populate the data into the template view using the data object
        textViewName.setText(contact.getName());
//        textViewPhone.setText(contact.getPhone());

        // Sự kiện nhấp vào cho mỗi mục liên hệ
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến trang chi tiết liên hệ với thông tin của liên hệ được nhấp vào
                Intent intent = new Intent(getContext(), ContactActivity.class);
                intent.putExtra("contact", contact.getId());
                getContext().startActivity(intent);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public int getCount() {
        return filteredContacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return filteredContacts.get(position);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ContactFilter();
        }
        return filter;
    }

    private class ContactFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<Contact> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // Nếu không có văn bản nào, trả về danh sách gốc
                results.count = contacts.size();
                results.values = contacts;
            } else {
                // Lọc danh sách theo văn bản được nhập vào
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Contact contact : contacts) {
                    if (contact.getName().toLowerCase().contains(filterPattern)
//                            || contact.getPhone().toLowerCase().contains(filterPattern)
                    ) {
                        filteredList.add(contact);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredContacts = (ArrayList<Contact>) results.values;
            notifyDataSetChanged(); // Cập nhật giao diện với danh sách đã lọc
        }
    }
}



