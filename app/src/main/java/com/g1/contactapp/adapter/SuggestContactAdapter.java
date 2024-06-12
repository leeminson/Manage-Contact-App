package com.g1.contactapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.g1.contactapp.MyApp;
import com.g1.contactapp.dao.ContactDao;
import com.g1.contactapp.dao.PhoneNumberDao;
import com.g1.contactapp.model.Contact;
import com.g1.contactapp.model.PhoneNumber;
import com.g1.contactapp.utils.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public class SuggestContactAdapter extends ArrayAdapter<Contact> implements Filterable {
    private final List<Contact> contacts;
    private List<Contact> filteredContacts;


    public SuggestContactAdapter(@NonNull Context context, @NonNull List<Contact> contacts) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        this.contacts = contacts;
        this.filteredContacts = new ArrayList<>(contacts);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Contact> filteredList = new ArrayList<>();

                if (constraint != null && constraint.length() >= 2) {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Contact contact : contacts) {
                        if (contact.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(contact);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    filteredContacts = (List<Contact>) results.values;
                    notifyDataSetChanged();
                } else {
                    filteredContacts.clear();
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        Contact contact = getItem(position);
        if (contact != null) {
            textView.setText(contact.getName()+" "+contact.getId());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return filteredContacts.size();
    }

    @Nullable
    @Override
    public Contact getItem(int position) {
        return filteredContacts.get(position);
    }

}