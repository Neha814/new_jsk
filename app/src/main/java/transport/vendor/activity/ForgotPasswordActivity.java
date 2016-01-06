package transport.vendor.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

public class ForgotPasswordActivity extends AppCompatActivity implements OnClickListener {

    EditText email_edt;
    TextInputLayout email_layout;
    Button forgot_button;
    TextView static_text;
    Typeface face ;
    Boolean isConnected;
    private AsyncHttpClient client;
    private ProgressDialog dialog;
    String TAG = "ForgotPaasword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_pswd);

        client = new AsyncHttpClient();
        client.setTimeout(Constants.connection_timeout);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading..");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        face=Typeface.createFromAsset(getAssets(), "Avenir-Book.otf");
        isConnected = NetConnection.checkInternetConnectionn(getApplicationContext());

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
      //  Func.set_title_to_actionbar("Forgot Password", this, mToolbar, false);
        LinearLayout back_layout = (LinearLayout) mToolbar.findViewById(R.id.back_layout);
        TextView title_tv = (TextView) mToolbar.findViewById(R.id.title_tv);
        ImageView back_img = (ImageView) mToolbar.findViewById(R.id.back_img);
        ImageView edit_img = (ImageView) mToolbar.findViewById(R.id.edit_img);
        LinearLayout edit_layout = (LinearLayout) mToolbar.findViewById(R.id.edit_layout);
        ImageView cancel_img = (ImageView) mToolbar.findViewById(R.id.cancel_img);
        LinearLayout cancel_layout = (LinearLayout) mToolbar.findViewById(R.id.cancel_layout);

        title_tv.setText("FORGOT PASSWORD");
        title_tv.setTypeface(face);
        back_layout.setVisibility(View.GONE);
        back_img.setVisibility(View.GONE);
        edit_img.setVisibility(View.GONE);
        edit_layout.setVisibility(View.GONE);
        cancel_img.setVisibility(View.GONE);
        cancel_layout.setVisibility(View.GONE);

        back_img.setOnClickListener(this);
        back_layout.setOnClickListener(this);

        initViews();
    }

    private void initViews() {

        email_edt = (EditText) findViewById(R.id.email_edt);
        email_layout = (TextInputLayout) findViewById(R.id.email_layout);
        forgot_button = (Button) findViewById(R.id.forgot_button);
        static_text = (TextView) findViewById(R.id.static_text);

        email_edt.setTypeface(face);
        email_layout.setTypeface(face);
        forgot_button.setTypeface(face);
        static_text.setTypeface(face);
        forgot_button.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_layout:
                finish();
            case R.id.back_img:
                finish();
            case R.id.forgot_button:
                CheckValidation();
        }
    }

    private void CheckValidation() {
        if (Func.editSize(email_edt) <= 0) {
            email_layout.setError("Please enter email");
            email_edt.requestFocus();
        } else if (!Func.isValidEmail(email_edt.getText())) {
            email_layout.setError("Please enter valid email");
            email_edt.requestFocus();
        } else {
            if (isConnected) {
                String email = StringUtils.getString(email_edt);

                CallForgotPassAPI(email);
            } else {
                StringUtils.showDialog(Constants.NO_INTERNET, ForgotPasswordActivity.this);
            }
        }
    }

    private void CallForgotPassAPI(String email) {
        // http://phphosting.osvin.net/JSKT/API/ForgotPassword.php?email=osvinandroid@gmail.com
        RequestParams params = new RequestParams();
        params.put("email", email);

        Log.e("parameters", params.toString());
        Log.e("URL", Constants.FORGOT_PASS_URL + "?" + params.toString());
        client.post(this, Constants.FORGOT_PASS_URL, params, new JsonHttpResponseHandler() {

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
                        showDialog(response.getString("MessageWhatHappen"), ForgotPasswordActivity.this);

                    } else {
                        showDialog(response.getString("MessageWhatHappen"), ForgotPasswordActivity.this);
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

    public  void showDialog(String msg, Context context) {
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
                    finish();
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