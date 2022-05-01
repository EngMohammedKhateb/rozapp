package rozapp.roz.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.FirebaseApp;

import java.util.Locale;

import rozapp.roz.app.Fcm.NotificationTestActivity;
import rozapp.roz.app.auth.FirstWelcomActvity;
import rozapp.roz.app.helper.Database;
import rozapp.roz.app.home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db= new Database(this);
        FirebaseApp.initializeApp(MainActivity.this);


        // setCurrentLanguage


        SharedPreferences pref=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language=pref.getString("language","en");

        if(language.equals("en")){
            Locale locale=new Locale("en");
            Locale.setDefault(locale);
            Configuration config=new Configuration();
            config.locale=locale;
            getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        }else{
            Locale locale=new Locale("ar");
            Locale.setDefault(locale);
            Configuration config=new Configuration();
            config.locale=locale;
            getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());

        }



//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "mohammed.khateb.rozapp",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        }
//        catch (PackageManager.NameNotFoundException e) {
//        }
//        catch (NoSuchAlgorithmException e) {
//        }
        new Handler().postDelayed(() -> {

            if(db.isLogin()){
                startActivity(new Intent( MainActivity.this,HomeActivity.class));
                finish();
                return;
            }else{
                startActivity(new Intent(MainActivity.this, FirstWelcomActvity.class));
                finish();
            }

        }, 3000);
////
//        if(db.isLogin()){
//            AuthResponse authResponse=new CallData(MainActivity.this).getAuthResponse();
//            if(authResponse.getUser().getToken().equals("verified")){
//                new Handler().postDelayed(() -> {
//                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
//                    finish();
//                }, 3000);
//
//            }else{
//
//                new Handler().postDelayed(() -> {
//                    startActivity(new Intent(MainActivity.this, ConfirmPasswordActivity.class));
//                    finish();
//                }, 3000);
//
//            }
//
//        }else{
//            new Handler().postDelayed(() -> {
//                startActivity(new Intent(MainActivity.this, FirstWelcomActvity.class));
//                finish();
//            }, 3000);
//        }


    }



}