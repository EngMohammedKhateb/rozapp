package rozapp.roz.app.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import rozapp.roz.app.MainActivity;
import rozapp.roz.app.auth.SingleAuthActivity;
import rozapp.roz.app.home.HomeActivity;
import rozapp.roz.app.setting.SettingShooserActivity;


public class Database {
    static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE=0;
    private static final String PRIF_NAME="LOGIN";
    private static final String LOGIN="IS_LOGIN";
    public static final String AUTHRESPONSE="AUTHRESPONSE";
    public static final String THEME="THEME";
    public static final String LANGUAGE="LANGUAGE";



    public Database(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences(PRIF_NAME,PRIVATE_MODE);
        editor=sharedPreferences.edit();
    }
    public void  createSession (String authResponse){
        editor.putString(AUTHRESPONSE,KhateebPattern.toBase64(authResponse));
        editor.putBoolean(LOGIN,true);
        editor.putString(THEME,"light");
        editor.putString(LANGUAGE,"english");
        editor.apply();
    }




    public boolean isLogin(){
        return sharedPreferences.getBoolean(LOGIN,false);
    }


    public String getAuthResponse(){
        return sharedPreferences.getString(AUTHRESPONSE,"empty");
    }

    public String getTheme(){
        return sharedPreferences.getString(THEME,"light");
    }
    public String getLanguage(){
        return sharedPreferences.getString(LANGUAGE,"english");
    }


    public void LogOut(){
        editor.clear();
        editor.commit();
        //  FirebaseMessaging.getInstance().unsubscribeFromTopic("notification");
        //  FirebaseMessaging.getInstance().unsubscribeFromTopic("message");
        Intent i=new Intent(context, SingleAuthActivity.class);
        context.startActivity(i);
        ((SettingShooserActivity)context).finish();

    }
    public void LogOutHome(){
        editor.clear();
        editor.commit();
        //  FirebaseMessaging.getInstance().unsubscribeFromTopic("notification");
        //  FirebaseMessaging.getInstance().unsubscribeFromTopic("message");
        Intent i=new Intent(context, SingleAuthActivity.class);
        context.startActivity(i);
        ((HomeActivity)context).finish();

    }
    public void LogOutSplash(){
        editor.clear();
        editor.commit();
        ((MainActivity)context).finish();
    }
    public static void editAuthResponse(String val){
        editor.putString(AUTHRESPONSE,KhateebPattern.toBase64(val)+"");
        editor.apply();
        editor.commit();
    }
    public static void changeLanguage(String val){
        editor.putString(LANGUAGE, val);
        editor.apply();
        editor.commit();
    }
    public static void changeTheme(String val){
        editor.putString(THEME, val);
        editor.apply();
        editor.commit();
    }
}
