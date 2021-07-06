package com.example.takeComplain.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.common_class.CommonClass;
import com.example.mbw.R;
import com.example.takeComplain.ImageLocalSave;
import com.example.utils.BitmapUtils;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.PrescriptionImageViewHolder> {

    RealmList<byte[]> imagePrescription = new RealmList<>();
    Context context;

    public ImageAdapter(RealmList<byte[]> imagePrescription, Context context) {
        this.imagePrescription = imagePrescription;
        this.context = context;

    }

    @NonNull
    @Override
    public PrescriptionImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new PrescriptionImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionImageViewHolder holder, final int position) {


        Glide.with(context).load(BitmapUtils.convertCompressedByteArrayToBitmap(imagePrescription.get(position))).into( holder.ivPrescriptionView);
     //   holder.ivPrescriptionView.setImageBitmap(BitmapUtils.convertCompressedByteArrayToBitmap(imagePrescription.get(position)));

        holder.ivDeletePrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RealmList<byte[]> imagePrescriptionDelete = new RealmList<>();
                final ImageLocalSave imageLocalSave=new ImageLocalSave();
                Realm realm = Realm.getDefaultInstance();
                for(int i=0;i<imagePrescription.size();i++)
                {
                   // Log.e("imageSize", "execute:11 " + imagePrescription.size());
                    if(!imagePrescription.get(i).equals(imagePrescription.get(position)))
                    {
                        imagePrescriptionDelete.add(imagePrescription.get(i));
                    }
                }
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                   /*     ImageLocalSave syncCompleteOrder = realm.where(ImageLocalSave.class).findFirst();
                        assert syncCompleteOrder != null;
                        syncCompleteOrder.deleteFromRealm();*/
                        final RealmResults<ImageLocalSave> alldata = realm.where(ImageLocalSave.class).findAll();

                       // Log.e("imageSize", "execute: " + alldata.size());
                        if (alldata.size() != 0) {
                            alldata.deleteAllFromRealm();
                        }
                    }
                });
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        imageLocalSave.setImageArrayList(imagePrescriptionDelete);

                       // Log.e("imageSize", "execute:22 " + imagePrescriptionDelete.size());
                      //  imageLocalSave.setId(1);
                        realm.insertOrUpdate(imageLocalSave);
                    }
                });
                //imagePrescription.remove(position);
                removeItem(position);
                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.e("datacheakeras", "onClick: "+imagePrescription.size() );
                CommonClass.imagePrescriptionFinal.clear();
                CommonClass.imagePrescriptionFinal.addAll(imagePrescription);
                CommonClass.selectedImage=imagePrescription.get(position);

            }
        });


    }

    @Override
    public int getItemCount() {
        return imagePrescription.size();
    }

    class PrescriptionImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDeletePrescription, ivPrescriptionView;

        public PrescriptionImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDeletePrescription = itemView.findViewById(R.id.ivDelete);
            ivPrescriptionView = itemView.findViewById(R.id.ivImage);
            ivPrescriptionView.setRotation(90);
        }
    }

    public void getUpdateImageList(RealmList<byte[]> imagePrescription) {
        this.imagePrescription = imagePrescription;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
        imagePrescription.remove(position);
    }
    public void clear(){
        imagePrescription.clear();
    }
}

