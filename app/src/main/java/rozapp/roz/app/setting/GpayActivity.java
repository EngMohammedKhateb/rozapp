package rozapp.roz.app.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.googlepaylauncher.GooglePayEnvironment;
import com.stripe.android.googlepaylauncher.GooglePayLauncher;
import com.stripe.android.googlepaylauncher.GooglePayPaymentMethodLauncher;
import com.stripe.android.paymentsheet.PaymentSheet;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.PayResponse;

public class GpayActivity extends AppCompatActivity {


    @BindView(R.id.progress_pay)
    ProgressBar progressBar;
    @BindView(R.id.payment_id_text)
    TextView payment_id_text;

    @BindView(R.id.btn_retry)
    Button btn_retry;

    @BindView(R.id.btn_pay)
    Button btn_pay;


    AuthResponse authResponse;
    String token ="";

    private String plan_id;
    private String amount;

    private String publish_key="pk_live_51JldkICytlgHaCaRWX35dAlEU1T1cdZuBkKEPx2FdIWX45padWOfiPqVRWaoT1koZxdiZKeTwZ5FqZqe2M7gud0B00qPjCvFai";
    private String secrt_key="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";
    private String customer_id;
    private String ephemeral_key;
    private String client_secret;

    PaymentSheet paymentSheet;
    String payment_id;
     GooglePayPaymentMethodLauncher googlePayLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpay);
        ButterKnife.bind(this);
        payment_id_text.setVisibility(View.GONE);
        btn_retry.setVisibility(View.GONE);
        authResponse=new CallData(this).getAuthResponse();

        plan_id=getIntent().getStringExtra("plan_id").toString();
        amount=getIntent().getStringExtra("amount").toString();


        PaymentConfiguration.init(this,publish_key);

                googlePayLauncher = new GooglePayPaymentMethodLauncher(
                this,
                new GooglePayPaymentMethodLauncher.Config(GooglePayEnvironment.Production, "EN", "Widget Store"),
                new GooglePayPaymentMethodLauncher.ReadyCallback() {
                    @Override
                    public void onReady(boolean b) {
                        btn_pay.setEnabled(true);
                        btn_pay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(client_secret != null){

                                    try{
                                        googlePayLauncher.present("usd",Integer.parseInt(amount+"00"));
                                    }catch (Exception ex){
                                        DynamicToast.makeError(GpayActivity.this,"Your Device not Support Google Pay",7).show();
                                    }
                                }else{
                                    DynamicToast.makeWarning(GpayActivity.this,"please wait....").show();
                                }
                            }
                        });
                    }
                },
                new GooglePayPaymentMethodLauncher.ResultCallback() {
                    @Override
                    public void onResult(@NonNull GooglePayPaymentMethodLauncher.Result result) {
                    }
                }

        );




        getCustomerId();



        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_retry.setVisibility(View.GONE);
                confirmPayment(payment_id);
            }
        });



    }




    private void onGooglePayResult(@NotNull GooglePayLauncher.Result result) {
        if (result instanceof GooglePayLauncher.Result.Completed) {
            DynamicToast.makeError(GpayActivity.this,"payment completed",7).show();
            confirmPayment(payment_id);
        } else if (result instanceof GooglePayLauncher.Result.Canceled) {
            DynamicToast.makeError(GpayActivity.this,"Google Pay canceled",7).show();
        } else if (result instanceof GooglePayLauncher.Result.Failed) {
            // Operation failed; inspect `result.getError()` for more details
            DynamicToast.makeError(GpayActivity.this,((GooglePayLauncher.Result.Failed) result).getError().getLocalizedMessage().toString(),7).show();
        }
    }


    private void getCustomerId(){
        KhateebPattern.getAuthStripServicesInstance(secrt_key).getCustomerId().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    try {
                        String string_response=response.body().string();
                        JSONObject json=new JSONObject(string_response);
                        customer_id=json.get("id").toString();
                        getEphemeralKey(customer_id);


                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    DynamicToast.makeError(GpayActivity.this,"Bad Internet Connection... try again later").show();
                    progressBar.setVisibility(View.GONE);
                    btn_retry.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DynamicToast.makeError(GpayActivity.this,t.getLocalizedMessage()).show();
            }
        });
    }

    private void getEphemeralKey(String customer_id){

        KhateebPattern.getAuthVersionStripServicesInstance(secrt_key).getEphemeralKey(customer_id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    try {
                        String string_response=response.body().string();
                        JSONObject json=new JSONObject(string_response);
                        ephemeral_key=json.get("id").toString();
                        getClientSecret(customer_id,ephemeral_key);


                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    DynamicToast.makeError(GpayActivity.this,"Bad Internet Connection... try again later",7).show();
                    progressBar.setVisibility(View.GONE);
                    btn_retry.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DynamicToast.makeError(GpayActivity.this,t.getLocalizedMessage()).show();
                progressBar.setVisibility(View.GONE);
                btn_retry.setVisibility(View.GONE);
            }
        });

    }


    private void getClientSecret(String customer_id,String ephemeral_key) {

        KhateebPattern.getAuthVersionStripServicesInstance(secrt_key).getClientSecret(customer_id,amount+"00","usd","true").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200){
                    try {
                        String string_response=response.body().string();
                        JSONObject json=new JSONObject(string_response);
                        client_secret=json.get("client_secret").toString();
                        payment_id=json.get("id").toString();


                        progressBar.setVisibility(View.GONE);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    DynamicToast.makeError(GpayActivity.this,"Bad Internet Connection... try again later",7).show();
                    progressBar.setVisibility(View.GONE);
                    btn_retry.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                DynamicToast.makeError(GpayActivity.this,t.getLocalizedMessage()).show();
            }
        });

    }


    private void confirmPayment(String stripToken) {
        progressBar.setVisibility(View.VISIBLE);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).payPlan( plan_id,stripToken,amount ).enqueue(new Callback<PayResponse>() {
            @Override
            public void onResponse(Call<PayResponse> call, Response<PayResponse> response) {
                if(response.code()==200){
                    DynamicToast.makeSuccess(GpayActivity.this,"your order completed successfully",5000).show();
                    progressBar.setVisibility(View.GONE);
                    btn_retry.setVisibility(View.GONE);
                }else{
                    DynamicToast.makeError(GpayActivity.this,"no internet connection try again later").show();

                    progressBar.setVisibility(View.GONE);
                    btn_retry.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {
                DynamicToast.makeError(GpayActivity.this,"Bad internet Connection please try again", 7).show();
                progressBar.setVisibility(View.GONE);
                btn_retry.setVisibility(View.VISIBLE);

            }
        });


    }
}