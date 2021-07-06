package com.example.createUserCar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.example.api_helper.ApiInterface;
import com.example.api_helper.BaseUrl;
import com.example.createUserCar.adapter.TransmissionTypeAdapter;
import com.example.createUserCar.adapter.WheelTypeAdapter;
import com.example.createUserCar.model.CreateNewUserCarModel;
import com.example.createUserCar.model.TransmissionTypeModel;
import com.example.createUserCar.model.WheelTypeModel;
import com.example.login.LoginActivity;
import com.example.login.model.User;
import com.example.mbw.MainActivity;
import com.example.mbw.R;

import com.example.mbw.databinding.ActivityCreateUsersBinding;
import com.example.mbw.model.Brands;
import com.example.mbw.model.CustomerCarInfoResponse;
import com.example.mbw.model.EngineTypes;
import com.example.mbw.model.LookUp;
import com.example.mbw.model.ModelType;
import com.example.utils.Timber;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CreateUsers extends AppCompatActivity implements ServerHelper.EntryUserCarInterface {

    ActivityCreateUsersBinding createUsersBinding;
    Button saveBt;
    String customerName, customerNum, customerEmail, introduced, ToName, ToNum;


    int customerTypeId, brandTypeId=0, engineTypeId, modelTypeId, fuelTypeCode;

    String regNo, chassisNo, EngineNo, driverNo, driverName,year,cc_ltr,address,companyName;
    int cutomerId,transmissionTypeCode,wheelTypeCode;

    Boolean isConnected = false;

    Realm realm;
    List<String> userTypeList = new ArrayList<>();
    List<Brands> brandsList = new ArrayList<>();
    List<String> brandsListTmp = new ArrayList<>();
    List<EngineTypes> engineTypesList = new ArrayList<>();
    List<String> engineTypesListTmp = new ArrayList<>();
    List<ModelType> modelTypeList = new ArrayList<>();
    List<String> modelTypeListTmp = new ArrayList<>();
    List<ModelType> modelTypeListRQTmp = new ArrayList<>();

    List<LookUp> lookUpList = new ArrayList<>();
    List<String> lookUpListTmp = new ArrayList<>();
    List<LookUp> lookUpListRQTmp = new ArrayList<>();


    List<TransmissionTypeModel> transmissionTypeModelListTmp = new ArrayList<>();
    List<WheelTypeModel> wheelTypeModelListTmp = new ArrayList<>();

    Timber timber;
    User user;
    int clickerState=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_users);


        realm = Realm.getDefaultInstance();
        timber = new Timber();

        createUsersBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_users);

        user = RealmHelper.getUser();
        brandsList = realm.copyFromRealm(realm.where(Brands.class).findAll());
        engineTypesList = realm.copyFromRealm(realm.where(EngineTypes.class).findAll());
        modelTypeList = realm.copyFromRealm(realm.where(ModelType.class).findAll());
        lookUpList = realm.copyFromRealm(realm.where(LookUp.class).findAll());

        //  timber.e("CreateUser",""+brandsList.size());
        //  timber.e("CreateUser",""+engineTypesList.size());
        //   timber.e("CreateUser",""+lookUpList.size());

        Tovuti.from(this).monitor(new Monitor.ConnectivityListener() {
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                CreateUsers.this.isConnected = isConnected;
                if (!isConnected)
                    Toast.makeText(CreateUsers.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
            }
        });

        invSpinnerAll();

        createUsersBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateUsers.this, MainActivity.class);
                startActivity(intent);
            }
        });

        createUsersBinding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();

                if (validation() && clickerState==0) {
                    clickerState=1;
                    saveOnline();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill up required field!", Toast.LENGTH_SHORT).show();


                }

            }
        });

    }

    public void saveOnline() {
        if (isConnected) {
            createUsersBinding.createCuCarSpinKit.setVisibility(View.VISIBLE);


            cutomerId = user.getId();
            customerName = createUsersBinding.mbwCustomerName.getText().toString().trim();
            companyName= createUsersBinding.mbwCompanyName.getText().toString().trim();


            customerNum = createUsersBinding.mbwCustomerContact.getText().toString().trim();
            customerEmail = createUsersBinding.mbwCustomerEmail.getText().toString().trim();
            introduced = createUsersBinding.mbwCustomerIntroduced.getText().toString().trim();
            ToName = createUsersBinding.mbwTransportOfficer.getText().toString().trim();
            ToNum = createUsersBinding.mbwTransportOfficerNumber.getText().toString().trim();


            regNo = createUsersBinding.mbwRegNo.getText().toString().trim();
            chassisNo = createUsersBinding.mbwChassisNo.getText().toString().trim();
            EngineNo = createUsersBinding.mbwEngineNo.getText().toString().trim();
            driverNo = createUsersBinding.mbwDriverNo.getText().toString().trim();

            driverName= createUsersBinding.mbwDriverName.getText().toString().trim();
            year= createUsersBinding.mbwYearModel.getText().toString().trim();
            cc_ltr= createUsersBinding.mbwCC.getText().toString().trim();

            address= createUsersBinding.mbwCustomerAddress.getText().toString().trim();

/*        CreateNewUserCarModel(int customer_type_id, int customer_id, String customer_name,
                String contact_no, String introduced_by,
                String transport_officer_name, String transport_officer_number,
                String email, String reg_no, String chessis_no,
        int engine_type_id, String engine_no,
        int vbrand_id, int vmodel_id, String driver_no,
        int fuel_type,String driver_name,int transmission_type,int wheel_type,String year,String cc_ltr,String address)*/



            CreateNewUserCarModel createNewUserCarModel = new CreateNewUserCarModel(customerTypeId,
                    cutomerId, customerName, customerNum, introduced, ToName, ToNum, customerEmail,
                    regNo, chassisNo, engineTypeId, EngineNo, brandTypeId, modelTypeId, driverNo, fuelTypeCode,
                    driverName,transmissionTypeCode,wheelTypeCode,year,cc_ltr,address,companyName

            );


            timber.e("create",""+createNewUserCarModel.toString());
            ServerHelper.requestEntryUserCar(user, createNewUserCarModel, this);
        }else {

            Toast.makeText(CreateUsers.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
            clickerState=0;
        }


    }

    public void invSpinnerAll() {

        userTypeList.add("Corporate");
        userTypeList.add("Personal");


        ArrayAdapter<String> userTypeAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, userTypeList);
        userTypeAdapter.setDropDownViewResource(R.layout.spinnerstyle);
        createUsersBinding.customerSpinner.setAdapter(userTypeAdapter);
        createUsersBinding.customerSpinner.setOnItemSelectedListener(new CustomerSpinner());


        if (brandsList.size() > 0) {
            for (int i = 0; i < brandsList.size(); i++) {
                brandsListTmp.add(brandsList.get(i).getTitle());
               // timber.e("CreateUser", "" + brandsList.get(i).getTitle());
            }
            ArrayAdapter<String> brandsAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, brandsListTmp);
            brandsAdapter.setDropDownViewResource(R.layout.spinnerstyle);
            createUsersBinding.engineSpinner.setAdapter(brandsAdapter);
            createUsersBinding.engineSpinner.setOnItemSelectedListener(new BrandTypeSpinner());
        }

        if (engineTypesList.size() > 0) {
            for (int i = 0; i < engineTypesList.size(); i++) {
                engineTypesListTmp.add(engineTypesList.get(i).getTitle());
               // timber.e("CreateUser", "" + engineTypesList.get(i).getTitle());
            }

            ArrayAdapter<String> engineTypesAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, engineTypesListTmp);
            engineTypesAdapter.setDropDownViewResource(R.layout.spinnerstyle);
            createUsersBinding.engineModelSpinner.setAdapter(engineTypesAdapter);
            createUsersBinding.engineModelSpinner.setOnItemSelectedListener(new EngineTypeSpinner());
        }





        if (lookUpList.size() > 0) {
            for (int i = 0; i < lookUpList.size(); i++) {
                if (lookUpList.get(i).getType().equals("fuel_type")) {
                    lookUpListTmp.add(lookUpList.get(i).getName());
                  //  timber.e("CreateUser", "" + lookUpList.get(i).getName());
                    lookUpListRQTmp.add(lookUpList.get(i));
                }
            }
            ArrayAdapter<String> lookUpAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, lookUpListTmp);
            lookUpAdapter.setDropDownViewResource(R.layout.spinnerstyle);
            createUsersBinding.fuelSpinner.setAdapter(lookUpAdapter);
            createUsersBinding.fuelSpinner.setOnItemSelectedListener(new FuelTypeSpinner());
        }

        getTransmissionTpSpinner();
        getWheelTpSpinner();

    }

    public void modelSpinnerInit(int id){
        modelTypeListTmp.clear();
        modelTypeId=0;
        modelTypeListRQTmp.clear();
        if (modelTypeList.size() > 0) {
            for (int i = 0; i < modelTypeList.size(); i++) {
                if(modelTypeList.get(i).getCustom_field1_id()!=null && modelTypeList.get(i).getCustom_field1_id()==id){

                        modelTypeListTmp.add(modelTypeList.get(i).getTitle());
                    modelTypeListRQTmp.add(modelTypeList.get(i));
                }

                // timber.e("CreateUser", "" + modelTypeList.get(i).getTitle());
            }

            if(modelTypeListTmp.size()>0){
                createUsersBinding.vehiclemodelSpinner.setVisibility(View.VISIBLE);
                createUsersBinding.vehicleModel.setVisibility(View.VISIBLE);

            }else{
                createUsersBinding.vehiclemodelSpinner.setVisibility(View.GONE);
                createUsersBinding.vehicleModel.setVisibility(View.GONE);
            }

            ArrayAdapter<String> modelTypeAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, modelTypeListTmp);
            modelTypeAdapter.setDropDownViewResource(R.layout.spinnerstyle);
            createUsersBinding.vehiclemodelSpinner.setAdapter(modelTypeAdapter);
            createUsersBinding.vehiclemodelSpinner.setOnItemSelectedListener(new ModelTypeSpinner());
        }
    }

    public void getTransmissionTpSpinner(){

        if (lookUpList.size() > 0) {
            for (int i = 0; i < lookUpList.size(); i++) {
                if (lookUpList.get(i).getType().equals("transmission_type")) {
                    transmissionTypeModelListTmp.add(new TransmissionTypeModel(lookUpList.get(i).getCode(),lookUpList.get(i).getName()));
                   // timber.e("CreateUser", "" + lookUpList.get(i).getName());
                }
            }

            TransmissionTypeAdapter adapter = new TransmissionTypeAdapter(this, transmissionTypeModelListTmp);
            createUsersBinding.transmissionTypeSpinner.setAdapter(adapter);

            createUsersBinding.transmissionTypeSpinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int position, long id)
                        {
                         transmissionTypeCode=  transmissionTypeModelListTmp.get(position).getCode();
                         //   timber.e("transmissionTypeCode", "" + transmissionTypeCode +" "+transmissionTypeModelListTmp.get(position).getCode());
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
        }
    }


    public void getWheelTpSpinner(){

        if (lookUpList.size() > 0) {
            for (int i = 0; i < lookUpList.size(); i++) {
                if (lookUpList.get(i).getType().equals("wheel_type")) {
                    wheelTypeModelListTmp.add(new WheelTypeModel(lookUpList.get(i).getCode(),lookUpList.get(i).getName()));
                    // timber.e("CreateUser", "" + lookUpList.get(i).getName());
                }
            }

            WheelTypeAdapter adapter = new WheelTypeAdapter(this, wheelTypeModelListTmp);
            createUsersBinding.wheelTypeSpinner.setAdapter(adapter);

            createUsersBinding.wheelTypeSpinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int position, long id)
                        {
                          wheelTypeCode=  wheelTypeModelListTmp.get(position).getCode();
                         // timber.e("wheelTypeCode", "" + wheelTypeCode +" "+wheelTypeModelListTmp.get(position).getCode());

                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
        }
    }

    @Override
    public void onEntryUserCarInterSuccess(String msg) {

        BaseUrl.getClient().create(ApiInterface.class).getComplainSrvcCustomerCar(user.getApiKey(),user.getId().toString()).enqueue(new Callback<CustomerCarInfoResponse>() {
            @Override
            public void onResponse(Call<CustomerCarInfoResponse> call, Response<CustomerCarInfoResponse> response) {

                if (response.body().getCustomerCarInfos() != null) {

                    RealmHelper.saveCustomerCarInfo(response.body().getCustomerCarInfos(),response.body().getComplainCustomerInfomation());
                    createUsersBinding.createCuCarSpinKit.setVisibility(View.GONE);
                    successfuly(msg);

                }

            }

            @Override
            public void onFailure(Call<CustomerCarInfoResponse> call, Throwable t) {
                createUsersBinding.createCuCarSpinKit.setVisibility(View.GONE);
                Toast.makeText(CreateUsers.this, ""+ t.toString(), Toast.LENGTH_SHORT).show();
                clickerState=0;
            }
        });


    }

    public void successfuly(String msg){
      //  Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        //  ToastCustom.ToastMsg(CreateUsers.this,msg);

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

        createUsersBinding.mbwCustomerName.setText("");
        createUsersBinding.mbwCompanyName.setText("");
        createUsersBinding.mbwCustomerContact.setText("");
        createUsersBinding.mbwCustomerEmail.setText("");
        createUsersBinding.mbwCustomerIntroduced.setText("");
        createUsersBinding.mbwTransportOfficer.setText("");
        createUsersBinding.mbwTransportOfficerNumber.setText("");
        createUsersBinding.mbwCustomerAddress.setText("");;

        createUsersBinding.mbwRegNo.setText("");
        createUsersBinding.mbwChassisNo.setText("");
        createUsersBinding.mbwEngineNo.setText("");
        createUsersBinding.mbwDriverName.setText("");
        createUsersBinding.mbwDriverNo.setText("");

        createUsersBinding.mbwYearModel.setText("");
        createUsersBinding.mbwCC.setText("");
        clickerState=0;
    }

    @Override
    public void onEntryUserCarFailed(String error) {
        clickerState=0;
        createUsersBinding.createCuCarSpinKit.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),""+ error, Toast.LENGTH_SHORT).show();

    }






    class CustomerSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           // timber.e("Customerspinner", "onItemSelected: " + position);

            if(position==0){
                customerTypeId=1;
            }else {
                customerTypeId=2;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class BrandTypeSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           // timber.e("EngineTypespinner", "onItemSelected: " + position);


            brandTypeId = brandsList.get(position).getId();
            modelSpinnerInit(brandTypeId);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class EngineTypeSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
           // timber.e("Modelspinner", "onItemSelected: " + position);

            engineTypeId = engineTypesList.get(position).getId();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class ModelTypeSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          //  timber.e("ModelTypeSpinner", "onItemSelected: " + position);
            modelTypeId = modelTypeListRQTmp.get(position).getId();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class FuelTypeSpinner implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          //  timber.e("Fuelspinner", "onItemSelected: " + position);


            fuelTypeCode = lookUpListRQTmp.get(position).getCode();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }



    private boolean validation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake_error);

        if (createUsersBinding.mbwCustomerName.getText().toString().trim().equals("") || createUsersBinding.mbwCustomerName.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwCustomerName.setError("PLease Type Customer Name");
            //  etCollectionAmount.requestFocus();

            createUsersBinding.mbwCustomerName.getParent().requestChildFocus(createUsersBinding.mbwCustomerName, createUsersBinding.mbwCustomerName);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwCustomerName.startAnimation(animation);
                }
            }, 1000);
            return false;
        }
        if (createUsersBinding.mbwCustomerContact.getText().toString().trim().equals("") || createUsersBinding.mbwCustomerContact.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwCustomerContact.setError("PLease Type Customer Number");
            //  etCollectionAmount.requestFocus();
            createUsersBinding.mbwCustomerContact.getParent().requestChildFocus(createUsersBinding.mbwCustomerContact, createUsersBinding.mbwCustomerContact);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwCustomerContact.startAnimation(animation);
                }
            }, 1000);

            return false;
        }
        if (createUsersBinding.mbwCustomerEmail.getText().toString().trim().equals("") || createUsersBinding.mbwCustomerEmail.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwCustomerEmail.setError("PLease Type Customer Email");
            createUsersBinding.mbwCustomerEmail.getParent().requestChildFocus(createUsersBinding.mbwCustomerEmail, createUsersBinding.mbwCustomerEmail);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwCustomerEmail.startAnimation(animation);
                }
            }, 1000);

            return false;
        }
        if (createUsersBinding.mbwCustomerIntroduced.getText().toString().trim().equals("") || createUsersBinding.mbwCustomerIntroduced.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwCustomerIntroduced.setError("PLease Type Introduced Officer's Name");
            createUsersBinding.mbwCustomerIntroduced.getParent().requestChildFocus(createUsersBinding.mbwCustomerIntroduced, createUsersBinding.mbwCustomerIntroduced);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwCustomerIntroduced.startAnimation(animation);
                }
            }, 1000);

            return false;
        }

        if (createUsersBinding.mbwCustomerAddress.getText().toString().trim().equals("") || createUsersBinding.mbwCustomerAddress.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwCustomerAddress.setError("PLease Type Address");
            createUsersBinding.mbwCustomerAddress.getParent().requestChildFocus(createUsersBinding.mbwCustomerAddress, createUsersBinding.mbwCustomerAddress);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwCustomerAddress.startAnimation(animation);
                }
            }, 1000);

            return false;
        }

        if (createUsersBinding.mbwTransportOfficer.getText().toString().trim().equals("") || createUsersBinding.mbwTransportOfficer.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwTransportOfficer.setError("PLease Type Transport Officer's Name");
            createUsersBinding.mbwTransportOfficer.getParent().requestChildFocus(createUsersBinding.mbwTransportOfficer, createUsersBinding.mbwTransportOfficer);
            //  etCollectionAmount.requestFocus();
            createUsersBinding.mbwTransportOfficer.startAnimation(animation);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwTransportOfficer.startAnimation(animation);
                }
            }, 1000);
            return false;
        }
        if (createUsersBinding.mbwTransportOfficerNumber.getText().toString().trim().equals("") || createUsersBinding.mbwTransportOfficerNumber.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwTransportOfficerNumber.setError("PLease Type Transport Officers Number");
            createUsersBinding.mbwTransportOfficerNumber.getParent().requestChildFocus(createUsersBinding.mbwTransportOfficerNumber, createUsersBinding.mbwTransportOfficerNumber);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwTransportOfficerNumber.startAnimation(animation);
                }
            }, 1000);

            return false;
        }

        if (createUsersBinding.mbwRegNo.getText().toString().trim().equals("") || createUsersBinding.mbwRegNo.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwRegNo.setError("PLease Type Reg No");
            createUsersBinding.mbwRegNo.getParent().requestChildFocus(createUsersBinding.mbwRegNo, createUsersBinding.mbwRegNo);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwRegNo.startAnimation(animation);
                }
            }, 1000);

            return false;
        }
        if (createUsersBinding.mbwChassisNo.getText().toString().trim().equals("") || createUsersBinding.mbwChassisNo.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwChassisNo.setError("PLease Type Chassis No");
            createUsersBinding.mbwChassisNo.getParent().requestChildFocus(createUsersBinding.mbwChassisNo, createUsersBinding.mbwChassisNo);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwChassisNo.startAnimation(animation);
                }
            }, 1000);

            return false;
        } if (createUsersBinding.mbwEngineNo.getText().toString().trim().equals("") || createUsersBinding.mbwEngineNo.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwEngineNo.setError("PLease Type Engine No");
            createUsersBinding.mbwEngineNo.getParent().requestChildFocus(createUsersBinding.mbwEngineNo, createUsersBinding.mbwEngineNo);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwEngineNo.startAnimation(animation);
                }
            }, 1000);

            return false;
        }

        if (createUsersBinding.mbwYearModel.getText().toString().trim().equals("") || createUsersBinding.mbwYearModel.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwYearModel.setError("PLease Type Year Model");
            createUsersBinding.mbwYearModel.getParent().requestChildFocus(createUsersBinding.mbwYearModel, createUsersBinding.mbwYearModel);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwYearModel.startAnimation(animation);
                }
            }, 1000);

            return false;
        }

        if (createUsersBinding.mbwCC.getText().toString().trim().equals("") || createUsersBinding.mbwCC.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwCC.setError("PLease Type CC");
            createUsersBinding.mbwCC.getParent().requestChildFocus(createUsersBinding.mbwCC, createUsersBinding.mbwCC);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwCC.startAnimation(animation);
                }
            }, 1000);

            return false;
        }
        if (createUsersBinding.mbwDriverName.getText().toString().trim().equals("") || createUsersBinding.mbwDriverName.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwDriverName.setError("PLease Type Driver Name");
            createUsersBinding.mbwDriverName.getParent().requestChildFocus(createUsersBinding.mbwDriverName, createUsersBinding.mbwDriverName);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwDriverName.startAnimation(animation);
                }
            }, 1000);

            return false;
        }
        if (createUsersBinding.mbwDriverNo.getText().toString().trim().equals("") || createUsersBinding.mbwDriverNo.getText().toString().trim().length() == 0) {
            createUsersBinding.mbwDriverNo.setError("PLease Type Driver Number");
            createUsersBinding.mbwDriverNo.getParent().requestChildFocus(createUsersBinding.mbwDriverNo, createUsersBinding.mbwDriverNo);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    createUsersBinding.mbwDriverNo.startAnimation(animation);
                }
            }, 1000);

            return false;
        }

        return true;
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

}