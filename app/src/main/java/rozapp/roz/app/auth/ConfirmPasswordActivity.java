package rozapp.roz.app.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ErrorHandler;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPasswordActivity extends AppCompatActivity {


    @BindView(R.id.tv_login)
    TextView tv_login;
    @BindView(R.id.resend)
    TextView resent;
    @BindView(R.id.progress_login)
    ProgressBar progress_login;
    @BindView(R.id.btn_login)
    RelativeLayout btn_login;

    @BindView(R.id.first_ed)
    EditText first_ed;
    @BindView(R.id.sec_ed)
    EditText sec_ed;
    @BindView(R.id.third_ed)
    EditText third_ed;
    @BindView(R.id.four_ed)
    EditText four_ed;

    AuthResponse authResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);
        ButterKnife.bind(this);
        authResponse=new CallData(this).getAuthResponse();


        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).sendOtp(authResponse.getUser().getEmail()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String f=first_ed.getText().toString();
                String s=sec_ed.getText().toString();
                String t=third_ed.getText().toString();
                String fo=four_ed.getText().toString();
                if(f.trim().equals("") || s.trim().equals("") || t.trim().equals("") || fo.trim().equals(""))
                    return;
                    tv_login.setVisibility(View.GONE);
                    progress_login.setVisibility(View.VISIBLE);
                    String digits=f+s+t+fo;
                    Log.e("digits :", digits);
                    KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).activeAccount(authResponse.getUser().getEmail(),digits ).enqueue(new Callback<ErrorHandler>() {
                        @Override
                        public void onResponse(Call<ErrorHandler> call, Response<ErrorHandler> response) {
                            if(response.isSuccessful()){
                                if(response.body().getStatus().equals("ok")){
                                    DynamicToast.makeSuccess(ConfirmPasswordActivity.this,response.body().getMessage()).show();


                                }else{
                                    DynamicToast.makeError(ConfirmPasswordActivity.this,response.body().getMessage()).show();

                                }

                            }
                            Log.e("response code",response.code()+"");
                            tv_login.setVisibility(View.VISIBLE);
                            progress_login.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<ErrorHandler> call, Throwable t) {
                            Log.e("response code", "request faild");
                            tv_login.setVisibility(View.VISIBLE);
                            progress_login.setVisibility(View.GONE);
                        }
                    });

                }


        });

    }
}