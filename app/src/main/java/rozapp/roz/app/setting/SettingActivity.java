package rozapp.roz.app.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.home.HomeActivity;
import rozapp.roz.app.models.AuthResponse;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.dark_switch)
    Switch switchDark;

    @BindView(R.id.chat_switch)
    Switch chat_switch;

    @BindView(R.id.video_switch)
    Switch video_switch;

    @BindView(R.id.container_coins)
    LinearLayout container_coins;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.arabic)
    RadioButton arabic;
    @BindView(R.id.english)
    RadioButton english;

    @BindView(R.id.min_max_tv)
    TextView min_max_tv;

    @BindView(R.id.btn_password)
    Button btn_password;

    @BindView(R.id.btn_coins)
    Button btn_coins;

    @BindView(R.id.ed_password)
    EditText ed_password;

    @BindView(R.id.ed_coins)
    EditText ed_coins;
    @BindView(R.id.ed_name)
    EditText ed_name;
    @BindView(R.id.btn_name)
    Button btn_name;
    @BindView(R.id.your_id_tv)
    TextView your_id_tv;

    ProgressDialog progressPassword;
    ProgressDialog progressCoins;
    ProgressDialog progressChat;
    ProgressDialog progressVideo;
    private AuthResponse authResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        authResponse=new CallData(this).getAuthResponse();
        your_id_tv.setText("id : "+authResponse.getUser().getId()+"");

        SharedPreferences pref=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language=pref.getString("language","en");
        if(language.equals("en")){
            min_max_tv.setText("tokens per minute must be between "+authResponse.getMin()+" to "+authResponse.getMax());

            switchDark.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            switchDark.setTextDirection(View.TEXT_DIRECTION_RTL);

            chat_switch.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            chat_switch.setTextDirection(View.TEXT_DIRECTION_RTL);

            video_switch.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            video_switch.setTextDirection(View.TEXT_DIRECTION_RTL);

        }else{
            min_max_tv.setText("يجب أن تكون القيمة بين "+authResponse.getMin()+" و "+authResponse.getMax());

            switchDark.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            switchDark.setTextDirection(View.TEXT_DIRECTION_LTR);

            chat_switch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            chat_switch.setTextDirection(View.TEXT_DIRECTION_LTR);

            video_switch.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            video_switch.setTextDirection(View.TEXT_DIRECTION_LTR);

        }




        themeManager();
        languageManager();
        namedManager();
        passwordManager();
        coinsManager();
        chatManager();
        videoManager();
    }

    private void namedManager() {

        btn_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=ed_name.getText().toString();
                if(name.trim().equals("")){
                    DynamicToast.makeError(SettingActivity.this,"you cant create empty name").show();
                    return;
                }

                progressVideo= new ProgressDialog(SettingActivity.this );
                progressVideo.setMessage("change name");
                progressVideo.setTitle("please wait...");
                progressVideo.show();
                KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).changeUserName(name).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code() == 200 ){
                            DynamicToast.makeSuccess(SettingActivity.this,"your name changed Successfully").show();
                        }
                        progressVideo.dismiss();
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        DynamicToast.makeError(SettingActivity.this,"Bad Internet Connection").show();
                        progressVideo.dismiss();
                    }
                });
            }
        });

    }

    private void videoManager() {

        if(authResponse.getUser().getDisableVideo()==0){
            video_switch.setChecked(false);
        }else{
            video_switch.setChecked(true);
        }
        video_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    // disable chat

                    progressVideo= new ProgressDialog(SettingActivity.this );
                    progressVideo.setMessage("disable video");
                    progressVideo.setTitle("please wait...");
                    progressVideo.show();
                    KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).toggleVideo(1).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.code()==200){


                                 DynamicToast.makeSuccess(SettingActivity.this,"video call disabled successfully").show();

                            }
                            progressVideo.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            DynamicToast.makeSuccess(SettingActivity.this,"no internet connection").show();
                            progressVideo.dismiss();
                        }
                    });

                }else{
                    // enable chat
                    progressVideo= new ProgressDialog(SettingActivity.this );
                    progressVideo.setMessage("enable video");
                    progressVideo.setTitle("please wait...");
                    progressVideo.show();
                    KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).toggleVideo(0).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.code()==200){
                                 DynamicToast.makeSuccess(SettingActivity.this,"video call enabled successfully").show();
                            }
                            progressVideo.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            DynamicToast.makeSuccess(SettingActivity.this,"no internet connection").show();
                            progressVideo.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void chatManager() {

        if(authResponse.getUser().getDisableChat()==0){
            chat_switch.setChecked(false);
        }else{
            chat_switch.setChecked(true);
        }

        chat_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    // disable chat

                    progressChat= new ProgressDialog(SettingActivity.this );
                    progressChat.setMessage("disable chat");
                    progressChat.setTitle("please wait...");
                    progressChat.show();
                    KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).toggleChat(1).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.code()==200){
                                 DynamicToast.makeSuccess(SettingActivity.this,"chat disabled successfully").show();

                            }
                            progressChat.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            DynamicToast.makeSuccess(SettingActivity.this,"no internet connection").show();
                            progressChat.dismiss();
                        }
                    });

                }else{
                    // enable chat
                    progressChat= new ProgressDialog(SettingActivity.this );
                    progressChat.setMessage("enable chat");
                    progressChat.setTitle("please wait...");
                    progressChat.show();
                    KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).toggleChat(0).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.code()==200){
                                   DynamicToast.makeSuccess(SettingActivity.this,"chat enabled successfully").show();
                            }
                            progressChat.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            DynamicToast.makeSuccess(SettingActivity.this,"no internet connection").show();
                            progressChat.dismiss();
                        }
                    });
                }
            }
        });


    }


    private void themeManager(){
        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            switchDark.setChecked(true);
        }

        switchDark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    new CallData(SettingActivity.this).changeTheme("dark");
                    rest();
                }else{
                  //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    new CallData(SettingActivity.this).changeTheme("light");
                    rest();
                }
            }
        });
    }
    private void languageManager(){

        SharedPreferences pref=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language=pref.getString("language","en");

        if(language.equals("en")){
            english.setChecked(true);
            arabic.setChecked(false);
        }else{
            english.setChecked(false);
            arabic.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i==R.id.arabic){

                    // setCurrentLanguage
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
                }else{

                    // setCurrentLanguage
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

            }
        });
    }
    private void rest(){
        Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void coinsManager() {

        if(authResponse.getUser().getType().equals("user")){
            container_coins.setVisibility(View.GONE);
        }else{
            container_coins.setVisibility(View.VISIBLE);
        }

        ed_coins.setText(authResponse.getUser().getCoinsPerMinute()+"");

        btn_coins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int coins =Integer.parseInt(ed_coins.getText().toString());

                if(coins< Integer.parseInt(authResponse.getMin())){
                    DynamicToast.makeError(SettingActivity.this,"tokens most be grater than "+authResponse.getMin()).show();
                    return;
                }

                if(coins > Integer.parseInt(authResponse.getMax())){
                    DynamicToast.makeError(SettingActivity.this,"tokens most be less than "+authResponse.getMax()).show();
                    return;
                }

                progressCoins= new ProgressDialog(SettingActivity.this );
                progressCoins.setMessage("change tokens per minute");
                progressCoins.setTitle("please wait...");
                progressCoins.show();
                KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).changeCoinsPerMinute(coins).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code()==200){
                            DynamicToast.makeSuccess(SettingActivity.this,"tokens per minute changed successfully").show();

                            authResponse.getUser().setCoinsPerMinute(coins);


                        }else{
                            DynamicToast.makeError(SettingActivity.this,"something went wrong").show();
                        }

                        progressCoins.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        DynamicToast.makeError(SettingActivity.this,"no internet connection").show();
                        progressCoins.dismiss();
                    }
                });

            }
        });

    }

    private void passwordManager() {

        btn_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String password=ed_password.getText().toString();
                if(password.trim().equals("")){
                    DynamicToast.makeError(SettingActivity.this,"you cant create empty password").show();
                    return;
                }
                if(password.length() < 8 ){
                    DynamicToast.makeError(SettingActivity.this,"password must be from 8 char at less").show();
                    return;
                }
                progressPassword= new ProgressDialog(SettingActivity.this );
                progressPassword.setMessage("change password");
                progressPassword.setTitle("please wait...");
                progressPassword.show();
                KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).changePassword(password).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code()==200){
                            DynamicToast.makeSuccess(SettingActivity.this,"password changed successfully").show();
                        }else{
                            DynamicToast.makeError(SettingActivity.this,"something went wrong").show();
                        }

                        progressPassword.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        DynamicToast.makeError(SettingActivity.this,"no internet connection").show();
                        progressPassword.dismiss();
                    }
                });


            }
        });


    }
}