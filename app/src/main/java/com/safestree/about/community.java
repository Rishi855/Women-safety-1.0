package com.safestree.about;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.safestree.R;

import java.util.ArrayList;

public class community extends Fragment {

    RecyclerView recyclerView;
    ArrayList<DataSingle> dataList;
    MyAdaptor adaptor;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("news");

    public community() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_community, container, false);

        recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dataList = new ArrayList<>();
        adaptor = new MyAdaptor(dataList,getContext());
        recyclerView.setAdapter(adaptor);

        databaseReference.child("admin").child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    DataSingle dataClass = dataSnapshot.getValue(DataSingle.class);
                    dataList.add(dataClass);
                }
//                DataClass dataClass = new DataClass();
//                dataClass =  snapshot.getValue(DataClass.class);
//                if(dataClass==null) return;
//                for(int i=0;i<dataClass.image.size();i++)
//                {
//                    DataSingle dataSingle = new DataSingle(dataClass.image.get(i),dataClass.caption.get(i));
//                    dataList.add(dataSingle);
//                }
                adaptor.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}