package com.example.takeComplain.MoreComplain;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.createUserCar.model.CreateNewUserCarModel;
import com.example.fragment.ListFragmentCSNOSearch;
import com.example.fragment.ListFragmentSubCategorySearch;
import com.example.login.model.User;
import com.example.mbw.R;
import com.example.mbw.databinding.ActivityAddMoreComplainBinding;
import com.example.mbw.model.ComplainCat;
import com.example.mbw.model.ComplainHeads;
import com.example.mbw.model.ComplainSubCat;
import com.example.mbw.model.LookUp;
import com.example.mbw.model.Priorities;
import com.example.takeComplain.MoreComplain.model.AddMoreComplain;
import com.example.takeComplain.MoreComplain.model.CustomerCSNO;
import com.example.utils.Timber;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class AddMoreComplainActivity extends AppCompatActivity implements ServerHelper.AddMoreComplainsInterface {

    ActivityAddMoreComplainBinding complainBinding;

    List<ComplainHeads> complainHeadsList = new ArrayList<>();
    List<String> complainHeadsListTmp = new ArrayList<>();
    List<ComplainCat> complainCatList = new ArrayList<>();
    List<String> complainCatListTmp = new ArrayList<>();

    Realm realm;
    List<Priorities> prioritiesList = new ArrayList<>();
    List<String> prioritiesListTmp = new ArrayList<>();

    String time, complainCatID, complainCatTitle, complainSubCatID="-1",
            srvcCustomerId, srvcCustomerCarId,complain_cause,complain_remarks,complain_sheet_id;
    String prioritiesTitle;
    int prioritiesID;

    User user;
    Timber timber;
    int clickerState = 0;
    int acDeId;
    ArrayList<String> acDenyarrayList = new ArrayList<>();
    boolean isPrirotySelecd=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_complain);

        complainBinding= DataBindingUtil.setContentView(this,R.layout.activity_add_more_complain);

        timber = new Timber();
        realm = Realm.getDefaultInstance();
        user = RealmHelper.getUser();



        complainHeadsList = realm.copyFromRealm(realm.where(ComplainHeads.class).findAll());
        complainCatList = realm.copyFromRealm(realm.where(ComplainCat.class).findAll());

        prioritiesList = realm.copyFromRealm(realm.where(Priorities.class).findAll());

        complainBinding.mbwSubCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListFragmentSubCategorySearch fragment = ListFragmentSubCategorySearch.newInstance("Sub-Category");
                fragment.show(getSupportFragmentManager(), "");

                fragment.setListenerSubCategory(new ListFragmentSubCategorySearch.OnItemSelectedListenerSubCategory() {
                    @Override
                    public void onItemSelect(ComplainSubCat complainSubCat) {
                        //    Log.e("ex",""+pos);
                        complainBinding.mbwSubCat.setError(null);
                        complainBinding.mbwSubCat.setText(complainSubCat.getTitle());


                        complainHeadInit(complainSubCat.getComplain_head_id());
                        complainCatInit(complainSubCat.getComplain_cat_id());
                        complainCatID = complainSubCat.getComplain_cat_id().toString();
                        complainSubCatID = complainSubCat.getId().toString();


                    }

                });

            }
        });




        acDenyarrayList.add("Accepted");
        acDenyarrayList.add("Deny");
        ArrayAdapter<String> lookUpAdapter = new ArrayAdapter<String>(this, R.layout.spinnerstyle, acDenyarrayList);
        lookUpAdapter.setDropDownViewResource(R.layout.spinnerstyle);
        complainBinding.complainSheetAccepted.setAdapter(lookUpAdapter);
        complainBinding.complainSheetAccepted.setOnItemSelectedListener(new AcDenySpinner());

        prioritySpinnerInit();

        complainBinding.back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });


        complainBinding.saveinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard();
                if (validateData() && clickerState==0) {
                    clickerState = 1;
                    saveOnline();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill up required field!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        complainBinding.mbwCSno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                ListFragmentCSNOSearch fragment = ListFragmentCSNOSearch.newInstance("CS no Search");

                fragment.show(getSupportFragmentManager(), "");
                fragment.setListenerCarIds(new ListFragmentCSNOSearch.OnItemSelectedListenerCustomerCSNO() {
                    @Override
                    public void onItemSelect(CustomerCSNO customerCSNO) {

                        if(customerCSNO.getCustomer_name()!=null){
                            complainBinding.CustomerName.setText(customerCSNO.getCustomer_name().toString());
                            marqueeLoad(complainBinding.CustomerName);
                        }
                        if(customerCSNO.getCompany_name()!=null){
                            complainBinding.CustomerCompnayName.setText(customerCSNO.getCompany_name().toString());
                            marqueeLoad(complainBinding.CustomerCompnayName);
                        }

                        if(customerCSNO.getCar_id()!=null){
                            complainBinding.customerCarIds.setText(customerCSNO.getCar_id().toString());
                        }
                        if(customerCSNO.getCs_no()!=null){
                            complainBinding.mbwCSno.setText(customerCSNO.getCs_no().toString());
                            complainBinding.csNo.setText(customerCSNO.getCs_no().toString());
                            complain_sheet_id=String.valueOf(customerCSNO.getComplain_sheet_id());
                        }


                    }
                });

            }
        });



    }
    public void saveOnline(){
        complainBinding.addMoreComplainSpinKit.setVisibility(View.VISIBLE);

        complain_remarks = complainBinding.mbwRemarks.getText().toString().trim();
        complain_cause = complainBinding.mbwCauseDtc.getText().toString().trim();

/*                    String complain_sheet_id, String compalin_cat_id, String complain_sub_cat_id,
               String priority_id, String complain_remarks, String complain_cause, String is_accepted

                     AddMoreComplain createNewUserCarModel = new AddMoreComplain(
                complain_sheet_id,  complainCatID,   complainSubCatID, String.valueOf(prioritiesID), complain_remarks ,complain_cause,
                String.valueOf(acDeId) );


 */

        AddMoreComplain createNewUserCarModel = new AddMoreComplain(
                complain_sheet_id,  complainCatID,   complainSubCatID, String.valueOf(prioritiesID), complain_remarks ,complain_cause,
                String.valueOf(acDeId) );

        ServerHelper.requestAddMoreComplains(user,createNewUserCarModel,this);

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
            complainBinding.complainHeadSpinner.setAdapter(complainHeadsListAdapter);
            complainBinding.complainHeadSpinner.setOnItemSelectedListener(new ComplainHeadSpinner());
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
            complainBinding.complainCatSpinner.setAdapter(complainCatAdapter);
            complainBinding.complainCatSpinner.setOnItemSelectedListener(new ComplainCatSpinner());
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
            complainBinding.prioritySpinner.setAdapter(complainPriorityAdp);


            complainPriorityAdp.insert("",0);


            complainBinding. prioritySpinner.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    // to set value of first selection, because setOnItemSelectedListener will not dispatch if the user selects first element

                    complainPriorityAdp.remove("");

                    isPrirotySelecd=true;

                    return false;
                }

            });

            complainBinding.prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    @Override
    public void onAddMoreComplainsInterfaceSuccess(String msg) {
        complainBinding.addMoreComplainSpinKit.setVisibility(View.GONE);
        clearAll(msg);

    }

    @Override
    public void onAddMoreComplainsInterfaceFailed(String error) {
        clickerState=0;
        complainBinding.addMoreComplainSpinKit.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),""+ error, Toast.LENGTH_SHORT).show();
    }

    public void clearAll(String msg){

        clickerState=0;
        complainBinding.mbwSubCat.setError(null);
        complainBinding.mbwSubCat.setText("");
        complainBinding.mbwRemarks.setText("");
        complainBinding.mbwCauseDtc.setText("");

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


    private boolean validateData() {


        if (complainBinding.mbwSubCat.getText().toString().trim().equals("") || complainBinding.mbwSubCat.getText().toString().trim().length() == 0) {
            complainBinding.mbwSubCat.setError("PLease Type Sub-Complain");
            //  etCollectionAmount.requestFocus();

            complainBinding.mbwSubCat.getParent().requestChildFocus(complainBinding.mbwSubCat, complainBinding.mbwSubCat);
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

    public void marqueeLoad(TextView textView) {
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSelected(true);
    }


}