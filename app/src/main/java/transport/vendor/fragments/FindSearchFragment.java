package transport.vendor.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import transport.vendor.activity.HomeActivity;
import transport.vendor.activity.R;
import utils.Constants;
import utils.NetConnection;
import utils.StringUtils;

/**
 * Created by sandeep on 1/18/16.
 */
public class FindSearchFragment extends Fragment implements View.OnClickListener {

    View rootView ;
    Boolean isConnected ;
    Typeface face;
    Spinner overweight_spinner;
    MultiAutoCompleteTextView search_et;
    TextView static_tv;
    RadioGroup radio_group;
    RadioButton radioButton_no, radioButton_yes;
    Button search_btn;
    MyAdapter mAdapter;
    String isHazardous= "No";
    String TAG = "FindSearchFragment" ;
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    ArrayList<HashMap<String , String>> SearchList = new ArrayList<HashMap<String, String>>();
    int firstTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.find_search, container, false);
        isConnected = NetConnection.checkInternetConnectionn(getActivity());
        face = Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Book.otf");
        HomeActivity.changeTitle("QUICK SEARCH", false, false);

        init();

        return rootView;
    }

    private void init() {

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        overweight_spinner = (Spinner) rootView.findViewById(R.id.overweight_spinner);
        search_btn = (Button) rootView.findViewById(R.id.search_btn);
        search_et = (MultiAutoCompleteTextView) rootView.findViewById(R.id.search_et);
        static_tv = (TextView) rootView.findViewById(R.id.static_tv);
        radio_group  = (RadioGroup) rootView.findViewById(R.id.radio_group);
        radioButton_no = (RadioButton) rootView.findViewById(R.id.radioButton_no);
        radioButton_yes = (RadioButton) rootView.findViewById(R.id.radioButton_yes);

        search_et.setTypeface(face);
        radioButton_no.setTypeface(face);
        radioButton_yes.setTypeface(face);
        static_tv.setTypeface(face);
        search_btn.setTypeface(face);

        search_btn.setOnClickListener(this);

        setItemsInOverweightSpinner();

        radioButton_no.setChecked(true);

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // find which radio button is selected

                if (checkedId == R.id.radioButton_no) {

                    isHazardous = "No";

                } else if (checkedId == R.id.radioButton_yes) {

                    isHazardous = "Yes";

                }

            }


        });

        search_et.addTextChangedListener(filterTextWatcher);



    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.e(TAG, "charSequence=" + s);
            if(s.length()>=3){
                String text = String.valueOf(s);
                CallSearchAPI(text);
            }
        }

    };

    private void setItemsInOverweightSpinner() {

        ArrayList<String> menuItems = new ArrayList<String>();
        menuItems.add("Overweight");
        menuItems.add("No");
        menuItems.add("20' (39 - 44.000 LBS)");
        menuItems.add("40/45' (44 - 48.000 LBS)");

        mAdapter = new MyAdapter(getActivity(),
                menuItems);
        overweight_spinner.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view==search_btn){
            String overweightText= overweight_spinner.getSelectedItem().toString();
            String searchText = search_et.getText().toString();

            String arr[] = searchText.split("-");
            searchText = arr[0];
            Log.e(TAG, "overweight==>"+overweightText);
            Log.e(TAG, "searchText==>"+searchText);
            Log.e(TAG, "hazardous==>" + isHazardous);

            Constants.ZIP_CODE = searchText;
            if(searchText.trim().length()<1){
                Toast.makeText(getActivity(),"Please select loading location.",Toast.LENGTH_SHORT).show();
            }
           else if(overweightText.equalsIgnoreCase("Overweight")){
                Toast.makeText(getActivity(),"Please select overweight.",Toast.LENGTH_SHORT).show();
            } else {
                GoToSearchRateScreen();
            }

        }
    }

    private void GoToSearchRateScreen() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = null;
        fragment = new CustomerSearchRates();
        if (fragment != null) {
            ft.replace(R.id.frame_layout, fragment);
        }
        else {
            ft.add(R.id.frame_layout, fragment);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    class MyAdapter extends BaseAdapter {

        LayoutInflater mInflater = null;

        ArrayList<String> menuItems = new ArrayList<String>();

        public MyAdapter(Context context,
                         ArrayList<String> menuList) {
            mInflater = LayoutInflater.from(getActivity());
            menuItems = menuList;

        }


        @Override
        public int getCount() {

            return menuItems.size();
        }

        @Override
        public Object getItem(int position) {
            return menuItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent, R.layout.menu_spinner_item, true);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent, R.layout.menu_spinner_item_dropdown, false);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent, int spinnerRow, boolean isDefaultRow) {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(spinnerRow, parent, false);
            TextView txt = (TextView) row.findViewById(R.id.text);

            txt.setText(menuItems.get(position));
            txt.setTypeface(face);

            return row;
        }

    }

    // **************** API for search rate ************************************//

    private void CallSearchAPI(String text) {

        // http://phphosting.osvin.net/JSKT/API/zip_search.php?search_tag=2

        RequestParams params = new RequestParams();
        params.put("search_tag",text);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.SEARCH_RATE + "?" + params.toString());

        client.post(getActivity(), Constants.SEARCH_RATE, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
              //  dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
              //  dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e("onsuccess", response.toString());
                    if (response.getBoolean("ResponseCode")) {

                        SearchList.clear();

                        JSONArray data = response.getJSONArray("Result");
                        for (int i = 0; i < data.length(); i++) {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("ID", data.getJSONObject(i).getString("ID"));
                            hashMap.put("ZipCode", data.getJSONObject(i).getString("ZipCode"));
                            hashMap.put("City", data.getJSONObject(i).getString("City"));
                            hashMap.put("State", data.getJSONObject(i).getString("State"));
                            hashMap.put("County", data.getJSONObject(i).getString("County"));
                            hashMap.put("AreaCode", data.getJSONObject(i).getString("AreaCode"));
                            hashMap.put("CityType", data.getJSONObject(i).getString("CityType"));
                            hashMap.put("CityAliasName", data.getJSONObject(i).getString("CityAliasName"));
                            hashMap.put("CityStateKey", data.getJSONObject(i).getString("CityStateKey"));
                            hashMap.put("CityMixedCase", data.getJSONObject(i).getString("CityMixedCase"));
                            hashMap.put("CityAliasMixedCase", data.getJSONObject(i).getString("CityAliasMixedCase"));
                            hashMap.put("CityDeliveryIndicator", data.getJSONObject(i).getString("CityDeliveryIndicator"));
                            hashMap.put("CarrierRouteRateSortation", data.getJSONObject(i).getString("CarrierRouteRateSortation"));
                            hashMap.put("FinanceNumber", data.getJSONObject(i).getString("FinanceNumber"));

                            SearchList.add(hashMap);
                        }

                        /*mAdapter = new MyAdapter(getActivity(), workOrderList);
                        listview.setAdapter(mAdapter);*/
                        int size = SearchList.size();
                        String[] SEARCHRATES = new String[size];

                        for (int i = 0; i < size; i++) {
                            String ZipCode = SearchList.get(i).get("ZipCode");
                            String City = SearchList.get(i).get("City");
                            String State = SearchList.get(i).get("State");

                            String searchString = ZipCode+" - "+City+", "+State;

                            SEARCHRATES[i] = (searchString);
                        }
                        Log.e(TAG, "SEARCHRATES==>" + SEARCHRATES);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                R.layout.state_country_listitem,R.id.text1, SEARCHRATES);

                    //    if(firstTime<2){
                            search_et.setAdapter(adapter);
                            search_et.setThreshold(1);
                        /*} else {
                            adapter.notifyDataSetChanged();
                        }*/
                        search_et.setAdapter(adapter);
                        search_et.setThreshold(1);
                        search_et.setTokenizer(new SpaceTokenizer());

                        firstTime++;


                    } else {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), getActivity());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString + "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e(TAG, "/" + statusCode);
                if (headers != null && headers.length > 0) {
                    for (int i = 0; i < headers.length; i++)
                        Log.e("here", headers[i].getName() + "//" + headers[i].getValue());
                }
            }
        });
    }

    public static class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer {

        private final char delimiter = ' ';

        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != delimiter) {
                i--;
            }
            while (i < cursor && text.charAt(i) == delimiter) {
                i++;
            }

            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == delimiter) {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        @Override
        public CharSequence terminateToken(CharSequence text) {
            return text;
        }
    }
}
