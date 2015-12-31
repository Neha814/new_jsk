package transport.vendor.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import utils.Constants;
import utils.Func;
import utils.NetConnection;
import utils.StringUtils;

/**
 * Created by bharat on 12/21/15.
 */
public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    Typeface face;
    EditText name_edt, email_edt,
            phone_edt, company_edt, lastname_edt;
    TextView static_text, sign_in;
    Button create_btn;
    TextInputLayout name_layout, email_layout,
            lastname_layout, company_layout, phone_layout;
    Boolean isConnected;
    private AsyncHttpClient SignUpClient;
    private ProgressDialog dialog;
    private String TAG = "SignUpActivity";
    SharedPreferences sp;
    CheckBox agree_checkbox;
    TextView terms_tv;
    boolean isCheckboxChecked = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        face = Typeface.createFromAsset(getAssets(), "Avenir-Book.otf");
        isConnected = NetConnection.checkInternetConnectionn(getApplicationContext());
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        initialize();

    }

    private void initialize() {

        SignUpClient = new AsyncHttpClient();
        SignUpClient.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        name_edt = (EditText) findViewById(R.id.name_edt);
        email_edt = (EditText) findViewById(R.id.email_edt);
     //   password_edt = (EditText) findViewById(R.id.password_edt);
        phone_edt = (EditText) findViewById(R.id.phone_edt);
        company_edt = (EditText) findViewById(R.id.company_edt);
        lastname_edt = (EditText) findViewById(R.id.lastname_edt);
        static_text = (TextView) findViewById(R.id.static_text);
        sign_in = (TextView) findViewById(R.id.sign_in);
        create_btn = (Button) findViewById(R.id.create_btn);
        name_layout = (TextInputLayout) findViewById(R.id.name_layout);
        email_layout = (TextInputLayout) findViewById(R.id.email_layout);
    //    password_layout = (TextInputLayout) findViewById(R.id.password_layout);
        lastname_layout = (TextInputLayout) findViewById(R.id.lastname_layout);
        company_layout = (TextInputLayout) findViewById(R.id.company_layout);
        phone_layout = (TextInputLayout) findViewById(R.id.phone_layout);
        phone_edt = (EditText) findViewById(R.id.phone_edt);
        terms_tv = (TextView) findViewById(R.id.terms_tv);
        agree_checkbox = (CheckBox) findViewById(R.id.agree_checkbox);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //  Func.set_title_to_actionbar("Register", this, mToolbar, false);
        LinearLayout back_layout = (LinearLayout) mToolbar.findViewById(R.id.back_layout);
        TextView title_tv = (TextView) mToolbar.findViewById(R.id.title_tv);
        ImageView back_img = (ImageView) mToolbar.findViewById(R.id.back_img);
        ImageView edit_img = (ImageView) mToolbar.findViewById(R.id.edit_img);
        LinearLayout edit_layout = (LinearLayout) mToolbar.findViewById(R.id.edit_layout);
        ImageView cancel_img = (ImageView) mToolbar.findViewById(R.id.cancel_img);
        LinearLayout cancel_layout = (LinearLayout) mToolbar.findViewById(R.id.cancel_layout);

        title_tv.setText("Sign Up");
        title_tv.setTypeface(face);
        back_layout.setVisibility(View.GONE);
        back_img.setVisibility(View.GONE);
        edit_img.setVisibility(View.GONE);
        edit_layout.setVisibility(View.GONE);
        cancel_img.setVisibility(View.GONE);
        cancel_layout.setVisibility(View.GONE);

        name_edt.setTypeface(face);
        email_edt.setTypeface(face);
      //  password_edt.setTypeface(face);
        static_text.setTypeface(face);
        sign_in.setTypeface(face);
        create_btn.setTypeface(face);
        phone_edt.setTypeface(face);
        company_edt.setTypeface(face);
        lastname_edt.setTypeface(face);
        agree_checkbox.setTypeface(face);
        terms_tv.setTypeface(face);

        sign_in.setOnClickListener(this);
        back_img.setOnClickListener(this);
        back_layout.setOnClickListener(this);
        create_btn.setOnClickListener(this);

        agree_checkbox
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            isCheckboxChecked = true;
                            agree_checkbox
                                    .setButtonDrawable(R.drawable.ic_checkbox);
                        } else {
                            isCheckboxChecked = false;
                            agree_checkbox
                                    .setButtonDrawable(R.drawable.ic_box);
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in:
                finish();
                break;
            case R.id.back_layout:
                finish();
            case R.id.back_img:
                finish();
            case R.id.create_btn:
                    CheckValidation();

        }
    }

    protected void showSignUpDialog() {
        final Dialog dialog;
        dialog = new Dialog(SignUpActivity.this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);

        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(0);
        dialog.getWindow().setBackgroundDrawable(d);
        final RadioGroup radio_group;
        Button ok_bt;
        TextView static_tv;
        RadioButton radioButton_cust, radioButton_vendor;
        LinearLayout cross_layout;
        ImageView cross_img;

        dialog.setContentView(R.layout.signup_dialog);

        radio_group = (RadioGroup) dialog.findViewById(R.id.radio_group);
        radioButton_cust = (RadioButton) dialog.findViewById(R.id.radioButton_cust);
        radioButton_vendor = (RadioButton) dialog.findViewById(R.id.radioButton_vendor);
        static_tv = (TextView) dialog.findViewById(R.id.static_tv);
        ok_bt = (Button) dialog.findViewById(R.id.ok_bt);
        cross_img = (ImageView) dialog.findViewById(R.id.cross_img);
        cross_layout = (LinearLayout) dialog.findViewById(R.id.cross_layout);

        static_tv.setTypeface(face);
        ok_bt.setTypeface(face);
        radioButton_cust.setTypeface(face);
        radioButton_vendor.setTypeface(face);

        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // find which radio button is selected

                if (checkedId == R.id.radioButton_cust) {

                    Constants.ROLE_ID_TO_SEND = "2";

                } else if (checkedId == R.id.radioButton_vendor) {

                    Constants.ROLE_ID_TO_SEND = "1";

                }

            }


        });


        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = radio_group.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    StringUtils.showDialog("Please select user to proceed further.", SignUpActivity.this);
                } else {
                    dialog.dismiss();
                    if (isConnected) {
                        String firstname = StringUtils.getString(name_edt);
                        String lastname = StringUtils.getString(lastname_edt);
                        String email = StringUtils.getString(email_edt);
                        String username = StringUtils.getString(company_edt);
                 //       String password = StringUtils.getString(password_edt);
                        String phonenumber = StringUtils.getString(phone_edt);
                        CallSignUpAPI(firstname, lastname, email, username, phonenumber);
                    } else {
                        StringUtils.showDialog(Constants.NO_INTERNET, SignUpActivity.this);
                    }
                }
            }
        });

        cross_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        cross_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private void CheckValidation() {


        if (Func.editSize(name_edt) <= 0) {
            name_layout.setError("Please enter Firstname");
            name_edt.requestFocus();
        } else if (Func.editSize(lastname_edt) <= 0) {
            lastname_layout.setError("Please enter Lastname");
            lastname_edt.requestFocus();
        } else if (Func.editSize(name_edt) <= 0) {
            email_layout.setError("Please enter email address");
            email_edt.requestFocus();
        } else if (!Func.isValidEmail(email_edt.getText())) {
            email_layout.setError("Please enter valid email");
            email_edt.requestFocus();
        } else if (Func.editSize(company_edt) <= 0) {
            company_layout.setError("Please enter company name");
            company_edt.requestFocus();
        }
       /* else if (Func.editSize(password_edt) <= 0 || Func.editSize(password_edt) <8) {
            password_layout.setError("Please enter password of minimum 8 characters");
            password_edt.requestFocus();
        }*/
        /*else if (!StringUtils.isValidPassword(StringUtils.getString(password_edt))) {
            password_layout.setError("Please enter password having atleast one capital letter, small letter, special charachter and number.");
            password_edt.requestFocus();
        }*/
       /* else if (Func.editSize(phone_edt) <= 0) {
            phone_layout.setError("Please enter phone number");
            phone_edt.requestFocus();
        } */

        else {
            if(isCheckboxChecked) {
                showSignUpDialog();
            } else {
                StringUtils.showDialog("Please accept terms and condition to proceed further.",SignUpActivity.this);
            }



        }
    }

    private void CallSignUpAPI(String firstname, String lastname, String email, String username, String phoneno) {
       // http://phphosting.osvin.net/JSKT/API/SignUp.php?firstname=Ekam&lastname=Kaur&
        // email=ekamkaur56312@gmail.com&username=EkamKaur&phone=9876543210&roleid=1&
        // company_name=Test company

        RequestParams params = new RequestParams();
        params.put("firstname", firstname);
        params.put("lastname", lastname);
        params.put("email", email);
        params.put("username", "");
        params.put("phone", phoneno);
        params.put("roleid", Constants.ROLE_ID_TO_SEND);
        params.put("company_name", username);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.SIGNUP_URL + "?" + params.toString());
        SignUpClient.post(this, Constants.SIGNUP_URL, params, new JsonHttpResponseHandler() {

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

                        String user_id = response.getString("userid");
                        String role_id = response.getString("roleid");
                        Constants.USER_ID = user_id;
                        Constants.ROLE_ID = role_id;
                        SharedPreferences.Editor e = sp.edit();
                        e.putString("user_id", user_id);
                        e.putString("role_id", role_id);
                        e.commit();

                        Toast.makeText(getApplicationContext(),"Registered Succesfully.",Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(in);
                        finish();

                    } else {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), SignUpActivity.this);
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
}
