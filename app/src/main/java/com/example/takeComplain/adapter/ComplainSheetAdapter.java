package com.example.takeComplain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.R;
import com.example.takeComplain.model.ComplainPriority;

import java.util.ArrayList;
import java.util.List;

public class ComplainSheetAdapter extends RecyclerView.Adapter<ComplainSheetAdapter.Viewholder> {

    List<ComplainPriority> complainPriorityList = new ArrayList<>();
    Context context;
    public ComplainSheetAdapterInterface complainSheetAdapterInterface;


    public void setComplainSheetAdapterInterface(ComplainSheetAdapterInterface complainSheetAdapterInterface) {
        this.complainSheetAdapterInterface = complainSheetAdapterInterface;
    }

    public ComplainSheetAdapter(List<ComplainPriority> list, Context context) {
        this.complainPriorityList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.complainlist, parent, false);
        return new Viewholder(itemView) ;

    }


    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        if(complainPriorityList.get(position).getCategoryTile()!=null){
            holder.cat.setText("Category: "+complainPriorityList.get(position).getCategoryTile());
        }
        if(complainPriorityList.get(position).getSubCategoryTile()!=null){
            holder.subcat.setText("Sub-Category: "+complainPriorityList.get(position).getSubCategoryTile());
        }if(complainPriorityList.get(position).getPriorityTile() !=null){
            holder.priority.setText("Priority: "+complainPriorityList.get(position).getPriorityTile());
        }


        holder.remarks.setText("Remarks: "+complainPriorityList.get(position).getRemarks());

        if(complainPriorityList.get(position).getAcDenyId()==1){
            holder.accepted.setText("Accepted");
        }else{
            holder.accepted.setText("Deny");
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complainSheetAdapterInterface.OnItemClickDelete(position);
            }
        });
    }



    @Override
    public int getItemCount() {
        return complainPriorityList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {


        TextView cat,subcat,priority,accepted,remarks;
        ImageView imageView;

        public Viewholder(View itemView) {
            super(itemView);


            cat=itemView.findViewById(R.id.tvCategory);
            subcat=itemView.findViewById(R.id.tvSubCategory);
            priority=itemView.findViewById(R.id.tvPriority);
            remarks=itemView.findViewById(R.id.tvRemarks);
            accepted=itemView.findViewById(R.id.tvAcDc);
            imageView=itemView.findViewById(R.id.tvDelete);



        }
    }

    public interface ComplainSheetAdapterInterface{
         void OnItemClickDelete(int position);

    }
}
