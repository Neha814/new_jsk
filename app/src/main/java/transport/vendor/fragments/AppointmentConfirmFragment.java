package transport.vendor.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private Calendar cal;
    private int day;
    private int month;
    private int year;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_appt_confrim, container, false);
        face= Typeface.createFromAsset(getActivity().getAssets(), "Avenir-Book.otf");

        if(Constants.CONFIRM_STATUS.equals("0")) {
            HomeActivity.changeTitle("APPOINTMENT CONFIRM", true, false);
        } else if(Constants.CONFIRM_STATUS.equals("1")){
            HomeActivity.changeTitle("VIEW/MODIFY APPOINTMENT", true, false);
        }
        isConnected = NetConnection.checkInternetConnectionn(getActivity());

        init();

        return  rootView ;
    }

    private void init() {

        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

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
            Date date = sd.parse(Constants.DATE);
            Log.e(TAG,"date=="+date);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        ets[0] = company_edt;
        ets[1] = telephone_edt;
        ets[2] = contact_edt;
        ets[3] = date_edt;
        ets[4] = time_edt;

        if(Constants.CONFIRM_STATUS.equals("0")) {
            SetFocusability(false, ets);
        } else if(Constants.CONFIRM_STATUS.equals("1")){
            SetFocusability(false, ets);
        }

        confirm_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidations();

            }
        });

        final Calendar myCalendar = Calendar.getInstance();


       final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                view.setMinDate(System.currentTimeMillis() - 1000);

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                date_edt.setText(sdf.format(myCalendar.getTime()));

                String myFormatToSend = "yyyy-MM-dd";
                SimpleDateFormat sdfToSend = new SimpleDateFormat(myFormatToSend, Locale.US);
                Constants.DATE_TO_SEND = (sdfToSend.format(myCalendar.getTime()));

            }

        };


        date_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constants.CONFIRM_STATUS.equals("1")) {
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        time_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constants.CONFIRM_STATUS.equals("1")){
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {


                        String AM_PM;
                        if (selectedHour < 12) {
                            AM_PM = "AM";
                            time_edt.setText(selectedHour + ":" + selectedMinute + ":00" + AM_PM);
                        } else {
                            AM_PM = "PM";
                            time_edt.setText((selectedHour - 12) + ":" + selectedMinute + ":00" + AM_PM);
                        }

                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        }
        });

        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallCancelAPI();
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
                Constants.COMPANY = company_edt.getText().toString();
                Constants.PHONE = telephone_edt.getText().toString();
                Constants.TIME = time_edt.getText().toString();
                Constants.CONTACT = contact_edt.getText().toString();
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

            if(Constants.CONFIRM_STATUS.equals("1")){
            if( i==3 || i==4){
                et[i].setClickable(true);
                et[i].setClickable(false);
                et[i].setLongClickable(false);
                et[i].setFocusableInTouchMode(false);
            } }
        }
    }


    private void CallConfrimAPI() {
        /*http://phphosting.osvin.net/JSKT/API/Confirm_Appt.php?workorderid=1&companyname=TEST&
         contactname=testing&date=10/12/2015&time=12:00:15&phone=9875641230&email=test@gmail.com*/

        RequestParams params = new RequestParams();
        params.put("workorderid",Constants.WORKORDER_ID);
        params.put("companyname",Constants.COMPANY);
        params.put("contactname",Constants.CONTACT);
        params.put("date",Constants.DATE_TO_SEND);
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

    private void CallCancelAPI() {
        /*http://phphosting.osvin.net/JSKT/API/confirm_delete.php?WorkOrder_ID=1*/

        RequestParams params = new RequestParams();
        params.put("WorkOrder_ID",Constants.WORKORDER_ID);


        Log.e("parameters", params.toString());

        Log.e("URL", Constants.CANCEL_APPT + "?" + params.toString());
        client.post(getActivity(), Constants.CANCEL_APPT, params, new JsonHttpResponseHandler() {

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
                        showDialog(response.getString("Message"), getActivity());
                    } else {
                        showDialog(response.getString("Message"), getActivity());
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
