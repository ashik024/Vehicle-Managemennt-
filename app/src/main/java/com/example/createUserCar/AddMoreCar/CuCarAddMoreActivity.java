package com.example.createUserCar.AddMoreCar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.example.api_helper.ApiInterface;
import com.example.api_helper.BaseUrl;
import com.example.createUserCar.AddMoreCar.model.AddMoreCarsModel;
import com.example.createUserCar.AddMoreCar.model.CommmonResponseSrvcCar;
import com.example.createUserCar.AddMoreCar.model.ComplainCustomerInfomation;
import com.example.createUserCar.adapter.TransmissionTypeAdapter;
import com.example.createUserCar.adapter.WheelTypeAdapter;
import com.example.createUserCar.model.CreateNewUserCarModel;
import com.example.createUserCar.model.TransmissionTypeModel;
import com.example.createUserCar.model.WheelTypeModel;

import com.example.fragment.ListFragmentCustomerInfoSearch;
import com.example.login.model.User;
import com.example.mbw.MainActivity;
import com.example.mbw.R;

import com.example.mbw.databinding.ActivityCuCarAddMoreBinding;
import com.example.mbw.model.Brands;
import com.example.mbw.model.CustomerCarInfo;
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


public class CuCarAddMoreActivity extends AppCompatActivity implements ServerHelper.AddCarInterface {

    ActivityCuCarAddMoreBinding carsBinding;

    int customerTypeId, brandTypeId=0, engineTypeId, modelTypeId, fuelTypeCode;

    int cutomerId,transmissionTypeCode,wheelTypeCode;

    int clickerState=0;
    String time, complainCatID, complainCatTitle, complainSubCatID="-1", srvcCustomerId, srvcCustomerCarId;

    String regNo, chassisNo, EngineNo, driverNo, driverName,year,cc_ltr,address,companyName;
//    int cutomerId,transmissionTypeCode,wheelTypeCode;

    List<Brands> brandsList = new ArrayList<>();
    List<String> brandsListTmp = new ArrayList<>();
    List<EngineTypes> engineTypesList = new ArrayList<>();
    List<String> engineTypesListTmp = new ArrayList<>();
    List<ModelType> modelTypeList = new ArrayList<>();
    List<String> modelTypeListTmp = new ArrayList<>();
    List<ModelType> modelTypeListRQTmp = new ArrayList<>();

    Realm realm;

    User user;
    Boolean isConnected = false;

    List<LookUp> lookUpList = new ArrayList<>();
    List<String> lookUpListTmp = new ArrayList<>();
    List<LookUp> lookUpListRQTmp = new ArrayList<>();

    List<TransmissionTypeModel> transmissionTypeModelListTmp = new ArrayList<>();
    List<WheelTypeModel> wheelTypeModelListTmp = new ArrayList<>();

    Timber timber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cu_car_add_more);

        realm = Realm.getDefaultInstance();


        carsBinding= DataBindingUtil.setContentView(this,R.layout.activity_cu_car_add_more);

        user = RealmHelper.getUser();
        brandsList = realm.copyFromRealm(realm.where(Brands.class).findAll());
        engineTypesList = realm.copyFromRealm(realm.where(EngineTypes.class).findAll());
        modelTypeList = realm.copyFromRealm(realm.where(ModelType.class).findAll());
        lookUpList = realm.copyFromRealm(realm.where(LookUp.class).findAll());



        Tovuti.from(this).monitor(new Monitor.ConnectivityListener() {
            @Override
            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                CuCarAddMoreActivity.this.isConnected = isConnected;
                if (!isConnected)
                    Toast.makeText(CuCarAddMoreActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
            }
        });

        invSpinnerAll();

        timber=new Timber();

        carsBinding.mbwCaridCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ListFragmentCustomerInfoSearch fragment = ListFragmentCustomerInfoSearch.newInstance("Customer Search");
                fragment.show(getSupportFragmentManager(), "");

                fragment.setListenerCarIds(new ListFragmentCustomerInfoSearch.OnItemSelectedListenerCarIds() {
                    @Override
                    public void onItemSelect(ComplainCustomerInfomation customerCarInfo) {
                        //    Log.e("ex",""+pos);

                        if (customerCarInfo.getContactNo() != null) {
                            carsBinding.mbwCaridCars.setText(customerCarInfo.getCustomerName());
                        }
                        if (customerCarInfo.getSrvcCustomerId() != null) {

                            srvcCustomerId = customerCarInfo.getSrvcCustomerId().toString();
                              timber.e("Complain","srvcCustomerId "+srvcCustomerId);
                        }
                        if (customerCarInfo.getCustomerName() != null) {
                            carsBinding.CnameValueCars.setText(customerCarInfo.getCustomerName().toString());
                            marqueeLoad(carsBinding.CnameValueCars);
                        }
                        if (customerCarInfo.getContactNo() != null) {
                            carsBinding.CPhoneValueCars.setText(customerCarInfo.getContactNo().toString());
                            marqueeLoad(carsBinding.CPhoneValueCars);
                        }

                        if (customerCarInfo.getHome_addr() != null) {
                            //   timber.e("getHome_addr",""+customerCarInfo.getHome_addr().toString());
                            carsBinding. billingAddressTvCars.setText(customerCarInfo.getHome_addr().toString());
                            marqueeLoad(carsBinding.billingAddressTvCars);

                        }
                        if (customerCarInfo.getOffice_addr() != null) {
                            //   timber.e("getHome_addr",""+customerCarInfo.getHome_addr().toString());
                            carsBinding.billingAddressTvCars.setText(customerCarInfo.getOffice_addr().toString());
                            marqueeLoad(carsBinding.billingAddressTvCars);

                        }


                    }

                });


            }
        });
        carsBinding.backCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CuCarAddMoreActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        carsBinding.saveCars.setOnClickListener(new View.OnClickListener() {
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

    public void marqueeLoad(TextView textView) {
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSelected(true);
    }

    public void invSpinnerAll() {




        if (brandsList.size() > 0) {
            for (int i = 0; i < brandsList.size(); i++) {
                brandsListTmp.add(brandsList.get(i).getTitle());
                // timber.e("CreateUser", "" + brandsList.get(i).getTitle());
            }
            ArrayAdapter<String> brandsAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, brandsListTmp);
            brandsAdapter.setDropDownViewResource(R.layout.spinnerstyle);
            carsBinding.engineSpinnerCars.setAdapter(brandsAdapter);

            carsBinding.engineSpinnerCars.setOnItemSelectedListener(new BrandTypeSpinner());
        }

        if (engineTypesList.size() > 0) {
            for (int i = 0; i < engineTypesList.size(); i++) {
                engineTypesListTmp.add(engineTypesList.get(i).getTitle());
                // timber.e("CreateUser", "" + engineTypesList.get(i).getTitle());
            }

            ArrayAdapter<String> engineTypesAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, engineTypesListTmp);
            engineTypesAdapter.setDropDownViewResource(R.layout.spinnerstyle);
            carsBinding.engineModelSpinnerCars.setAdapter(engineTypesAdapter);
            carsBinding.engineModelSpinnerCars.setOnItemSelectedListener(new EngineTypeSpinner());
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
            carsBinding.fuelSpinnerCars.setAdapter(lookUpAdapter);
            carsBinding.fuelSpinnerCars.setOnItemSelectedListener(new FuelTypeSpinner());
        }

        getTransmissionTpSpinner();
        getWheelTpSpinner();

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
            carsBinding.transmissionTypeSpinnerCars.setAdapter(adapter);

            carsBinding.transmissionTypeSpinnerCars.setOnItemSelectedListener(
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
            carsBinding.wheelTypeSpinnerCars.setAdapter(adapter);

            carsBinding.wheelTypeSpinnerCars.setOnItemSelectedListener(
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
    public void onAddCarInterfaceSuccess(String msg,int id) {


        BaseUrl.getClient().create(ApiInterface.class).getCarAddMore(user.getApiKey(),user.getId().toString(),String.valueOf(id)).
                enqueue(new Callback<CommmonResponseSrvcCar>() {
                    @Override
                    public void onResponse(Call<CommmonResponseSrvcCar> call, Response<CommmonResponseSrvcCar> response) {
                        if (!response.body().getError()) {

                            if(response.body().getCustomerCarInfo()!=null){

                             //   Log.e("id..",""+response.body().getCustomerCarInfo().toString());
                                RealmHelper.saveCustomerCarInfoMore(response.body().getCustomerCarInfo());
                                carsBinding.createCuCarSpinKitCars.setVisibility(View.GONE);
                                successfuly(msg);
                            }
                            carsBinding.createCuCarSpinKitCars.setVisibility(View.GONE);

                        }

                    }

                    @Override
                    public void onFailure(Call<CommmonResponseSrvcCar> call, Throwable t) {
                        carsBinding.createCuCarSpinKitCars.setVisibility(View.GONE);
                        Toast.makeText(CuCarAddMoreActivity.this, ""+ t.toString(), Toast.LENGTH_SHORT).show();
                        clickerState=0;
                    }
                });
        clickerState=0;
    }

    @Override
    public void onAddCarInterfaceFailed(String error) {
        carsBinding.createCuCarSpinKitCars.setVisibility(View.GONE);
        Toast.makeText(CuCarAddMoreActivity.this, ""+ error, Toast.LENGTH_SHORT).show();
        clickerState=0;
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


        carsBinding.mbwCaridCars.setError(null);
        carsBinding.mbwRegNoCars.setText("");
        carsBinding.mbwChassisNoCars.setText("");
        carsBinding.mbwEngineNoCars.setText("");
        carsBinding.mbwDriverNameCars.setText("");
        carsBinding.mbwDriverNoCars.setText("");

        carsBinding.mbwYearModelCars.setText("");
        carsBinding.mbwCCCars.setText("");
        clickerState=0;
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
                carsBinding.vehiclemodelSpinnerCars.setVisibility(View.VISIBLE);
                carsBinding.vehicleModelCars.setVisibility(View.VISIBLE);

            }else{
                carsBinding.vehiclemodelSpinnerCars.setVisibility(View.GONE);
                carsBinding.vehicleModelCars.setVisibility(View.GONE);
            }

            ArrayAdapter<String> modelTypeAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, modelTypeListTmp);
            modelTypeAdapter.setDropDownViewResource(R.layout.spinnerstyle);
            carsBinding.vehiclemodelSpinnerCars.setAdapter(modelTypeAdapter);
            carsBinding.vehiclemodelSpinnerCars.setOnItemSelectedListener(new ModelTypeSpinner());
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

    private void hideKeyboard() {

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (inputMethodManager != null && view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean validation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake_error);

        if (carsBinding.mbwCaridCars.getText().toString().trim().equals("") || carsBinding.mbwCaridCars.getText().toString().trim().length() == 0) {
            carsBinding.mbwCaridCars.setError("PLease Type Car");
            //  etCollectionAmount.requestFocus();

            carsBinding.mbwCaridCars.getParent().requestChildFocus(carsBinding.mbwCaridCars, carsBinding.mbwCaridCars);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carsBinding.mbwCaridCars.startAnimation(animation);
                }
            }, 1000);
            return false;
        }
        if (carsBinding.mbwRegNoCars.getText().toString().trim().equals("") || carsBinding.mbwRegNoCars.getText().toString().trim().length() == 0) {
            carsBinding.mbwRegNoCars.setError("PLease Type Reg No");
            carsBinding.mbwRegNoCars.getParent().requestChildFocus(carsBinding.mbwRegNoCars, carsBinding.mbwRegNoCars);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carsBinding.mbwRegNoCars.startAnimation(animation);
                }
            }, 1000);

            return false;
        }
        if (carsBinding.mbwChassisNoCars.getText().toString().trim().equals("") || carsBinding.mbwChassisNoCars.getText().toString().trim().length() == 0) {
            carsBinding.mbwChassisNoCars.setError("PLease Type Chassis No");
            carsBinding.mbwChassisNoCars.getParent().requestChildFocus(carsBinding.mbwChassisNoCars, carsBinding.mbwChassisNoCars);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carsBinding.mbwChassisNoCars.startAnimation(animation);
                }
            }, 1000);

            return false;
        } if (carsBinding.mbwEngineNoCars.getText().toString().trim().equals("") || carsBinding.mbwEngineNoCars.getText().toString().trim().length() == 0) {
            carsBinding.mbwEngineNoCars.setError("PLease Type Engine No");
            carsBinding.mbwEngineNoCars.getParent().requestChildFocus(carsBinding.mbwEngineNoCars, carsBinding.mbwEngineNoCars);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carsBinding.mbwEngineNoCars.startAnimation(animation);
                }
            }, 1000);

            return false;
        }

        if (carsBinding.mbwYearModelCars.getText().toString().trim().equals("") || carsBinding.mbwYearModelCars.getText().toString().trim().length() == 0) {
            carsBinding.mbwYearModelCars.setError("PLease Type Year Model");
            carsBinding.mbwYearModelCars.getParent().requestChildFocus(carsBinding.mbwYearModelCars, carsBinding.mbwYearModelCars);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carsBinding.mbwYearModelCars.startAnimation(animation);
                }
            }, 1000);

            return false;
        }

        if (carsBinding.mbwCCCars.getText().toString().trim().equals("") || carsBinding.mbwCCCars.getText().toString().trim().length() == 0) {
            carsBinding.mbwCCCars.setError("PLease Type CC");
            carsBinding.mbwCCCars.getParent().requestChildFocus(carsBinding.mbwCCCars, carsBinding.mbwCCCars);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carsBinding.mbwCCCars.startAnimation(animation);
                }
            }, 1000);

            return false;
        }
        if (carsBinding.mbwDriverNameCars.getText().toString().trim().equals("") || carsBinding.mbwDriverNameCars.getText().toString().trim().length() == 0) {
            carsBinding.mbwDriverNameCars.setError("PLease Type Driver Name");
            carsBinding.mbwDriverNameCars.getParent().requestChildFocus(carsBinding.mbwDriverNameCars, carsBinding.mbwDriverNameCars);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carsBinding.mbwDriverNameCars.startAnimation(animation);
                }
            }, 1000);

            return false;
        }
        if (carsBinding.mbwDriverNoCars.getText().toString().trim().equals("") || carsBinding.mbwDriverNoCars.getText().toString().trim().length() == 0) {
            carsBinding.mbwDriverNoCars.setError("PLease Type Driver Number");
            carsBinding.mbwDriverNoCars.getParent().requestChildFocus(carsBinding.mbwDriverNoCars, carsBinding.mbwDriverNoCars);
            //  etCollectionAmount.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    carsBinding.mbwDriverNoCars.startAnimation(animation);
                }
            }, 1000);

            return false;
        }

        return true;
    }

    public void saveOnline() {
        if (isConnected) {
            carsBinding.createCuCarSpinKitCars.setVisibility(View.VISIBLE);


//            cutomerId = user.getId();


            regNo = carsBinding.mbwRegNoCars.getText().toString().trim();
            chassisNo = carsBinding.mbwChassisNoCars.getText().toString().trim();
            EngineNo = carsBinding.mbwEngineNoCars.getText().toString().trim();
            driverNo = carsBinding.mbwDriverNoCars.getText().toString().trim();

            driverName= carsBinding.mbwDriverNameCars.getText().toString().trim();
            year= carsBinding.mbwYearModelCars.getText().toString().trim();
            cc_ltr= carsBinding.mbwCCCars.getText().toString().trim();


            AddMoreCarsModel addMoreCarsModel = new AddMoreCarsModel(
                    srvcCustomerId,regNo, chassisNo, engineTypeId, EngineNo, brandTypeId, modelTypeId, driverNo, fuelTypeCode,
                    driverName,transmissionTypeCode,wheelTypeCode,year,cc_ltr);


         //   Log.e("valueaddcars",addMoreCarsModel.toString());
//            ServerHelper.requestAddMoreCar(user,addMoreCarsModel, (ServerHelper.AddCarInterface) this);
            ServerHelper.requestAddMoreCar(user, addMoreCarsModel,  this);


        }else {

            Toast.makeText(CuCarAddMoreActivity.this, "Please Check Your Network Connection", Toast.LENGTH_SHORT).show();
            clickerState=0;
        }


    }

}
