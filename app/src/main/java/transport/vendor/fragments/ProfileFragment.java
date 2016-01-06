package transport.vendor.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ScrollView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import transport.vendor.activity.HomeActivity;
import transport.vendor.activity.R;
import utils.Constants;
import utils.Func;
import utils.NetConnection;
import utils.StringUtils;

/**
 * Created by bharat on 12/22/15.
 */
public class ProfileFragment extends Fragment {

    View rootView;

    Typeface face;

    Button submit_bt;

    TextInputLayout company_layout, mcno_layout, address_layout, city_layout, state_layout,
            country_layout, zipcode_layout, telephone_layout, fax_layout, firstname_layout,
            lastname_layout, username_layout, email_layout;

    static EditText company_edt, mcno_edt, address_edt, city_edt, zipcode_edt,
            telephone_edt, fax_edt, firstname_edt, lastname_edt, username_edt,email_edt;

    static MultiAutoCompleteTextView country_mtv, state_mtv;

    static LinearLayout submit_layout;

    static EditText[] ets = new EditText[11];

    static MultiAutoCompleteTextView[] mtv = new MultiAutoCompleteTextView[2];

    static TextInputLayout[] inputLyoutList = new TextInputLayout[13];

    private AsyncHttpClient client;

    boolean isConnected;

    private ProgressDialog dialog;

    public static String TAG = "ProfileFragment";

    static ScrollView scrollView;

    static Context ctx;
    //  MyAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        face = Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Book.otf");
        isConnected = NetConnection.checkInternetConnectionn(getActivity());

        init();

        return rootView;
    }

    private void init() {

        HomeActivity.changeTitle("MY PROFILE", false, false);
        ctx = getActivity();

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        company_layout = (TextInputLayout) rootView.findViewById(R.id.company_layout);
        mcno_layout = (TextInputLayout) rootView.findViewById(R.id.mcno_layout);
        address_layout = (TextInputLayout) rootView.findViewById(R.id.address_layout);
        city_layout = (TextInputLayout) rootView.findViewById(R.id.city_layout);
        state_layout = (TextInputLayout) rootView.findViewById(R.id.state_layout);
        country_layout = (TextInputLayout) rootView.findViewById(R.id.country_layout);
        zipcode_layout = (TextInputLayout) rootView.findViewById(R.id.zipcode_layout);
        telephone_layout = (TextInputLayout) rootView.findViewById(R.id.telephone_layout);
        fax_layout = (TextInputLayout) rootView.findViewById(R.id.fax_layout);
        firstname_layout = (TextInputLayout) rootView.findViewById(R.id.firstname_layout);
        lastname_layout = (TextInputLayout) rootView.findViewById(R.id.lastname_layout);
        username_layout = (TextInputLayout) rootView.findViewById(R.id.username_layout);
        email_layout = (TextInputLayout) rootView.findViewById(R.id.email_layout);
        submit_layout = (LinearLayout) rootView.findViewById(R.id.submit_layout);
        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);

        submit_bt = (Button) rootView.findViewById(R.id.submit_bt);

        company_edt = (EditText) rootView.findViewById(R.id.company_edt);
        mcno_edt = (EditText) rootView.findViewById(R.id.mcno_edt);
        address_edt = (EditText) rootView.findViewById(R.id.address_edt);
        city_edt = (EditText) rootView.findViewById(R.id.city_edt);
        state_mtv = (MultiAutoCompleteTextView) rootView.findViewById(R.id.state_mtv);
        country_mtv = (MultiAutoCompleteTextView) rootView.findViewById(R.id.country_mtv);
        zipcode_edt = (EditText) rootView.findViewById(R.id.zipcode_edt);
        telephone_edt = (EditText) rootView.findViewById(R.id.telephone_edt);
        fax_edt = (EditText) rootView.findViewById(R.id.fax_edt);
        firstname_edt = (EditText) rootView.findViewById(R.id.firstname_edt);
        lastname_edt = (EditText) rootView.findViewById(R.id.lastname_edt);
        username_edt = (EditText) rootView.findViewById(R.id.username_edt);
        email_edt = (EditText) rootView.findViewById(R.id.email_edt);


        ets[0] = company_edt;
        ets[1] = mcno_edt;
        ets[2] = address_edt;
        ets[3] = city_edt;

        ets[4] = zipcode_edt;
        ets[5] = telephone_edt;
        ets[6] = fax_edt;
        ets[7] = firstname_edt;
        ets[8] = lastname_edt;
        ets[9] = username_edt;
        ets[10] = email_edt;

        mtv[0] = country_mtv;
        mtv[1] = state_mtv;


        setTypeface();
        stopEditing();

        if (isConnected) {
            getProfile();
        } else {
            StringUtils.showDialog(Constants.NO_INTERNET, getActivity());
        }

        submit_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });

        country_mtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);


                Constants.COMPANY_NAME = selection;

                for (int i = 0; i < Constants.countryList.size(); i++) {

                    if (Constants.countryList.get(i).get("COUNTRY").equalsIgnoreCase(selection)) {
                        Log.e(TAG, "country(i)=" +Constants.countryList.get(i).get("COUNTRY"));
                        Log.e(TAG, "selection=" +selection);

                        String countryID = Constants.countryList.get(i).get("SNO");
                        Constants.COUNTRY_ID = countryID;
                    }
                }

                Log.e(TAG, "country=" + selection + " id=" + Constants.COUNTRY_ID);

            }
        });

        state_mtv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);


                Constants.STATE = selection;
                for (int i = 0; i < Constants.stateList.size(); i++) {

                    if (Constants.stateList.get(i).get("State").equalsIgnoreCase(selection)) {
                        String stateID = Constants.stateList.get(i).get("StateCode");
                        Constants.STATE_ID = stateID;
                    }
                }

                Log.e(TAG, "state=" + selection + " id=" + Constants.STATE_ID);
            }
        });


    }

    /**
     * state API to get state listing
     */
    private void CallStateAPI() {
        // http://phphosting.osvin.net/JSKT/API/StateCodeList.php
        RequestParams params = new RequestParams();

        Log.e("URL", Constants.STATE_URL + "?" + params.toString());
        client.post(getActivity(), Constants.STATE_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
             //   dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
             //   dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e("onsuccess", response.toString());
                    if (response.getBoolean("ResponseCode")) {
                        JSONArray data = response.getJSONArray("Data");
                        for (int i = 0; i < data.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("ID", data.getJSONObject(i).getString("ID"));
                            map.put("State", data.getJSONObject(i).getString("State"));
                            map.put("StateCode", data.getJSONObject(i).getString("StateCode"));

                            if(data.getJSONObject(i).getString("StateCode").equalsIgnoreCase(Constants.STATE_ID)){
                                Constants.STATE =  data.getJSONObject(i).getString("State");
                            }

                            Constants.stateList.add(map);
                        }

                        // set adapter

                        //  setStateAdapter();


                    } else {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), getActivity());
                    }

                    FillDetails();

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


    /**
     * Country API to get country listing
     */

    private void CallCountryAPI() {
        // http://phphosting.osvin.net/JSKT/API/CountryCodeList.php
        RequestParams params = new RequestParams();

        Log.e("URL", Constants.COUNTRY_URL + "?" + params.toString());
        client.post(getActivity(), Constants.COUNTRY_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
               // dialog.show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
               // dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    Log.e("onsuccess", response.toString());
                    if (response.getBoolean("ResponseCode")) {
                        JSONArray data = response.getJSONArray("Data");
                        for (int i = 0; i < data.length(); i++) {
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("SNO", data.getJSONObject(i).getString("SNO"));
                            map.put("COUNTRY", data.getJSONObject(i).getString("COUNTRY"));


                            if(data.getJSONObject(i).getString("SNO").equalsIgnoreCase(Constants.COUNTRY_ID)){
                                Log.e(TAG,"sno=="+data.getJSONObject(i).getString("SNO"));
                                Log.e(TAG,"COUNTRY_ID=="+Constants.COUNTRY_ID);
                               Constants.COUNTRY_NAME =  data.getJSONObject(i).getString("COUNTRY");
                            }
                            Constants.countryList.add(map);
                        }

                    } else {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), getActivity());
                    }

                    CallStateAPI();
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

    /**
     * method to check validations
     */
    private void checkValidation() {
        if (Func.editSize(firstname_edt) <= 0) {
            firstname_layout.setError("Please enter Firstname");
            firstname_edt.requestFocus();
        } else if (Func.editSize(lastname_edt) <= 0) {
            lastname_layout.setError("Please enter Lastname");
            lastname_edt.requestFocus();
        } else if (Func.editSize(username_edt) <= 0) {
            username_layout.setError("Please enter username");
            username_edt.requestFocus();
        } else if (Func.editSize(company_edt) <= 0) {
            company_layout.setError("Please enter Company name");
            company_edt.requestFocus();
        } else if (Func.editSize(mcno_edt) <= 0) {
            mcno_layout.setError("Please enter mc no");
            mcno_edt.requestFocus();
        } else if (Func.editSize(address_edt) <= 0) {
            address_layout.setError("Please enter address");
            address_edt.requestFocus();
        } else if (Func.editSize(city_edt) <= 0) {
            city_layout.setError("Please enter city");
            city_edt.requestFocus();
        } else if (Func.editSize(state_mtv) <= 0) {
            state_layout.setError("Please enter state");
            state_mtv.requestFocus();
        } else if (Func.editSize(country_mtv) <=0) {
            country_layout.setError("Please enter country");
            country_mtv.requestFocus();
        } else if (Func.editSize(zipcode_edt) <= 0) {
            zipcode_layout.setError("Please enter zipcode");
            zipcode_edt.requestFocus();
        } else if (Func.editSize(telephone_edt) <= 0) {
            telephone_layout.setError("Please enter telephone number");
            telephone_edt.requestFocus();
        } else if (Func.editSize(fax_edt) <= 0) {
            fax_layout.setError("Please enter fax");
            fax_edt.requestFocus();
        } else {
            String firstname = StringUtils.getString(firstname_edt);
            String lastname = StringUtils.getString(lastname_edt);
            String username = StringUtils.getString(username_edt);
            String company = StringUtils.getString(company_edt);
            String mc_no = StringUtils.getString(mcno_edt);
            String address = StringUtils.getString(address_edt);
            String city = StringUtils.getString(city_edt);
           String stateName = StringUtils.getString(state_mtv);
            String countryName = StringUtils.getString(country_mtv);
            String zipcode = StringUtils.getString(zipcode_edt);
            String telephone = StringUtils.getString(telephone_edt);
            String fax = StringUtils.getString(fax_edt);
            String state = Constants.STATE_ID;
            String country = Constants.COUNTRY_ID;

            CallUpdateProfileAPI(firstname, lastname, username, company, mc_no, address, city,
                    state, country, zipcode, telephone, fax,stateName,countryName);

        }
    }

    /**
     * API to update user profile
     *
     * @param firstname
     * @param lastname
     * @param username
     * @param company
     * @param mc_no
     * @param address
     * @param city
     * @param state
     * @param country
     * @param zipcode
     * @param telephone
     * @param fax
     */

    private void CallUpdateProfileAPI(final String firstname, final String lastname, final String username,
                                      final String company, final String mc_no, final String address, final String city,
                                      final String state, final String country, final String zipcode,
                                      final String telephone, final String fax, final String stateName,
                                      final String countryNME) {

       /* http://phphosting.osvin.net/JSKT/API/UpdateProfile.php?
        userid=17&firstname=test987&lastname=test987&username=test987&phone=465123789
        &countryid=1&companyname=testcompany&mcno=98&address=testaddress&city=Chandigarh
        &state=CHD&zipcode=160101&fax=456123*/

        RequestParams params = new RequestParams();
        params.put("userid", Constants.USER_ID);
        params.put("firstname", firstname);
        params.put("lastname", lastname);
        params.put("username", username);
        params.put("countryid", country);
        params.put("mcno", mc_no);
        params.put("address", address);
        params.put("city", city);
        params.put("state", state);
        params.put("companyname", company);
        params.put("zipcode", zipcode);
        params.put("phone", telephone);
        params.put("fax", fax);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.UPDATE_PROFILE_URL + "?" + params.toString());
        client.post(getActivity(), Constants.UPDATE_PROFILE_URL, params, new JsonHttpResponseHandler() {

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

                        StringUtils.showDialog(response.getString("MessageWhatHappen"), getActivity());
                        Constants.FIRSTNAME = firstname;
                        Constants.LASTNAME = lastname;
                        Constants.USERNAME = username;
                        Constants.COMPANY_NAME = company;
                        Constants.MC_NO = mc_no;
                        Constants.ADDRESS = address;
                        Constants.CITY = city;
                        Log.e(TAG,"onSuccess stateName="+stateName);
                        Log.e(TAG,"onSuccess countryNME="+countryNME);
                        Constants.STATE = stateName;
                        Constants.COUNTRY_NAME = countryNME;
                        Constants.ZIPCODE = zipcode;
                        Constants.PHONE = telephone;
                        Constants.FAX = fax;

                        HomeActivity.SetEditCancelToDefault();

                        FillDetails();
                        stopEditing();
                    } else {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), getActivity());
                    }
                    FillDetails();
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

    /**
     * API to get user profile
     */
    private void getProfile() {
        // http://phphosting.osvin.net/JSKT/API/ViewProfile.php?userid=13
        RequestParams params = new RequestParams();
        params.put("userid", Constants.USER_ID);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.GET_PROFILE_URL + "?" + params.toString());
        client.post(getActivity(), Constants.GET_PROFILE_URL, params, new JsonHttpResponseHandler() {

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
                        JSONObject data = response.getJSONObject("Data");
                        Constants.FIRSTNAME = data.getString("FirstName");
                        Constants.LASTNAME = data.getString("LastName");
                        Constants.USERNAME = data.getString("UserName");
                        Constants.PHONE = data.getString("Phone");
                        Constants.COMPANY_NAME = data.getString("CompanyName");
                        Constants.MC_NO = data.getString("mcno");
                        Constants.ADDRESS = data.getString("Address");
                        Constants.CITY = data.getString("City");
                        Constants.STATE_ID = data.getString("State");
                        Constants.ZIPCODE = data.getString("ZipCode");
                        Constants.COUNTRY_ID = data.getString("CountryID");
                        Constants.FAX = data.getString("FaxNumber");
                        Constants.EMAIL_ID = data.getString("EmailID");

                    } else {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), getActivity());
                    }


                    //   if (Constants.countryList.size() <= 0) {
                    //      Log.e(TAG, "" + Constants.countryList);
                     CallCountryAPI();
                    //  }

                    //    if (Constants.stateList.size() <= 0) {

                    //   }

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


    /**
     * method to fill details in editText
     */
    private void FillDetails() {
        company_edt.setText(Constants.COMPANY_NAME);
        mcno_edt.setText(Constants.MC_NO);
        address_edt.setText(Constants.ADDRESS);
        city_edt.setText(Constants.CITY);
        state_mtv.setText(Constants.STATE);
        country_mtv.setText(Constants.COUNTRY_NAME);
        zipcode_edt.setText(Constants.ZIPCODE);
        telephone_edt.setText(Constants.PHONE);
        fax_edt.setText(Constants.FAX);
        firstname_edt.setText(Constants.FIRSTNAME);
        lastname_edt.setText(Constants.LASTNAME);
        username_edt.setText(Constants.USERNAME);
        email_edt.setText(Constants.EMAIL_ID);
    }

    /**
     * method to set font
     */
    private void setTypeface() {
        company_edt.setTypeface(face);
        mcno_edt.setTypeface(face);
        address_edt.setTypeface(face);
        city_edt.setTypeface(face);
        state_mtv.setTypeface(face);
        country_mtv.setTypeface(face);
        zipcode_edt.setTypeface(face);
        telephone_edt.setTypeface(face);
        fax_edt.setTypeface(face);
        firstname_edt.setTypeface(face);
        lastname_edt.setTypeface(face);
        username_edt.setTypeface(face);
        email_edt.setTypeface(face);
        submit_bt.setTypeface(face);

        inputLyoutList[0] = company_layout;
        inputLyoutList[1] = mcno_layout;
        inputLyoutList[2] = address_layout;
        inputLyoutList[3] = city_layout;
        inputLyoutList[4] = state_layout;
        inputLyoutList[5] = country_layout;
        inputLyoutList[6] = zipcode_layout;
        inputLyoutList[7] = telephone_layout;
        inputLyoutList[8] = fax_layout;
        inputLyoutList[9] = firstname_layout;
        inputLyoutList[10] = lastname_layout;
        inputLyoutList[11] = username_layout;
        inputLyoutList[12] = email_layout;

        LayFont(face, inputLyoutList);
    }

    /**
     * method to apply font on hint
     *
     * @param tf
     * @param layouts
     */
    public static void LayFont(Typeface tf, TextInputLayout layouts[]) {
        for (int i = 0; i < layouts.length; i++) {
            try {
                layouts[i].getEditText().setTypeface(tf);
                final Field cthf = layouts[i].getClass().getDeclaredField("mCollapsingTextHelper");
                cthf.setAccessible(true);
                final Object cth = cthf.get(layouts[i]);
                final Field tpf = cth.getClass().getDeclaredField("mTextPaint");
                tpf.setAccessible(true);
                ((TextPaint) tpf.get(cth)).setTypeface(tf);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * method to make editTexts focusablity to true or false
     *
     * @param value
     * @param et
     */
    public static void SetFocusability(boolean value, EditText et[]) {
        for (int i = 0; i < et.length; i++) {
            Log.e(TAG, "i==" + i+" et[i]"+et[i]);
            et[i].setFocusable(value);
            et[i].setClickable(value);
            et[i].setLongClickable(value);
            et[i].setFocusableInTouchMode(value);

            //et[i].setEnabled(value);


        }
    }

    public static void setCountryAdapter() {


        String[] COUNTRIES = new String[Constants.countryList.size()];
        for (int i = 0; i < Constants.countryList.size(); i++) {
            COUNTRIES[i] = (Constants.countryList.get(i).get("COUNTRY"));
        }
        Log.e(TAG, "COUNTRIES==>" + COUNTRIES);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
                R.layout.state_country_listitem,R.id.text1, COUNTRIES);

        country_mtv.setAdapter(adapter);
        country_mtv.setThreshold(1);
//        country_mtv.setText("Select Country");
        country_mtv.setTokenizer(new SpaceTokenizer());




    }

    public static void setStateAdapter() {
        String[] STATES = new String[Constants.stateList.size()];
        for (int i = 0; i < Constants.stateList.size(); i++) {
            STATES[i] = (Constants.stateList.get(i).get("State"));
        }
        Log.e(TAG, "STATES==>" + STATES);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
                android.R.layout.simple_dropdown_item_1line,android.R.id.text1, STATES);

        state_mtv.setAdapter(adapter);
        state_mtv.setThreshold(1);
    //    state_mtv.setText("Select State");
        state_mtv.setTokenizer(new SpaceTokenizer());
    }

    /**
     * method to make autocomplete textviews focusablity to true or false
     *
     * @param value
     * @param mtv
     */
    public static void SetMtvFocusability(boolean value, MultiAutoCompleteTextView mtv[]) {
        for (int i = 0; i < mtv.length; i++) {
             mtv[i].setFocusable(value);
            mtv[i].setClickable(value);
            mtv[i].setLongClickable(value);
            mtv[i].setFocusableInTouchMode(value);

            //mtv[i].setEnabled(value);
        }
    }

    /**
     * method to remove errors fro textInutLayout
     *
     * @param layouts
     */
    public static void removeSetError(TextInputLayout layouts[]) {
        for (int i = 0; i < layouts.length; i++) {
            layouts[i].setError(null);
        }
    }

    /**
     * method to enable edititing
     */
    public static void enableEditing() {

        SetFocusability(true, ets);
        SetMtvFocusability(true, mtv);

        setCountryAdapter();
        setStateAdapter();

        submit_layout.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
        params.weight = 8.7f;
        scrollView.setLayoutParams(params);
    }

    /**
     * method to stop editing
     */
    public static void stopEditing() {
        SetFocusability(false, ets);
        SetMtvFocusability(false, mtv);
        removeSetError(inputLyoutList);

        submit_layout.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
        params.weight = 10f;
        scrollView.setLayoutParams(params);
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
