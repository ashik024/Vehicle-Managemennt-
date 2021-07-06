package com.example.common_class;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.login.model.User;
import com.example.takeComplain.model.ComplainModel;
import com.google.android.gms.maps.model.LatLng;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmList;

public class CommonClass {
    public static double totalAmount=0;
    public static int OrderState=0;
    public static Map<String, Integer> OrderDataCheck = new HashMap<>();
    public static Map<String, Integer> chemistNAme = new HashMap<>();
    public static Map<Integer, Integer> radioButtonChecker = new HashMap<>();
    public static Map<String, String> dist = new HashMap<>();
    public static Map<String, String> distributorList = new HashMap<>();
    public static ArrayList<String> distributorName= new ArrayList<>();
/*    public static List<Product> SelectedProductList = new ArrayList<>();
    public static List<Product> SelectedDepoList = new ArrayList<>();

    public static List<Product> productsList = new ArrayList<>();*/
    public static int state = 0;

    public static String cash="";
    public static String collectionDate,delivaryDate;
    public static int stateProuduct=0;
    public static String distributorid="0";
    public static int deleteId=0;
    public static String discount="0";

    public static int collectionState=0;
    public static  String name="name";


    public static Map<String, ArrayList<String>> areaToTerr = new HashMap<>();
    public static ArrayList<String> areaList=new ArrayList<>();
    public static ArrayList<String>DepoList=new ArrayList<>();
    public static Map<String, Integer> teriturriCheaker = new HashMap<>();

    public static Map<String, String> stockInformation = new HashMap<>();


    public static String api_key="e10adc3949ba59abbe56e057f20f883e";
    public static String disName="s";
    public static String disAddress="address";
    public static String note="";
    public static String version;
    public static byte[] selectedImage;
    public static byte[] userselectedImage;
    public static String doctorsId;
    public static Map<String,Integer> productPosition=new HashMap<>();
    public static ArrayList<String> prescriptionProductsList = new ArrayList<>();
    public static String user_id="";
    public static int getUserType=0;
    public static RealmList<byte[]> imagePrescriptionFinal = new RealmList<>();;
    public static String terrid="";
    public static int doctorsState=0;
    public static int depoState=0;
/*    public static NotificationModel notificationData=new NotificationModel();*/

    public static ArrayList<Bitmap> capturedImgBitmapList = new ArrayList<>();
    public static ArrayList<byte[]> capturedImgByteList = new ArrayList<>();

    public static ArrayList<Bitmap> user_capturedImgBitmapList = new ArrayList<>();
    public static ArrayList<byte[]> user_capturedImgByteList = new ArrayList<>();
    public static int finalListState=0;
    public static int DoctorsDataSaveState=0;
    public static String date;
    public static String doctorsIdGet="";
    public static int updateDoctors=0;
    public static int locationSave=0;
    public static int UpdateUserlocationSave=0;
    public static int locationSaveTwo=0;
    public static String distributorIdUpdateUser="0";

    public static int distributerSize=0;
    public static int distributerposotion=0;

    public static ArrayList<LatLng> mapdistributorName= new ArrayList<>();
    public static ArrayList<String> mapdistributorTitle= new ArrayList<>();
    public static ArrayList<Integer> mapdistributorid= new ArrayList<>();

    public static ComplainModel complainModel;
    public static User user;
    public static Context context;

}
