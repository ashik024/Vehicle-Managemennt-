package com.example.takeComplain;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ImageLocalSave extends RealmObject {
    @PrimaryKey
    private int id;
    public RealmList<byte[]> imageArrayList;

    public ImageLocalSave() {
    }

    public ImageLocalSave(RealmList<byte[]> imageArrayList) {
        this.imageArrayList = imageArrayList;
    }

    public RealmList<byte[]> getImageArrayList() {
        return imageArrayList;
    }

    public void setImageArrayList(RealmList<byte[]> imageArrayList) {
        this.imageArrayList = imageArrayList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
