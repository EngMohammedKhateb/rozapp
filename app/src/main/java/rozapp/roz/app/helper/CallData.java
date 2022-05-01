package rozapp.roz.app.helper;

import android.content.Context;

import com.google.gson.Gson;

import rozapp.roz.app.models.AuthResponse;

public class CallData {

    private Database db;
    private String user_info;
    Gson gson;

    public CallData(Context context){
        db=new Database(context);
        gson = new Gson();
        user_info=db.getAuthResponse();
    }
    public AuthResponse getAuthResponse()  {
        String decoded_value=KhateebPattern.fromBase64(this.user_info);
        AuthResponse res;
        res=gson.fromJson( decoded_value,AuthResponse.class);
        return  res;
    }
    public String stored(){
        String decoded_value=KhateebPattern.fromBase64(this.user_info);
        return decoded_value;
    }

    public void updateAuthResponse(String auth){
        db.editAuthResponse(auth);
    }

    public String getCurrentTheme(){
        return db.getTheme();
    }

    public String getCurrentLanguage(){
        return db.getLanguage();
    }

    public void changeTheme(String theme){
         Database.changeTheme(theme);
    }

    public void changeLanguage(String language){
        Database.changeLanguage(language);
    }
    public void LogOut(){
        db.LogOut();
    }
  public void LogOutHome(){
        db.LogOutHome();
    }

    public void LogOutSplash(){
        db.LogOutSplash();
    }

    public boolean isLogin(){
        return db.isLogin();
    }


}
