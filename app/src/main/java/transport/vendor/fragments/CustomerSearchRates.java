package transport.vendor.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import transport.vendor.activity.HomeActivity;
import transport.vendor.activity.R;
import utils.Constants;
import utils.NetConnection;
import utils.StringUtils;

/**
 * Created by bharat on 12/28/15.
 */
public class CustomerSearchRates  extends Fragment implements View.OnClickListener {

    View rootView;
    Typeface face;
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    String TAG = "CustomerSearchRates";
    Boolean isConnected;
    MyAdapter mAdapter;
    ArrayList<HashMap<String, String>> searchList = new ArrayList<HashMap<String, String>>();
    EditText search_edt;
    Button cargo_bt;
    Button find_bt;
    ListView listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.search_list, container, false);
        isConnected = NetConnection.checkInternetConnectionn(getActivity());
        face = Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Book.otf");
        HomeActivity.changeTitle("Search Rates", true, false);

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

        search_edt = (EditText) rootView.findViewById(R.id.search_edt);
        cargo_bt = (Button) rootView.findViewById(R.id.cargo_bt);
        find_bt = (Button) rootView.findViewById(R.id.find_bt);
        listview = (ListView) rootView.findViewById(R.id.listview);

        search_edt.setTypeface(face);
        cargo_bt.setTypeface(face);
        find_bt.setTypeface(face);

        if (isConnected) {
            CallSearchRateAPI();
        } else {
            StringUtils.showDialog(Constants.NO_INTERNET, getActivity());
        }

        search_edt.setText("");

        search_edt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String text = search_edt.getText().toString()
                            .toLowerCase(Locale.getDefault());
                    mAdapter.filter(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private void CallSearchRateAPI() {
        // http://phphosting.osvin.net/JSKT/API/CustomerSearchRates.php
        RequestParams params = new RequestParams();

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.CUST_SEARCH_RATE + "?" + params.toString());
        client.post(getActivity(), Constants.CUST_SEARCH_RATE, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e("onsuccess", response.toString());
                    if (response.getBoolean("ResponseCode")) {

                        searchList.clear();
                        JSONArray data = response.getJSONArray("Data");
                        for (int i = 0; i < data.length(); i++) {

                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            hashMap.put("ID", data.getJSONObject(i).getString("ID"));
                            hashMap.put("RelayID", data.getJSONObject(i).getString("RelayID"));
                            hashMap.put("Origin_ZipCode", data.getJSONObject(i).getString("Origin_ZipCode"));
                            hashMap.put("Origin_State", data.getJSONObject(i).getString("Origin_State"));
                            hashMap.put("Origin_City", data.getJSONObject(i).getString("Origin_City"));
                            hashMap.put("Dest_ZipCode", data.getJSONObject(i).getString("Dest_ZipCode"));
                            hashMap.put("Dest_State", data.getJSONObject(i).getString("Dest_State"));
                            hashMap.put("Dest_City", data.getJSONObject(i).getString("Dest_City"));
                            hashMap.put("Rate20FT", data.getJSONObject(i).getString("Rate20FT"));
                            hashMap.put("Rate40FT", data.getJSONObject(i).getString("Rate40FT"));
                            hashMap.put("Rate45FT", data.getJSONObject(i).getString("Rate45FT"));
                            hashMap.put("Commodity", data.getJSONObject(i).getString("Commodity"));
                            hashMap.put("Remarks", data.getJSONObject(i).getString("Remarks"));
                            hashMap.put("IsActive", data.getJSONObject(i).getString("IsActive"));
                            hashMap.put("IsDeleted", data.getJSONObject(i).getString("IsDeleted"));
                            hashMap.put("Created_Date", data.getJSONObject(i).getString("Created_Date"));
                            hashMap.put("CreatedBy", data.getJSONObject(i).getString("CreatedBy"));
                            hashMap.put("Modified_Date", data.getJSONObject(i).getString("Modified_Date"));
                            hashMap.put("ModifiedBy", data.getJSONObject(i).getString("ModifiedBy"));

                            searchList.add(hashMap);
                        }

                        mAdapter = new MyAdapter(getActivity(), searchList);
                        listview.setAdapter(mAdapter);

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

    class MyAdapter extends BaseAdapter {

        LayoutInflater mInflater = null;
        private ArrayList<HashMap<String, String>> mDisplayedValues;

        public MyAdapter(Context context,
                         ArrayList<HashMap<String, String>> list) {
            mInflater = LayoutInflater.from(getActivity());

            mDisplayedValues = new ArrayList<HashMap<String, String>>();
            mDisplayedValues.addAll(searchList);
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());

            searchList.clear();
            if (charText.length() == 0) {

                searchList.addAll(mDisplayedValues);
            } else {

                for (int i = 0 ;i<mDisplayedValues.size();i++) {

                    if (mDisplayedValues.get(i).get("Origin_ZipCode").toLowerCase(Locale.getDefault())
                            .startsWith(charText) ||mDisplayedValues.get(i).get("Dest_ZipCode").
                            toLowerCase(Locale.getDefault())
                            .startsWith(charText) ) {


                        searchList.add(mDisplayedValues.get(i));

                    }
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {

            return searchList.size();
        }

        @Override
        public Object getItem(int position) {
            return searchList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.search_listitem,
                        null);

                holder.detail1 = (TextView) convertView.findViewById(R.id.detail1);
                holder.status_bt = (Button) convertView.findViewById(R.id.status_bt);
                holder.route_bt = (Button) convertView.findViewById(R.id.route_bt);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.detail1.setTypeface(face);
            holder.status_bt.setTypeface(face);
            holder.route_bt.setTypeface(face);


            holder.status_bt.setTag(position);
            holder.route_bt.setTag(position);

            holder.detail1.setText(searchList.get(position).get("Origin_ZipCode") + " - " +
                    searchList.get(position).get("Dest_ZipCode") + " - " +
                    searchList.get(position).get("Rate45FT"));

            holder.route_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (Integer)view.getTag();
                    showOnMap(searchList.get(pos).get("Origin_ZipCode"), searchList.get(pos).get("Dest_ZipCode"));
                    goToMapScreen();
                }
            });

            return convertView;
        }

        private void goToMapScreen() {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = null;
            fragment = new ViewMapFragment();
            if (fragment != null) {
                ft.replace(R.id.frame_layout, fragment);
            }
            else {
                ft.add(R.id.frame_layout, fragment);
            }
            ft.addToBackStack(null);
            ft.commit();
        }

        class ViewHolder {
            TextView detail1;
            Button status_bt, route_bt;
        }

    }

    private void showOnMap(String fromZip , String toZip) {

        final Geocoder geocoder = new Geocoder(getActivity());

        try{
            for(int i=0 ; i<2;i++) {
                if(i==0) {
                    List<Address> addresses = geocoder.getFromLocationName(fromZip, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        Constants.FromLat = address.getLatitude();
                        Constants.FromLng = address.getLongitude();
                    }
                } else if(i==1){
                    List<Address> addresses = geocoder.getFromLocationName(toZip, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        Constants.ToLat = address.getLatitude();
                        Constants.ToLng = address.getLongitude();
                    }
                }
            }

            Log.e(TAG,"Constants.FromLat="+Constants.FromLat);
            Log.e(TAG,"Constants.FromLng="+Constants.FromLng);
            Log.e(TAG,"Constants.ToLat="+Constants.ToLat);
            Log.e(TAG,"Constants.ToLng="+Constants.ToLng);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
