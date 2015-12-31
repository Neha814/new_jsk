package transport.vendor.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

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
public class AppointmentConfirmFragment extends Fragment {
    View rootView;
    Typeface face ;
    TextInputLayout company_layout,telephone_layout, contact_layout,
            date_layout, time_layout;
    EditText company_edt, telephone_edt, contact_edt, date_edt, time_edt;
    Button confirm_bt, cancel_bt;
    static EditText[] ets = new EditText[5];
    String TAG = "AppointmentConfirmFragment";
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    Boolean isConnected;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_appt_confrim, container, false);
        face= Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Book.otf");
        HomeActivity.changeTitle("Appointment Confirm", true,false);
        isConnected = NetConnection.checkInternetConnectionn(getActivity());

        init();

        return  rootView ;
    }

    private void init() {

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        company_layout = (TextInputLayout) rootView.findViewById(R.id.company_layout);
        telephone_layout = (TextInputLayout) rootView.findViewById(R.id.telephone_layout);
        contact_layout = (TextInputLayout) rootView.findViewById(R.id.contact_layout);
        date_layout = (TextInputLayout) rootView.findViewById(R.id.date_layout);
        time_layout = (TextInputLayout) rootView.findViewById(R.id.time_layout);

        company_edt = (EditText) rootView.findViewById(R.id.company_edt);
        telephone_edt = (EditText) rootView.findViewById(R.id.telephone_edt);
        contact_edt = (EditText) rootView.findViewById(R.id.contact_edt);
        date_edt = (EditText) rootView.findViewById(R.id.date_edt);
        time_edt = (EditText) rootView.findViewById(R.id.time_edt);

        confirm_bt = (Button) rootView.findViewById(R.id.confirm_bt);
        cancel_bt = (Button) rootView.findViewById(R.id.cancel_bt);

        company_edt.setTypeface(face);
        telephone_edt.setTypeface(face);
        contact_edt.setTypeface(face);
        date_edt.setTypeface(face);
        time_edt.setTypeface(face);
        confirm_bt.setTypeface(face);
        cancel_bt.setTypeface(face);

        company_edt.setText(Constants.COMPANY);
        telephone_edt.setText(Constants.TELEPHONE);
        contact_edt.setText(Constants.CONTACT);
        date_edt.setText(Constants.DATE);
        time_edt.setText(Constants.TIME);

        Log.e(TAG, "date==" + Constants.DATE);
        Log.e(TAG,"date=="+Constants.TIME);

        try{
            SimpleDateFormat sd = new SimpleDateFormat("dd MMM yyyy");
            //also tried SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            //as well as sd.setTimeZone(TimeZone.getDefault());
            Date date = sd.parse(Constants.DATE);
            Log.e(TAG,"date=="+date);
        }catch(Exception e)
        {
            e.printStackTrace();
            //fails with java.text.ParseException: Unparseable date: "Tue Mar 13 12:00:00 EST 2012"
        }

        ets[0] = company_edt;
        ets[1] = telephone_edt;
        ets[2] = contact_edt;
        ets[3] = date_edt;
        ets[4] = time_edt;

        if(Constants.CONFIRM_STATUS.equals("0")) {
            SetFocusability(false, ets);
        } else if(Constants.CONFIRM_STATUS.equals("1")){
            SetFocusability(true, ets);
        }

        confirm_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();

            }
        });

    }

    private void checkValidations() {
        if (Func.editSize(company_edt) <= 0) {
            company_layout.setError("Please enter company name");
            company_edt.requestFocus();
        } else if (Func.editSize(telephone_edt) <= 0) {
            telephone_layout.setError("Please enter telephone number");
            telephone_edt.requestFocus();
        }else if (Func.editSize(contact_edt) <= 0) {
            contact_layout.setError("Please enter contact name");
            contact_edt.requestFocus();
        }else if (Func.editSize(date_edt) <= 0) {
            date_layout.setError("Please enter date");
            date_edt.requestFocus();
        }else if (Func.editSize(time_edt) <= 0) {
            time_layout.setError("Please enter time");
            time_edt.requestFocus();
        }else   {
            if (isConnected) {
                CallConfrimAPI();
            } else {
                StringUtils.showDialog(Constants.NO_INTERNET, getActivity());
            }
        }
    }

    public static void SetFocusability(boolean value, EditText et[]) {
        for (int i = 0; i < et.length; i++) {
            et[i].setFocusable(value);
            et[i].setClickable(value);
            et[i].setLongClickable(value);
            et[i].setFocusableInTouchMode(value);
        }
    }

    private void CallConfrimAPI() {
        /*http://phphosting.osvin.net/JSKT/API/Confirm_Appt.php?workorderid=1&companyname=TEST&
         contactname=testing&date=10/12/2015&time=12:00:15&phone=9875641230&email=test@gmail.com*/

        RequestParams params = new RequestParams();
        params.put("workorderid",Constants.WORKORDER_ID);
        params.put("companyname",Constants.COMPANY);
        params.put("contactname",Constants.CONTACT);
        params.put("date",Constants.DATE);
        params.put("time",Constants.TIME);
        params.put("phone",Constants.PHONE);
        params.put("email",Constants.EMAIL);

        Log.e("parameters", params.toString());

        Log.e("URL", Constants.CONFIRM_APPT + "?" + params.toString());
        client.post(getActivity(), Constants.CONFIRM_APPT, params, new JsonHttpResponseHandler() {

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
                        showDialog(response.getString("MessageWhatHappen"), getActivity());
                    } else {
                        showDialog(response.getString("MessageWhatHappen"), getActivity());
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

    public static void showDialog(String msg, Context context) {
        try {
            AlertDialog alertDialog = new AlertDialog.Builder(
                    context).create();


            // Setting Dialog Message
            alertDialog.setMessage(msg);

            // Setting Icon to Dialog
            //	alertDialog.setIcon(R.drawable.browse);

            // Setting OK Button
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    dialog.cancel();
                 //   HomeActivity.popStack();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
