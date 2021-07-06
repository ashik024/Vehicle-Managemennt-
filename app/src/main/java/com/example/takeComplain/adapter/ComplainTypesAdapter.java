package com.example.takeComplain.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.R;
import com.example.mbw.model.LookUp;
import com.example.takeComplain.model.ComplainTypes;

import java.util.ArrayList;
import java.util.List;

public class ComplainTypesAdapter extends RecyclerView.Adapter<ComplainTypesAdapter.ComplainViewHolder> {

    List<ComplainTypes> lookUpComplainList = new ArrayList<>();
    ComplainTypesInterface complainTypesInterface;

    Context context;


    public void setComplainTypesInterface(ComplainTypesInterface complainTypesInterface) {
        this.complainTypesInterface = complainTypesInterface;
    }

    public ComplainTypesAdapter(List<ComplainTypes> lookUpComplainList, Context context) {
        this.lookUpComplainList = lookUpComplainList;
        this.context = context;
    }

    @NonNull

    @Override
    public ComplainViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.complaincheckbox, parent, false);
        return new ComplainViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ComplainViewHolder holder, int position) {

        holder.checkBox.setText(lookUpComplainList.get(position).getName());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if (holder.checkBox.isChecked()){
                  // Log.e("lookUpComplainList",""+ lookUpComplainList.get(position).getCode());
                 //  Log.e("lookUpComplainList",""+ lookUpComplainList.get(position).getName());

                   complainTypesInterface.insert(lookUpComplainList.get(position).getCode(),position);

               }else if(!holder.checkBox.isChecked()){
                 //  Log.e("lookUpComplainListfff",""+ lookUpComplainList.get(position).getCode());
                 //  Log.e("lookUpComplainListff",""+ lookUpComplainList.get(position).getName());
                   complainTypesInterface.delete(lookUpComplainList.get(position).getCode(),position);
               }
            }
        });

    }

    @Override
    public int getItemCount() {
        return lookUpComplainList.size();
    }

    public class ComplainViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        public ComplainViewHolder(@NonNull  View itemView) {
            super(itemView);

            checkBox= itemView.findViewById(R.id.checkBox);
        }
    }

    public interface ComplainTypesInterface{
        public void insert(int code,int position);
        public void delete(int code,int position);
    }

    public void clear(){
        lookUpComplainList.clear();
    }
}
