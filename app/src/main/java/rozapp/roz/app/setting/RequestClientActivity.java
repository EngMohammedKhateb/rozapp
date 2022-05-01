package rozapp.roz.app.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestClientActivity extends AppCompatActivity {

    @BindView(R.id.pick_date_button)
    Button pick_date_button;

    @BindView(R.id.btn_login)
    RelativeLayout btn_login;

    @BindView(R.id.progress_login)
    ProgressBar progress_login;
    @BindView(R.id.tv_login)
    TextView tv_login;

    @BindView(R.id.name_ed)
    TextInputEditText name_ed;
    @BindView(R.id.phone_ed)
    TextInputEditText phone_ed;


    String birthday="";
    AuthResponse authResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_client);
        ButterKnife.bind(this);
        authResponse=new CallData(this).getAuthResponse();
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("select your birthday");
        MaterialDatePicker<Long> picker = builder.build();

        pick_date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                picker.show(getSupportFragmentManager(), picker.toString());
            }
        });

        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override public void onPositiveButtonClick(Long selection) {
                TimeZone timeZoneUTC = TimeZone.getDefault();
                // It will be negative, so that's the -1
                int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;

                // Create a date format, then a date object with our offset
                SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date date = new Date(selection + offsetFromUTC);

                pick_date_button.setText(simpleFormat.format(date));
                birthday=simpleFormat.format(date);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestClient();
            }
        });

    }

    private void requestClient() {

        String phone=phone_ed.getText().toString();
        String name=name_ed.getText().toString();

        if(birthday.equals("")){
            DynamicToast.makeError(RequestClientActivity.this,"please select your birthday").show();
            return;
        }
        if(name.trim().equals("")){
            DynamicToast.makeError(RequestClientActivity.this,"please type your name").show();
            return;
        }
        if(phone.trim().equals("")){
            DynamicToast.makeError(RequestClientActivity.this,"please type your phone").show();
            return;
        }
        progress_login.setVisibility(View.VISIBLE);
        tv_login.setVisibility(View.GONE);

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).requestClient(name,phone,birthday).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){

                    try {
                        JSONObject job =new JSONObject(response.body().string());

                        String status=job.getString("status");
                        String message=job.getString("message");
                        if(status.equals("ok")){
                            DynamicToast.makeSuccess(RequestClientActivity.this,message ).show();
                        }else{
                            DynamicToast.makeError(RequestClientActivity.this,message ).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    DynamicToast.makeError(RequestClientActivity.this,"something went wrong").show();
                }
                progress_login.setVisibility(View.GONE);
                tv_login.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DynamicToast.makeError(RequestClientActivity.this,"bad internet connection").show();
                progress_login.setVisibility(View.GONE);
                tv_login.setVisibility(View.VISIBLE);
            }
        });

    }
}