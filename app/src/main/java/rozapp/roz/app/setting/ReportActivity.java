package rozapp.roz.app.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ErrorHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity {


    @BindView(R.id.ed_note)
    TextInputEditText ed_note;

    @BindView(R.id.arrow_back)
    ImageView arrow_back;

    @BindView(R.id.btn_pay)
    RelativeLayout btn_pay;



    private AuthResponse authResponse;

    String target_id;
    String desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        authResponse=new CallData(this).getAuthResponse();
        target_id=getIntent().getStringExtra("target_id");

        SharedPreferences pref=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language=pref.getString("language","en");
        if(language.equals("en")) {
            arrow_back.setImageDrawable(getDrawable(R.drawable.arrow_left));
        }else{
            arrow_back.setImageDrawable(getDrawable(R.drawable.arrow_right));
        }
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desc=ed_note.getText().toString();

                final ProgressDialog pd = new ProgressDialog(ReportActivity.this );
                pd.setMessage("sending report");
                pd.setTitle("please wait...");
                pd.show();
                KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).report(target_id,desc).enqueue(new Callback<ErrorHandler>() {
                    @Override
                    public void onResponse(Call<ErrorHandler> call, Response<ErrorHandler> response) {

                        if(response.code()==200){
                            DynamicToast.makeSuccess(ReportActivity.this,"report sent successfully").show();
                        }else{
                            DynamicToast.makeError(ReportActivity.this,"something went wrong").show();

                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ErrorHandler> call, Throwable t) {
                        DynamicToast.makeError(ReportActivity.this,"bad internet connection try again later").show();
                        pd.dismiss();
                    }
                });
            }
        });

    }
}