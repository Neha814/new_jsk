package transport.vendor.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;


public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    protected boolean _active = true;
    private Thread splashTread;
    SharedPreferences sp;
    /**
     * The _splash time.
     */
    protected int _splashTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        Thread t = new Thread(){
            public void run(){
                try {
                    sleep(3 * 1000);
                    boolean inHome = sp.getBoolean("inHome",false);
                    if(inHome) {
                        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        };t.start();
    }



}


