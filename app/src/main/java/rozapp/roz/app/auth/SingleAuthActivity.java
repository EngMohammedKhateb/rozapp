package rozapp.roz.app.auth;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Database;
import rozapp.roz.app.helper.EnglishLanguage;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.home.HomeActivity;
import rozapp.roz.app.models.AuthResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rozapp.roz.app.models.ErrorHandler;
import rozapp.roz.app.setting.PrivacyPolicyActivity;

public class SingleAuthActivity extends AppCompatActivity  {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @BindView(R.id.btn_google)
    LinearLayout btn_google;
    @BindView(R.id.btn_facebook)
    LinearLayout btn_facebook;
    @BindView(R.id.guest_tv)
    EnglishLanguage guest_tv;

    @BindView(R.id.tv_privacy)
    EnglishLanguage tv_privacy;




    private static final int RC_SIGN_IN = 1;
    private static final String EMAIL = "email";
    CallbackManager callbackManager;
    private String user_name;
    private String user_email;
    Database db;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_RozApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_auth);
        ButterKnife.bind(this);
        db = new Database(this);
        dialog=new Dialog(this);

        if(db.isLogin()){
            startActivity(new Intent(SingleAuthActivity.this,HomeActivity.class));
            finish();
            return;
        }



        tv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  startActivity(new Intent(SingleAuthActivity.this, PrivacyPolicyActivity.class));
            }
        });



        // guest login
        guest_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SingleAuthActivity.this,LoginActivity.class));

            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        try{
            gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                }
            });
        }catch (Exception ex){
            ex.printStackTrace();
        }

         // GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        //if(acct!=null){ String personName = acct.getDisplayName(); }

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();
            }
        });

        //facebook
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        try{
            if(isLoggedIn){
                LoginManager.getInstance().logOut();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logInWithReadPermissions(SingleAuthActivity.this, Arrays.asList("public_profile"));
            }
        });




//        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                    getUserInfo();
                    //     DynamicToast.makeError(SingleAuthActivity.this,"successed" ,3000).show();
                    }

                    @Override
                    public void onCancel() {
                    //    DynamicToast.makeError(SingleAuthActivity.this,"canceled" ,3000).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    //  DynamicToast.makeError(SingleAuthActivity.this,exception.getMessage(),3000).show();
                    }
        });
    }

    private void getUserInfo() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        String name= null;
                        String id=null;
                        try {
                            name = object.getString("name").toString();
                            id =object.getString("id").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        user_name=name;
                        user_email="facebook"+id+""+"@faceb.com";
                        callOurApi();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        request.setParameters(parameters);
        request.executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



    void signIn(){

        Intent intent =gsc.getSignInIntent();
        setResult(1000, intent);
        activityResultLaunch.launch(intent);


    }




    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {


                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());

                        try {
                            task.getResult(ApiException.class);
                            Log.e("name",task.getResult().getDisplayName());
                            Log.e("id",task.getResult().getId());
                            user_name=task.getResult().getDisplayName();
                            user_email="gmail"+task.getResult().getId()+""+"@gmail.com";
                            callOurApi();
                        } catch (ApiException e) {
                            e.printStackTrace();
                            DynamicToast.makeError(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                }
            });




    private void callOurApi(){

        KhateebPattern.getServicesInstance().checkAccount(user_email).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                 if(response.code()==200){
                    Gson gson =new Gson();
                    String store=gson.toJson(response.body()).toString();
                    db.createSession(  store );
                    DynamicToast.makeSuccess(SingleAuthActivity.this,"welcome "+response.body().getUser().getName()).show();
                    startActivity(new Intent(SingleAuthActivity.this, HomeActivity.class));
                    finish();
                }
                else if(response.code()==201){
                    // complete register
                    Intent intent=new Intent(SingleAuthActivity.this,CompleteRegisterActivity.class);
                    intent.putExtra("name",user_name);
                    intent.putExtra("email",user_email);
                    startActivity(intent);
                    finish();
                }
                else if(response.code()==202){
                    // blocked account
                    DynamicToast.makeError(SingleAuthActivity.this,"your account was blocked").show();
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                    if(isLoggedIn){
                        LoginManager.getInstance().logOut();
                    }
                    gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {

                        }
                    });
                }else{
                     DynamicToast.makeError(SingleAuthActivity.this,"oops something went wrong").show();

                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                    if(isLoggedIn){
                        LoginManager.getInstance().logOut();
                    }
                    gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {

                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                DynamicToast.makeError(SingleAuthActivity.this,"no internet connection").show();
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

                if(isLoggedIn){
                    LoginManager.getInstance().logOut();
                }
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {

                    }
                });
            }
        });


    }

}