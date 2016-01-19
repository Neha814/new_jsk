package transport.vendor.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class LoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher {


    private EditText email_edt, password_edt;
    private ProgressDialog dialog;
    private Button login_btn;
    TextView static_text, sign_up;
    private String TAG = "LoginActivity", regid;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 101;
    private TextInputLayout email_layout, password_layout, forget_layout;
    private AsyncHttpClient loginClient;
    private TextView forget_pwd;
    public static final int OtherCode = 103;
    Typeface face;
    Boolean isConnected;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        face = Typeface.createFromAsset(getAssets(), "Avenir-Book.otf");
        isConnected = NetConnection.checkInternetConnectionn(getApplicationContext());
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        initialize();
    }

    private void initialize() {
        loginClient = new AsyncHttpClient();
        loginClient.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // Func.set_title_to_actionbar("Login", this, mToolbar, false);
        LinearLayout back_layout = (LinearLayout) mToolbar.findViewById(R.id.back_layout);
        TextView title_tv = (TextView) mToolbar.findViewById(R.id.title_tv);
        ImageView back_img = (ImageView) mToolbar.findViewById(R.id.back_img);
        /*ImageView edit_img = (ImageView) mToolbar.findViewById(R.id.edit_img);
        LinearLayout edit_layout = (LinearLayout) mToolbar.findViewById(R.id.edit_layout);
        ImageView cancel_img = (ImageView) mToolbar.findViewById(R.id.cancel_img);
        LinearLayout cancel_layout = (LinearLayout) mToolbar.findViewById(R.id.cancel_layout);*/
        TextView logout_img = (TextView) mToolbar.findViewById(R.id.logout_img);
        LinearLayout logout_layout = (LinearLayout) mToolbar.findViewById(R.id.logout_layout);

        title_tv.setText("SIGN IN");
        title_tv.setTypeface(face);
        back_layout.setVisibility(View.GONE);
        back_img.setVisibility(View.GONE);
        logout_img.setVisibility(View.GONE);
        logout_layout.setVisibility(View.GONE);


        login_btn = (Button) findViewById(R.id.login_btn);
        email_edt = (EditText) findViewById(R.id.email_edt);
        password_edt = (EditText) findViewById(R.id.password_edt);
        forget_pwd = (TextView) findViewById(R.id.forget_pwd);
        static_text = (TextView) findViewById(R.id.static_text);
        sign_up = (TextView) findViewById(R.id.sign_up);


        email_layout = (TextInputLayout) findViewById(R.id.email_layout);
        password_layout = (TextInputLayout) findViewById(R.id.password_layout);
        forget_layout = (TextInputLayout) findViewById(R.id.forget_layout);

        login_btn.setOnClickListener(this);
        forget_pwd.setOnClickListener(this);
        sign_up.setOnClickListener(this);

        email_edt.addTextChangedListener(this);
        password_edt.addTextChangedListener(this);


        // set typeface
        login_btn.setTypeface(face);
        email_edt.setTypeface(face);
        password_edt.setTypeface(face);
        forget_pwd.setTypeface(face);
        static_text.setTypeface(face);
        sign_up.setTypeface(face);
    }

  /*  public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {

                finish();
            }
            return false;
        }
        return true;
    }
*/


    /*private void EnableUI(boolean value) {
        login_btn.setEnabled(value);
    }*/

    public void check_validations() {

        if (Func.editSize(email_edt) <= 0) {
            email_layout.setError("Please enter email");
            email_edt.requestFocus();
        }
        /*else if (!Func.isValidEmail(email_edt.getText())) {
            email_layout.setError("Please enter valid email");
            email_edt.requestFocus();
        } */
        else if (Func.editSize(password_edt) <= 0) {
            password_layout.setError("Please enter password");
            password_edt.requestFocus();
        } else {
            if (isConnected) {
                String email = StringUtils.getString(email_edt);
                String pasword = StringUtils.getString(password_edt);
                CallLoginAPI(email, pasword);
            } else {
                StringUtils.showDialog(Constants.NO_INTERNET, LoginActivity.this);
            }

        }
    }

    private void CallLoginAPI(String email, String password) {
        // http://phphosting.osvin.net/JSKT/API/SignIn.php?EmailId=CUSTOMER&UserPassword=CUST123
        RequestParams params = new RequestParams();
        params.put("EmailId", email);
        params.put("UserPassword", password);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.LOGIN_URL + "?" + params.toString());
        loginClient.post(this, Constants.LOGIN_URL, params, new JsonHttpResponseHandler() {

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
                        String firstname = response.getString("firstName");
                        Constants.USER_ID = user_id;
                        Constants.ROLE_ID = role_id;
                        Constants.NAME = firstname;
                        SharedPreferences.Editor e = sp.edit();
                        e.putString("user_id", user_id);
                        e.putString("role_id", role_id);
                        e.putString("name", firstname);
                        e.commit();


                            Intent in = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(in);
                            finish();



                    } else {
                        StringUtils.showDialog(response.getString("MessageWhatHappen"), LoginActivity.this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:

                // EnableUI(true);
                check_validations();

                break;

            case R.id.forget_pwd:
                startActivityForResult(new Intent(this, ForgotPasswordActivity.class), OtherCode);
                break;

            case R.id.sign_up:

                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
