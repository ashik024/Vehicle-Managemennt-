package com.example.takeComplain;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.example.common_class.CommonClass;

import com.example.fragment.ListFragmentCustomerCarInfoSearch;
import com.example.fragment.ListFragmentSubCategorySearch;
import com.example.login.model.User;
import com.example.mbw.MainActivity;
import com.example.mbw.R;
import com.example.mbw.model.ComplainCat;
import com.example.mbw.model.ComplainHeads;
import com.example.mbw.model.ComplainSubCat;
import com.example.mbw.model.CustomerCarInfo;
import com.example.mbw.model.LookUp;
import com.example.mbw.model.Priorities;
import com.example.takeComplain.adapter.ComplainSheetAdapter;
import com.example.takeComplain.adapter.ComplainTypesAdapter;
import com.example.takeComplain.adapter.ImageAdapter;
import com.example.takeComplain.model.ComplainModel;
import com.example.takeComplain.model.ComplainPriority;
import com.example.takeComplain.model.ComplainPrioritytmp;
import com.example.takeComplain.model.ComplainTypes;
import com.example.takeComplain.service.ComplainService;
import com.example.utils.ClearEditText;
import com.example.utils.Timber;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.github.ybq.android.spinkit.SpinKitView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class TakeComplains extends AppCompatActivity implements ComplainService.ComplainInterface {

    EditText mbwDriverNameET, mbwDriverNumberET, mbwSubCatET, mbwCaridET, mbwComplainTimeET, OdoKM, mbwRemarks;
    Button imgsSelect, save,mConfirmButton,mClearButton;
    String ReceiveTime, DriverName, DriverNumber;
    ImageView back;
    private SignaturePad mSignaturePad;

    TextView CnameValue, CPhoneValue,billingAddressTv, introValue, transOffValue, transOffinum, CemaileValue, CaridValue, regNoValue,
            ChessisnoValue, EnginetypeidValue, EnginenoValue, brandValue, modelValue, driveNm, driveValue, fuelvalue;
    Spinner complainHeadSpinner, complainCatSpinner, ODOSpin, prioritySpinner, complainAccept,billingAddressSpinner;

    private static final String TAG = "mycamera";
    public static int REQ_CODE_CAPTURE_INTENT = 100;
    public static String CAPTURE_IMAGES = "CAPTURE_IMAGES";

    private ImageView ivCaptureImage, driveNmCopy, drivePhoneCopy;
    private ImageLocalSave imageLocalSave;
    private Realm realmImageSave;
    private RealmList<byte[]> imageArrayList = new RealmList<>();
    private RecyclerView rvPrescriptionImageView, rvcomplainSheet;
    private ImageAdapter imageAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ComplainSheetAdapter complainSheetAdapter;

    private RecyclerView.LayoutManager layoutManagerComplain;

    private RecyclerView complaintypesRV;
    private ComplainTypesAdapter complainTypesAdapter;
    private RecyclerView.LayoutManager complaintypesLM;

    AlertDialog alertDialog;

    List<ComplainHeads> complainHeadsList = new ArrayList<>();
    List<String> complainHeadsListTmp = new ArrayList<>();
    List<ComplainCat> complainCatList = new ArrayList<>();
    List<String> complainCatListTmp = new ArrayList<>();

    List<LookUp> lookUpList = new ArrayList<>();
    List<ComplainTypes> complainTypesListTmp = new ArrayList<>();
    List<Integer> complainTypesListFinal = new ArrayList<>();


    List<LookUp> billingAddressList = new ArrayList<>();
    List<String> billingAddressNameList = new ArrayList<>();

    List<Priorities> prioritiesList = new ArrayList<>();
    List<String> prioritiesListTmp = new ArrayList<>();

    List<ComplainPriority> complainPriorityList = new ArrayList<>();
    List<ComplainPrioritytmp> complainPriorityListTmp = new ArrayList<>();

    ArrayList<String> acDenyarrayList = new ArrayList<>();

    String prioritiesTitle;
    int prioritiesID;

    Realm realm;

    SpinKitView spinKitView;
    private int mYear, mMonth, mDay, mHour, mMinute;

    String time, complainCatID, complainCatTitle, complainSubCatID="-1", srvcCustomerId, srvcCustomerCarId;
    String date;

    User user;

    Timber timber;
    List<String> userSelectImageBase64 = new ArrayList<>();

    BroadcastReceiver mBroadcastReceiver;

    View btsaveLocal;

    String odo;
    int ODO = 1;
    String ODOValue = "0";
    SeekBar levelCustomSeekbar;
    TextView levelTV, subLevelTv;

    SeekBar subLevelCustomSeekbar;

    int fuelLevel = 0;
    int SubfuelLevel = 0;
    int clickerState = 0;
    int acDeId;
    int billingAddressId=0;
    String billingAddressHome="";
    String billingAddressOffic="";
    Boolean isConnected = false;
    boolean isPrirotySelecd=false;
    String bitmapToSignatureImage="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_complains);

        timber = new Timber();
        realm = Realm.getDefaultInstance();
        user = RealmHelper.getUser();

        spinKitView = findViewById(R.id.takeComplain_spin_kit);
        mbwComplainTimeET = findViewById(R.id.mbwComplainTime);
        mbwDriverNameET = findViewById(R.id.mbwDriverNameTC);
        mbwDriverNumberET = findViewById(R.id.mbwDriverNumberTC);
        mbwSubCatET = findViewById(R.id.mbwSubCat);
        imgsSelect = findViewById(R.id.pickImg);
        save = findViewById(R.id.saveinfo);
        back = findViewById(R.id.back2);
        mbwCaridET = findViewById(R.id.mbwCarid);

        complainHeadSpinner = findViewById(R.id.complainHeadSpinner);
        complainCatSpinner = findViewById(R.id.complainCatSpinner);


        CnameValue = findViewById(R.id.CnameValue);
        CPhoneValue = findViewById(R.id.CPhoneValue);
        introValue = findViewById(R.id.introValue);
        billingAddressTv = findViewById(R.id.billingAddressTv);
        transOffValue = findViewById(R.id.transOffValue);
        transOffinum = findViewById(R.id.transOffinum);
        CemaileValue = findViewById(R.id.CemaileValue);
        CaridValue = findViewById(R.id.CaridValue);
        regNoValue = findViewById(R.id.regNoValue);
        ChessisnoValue = findViewById(R.id.ChessisnoValue);
        EnginetypeidValue = findViewById(R.id.EnginetypeidValue);
        EnginenoValue = findViewById(R.id.EnginenoValue);
        brandValue = findViewById(R.id.brandValue);
        // modelValue = findViewById(R.id.modelValue);
        driveNm = findViewById(R.id.driveNm);
        driveValue = findViewById(R.id.driveValue);
        fuelvalue = findViewById(R.id.fuelvalue);
        complaintypesRV = findViewById(R.id.complaintypesRv);
        rvcomplainSheet = findViewById(R.id.ComplainList);
        driveNmCopy = findViewById(R.id.driveNmCopy);
        drivePhoneCopy = findViewById(R.id.drivePhoneCopy);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        mbwRemarks = findViewById(R.id.mbwRemarks);
        complainAccept = findViewById(R.id.complainSheetAccepted);
        btsaveLocal = findViewById(R.id.btsaveLocal);


        realmImageSave = Realm.getDefaultInstance();
        rvPrescriptionImageView = findViewById(R.id.rvPrescriptionImage);

        imageLocalSave = new ImageLocalSave();

        complainHeadsList = realm.copyFromRealm(realm.where(ComplainHeads.class).findAll());
        complainCatList = realm.copyFromRealm(realm.where(ComplainCat.class).findAll());
        lookUpList = realm.copyFromRealm(realm.where(LookUp.class).findAll());
        prioritiesList = realm.copyFromRealm(realm.where(Priorities.class).findAll());

        Tovuti.from(this).monitor(new Monitor.ConnectivityListener() {
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                TakeComplains.this.isConnected = isConnected;
                if (!isConnected)
                    Toast.makeText(TakeComplains.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
            }
        });



        prioritySpinnerInit();

        deleteSelectImages();

        LinearLayoutManager layoutManager = new LinearLayoutManager(TakeComplains.this);
        rvcomplainSheet.setLayoutManager(layoutManager);
        complainSheetAdapter = new ComplainSheetAdapter(complainPriorityList, TakeComplains.this);
        rvcomplainSheet.setAdapter(complainSheetAdapter);
        complainSheetAdapter.setComplainSheetAdapterInterface(new ComplainSheetAdapter.ComplainSheetAdapterInterface() {
            @Override
            public void OnItemClickDelete(int position) {
                complainPriorityList.remove(position);
                complainPriorityListTmp.remove(position);
                complainSheetAdapter.notifyDataSetChanged();
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard();
                if (validateData() && clickerState==0) {
                    clickerState = 1;
                    saveOnline();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill up required field!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        imgsSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TakeComplains.this, CameraActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TakeComplains.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mbwSubCatET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListFragmentSubCategorySearch fragment = ListFragmentSubCategorySearch.newInstance("Sub-Category");
                fragment.show(getSupportFragmentManager(), "");

                fragment.setListenerSubCategory(new ListFragmentSubCategorySearch.OnItemSelectedListenerSubCategory() {
                    @Override
                    public void onItemSelect(ComplainSubCat complainSubCat) {
                        //    Log.e("ex",""+pos);
                        mbwSubCatET.setText(complainSubCat.getTitle());

                        complainHeadInit(complainSubCat.getComplain_head_id());
                        complainCatInit(complainSubCat.getComplain_cat_id());

                        complainCatID = complainSubCat.getComplain_cat_id().toString();
                        complainSubCatID = complainSubCat.getId().toString();
                    }

                });


            }
        });

        mbwCaridET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ListFragmentCustomerCarInfoSearch fragment = ListFragmentCustomerCarInfoSearch.newInstance("Car Search");

                fragment.show(getSupportFragmentManager(), "");

                fragment.setListenerCarIds(new ListFragmentCustomerCarInfoSearch.OnItemSelectedListenerCarIds() {
                    @Override
                    public void onItemSelect(CustomerCarInfo customerCarInfo) {
                        //    Log.e("ex",""+pos);


                        marqueeLoad(billingAddressTv);
                        billingAddressTv.setSelected(true);
                        if (customerCarInfo.getCarId() != null) {
                            mbwCaridET.setText(customerCarInfo.getCarId());
                        }
                        if (customerCarInfo.getSrvcCustomerId() != null) {

                            srvcCustomerId = customerCarInfo.getSrvcCustomerId().toString();
                            //   timber.e("Complain","srvcCustomerId "+srvcCustomerId);
                        }
                        if (customerCarInfo.getSrvcCustomerCarId() != null) {
                            srvcCustomerCarId = customerCarInfo.getSrvcCustomerCarId().toString();
                            //  timber.e("Complain","srvcCustomerCarId "+srvcCustomerCarId);
                        }
                        if (customerCarInfo.getCustomerName() != null) {
                            CnameValue.setText(customerCarInfo.getCustomerName().toString());
                            marqueeLoad(CnameValue);
                        }
                        if (customerCarInfo.getContactNo() != null) {
                            CPhoneValue.setText(customerCarInfo.getContactNo().toString());
                            marqueeLoad(CPhoneValue);
                        }

                        if (customerCarInfo.getHome_addr() != null) {
                         //   timber.e("getHome_addr",""+customerCarInfo.getHome_addr().toString());
                            billingAddressHome=customerCarInfo.getHome_addr().toString();
                            if(billingAddressId==145){
                                billingAddressTv.setText(billingAddressHome);
                                marqueeLoad(billingAddressTv);
                            }

                        } if (customerCarInfo.getOffice_addr() != null) {
                         //   timber.e("getOffice_addr",""+customerCarInfo.getOffice_addr().toString());
                            billingAddressOffic=customerCarInfo.getOffice_addr().toString();
                            if(billingAddressId==146){
                                billingAddressTv.setText(billingAddressOffic);
                                marqueeLoad(billingAddressTv);
                            }
                        }
                        if (customerCarInfo.getIntroducedBy() != null) {
                            introValue.setText(customerCarInfo.getIntroducedBy().toString());
                            marqueeLoad(introValue);
                        }
                        if (customerCarInfo.getTransportOfficerName() != null) {
                            transOffValue.setText(customerCarInfo.getTransportOfficerName().toString());
                            marqueeLoad(transOffValue);
                        }
                        if (customerCarInfo.getTransportOfficerNumber() != null) {
                            transOffinum.setText(customerCarInfo.getTransportOfficerNumber().toString());
                            marqueeLoad(transOffinum);
                        }
                        if (customerCarInfo.getEmail() != null) {
                            CemaileValue.setText(customerCarInfo.getEmail().toString());
                            marqueeLoad(CemaileValue);
                        }
                        if (customerCarInfo.getCarId() != null) {
                            CaridValue.setText(customerCarInfo.getCarId().toString());
                        }
                        if (customerCarInfo.getRegNo() != null) {
                            regNoValue.setText(customerCarInfo.getRegNo().toString());
                        }
                        if (customerCarInfo.getChessisNo() != null) {
                            ChessisnoValue.setText(customerCarInfo.getChessisNo().toString());
                        }
                        if (customerCarInfo.getEngineTypeId() != null) {
                            EnginetypeidValue.setText(customerCarInfo.getEngineTypesTitle().toString());
                        }
                        if (customerCarInfo.getEngineNo() != null) {
                            EnginenoValue.setText(customerCarInfo.getEngineNo().toString());
                        }

                        if (customerCarInfo.getDriver_name() != null) {

                            driveNm.setText(customerCarInfo.getDriver_name().toString());
                            driveNmCopy.setVisibility(View.VISIBLE);
                            driveNmCopy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("driverName", customerCarInfo.getDriver_name().toString());
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(getApplicationContext(), "Driver name copy", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                        if (customerCarInfo.getDriverNo() != null) {
                            driveValue.setText(customerCarInfo.getDriverNo().toString());
                            drivePhoneCopy.setVisibility(View.VISIBLE);
                            drivePhoneCopy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("driverPhone", customerCarInfo.getDriverNo().toString());
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(getApplicationContext(), "Driver phone number copy", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        if (customerCarInfo.getFuelTypeName() != null) {
                            fuelvalue.setText(customerCarInfo.getFuelTypeName().toString());
                        }
                        if (customerCarInfo.getVbrandTitle() != null) {
                            brandValue.setText(customerCarInfo.getVbrandTitle().toString());
                        }


                    }

                });


            }
        });

        mbwComplainTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTime();
                getDate();


            }
        });


        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(RECEIVER_MESSAGE);
                boolean isError = intent.getBooleanExtra(RECEIVER_ISERROR, false);
                if (isError) {
                    spinKitView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    clickerState = 0;
                } else {
                    spinKitView.setVisibility(View.GONE);
                     clearAll(message.toString());

                    clickerState = 0;

                }

            }
        };

        //

        OdoKM = findViewById(R.id.odo);

        ODOSpin = findViewById(R.id.odopinner);
        billingAddressSpinner = findViewById(R.id.billingAddressSpinner);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("KM");
        arrayList.add("MILLAGE");
        levelCustomSeekbar = (SeekBar) findViewById(R.id.seekBar);
        levelTV = (TextView) findViewById(R.id.levelTV);


        levelTV.setText("Fuel Level : " + levelCustomSeekbar.getProgress() + "%");
        levelCustomSeekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        fuelLevel = progresValue;
                        levelTV.setText("Fuel Level : " + fuelLevel + "%");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        levelTV.setText("Fuel Level : " + fuelLevel + "%");
                    }
                });


        subLevelCustomSeekbar = (SeekBar) findViewById(R.id.seekBar2);
        subLevelTv = (TextView) findViewById(R.id.subLevelTv);
        subLevelTv.setText("Sub-Fuel Level : " + subLevelCustomSeekbar.getProgress() + "%");
        subLevelCustomSeekbar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        SubfuelLevel = progresValue;
                        subLevelTv.setText("Sub-Fuel Level : " + SubfuelLevel + "%");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        subLevelTv.setText("Sub-Fuel Level : " + SubfuelLevel + "%");
                    }
                });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, arrayList);
        arrayAdapter.setDropDownViewResource(R.layout.spinnerstyle);
        ODOSpin.setAdapter(arrayAdapter);
        ODOSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                odo = parent.getItemAtPosition(position).toString();

                if (odo.equals("KM")) {
                    ODO = 1;
                } else {
                    ODO = 2;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        complinsTypeInit();
        billingAddressSpinnerInit();


        acDenyarrayList.add("Accepted");
        acDenyarrayList.add("Deny");
        ArrayAdapter<String> lookUpAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, acDenyarrayList);
        lookUpAdapter.setDropDownViewResource(R.layout.spinnerstyle);
        complainAccept.setAdapter(lookUpAdapter);
        complainAccept.setOnItemSelectedListener(new AcDenySpinner());


        btsaveLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    public ComplainPriority(int categoryId, String categoryTile,
                            int subCategoryId, String subCategoryTile, int priorityId,
                            String priorityTile, String remarks,
                            int acDenyId) {
                */


                if(complainSubCatID.equals("-1") ){
                    Toast.makeText(getApplicationContext(), "Please select sub-category !", Toast.LENGTH_SHORT).show();
                }else{
                    if (validationSubcatList(Integer.parseInt(complainSubCatID)) == true) {
                        String SubCatTitle = mbwSubCatET.getText().toString().trim();
                        String mbwRemarksTitle = mbwRemarks.getText().toString().trim();


                        ComplainPriority complainPriority = new ComplainPriority(Integer.parseInt(complainCatID), complainCatTitle,
                                Integer.parseInt(complainSubCatID), SubCatTitle, prioritiesID, prioritiesTitle, mbwRemarksTitle, acDeId
                        );
                        complainPriorityList.add(complainPriority);

                        ComplainPrioritytmp complainPriorityTmp = new ComplainPrioritytmp(
                                Integer.parseInt(complainCatID), Integer.parseInt(complainSubCatID), mbwRemarksTitle,
                                prioritiesID, acDeId);

                        complainPriorityListTmp.add(complainPriorityTmp);

                        complainSheetAdapter.notifyDataSetChanged();
                        ClearEditText.ediTextClear(mbwRemarks);
                    } else {
                        Toast.makeText(getApplicationContext(), "Already added this sub-category !", Toast.LENGTH_SHORT).show();

                    }
              }


            }
        });

        //signature

        mClearButton = (Button) findViewById(R.id.clear_button);
        mConfirmButton = (Button) findViewById(R.id.save_button);
        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                // Toast.makeText(SignatureActivity.this, "StartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mConfirmButton.setEnabled(true);
                mClearButton.setEnabled(true);

            }

            @Override
            public void onClear() {
                mConfirmButton.setEnabled(false);
                mClearButton.setEnabled(false);
                bitmapToSignatureImage="";
                mConfirmButton.setBackgroundColor(getResources().getColor(R.color.mbwtakeComplainClear));
                mClearButton.setBackgroundColor(getResources().getColor(R.color.mbwtakeComplainClear));

            }
        });


        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
                mConfirmButton.setBackgroundColor(getResources().getColor(R.color.mbwtakeComplainClear));
                mClearButton.setBackgroundColor(getResources().getColor(R.color.mbwtakeComplainClear));

            }
        });

        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();

                bitmapToSignatureImage=toStringImage(signatureBitmap);
             /*   Log.e("signatureBitmap",""+signatureBitmap.toString());
                Log.e("capturedImgBitmapList",""+capturedImgBitmapList.toString());
                bitmapToimage=toStringImage(signatureBitmap);
                Log.e("bitmapToimage",""+bitmapToimage.toString());
                if (signatureBitmap!=null) {

                    if(!bitmapToimage.equals("")){
                        save(bitmapToimage);
                    }

                } else if (signatureBitmap ==null){
                    Toast.makeText(SignatureActivity.this, "Unable to store the signature", Toast.LENGTH_SHORT).show();
                }*/


                mConfirmButton.setBackgroundColor(getResources().getColor(R.color.mbwtakeComplainConfirm));

            }
        });

    }


    private boolean validationSubcatList(int subCatId) {
        boolean found = true;


        for (ComplainPriority p : complainPriorityList) {

          //  Log.e("title", "" + p.getCategoryTile());

            if (p.getSubCategoryId() == subCatId) {
                found = false;
                break;
            }

        }


        return found;
    }


    public void billingAddressSpinnerInit(){


        if (lookUpList.size() > 0) {
         /*   for (int i = 0; i < lookUpList.size(); i++) {
                if (lookUpList.get(i).getType().equals("complain_types")) {
                    complainTypesListTmp.add(new ComplainTypes(lookUpList.get(i).getCode(), lookUpList.get(i).getName()))
                    // timber.e("CreateUser", "" + lookUpList.get(i).getName());
                }
            }*/

            for(LookUp lookUp:lookUpList){
                if(lookUp.getType().equals("home_office")){
                    billingAddressList.add(lookUp);
                    billingAddressNameList.add(lookUp.getName());
                }
            }


            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, billingAddressNameList);
            arrayAdapter.setDropDownViewResource(R.layout.spinnerstyle);
            billingAddressSpinner.setAdapter(arrayAdapter);
            billingAddressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    billingAddressId=  billingAddressList.get(position).getCode();

                    if(billingAddressId==145){
                        if (billingAddressHome != null) {
                            billingAddressTv.setText(billingAddressHome);
                            marqueeLoad(billingAddressTv);
                        }
                    }if(billingAddressId==146){
                        if (billingAddressOffic != null) {
                            billingAddressTv.setText(billingAddressOffic);
                            marqueeLoad(billingAddressTv);
                        }
                    }


                //    timber.e("billingAddressId",""+billingAddressId);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }



    }
    public void complinsTypeInit() {


        if (lookUpList.size() > 0) {
            for (int i = 0; i < lookUpList.size(); i++) {
                if (lookUpList.get(i).getType().equals("complain_types")) {
                    complainTypesListTmp.add(new ComplainTypes(lookUpList.get(i).getCode()
                            , lookUpList.get(i).getName()));
                    // timber.e("CreateUser", "" + lookUpList.get(i).getName());
                }
            }


            complaintypesLM = new GridLayoutManager(TakeComplains.this, 3);
            complaintypesRV.setLayoutManager(complaintypesLM);
            complainTypesAdapter = new ComplainTypesAdapter(complainTypesListTmp, TakeComplains.this);
            complaintypesRV.setAdapter(complainTypesAdapter);

            complainTypesAdapter.setComplainTypesInterface(new ComplainTypesAdapter.ComplainTypesInterface() {
                @Override
                public void insert(int code, int position) {
                    complainTypesListFinal.add(code);
                    //    timber.e("insert",""+code );
                }

                @Override
                public void delete(int code, int position) {
                    int positionCode = complainTypesListFinal.indexOf(code);
                    complainTypesListFinal.remove(positionCode);
                    //   timber.e("delete",""+code +" positionCode "+positionCode);


                }
            });


        }
    }


    private void deleteSelectImages() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmResults<ImageLocalSave> alldata = realm.where(ImageLocalSave.class).findAll();

                //   Log.e("imageSize", "execute: " + alldata.size());
                if (alldata.size() != 0) {
                    alldata.deleteAllFromRealm();
                }
            }
        });


    }


    public void saveOnline() {
        if(isConnected){

            spinKitView.setVisibility(View.VISIBLE);
            if(complainPriorityList.size()==0){
                String SubCatTitle = mbwSubCatET.getText().toString().trim();
                String mbwRemarksTitle = mbwRemarks.getText().toString().trim();


                ComplainPriority complainPriority = new ComplainPriority(Integer.parseInt(complainCatID), complainCatTitle,
                        Integer.parseInt(complainSubCatID), SubCatTitle, prioritiesID, prioritiesTitle, mbwRemarksTitle, acDeId
                );
                complainPriorityList.add(complainPriority);

                ComplainPrioritytmp complainPriorityTmp = new ComplainPrioritytmp(
                        Integer.parseInt(complainCatID), Integer.parseInt(complainSubCatID), mbwRemarksTitle,
                        prioritiesID, acDeId);
                complainPriorityListTmp.add(complainPriorityTmp);
            }

            ComplainModel complainModel = new ComplainModel(srvcCustomerId, srvcCustomerCarId, String.valueOf(billingAddressId),
                    ReceiveTime, DriverName, DriverNumber, complainCatID,
                    complainSubCatID, userSelectImageBase64, ODOValue, String.valueOf(ODO), String.valueOf(fuelLevel), String.valueOf(SubfuelLevel),
                    complainTypesListFinal, complainPriorityListTmp,bitmapToSignatureImage
            );
            //  timber.e("Complain Model", "" + complainModel.toString());


      /*  if(userSelectImageBase64.size()>0){
             complainModel = new ComplainModel(srvcCustomerId,srvcCustomerCarId,"null",ReceiveTime,DriverName,DriverNumber,complainCatID,
                    complainSubCatID,userSelectImageBase64,ODOValue,String.valueOf(ODO),String.valueOf(fuelLevel),String.valueOf(SubfuelLevel)
                     ,complainTypesListFinal
            );
        }else{
            complainModel = new ComplainModel(srvcCustomerId,srvcCustomerCarId,"null",ReceiveTime,DriverName,DriverNumber,complainCatID,
                    complainSubCatID,userSelectImageBase64,ODOValue,String.valueOf(ODO),String.valueOf(fuelLevel),String.valueOf(SubfuelLevel)
                    ,complainTypesListFinal
            );
        }*/

            //  ServerHelper.requestComplainCar(user, complainModel, this);

            // timber.e("Complain Model",""+complainPriorityListTmp.toString());


            Intent serviceIntent = new Intent(getApplicationContext(), ComplainService.class);


            CommonClass.complainModel = complainModel;
            CommonClass.user = user;
            CommonClass.context = TakeComplains.this;

            // ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);

            serviceIntent.putExtra("contextClass", TakeComplains.class);
            try {
                startService(serviceIntent);
            } catch (Exception e1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    this.startForegroundService(serviceIntent);
                } else {

                    this.startService(serviceIntent);
                    spinKitView.setVisibility(View.GONE);
                }
            }
        }else{
            clickerState=0;
            Toast.makeText(TakeComplains.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean validateData() {

        ReceiveTime = mbwComplainTimeET.getText().toString().trim();
        DriverName = mbwDriverNameET.getText().toString().trim();
        DriverNumber = mbwDriverNumberET.getText().toString().trim();
        ODOValue = OdoKM.getText().toString().trim();

     /*   timber.e("takeComplain","ReceiveTime "+ReceiveTime+ "DriverName "+DriverName +"DriverNumber "+DriverNumber+"complainCatID "+
                        complainCatID+"complainSubCatID "+complainSubCatID);*/


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake_error);

        if (mbwCaridET.getText().toString().trim().equals("") || mbwCaridET.getText().toString().trim().length() == 0) {
            mbwCaridET.setError("PLease Type Car");
            //  etCollectionAmount.requestFocus();

            mbwCaridET.getParent().requestChildFocus(mbwCaridET, mbwCaridET);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mbwCaridET.startAnimation(animation);
                }
            }, 1000);
            return false;
        }

        if (mbwComplainTimeET.getText().toString().trim().equals("") || mbwComplainTimeET.getText().toString().trim().length() == 0) {
            mbwComplainTimeET.setError("PLease Type Receive Time");
            //  etCollectionAmount.requestFocus();

            mbwComplainTimeET.getParent().requestChildFocus(mbwComplainTimeET, mbwComplainTimeET);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mbwComplainTimeET.startAnimation(animation);
                }
            }, 1000);
            return false;
        }
        if (mbwDriverNameET.getText().toString().trim().equals("") || mbwDriverNameET.getText().toString().trim().length() == 0) {
            mbwDriverNameET.setError("PLease Type Driver Name");
            //  etCollectionAmount.requestFocus();

            mbwDriverNameET.getParent().requestChildFocus(mbwDriverNameET, mbwDriverNameET);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mbwDriverNameET.startAnimation(animation);
                }
            }, 1000);
            return false;
        }
        if (mbwDriverNumberET.getText().toString().trim().equals("") || mbwDriverNumberET.getText().toString().trim().length() == 0) {
            mbwDriverNumberET.setError("PLease Type Driver Number");
            //  etCollectionAmount.requestFocus();

            mbwDriverNumberET.getParent().requestChildFocus(mbwDriverNumberET, mbwDriverNumberET);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mbwDriverNumberET.startAnimation(animation);
                }
            }, 1000);
            return false;
        }if(billingAddressId==0){
            billingAddressSpinner.getParent().requestChildFocus(billingAddressSpinner, billingAddressSpinner);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    billingAddressSpinner.startAnimation(animation);
                }
            }, 1000);
            return false;
        }
        if (OdoKM.getText().toString().trim().equals("") || OdoKM.getText().toString().trim().length() == 0) {
            OdoKM.setError("PLease Type ODO Reading KM");
            //  etCollectionAmount.requestFocus();

            OdoKM.getParent().requestChildFocus(OdoKM, OdoKM);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OdoKM.startAnimation(animation);
                }
            }, 1000);
            return false;
        }
        if (complainTypesListFinal.size() <= 0) {
            Toast.makeText(getApplicationContext(), "Please fill up  complain types!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mbwSubCatET.getText().toString().trim().equals("") || mbwSubCatET.getText().toString().trim().length() == 0) {
            mbwSubCatET.setError("PLease Type Sub-Cat");
            //  etCollectionAmount.requestFocus();

            mbwSubCatET.getParent().requestChildFocus(mbwSubCatET, mbwSubCatET);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mbwSubCatET.startAnimation(animation);
                }
            }, 1000);
            return false;
        }

        return true;

    }

    private void getDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(TakeComplains.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        editTextOne.append(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        mbwComplainTimeET.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " ");
//                        date = dayOfMonth + "-"+  (monthOfYear + 1) + "-" + year;
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    private void getTime() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(TakeComplains.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mbwComplainTimeET.append(hourOfDay + ":" + minute);
//                        editTextOne.setText(hourOfDay + ":"  +minute);
                        time = hourOfDay + ":" + minute;
                        Log.i("asd", time);
                    }
                }, mHour, mMinute, false);
        // Log.i("asd", String.valueOf(mHour));
        timePickerDialog.show();
    }


    public void marqueeLoad(TextView textView) {
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSelected(true);
    }

    public void complainHeadInit(int id) {
        complainHeadsListTmp.clear();
        if (complainHeadsList.size() > 0) {
            for (int i = 0; i < complainHeadsList.size(); i++) {
                //  timber.e("CreateUser", "" + brandsList.get(i).getTitle());
                if (complainHeadsList.get(i).getId() == id) {
                    complainHeadsListTmp.add(complainHeadsList.get(i).getTitle());
                }
            }
            ArrayAdapter<String> complainHeadsListAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, complainHeadsListTmp);
            complainHeadsListAdapter.setDropDownViewResource(R.layout.spinnerstyle);
            complainHeadSpinner.setAdapter(complainHeadsListAdapter);
            complainHeadSpinner.setOnItemSelectedListener(new ComplainHeadSpinner());
        }
    }

    public void complainCatInit(int id) {
        complainCatListTmp.clear();
        if (complainCatList.size() > 0) {
            for (int i = 0; i < complainCatList.size(); i++) {
                if (complainCatList.get(i).getId() == id) {
                    complainCatListTmp.add(complainCatList.get(i).getTitle());
                }
                //    timber.e("CreateUser", "" + brandsList.get(i).getTitle());
            }
            ArrayAdapter<String> complainCatAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, complainCatListTmp);
            complainCatAdapter.setDropDownViewResource(R.layout.spinnerstyle);
            complainCatSpinner.setAdapter(complainCatAdapter);
            complainCatSpinner.setOnItemSelectedListener(new ComplainCatSpinner());
        }
    }

    public void prioritySpinnerInit() {
        prioritiesListTmp.clear();
        if (prioritiesList.size() > 0) {
            for (int i = 0; i < prioritiesList.size(); i++) {
                prioritiesListTmp.add(prioritiesList.get(i).getTitle());
            }
            ArrayAdapter<String> complainPriorityAdp = new ArrayAdapter<String>(this, R.layout.spinnerstyle, prioritiesListTmp);
            complainPriorityAdp.setDropDownViewResource(R.layout.spinnerstyle);
            prioritySpinner.setAdapter(complainPriorityAdp);
          //  prioritySpinner.setOnItemSelectedListener(new PrioritiesSpinner());

            complainPriorityAdp.insert("",0);


            prioritySpinner.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    // to set value of first selection, because setOnItemSelectedListener will not dispatch if the user selects first element

                    complainPriorityAdp.remove("");

                    isPrirotySelecd=true;

                    return false;
                }

            });

            prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    //int pos = deadlineSpinner.getSelectedItemPosition();  //why find position here?

                    if(isPrirotySelecd){
                        prioritiesID = prioritiesList.get(position).getId();
                        prioritiesTitle = prioritiesList.get(position).getTitle();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }
            });
        }
    }

    private void _initRv() {


        realmImageSave.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<ImageLocalSave> alldata = realm.where(ImageLocalSave.class).findAll();
                //   Log.e("imageSize", "execute22: " + alldata.size());
                if (alldata.size() != 0) {
                    RealmList<byte[]> imageArrayListLocal = new RealmList<>();
                    imageArrayListLocal.clear();
                    for (int i = 0; i < alldata.size(); i++) {
                        //     Log.e("imageSize", "kibalcal " + alldata.get(i).getImageArrayList().size());
                        imageArrayListLocal.addAll(alldata.get(i).getImageArrayList());

                        if (i == alldata.size() - 1) {
                            layoutManager = new GridLayoutManager(TakeComplains.this, 2);
                            rvPrescriptionImageView.setLayoutManager(layoutManager);
                            imageAdapter = new ImageAdapter(imageArrayListLocal, TakeComplains.this);
                            rvPrescriptionImageView.setAdapter(imageAdapter);

                        }
                    }

                    // Log.e("imageSize", "image arraylist: " + imageArrayListLocal.size() + "     data ");

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {

                //Log.e("image data", "222: " + CommonClass.capturedImgByteList.size());
                if (CommonClass.capturedImgByteList.size() > 0) {


                    final RealmList<byte[]> byteImglist = new RealmList<>();
                    realmImageSave.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            RealmResults<ImageLocalSave> alldata = realm.where(ImageLocalSave.class).findAll();
                            //  Log.e("imageSize", "execute: " + alldata.size());
                            if (alldata.size() != 0) {
                                RealmList<byte[]> imageArrayListLocal = new RealmList<>();
                                imageArrayListLocal.clear();
                                for (int i = 0; i < alldata.size(); i++) {
                                    //   Log.e("imageSize", "kibalcal " + alldata.get(i).getImageArrayList().size());
                                    imageArrayListLocal.addAll(alldata.get(i).getImageArrayList());
                                    if (i == alldata.size() - 1) {
                                        byteImglist.addAll(imageArrayListLocal);
                                        byteImglist.addAll(CommonClass.capturedImgByteList);
                                        //   Log.e("dataColena", "onActivityResult: " + byteImglist.size());
                                        saveLocalStorage(byteImglist);
                                    }
                                }
                                //    Log.e("imageSize", "image arraylist: " + imageArrayListLocal.size() + "     data ");

                            } else {
                                byteImglist.addAll(CommonClass.capturedImgByteList);
                                //  Log.e("dataColena", "onActivityResult: " + byteImglist.size());
                                saveLocalStorage(byteImglist);
                            }


                        }
                    });

                }
            }
        }
    }

    public void byteToBase64AllImages() {

        userSelectImageBase64.clear();
        RealmResults<ImageLocalSave> alldata = realm.where(ImageLocalSave.class).findAll();
        RealmList<byte[]> imageArrayListLocal = new RealmList<>();

        for (int i = 0; i < alldata.size(); i++) {
            imageArrayListLocal.addAll(alldata.get(i).getImageArrayList());
        }
        for (int i = 0; i < imageArrayListLocal.size(); i++) {
            String Base64tmp = toStringImage(
                    BitmapFactory.decodeByteArray(imageArrayListLocal.get(i), 0, imageArrayListLocal.get(i).length)
            );
            userSelectImageBase64.add(Base64tmp);
        }

    }


    private void saveLocalStorage(RealmList<byte[]> imageArrayList) {


        final ImageLocalSave imageLocalSave = new ImageLocalSave();
        imageLocalSave.setImageArrayList(imageArrayList);

        realmImageSave.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number num = realm.where(ImageLocalSave.class).max("id");
                imageLocalSave.setId(0);
                realm.insertOrUpdate(imageLocalSave);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                // Log.d(TAG, "onSuccess: save end");
                _initRv();
                byteToBase64AllImages();
            }
        });
    }

    private void deleteData() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ImageLocalSave syncCompleteOrder = realm.where(ImageLocalSave.class).findFirst();
                assert syncCompleteOrder != null;
                syncCompleteOrder.deleteFromRealm();
            }
        });
    }

    @Override
    public void onComplainSuccess(String msg) {
        clickerState = 0;
        spinKitView.setVisibility(View.GONE);
        deleteSelectImages();
        if (userSelectImageBase64.size() > 0) {
            imageAdapter.clear();
            imageAdapter.notifyDataSetChanged();
            userSelectImageBase64.clear();
        }

        subLevelCustomSeekbar.setProgress(0);
        levelCustomSeekbar.setProgress(0);

        levelTV.setText("Fuel Level : " + levelCustomSeekbar.getProgress() + "%");
        subLevelTv.setText("Sub-Fuel Level : " + levelCustomSeekbar.getProgress() + "%");

        complainTypesListFinal.clear();
        complainTypesAdapter.clear();
        complainTypesAdapter.notifyDataSetChanged();
        complinsTypeInit();

        driveNmCopy.setVisibility(View.GONE);
        drivePhoneCopy.setVisibility(View.GONE);

        ClearEditText.ediTextClear(mbwSubCatET);
        ClearEditText.ediTextClear(mbwCaridET);
        ClearEditText.ediTextClear(mbwComplainTimeET);
        ClearEditText.ediTextClear(mbwDriverNameET);
        ClearEditText.ediTextClear(mbwDriverNumberET);
        ClearEditText.ediTextClear(OdoKM);




    }

    @Override
    public void onComplainFailed(String error) {
        clickerState = 0;
        spinKitView.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

    }


    class ComplainHeadSpinner implements AdapterView.OnItemSelectedListener {


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // Log.e("ComplainHeadSpinner", "onItemSelected: " + position);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    class ComplainCatSpinner implements AdapterView.OnItemSelectedListener {


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // Log.e("ComplainCatSpinner", "onItemSelected: " + position);


            complainCatID = complainCatList.get(position).getId().toString();
            complainCatTitle = complainCatList.get(position).getTitle().toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }

    class PrioritiesSpinner implements AdapterView.OnItemSelectedListener {


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // Log.e("ComplainCatSpinner", "onItemSelected: " + position);

            prioritiesID = prioritiesList.get(position).getId();
            prioritiesTitle = prioritiesList.get(position).getTitle();


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }


    class AcDenySpinner implements AdapterView.OnItemSelectedListener {


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // Log.e("ComplainCatSpinner", "onItemSelected: " + position);

            String acDntmp = acDenyarrayList.get(position);

            if (acDntmp.equals("Accepted")) {
                acDeId = 1;
            } else {
                acDeId = 2;
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    }


    public String toStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private void hideKeyboard() {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Clear the Activity's bundle of the subsidiary fragments' bundles.
        outState.clear();
    }

    public void clearAll(String msg) {

        clickerState = 0;
        deleteSelectImages();


        if (userSelectImageBase64.size() > 0) {
            imageAdapter.clear();
            imageAdapter.notifyDataSetChanged();
            userSelectImageBase64.clear();
        }

        subLevelCustomSeekbar.setProgress(0);
        levelCustomSeekbar.setProgress(0);

        levelTV.setText("Fuel Level : " + levelCustomSeekbar.getProgress() + "%");
        subLevelTv.setText("Sub-Fuel Level : " + levelCustomSeekbar.getProgress() + "%");

        complainTypesListFinal.clear();
        complainTypesAdapter.clear();
        complainTypesAdapter.notifyDataSetChanged();
        complinsTypeInit();


        driveNmCopy.setVisibility(View.GONE);
        drivePhoneCopy.setVisibility(View.GONE);

        ClearEditText.ediTextClear(OdoKM);
        ClearEditText.ediTextClear(mbwSubCatET);
        ClearEditText.ediTextClear(mbwCaridET);
        ClearEditText.ediTextClear(mbwComplainTimeET);
        ClearEditText.ediTextClear(mbwDriverNameET);
        ClearEditText.ediTextClear(mbwDriverNumberET);
        ClearEditText.ediTextClear(mbwRemarks);


        mbwCaridET.setError(null);
        complainPriorityList.clear();
        complainPriorityListTmp.clear();
        complainSheetAdapter.notifyDataSetChanged();


        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.save_alert_dialoge, null);
        TextView textView=promptsView.findViewById(R.id.text_dialog);
        textView.setText(msg.toString());
        android.app.AlertDialog.Builder alertDialogBuilder = new
                android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        mSignaturePad.clear();
        bitmapToSignatureImage="";
        mConfirmButton.setBackgroundColor(getResources().getColor(R.color.mbwtakeComplainClear));
        mClearButton.setBackgroundColor(getResources().getColor(R.color.mbwtakeComplainClear));
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mBroadcastReceiver),
                new IntentFilter(RECEIVER_INTENT)
        );
    }


    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

    public static final String RECEIVER_INTENT = "RECEIVER_INTENT";
    public static final String RECEIVER_MESSAGE = "RECEIVER_MESSAGE";
    public static final String RECEIVER_ISERROR = "isError";


}


