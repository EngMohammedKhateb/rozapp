package rozapp.roz.app.setting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

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

public class CheckOutPlan extends AppCompatActivity {



    @BindView(R.id.progress_pay)
    ProgressBar progressBar;


    AuthResponse authResponse;
    String token ="";

    private String plan_id;
    private String amount;

    private String publish_key="pk_test_51JldkICytlgHaCaRvRyFJKGFKPCDATpQbaZ34iOWijTK6K631ftGxpp43qeDDrSPdWa1RZ3yCNSlNlVXP5rxQ65W00jZ7cfoae";
    private String secrt_key="sk_test_51JldkICytlgHaCaRrDDxVJWK3vgr8l6VTFyWoddaeWHQ5C3r5nQSVaRqCFR6CmDt3iYCDiK93JP3hpM4U6EeRtF000V3vpVPZi";
    private String customer_id;
    private String ephemeral_key;
    private String client_secret;

    PaymentSheet paymentSheet;

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

        PaymentConfiguration.init(this,publish_key);
        paymentSheet=new PaymentSheet(this,paymentSheetResult -> {
            onPaymentResult(paymentSheetResult);
        });

        getCustomerId();





       //confirmPayment(token);


    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult){
        if(paymentSheetResult instanceof PaymentSheetResult.Canceled){
            DynamicToast.makeError(CheckOutPlan.this,"payment canceled").show();
        }
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            DynamicToast.makeSuccess(CheckOutPlan.this,"payment completed").show();
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

                        KhateebPattern.showToast(CheckOutPlan.this,"customer_id:"+customer_id);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        String string_response=response.body().string();
                        Log.e("response error",string_response);
                        DynamicToast.makeError(CheckOutPlan.this,"status_code"+response.code()+string_response).show();
                    }catch (Exception ex){

                    }


                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DynamicToast.makeError(CheckOutPlan.this,t.getLocalizedMessage()).show();
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

                        KhateebPattern.showToast(CheckOutPlan.this,"ephemeral_key:"+ephemeral_key);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    DynamicToast.makeError(CheckOutPlan.this,"status_code"+response.code()).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DynamicToast.makeError(CheckOutPlan.this,t.getLocalizedMessage()).show();
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
                        KhateebPattern.showToast(CheckOutPlan.this,"client_secret:"+client_secret);
                        paymentFlow();
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    DynamicToast.makeError(CheckOutPlan.this,"status_code"+response.code()).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DynamicToast.makeError(CheckOutPlan.this,t.getLocalizedMessage()).show();
            }
        });

    }

    private void paymentFlow(){
        paymentSheet.presentWithPaymentIntent(
                client_secret,
                new PaymentSheet.Configuration("Roz App",new PaymentSheet.CustomerConfiguration(customer_id,ephemeral_key))

        );
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

            }

            @Override
            public void onFailure(Call<PayResponse> call, Throwable t) {
                DynamicToast.makeError(CheckOutPlan.this,"no internet connection try again later").show();
                progressBar.setVisibility(View.GONE);

            }
        });


    }

}