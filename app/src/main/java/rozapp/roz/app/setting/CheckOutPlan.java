package rozapp.roz.app.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.PayResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutPlan extends AppCompatActivity {

    @BindView(R.id.btn_pay)
    Button btn_pay;


    @BindView(R.id.exp_year_ed)
    EditText exp_year_ed;

    @BindView(R.id.exp_month_ed)
    EditText exp_month_ed;

    @BindView(R.id.card_number_ed)
    EditText card_number_ed;

    @BindView(R.id.cvc_ed)
    EditText cvc_ed;

    @BindView(R.id.progress_pay)
    ProgressBar progressBar;


    AuthResponse authResponse;
    String token ="";

    private String plan_id;
    private String amount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_plan);
        ButterKnife.bind(this);
        authResponse=new CallData(this).getAuthResponse();

        plan_id=getIntent().getStringExtra("plan_id").toString();
        amount=getIntent().getStringExtra("amount").toString();

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String card_number=card_number_ed.getText().toString();
                        String cvc=cvc_ed.getText().toString();
                        String month=exp_month_ed.getText().toString();
                        String year=exp_year_ed.getText().toString();

                        getToken(card_number,cvc,month,year);

                    }
                });


            }
        });




    }

    private void getToken(String card_number, String cvc, String month, String year) {

        progressBar.setVisibility(View.VISIBLE);
        btn_pay.setVisibility(View.GONE);

        KhateebPattern.getAuthStripServicesInstance(authResponse.getPk()).getStripToken(card_number,month,year,cvc).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                if(response.code()==200){
                    JSONObject job= null;
                    try {
                        job = new JSONObject(response.body().string());
                        token   = job.getString("id");

                        confirmPayment(token);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    DynamicToast.makeError(CheckOutPlan.this,"error invalid credential").show();
                    try {
                        Log.e("error" ,response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);
                    btn_pay.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DynamicToast.makeError(CheckOutPlan.this,"no internet connection").show();
                progressBar.setVisibility(View.GONE);
                btn_pay.setVisibility(View.VISIBLE);
            }
        });
    }

    private void confirmPayment(String stripToken) {

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).payPlan( plan_id,token,amount ).enqueue(new Callback<PayResponse>() {
            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {
                if(response.code()==200){
                    DynamicToast.makeSuccess(CheckOutPlan.this,"your order completed successfully",5000).show();
                }else{
                    DynamicToast.makeError(CheckOutPlan.this,"no internet connection try again later").show();
                }
                progressBar.setVisibility(View.GONE);
                btn_pay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {
                DynamicToast.makeError(CheckOutPlan.this,"no internet connection try again later").show();
                progressBar.setVisibility(View.GONE);
                btn_pay.setVisibility(View.VISIBLE);
            }
        });


    }

}