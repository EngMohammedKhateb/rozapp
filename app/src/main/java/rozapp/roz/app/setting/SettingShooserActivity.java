package rozapp.roz.app.setting;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rozapp.roz.app.MainActivity;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.GradientTextView;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.home.HomeActivity;
import rozapp.roz.app.models.AuthResponse;

public class SettingShooserActivity extends AppCompatActivity {


    @BindView(R.id.back)
    ImageView back;

    @BindView(R.id.account)
    GradientTextView account;
    @BindView(R.id.pay_plan)
    GradientTextView pay_plan;
    @BindView(R.id.request_client)
    GradientTextView request_client;
    @BindView(R.id.pay_out)
    GradientTextView pay_out;
    @BindView(R.id.privacy_policy)
    GradientTextView privacy_policy;

    @BindView(R.id.arabic)
    GradientTextView arabic;
    @BindView(R.id.english)
    GradientTextView english;
    @BindView(R.id.light_mode)
    GradientTextView light_mode;
    @BindView(R.id.night_mode)
    GradientTextView night_mode;

    @BindView(R.id.log_out)
    GradientTextView log_out;

    @BindView(R.id.block)
    GradientTextView block;
    @BindView(R.id.rate)
    GradientTextView rate;
    private AuthResponse authResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_shooser);
        ButterKnife.bind(this);

        authResponse=new CallData(this).getAuthResponse();


        if(authResponse.getUser().getType().equals("client")){
            request_client.setVisibility(View.GONE);
            pay_plan.setVisibility(View.GONE);
        }

        if(authResponse.getUser().getType().equals("user")){
           pay_out.setVisibility(View.GONE);
        }

        SharedPreferences pref=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String alanguage=pref.getString("language","en");
        if(alanguage.equals("en")) {
            back.setImageDrawable(getDrawable(R.drawable.arrow_left));
            english.setVisibility(View.GONE);
            arabic.setVisibility(View.VISIBLE);
        }else{
             back.setImageDrawable(getDrawable(R.drawable.arrow_right));
             english.setVisibility(View.VISIBLE);
             arabic.setVisibility(View.GONE);
        }

        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            light_mode.setVisibility(View.VISIBLE);
            night_mode.setVisibility(View.GONE);
        }else{
            light_mode.setVisibility(View.GONE);
            night_mode.setVisibility(View.VISIBLE);
        }

        light_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CallData(SettingShooserActivity.this).changeTheme("light");
                Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        night_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CallData(SettingShooserActivity.this).changeTheme("dark");
                Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        arabic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale locale=new Locale("ar");
                Locale.setDefault(locale);
                Configuration config=new Configuration();
                config.locale=locale;
                getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
                SharedPreferences.Editor editor=getSharedPreferences("settings",MODE_PRIVATE).edit();
                editor.putString("language","ar");
                editor.apply();
                editor.commit();
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Locale locale=new Locale("en");
                Locale.setDefault(locale);
                Configuration config=new Configuration();
                config.locale=locale;
                getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
                SharedPreferences.Editor editor=getSharedPreferences("settings",MODE_PRIVATE).edit();
                editor.putString("language","en");
                editor.apply();
                editor.commit();
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingShooserActivity.this,PrivacyPolicyActivity.class));
            }
        });
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingShooserActivity.this,BlockActivity.class));
            }
        });
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=rozapp.roz.app")));

                }catch (ActivityNotFoundException ex){

                }

            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingShooserActivity.this, SettingActivity.class));
            }
        });

        pay_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingShooserActivity.this, PlansActivity.class));
            }
        });
        request_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingShooserActivity.this, RequestClientActivity.class));
            }
        });

        pay_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingShooserActivity.this, PayoutActivity.class));
            }
        });

    }
    private void LogOut() {
        FirebaseMessaging.getInstance().subscribeToTopic("follow"+authResponse.getUser().getId());
        FirebaseMessaging.getInstance().subscribeToTopic("call"+authResponse.getUser().getId());
        FirebaseMessaging.getInstance().subscribeToTopic("message"+authResponse.getUser().getId());
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).logOut().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DynamicToast.makeSuccess(SettingShooserActivity.this,"good bye").show();
                try{
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                    if(isLoggedIn){
                        LoginManager.getInstance().logOut();
                    }
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                    GoogleSignInClient gsc = GoogleSignIn.getClient(SettingShooserActivity.this,gso);
                    gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {

                        }
                    });
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                new CallData(SettingShooserActivity.this).LogOut();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                DynamicToast.makeSuccess(SettingShooserActivity.this,"good bye").show();
                try{
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                if(isLoggedIn){
                    LoginManager.getInstance().logOut();
                }
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
                GoogleSignInClient gsc = GoogleSignIn.getClient(SettingShooserActivity.this,gso);
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {

                    }
                });
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                new CallData(SettingShooserActivity.this).LogOut();
            }
        });
    }






}