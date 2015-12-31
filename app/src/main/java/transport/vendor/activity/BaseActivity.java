package transport.vendor.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        checkNetwork();

    }

    private void checkNetwork() {

        if (isNetworkAvailable())
            return;

        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        myDialog.setTitle("Network Connection");
        myDialog.setMessage("You are not connected to any network.Press ok to change settings");
        myDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Intent settings = new Intent(Settings.ACTION_WIFI_SETTINGS);
                settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settings);
                dialog.dismiss();

            }
        });
        myDialog.setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        exitFromApp();
                        dialog.dismiss();
                    }
                });
        myDialog.setCancelable(false);
        AlertDialog alertd = myDialog.create();
        alertd.show();
    }
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }

    protected void exitFromApp() {

        finish();

    }

}
