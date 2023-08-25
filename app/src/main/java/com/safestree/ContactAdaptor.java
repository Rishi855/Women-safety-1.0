package com.safestree;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;


public class ContactAdaptor extends ArrayAdapter<CustomListView> {
    //    static int temp=0;
    public ContactAdaptor(@NonNull Context context, ArrayList<CustomListView> arrayList) {
        super(context, 0, arrayList);
    }
    static class ViewHolder
    {
        ImageView imageView;
        TextView contactName;
        TextView contactNumber;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
//        View currentItemView = convertView;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_contact_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.imageView);
            viewHolder.contactName = convertView.findViewById(R.id.contactName);
            viewHolder.contactNumber = convertView.findViewById(R.id.contactNumber);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CustomListView currentNumberPosition = getItem(position);
        viewHolder.imageView.setImageResource(currentNumberPosition.getContactImage());
        viewHolder.contactName.setText(currentNumberPosition.getContactName());
        viewHolder.contactNumber.setText(currentNumberPosition.getContactNumber());
//        Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
//            CustomListView currentNumberPosition = getItem(position);
//            ImageView numbersImage = currentItemView.findViewById(R.id.imageView);
//            assert currentNumberPosition != null;
//            numbersImage.setImageResource(currentNumberPosition.getContactImage());
//
//            TextView contactName = currentItemView.findViewById(R.id.contactName);
//            contactName.setText(currentNumberPosition.getContactName());
//
//            TextView contactNumber = currentItemView.findViewById(R.id.contactNumber);
//            contactNumber.setText(currentNumberPosition.getContactNumber());
//
        SharedPreferences pSharedPref = getContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String a = pSharedPref.getString("emergencyName", "");
        String b = pSharedPref.getString("emergencyContact", "");
        if(currentNumberPosition.getContactNumber().toString().equals(b) && currentNumberPosition.getContactName().equals(a)) {
//            Toast.makeText(getContext(), ""+position, Toast.LENGTH_SHORT).show();
            LinearLayout someView = convertView.findViewById(R.id.customItem);
//            View root = someView.getRootView();
//            someView.setVisibility(View.GONE);
            someView.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.bg_selected_emergency));

//                Toast.makeText(getContext(), position + " " + a + " " + currentNumberPosition.getContactNumber() + " " + b, Toast.LENGTH_SHORT).show();
        }
        else {
            LinearLayout someView = convertView.findViewById(R.id.customItem);
//            View root = someView.getRootView();
//            someView.setVisibility(View.GONE);
            someView.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.bg_each_contact_item));
        }
//            Toast.makeText(getContext(), position + " " + a + " " + currentNumberPosition.getContactNumber() + " " + b, Toast.LENGTH_SHORT).show();

        return convertView;
    }
}
