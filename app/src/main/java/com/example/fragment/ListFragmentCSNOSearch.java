package com.example.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.api_helper.ApiInterface;
import com.example.api_helper.BaseUrl;
import com.example.login.model.User;
import com.example.mbw.R;
import com.example.mbw.model.ComplainSubCat;
import com.example.mbw.model.CustomerCarInfo;
import com.example.takeComplain.MoreComplain.model.ComResCustomerCSNO;
import com.example.takeComplain.MoreComplain.model.CustomerCSNO;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragmentCSNOSearch extends DialogFragment {

    private static final String TAG = "ryx_ListFragmentMultiS";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Realm realm;

    public ListFragmentCSNOSearch() {
        // Required empty public constructor
    }

    public static ListFragmentCSNOSearch newInstance(String param2) {
        ListFragmentCSNOSearch fragment = new ListFragmentCSNOSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM2);
        }
        // Log.d(TAG, "onCreate: " + data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list_search_csno, container, false);
        initView();
        initEvents();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout((int) (getScreenWidth(getActivity()) *.99), (int) (getScreenHeight(getActivity()) *.99 ));
        }
        getDataCsNo();

    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.y;
    }

    private ArrayList<CustomerCSNO> data;

    private String title;

    private View rootView;
    private RecyclerView rv;
    ProgressBar csNoloading;
    Button csbt;
    private Button btnsubmit;
    private TextView tvTitle;
    private SearchView editText;
    private BasicMulitSelectRvAdapter adapter;
    int pageNumber=1;
    User user;
    String searchQuery="";

    //

    Boolean isScrolling = false;
    Boolean isScrollingAPi = true;
    int currentItems, totalItems, scrollOutItems;
    String token = "";
    LinearLayoutManager manager;

    private OnItemSelectedListenerCustomerCSNO Visitlistener;

    private void initView() {

        if (data == null)
            data = new ArrayList<>();


        user = RealmHelper.getUser();

        manager = new LinearLayoutManager(getContext());
        rv = rootView.findViewById(R.id.rv);
        editText = rootView.findViewById(R.id.et_search);
        tvTitle = rootView.findViewById(R.id.tv_title);
        rv.setLayoutManager(manager);
        adapter = new BasicMulitSelectRvAdapter(getActivity(), data);
        rv.setAdapter(adapter);
        tvTitle.setText(title);
        csNoloading= rootView.findViewById(R.id.csNoloading);
       // csbt= rootView.findViewById(R.id.csbt);





    }


    private void initEvents() {




        rv.addOnItemTouchListener(new RecyclerViewItemTouchListener(getActivity(), new RecyclerViewItemTouchListener.OnItemClickEventListener() {
            @Override
            public void onItemLongClick(View longClickedView, int adapterPosition) {

            }

            @Override
            public void onItemClick(View clickedView, int adapterPosition) {
                if (!data.isEmpty()) {
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).equals(data.get(adapterPosition))) {

                            CustomerCSNO CustomerCSNO= data.get(i);
                            Visitlistener.onItemSelect(CustomerCSNO);
                            dismiss();

                            break;


                        }
                    }
                } else {

                }

            }

            @Override
            public void onItemDoubleClick(View doubleClickedView, int adapterPosition) {

            }
        }));


        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {

                    if(isScrollingAPi){
                        isScrolling = false;
                        //  Log.e("log...","a..............");
                        pageNumber++;
                        getDataCsNo();
                    }

                }
            }
        });

    /*    csbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchQuery = editText.getText().toString().trim();
                isScrollingAPi=true;
                pageNumber=1;
                data.clear();
                adapter.notifyDataSetChanged();
                getDataCsNo();

            }
        });
*/

        editText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchQuery = query;
                isScrollingAPi=true;
                pageNumber=1;
                data.clear();
                adapter.notifyDataSetChanged();
                getDataCsNo();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery=newText;

                return false;
            }


        });



      int searchButtonId = editText.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
     //   int searchButtonId = editText.getContext().getResources().getIdentifier("android:id/search_button", null, null);
        ImageView searchButton = (ImageView) this.editText.findViewById(searchButtonId);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isScrollingAPi=true;
                pageNumber=1;
                data.clear();
                adapter.notifyDataSetChanged();
                getDataCsNo();

            }
        });


        int searchCloseButtonId = editText.getContext().getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.editText.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setQuery("", false);
                searchQuery = "";
                isScrollingAPi=true;
                pageNumber=1;
                data.clear();
                adapter.notifyDataSetChanged();
                getDataCsNo();

            }
        });




    }


    public void getDataCsNo(){
        csNoloading.setVisibility(View.VISIBLE);
        BaseUrl.getClient().create(ApiInterface.class).postCustomerCSNO(user.getApiKey(),user.getId().toString(),String.valueOf(pageNumber),searchQuery).enqueue(new Callback<ComResCustomerCSNO>() {
            @Override
            public void onResponse(Call<ComResCustomerCSNO> call, Response<ComResCustomerCSNO> response) {

                if(response.body().getCustomerCSNOList().size()>0){
                    if(response.body().getCustomerCSNOList()!= null){
                        //   Log.e("datalist",""+response.body().getCustomerCSNOList().toString());

                        List<CustomerCSNO> data2=response.body().getCustomerCSNOList();
                        data.addAll(data2);

                        adapter = new BasicMulitSelectRvAdapter(getActivity(), data);
                        rv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        csNoloading.setVisibility(View.GONE);
                    }
                }else if (response.body().getCustomerCSNOList().size()<=0){
                    isScrollingAPi=false;
                    csNoloading.setVisibility(View.GONE);
                }




            }

            @Override
            public void onFailure(Call<ComResCustomerCSNO> call, Throwable t) {
                csNoloading.setVisibility(View.GONE);
            }
        });
    }


    public OnItemSelectedListenerCustomerCSNO getListenerCarIds() {
        return Visitlistener;
    }

    public void setListenerCarIds(OnItemSelectedListenerCustomerCSNO Visitlistener) {
        this.Visitlistener = Visitlistener;
    }


    class BasicMulitSelectRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

        private ArrayList<CustomerCSNO> data = new ArrayList<>();
        private Context context;


        BasicMulitSelectRvAdapter(Context context, ArrayList<CustomerCSNO> data) {
            this.context = context;
            this.data = data;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_dropdown_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            if (data.isEmpty())
                return;

            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).tvText.setText(data.get(position).getCs_no()+"\n"+data.get(position).getCustomer_name()+"\n"+data.get(position).getCompany_name()+"\n"+
                        data.get(position).getCar_id());
            }
        }

        @Override
        public int getItemCount() {
             return data.size();
        }



        ///////-------------------------ItemViewHolder Classes----------------------------/////////

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvText;
            private ImageView ivSelected;

            ViewHolder(View view) {
                super(view);
                tvText = view.findViewById(R.id.tv);

            }
        }
    }

    public interface OnItemSelectedListenerCustomerCSNO {
        void onItemSelect(CustomerCSNO customerCSNO);


    }
}