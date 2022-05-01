package rozapp.roz.app.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Database;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.home.HomeActivity;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ErrorResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.go_create_tv)
    TextView go_create_tv;
    @BindView(R.id.tv_login)
    TextView tv_login;
    @BindView(R.id.progress_login)
    ProgressBar progress_login;
    @BindView(R.id.email_ed)
    EditText email_ed;
    @BindView(R.id.password_ed)
    EditText password_ed;
    @BindView(R.id.btn_login)
    RelativeLayout btn_login;
    Database db;
    boolean loading=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        db = new Database(this);
        if (db.isLogin()) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
        go_create_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password=password_ed.getText().toString();
                String email=email_ed.getText().toString();
                if(password.trim().equals("") || email.trim().equals("")){
                    DynamicToast.makeError(LoginActivity.this,"please fill all field").show();
                    return;
                }
                if(loading){return;}
                tv_login.setVisibility(View.GONE);
                progress_login.setVisibility(View.VISIBLE);
                loading=true;
                KhateebPattern.getServicesInstance().Login(email,password).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        Gson gson =new Gson();

                        if(response.code()==200){
                            String store=gson.toJson(response.body()).toString();
                            db.createSession(  store );
                            DynamicToast.makeSuccess(LoginActivity.this,"welcome "+response.body().getUser().getName()).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }else{
                            try {
                                JSONObject jsonObject=new JSONObject(response.errorBody().string());
                                ErrorResponse error_response=gson.fromJson(String.valueOf(jsonObject),ErrorResponse.class);
                                DynamicToast.makeError(LoginActivity.this, error_response.getMessage()+"").show();
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                        tv_login.setVisibility(View.VISIBLE);
                        progress_login.setVisibility(View.GONE);
                        loading=false;
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        DynamicToast.makeError(LoginActivity.this,"bad internet connection").show();
                        tv_login.setVisibility(View.VISIBLE);
                        progress_login.setVisibility(View.GONE);
                        loading=false;
                    }
                });
            }
        });

    }
}