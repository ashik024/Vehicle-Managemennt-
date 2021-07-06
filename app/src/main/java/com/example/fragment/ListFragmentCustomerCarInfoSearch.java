package com.example.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.R;
import com.example.mbw.model.ComplainSubCat;
import com.example.mbw.model.CustomerCarInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ListFragmentCustomerCarInfoSearch extends DialogFragment {

    private static final String TAG = "ryx_ListFragmentMultiS";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Realm realm;
    List<CustomerCarInfo>customerCarInfoList= new ArrayList<>();
    public ListFragmentCustomerCarInfoSearch() {
        // Required empty public constructor
    }

    public static ListFragmentCustomerCarInfoSearch newInstance(String param2) {
       ListFragmentCustomerCarInfoSearch fragment = new ListFragmentCustomerCarInfoSearch();
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
        rootView = inflater.inflate(R.layout.fragment_list_search, container, false);
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

    private ArrayList<String> data;
    private ArrayList<String> filterData;
    private String title;

    private View rootView;
    private RecyclerView rv;
    private Button btnsubmit;
    private TextView tvTitle;
    private EditText editText;
    private BasicMulitSelectRvAdapter adapter;

    private OnItemSelectedListenerCarIds Visitlistener;

    private void initView() {

        if (data == null)
            data = new ArrayList<>();

        customerCarInfoList = realm.copyFromRealm(realm.where(CustomerCarInfo.class).findAll());
      //  Log.e("CreateUser", "" + customerCarInfoList.size());
        for (int i = 0; i < customerCarInfoList.size(); i++) {
            data.add(customerCarInfoList.get(i).getCarId().toString());

        }

        filterData = new ArrayList<>();
        filterData = data;
        rv = rootView.findViewById(R.id.rv);
        editText = rootView.findViewById(R.id.et_search);
        tvTitle = rootView.findViewById(R.id.tv_title);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BasicMulitSelectRvAdapter(getActivity(), data);
        rv.setAdapter(adapter);
        tvTitle.setText(title);


    }


    private void initEvents() {
        rv.addOnItemTouchListener(new RecyclerViewItemTouchListener(getActivity(), new RecyclerViewItemTouchListener.OnItemClickEventListener() {
                    @Override
                    public void onItemLongClick(View longClickedView, int adapterPosition) {

                    }

                    @Override
                    public void onItemClick(View clickedView, int adapterPosition) {

                      //  Log.e("CreateUser", "" + filterData.get(adapterPosition));

                      //  adapter.setSelectedDataMulti(filterData.get(adapterPosition), adapterPosition);
                      if (!filterData.isEmpty()) {
                            for (int i = 0; i < data.size(); i++) {
                                if (data.get(i).equals(filterData.get(adapterPosition))) {

                                    CustomerCarInfo customerCarInfo= customerCarInfoList.get(i);
                                    Visitlistener.onItemSelect(customerCarInfo);
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

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    public OnItemSelectedListenerCarIds getListenerCarIds() {
        return Visitlistener;
    }

    public void setListenerCarIds(OnItemSelectedListenerCarIds Visitlistener) {
        this.Visitlistener = Visitlistener;
    }

    class BasicMulitSelectRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

        private ArrayList<String> data = new ArrayList<>();
        private Context context;
        private ArrayList<String> selectedData = new ArrayList<>();
        private ArrayList<String> dataFilter = new ArrayList<>();

        BasicMulitSelectRvAdapter(Context context, ArrayList<String> data) {
            this.context = context;
            this.data = data;
            this.dataFilter = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_dropdown_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (dataFilter.isEmpty())
                return;
            if (holder instanceof ViewHolder) {
                ((ViewHolder) holder).tvText.setText(dataFilter.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (dataFilter.isEmpty())
                return 0;
            else
                return dataFilter.size();
        }



        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        dataFilter = data;
                    } else {
                        ArrayList<String> filteredList = new ArrayList<>();
                        for (String row : data) {
                           // if (row.toLowerCase().replace("-","").contains(charString.toLowerCase())) {
                            if (row.toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }
                        dataFilter = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = dataFilter;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    dataFilter = (ArrayList<String>) filterResults.values;
                    filterData = (ArrayList<String>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
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

    public interface OnItemSelectedListenerCarIds {
        void onItemSelect(CustomerCarInfo customerCarInfo);


    }
}
