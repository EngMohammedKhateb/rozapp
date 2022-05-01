package rozapp.roz.app.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.EnglishLanguage;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ErrorHandler;
import rozapp.roz.app.models.Gate;
import rozapp.roz.app.models.PayoutInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayoutActivity extends AppCompatActivity {


    @BindView(R.id.ed_note)
    TextInputEditText ed_note;
    @BindView(R.id.ed_phone)
    TextInputEditText ed_phone;

    @BindView(R.id.arrow_left)
    ImageView arrow_left;

    @BindView(R.id.menu)
    TextInputLayout text_input_layout;
    @BindView(R.id.auto_tv)
    AutoCompleteTextView autoComplete;



    @BindView(R.id.you_have_tv)
    EnglishLanguage you_have_tv;
    @BindView(R.id.coins_tv)
    EnglishLanguage coins_tv;
    @BindView(R.id.token_tv)
    EnglishLanguage token_tv;
    @BindView(R.id.note)
    EnglishLanguage note;
    @BindView(R.id.price)
    EnglishLanguage price;

    @BindView(R.id.btn_pay)
    RelativeLayout btn_pay;

    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.page)
    LinearLayout page;

    @BindView(R.id.scroll)
    ScrollView scroll;

    JSONArray jsonArray;
    private ArrayList<String> arrayList_country;
    private ArrayAdapter<String> arrayAdapter_country;

    private AuthResponse authResponse;

    private String payout_name="empty";


    private List<Gate> gates=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout);
        ButterKnife.bind(this);
        page.setVisibility(View.VISIBLE);
        arrayList_country=new ArrayList<>();
        scroll.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        authResponse=new CallData(this).getAuthResponse();
        SharedPreferences pref=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language=pref.getString("language","en");
        if(language.equals("en")) {
            arrow_left.setImageDrawable(getDrawable(R.drawable.arrow_left));
        }else{
            arrow_left.setImageDrawable(getDrawable(R.drawable.arrow_right));
        }
        arrow_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getPayoutInfo().enqueue(new Callback<PayoutInfo>() {
            @Override
            public void onResponse(Call<PayoutInfo> call, Response<PayoutInfo> response) {
                if(response.code()==200){
                     you_have_tv.setText("you have");
                    coins_tv.setText(" "+authResponse.getUser().getCoins()+" ");
                    token_tv.setText("token");

                    double usd=Double.parseDouble(authResponse.getUser().getCoins())/Double.parseDouble(response.body().getCoinsUsd().toString());

                    price.setText("Worth $"+String.format(Locale.US, "%.2f", usd)+"");
                    note.setText("");
                    for (Gate gate:response.body().getGates()){
                        arrayList_country.add(gate.getName());
                        gates.add(gate);
                    }
                    fullSelect();
                    scroll.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }else{
                    DynamicToast.makeError(PayoutActivity.this,"bad internet connection try again later").show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PayoutInfo> call, Throwable t) {
                DynamicToast.makeError(PayoutActivity.this,"bad internet connection try again later").show();
                finish();
            }
        });

        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });

    }

    private void fullSelect() {
        //fill countries


        arrayAdapter_country=new ArrayAdapter<>(PayoutActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,arrayList_country);
        autoComplete.setAdapter(arrayAdapter_country);
        autoComplete.setThreshold(1);

        autoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Object item = adapterView.getItemAtPosition(i);

                payout_name=item.toString();
                setNote(item.toString());
            }
        });



    }

    private void setNote(String toString) {
        for (Gate gate:gates) {
            if(gate.getName().equals(toString)){
                note.setText(gate.getNote());
                break;
            }
        }
    }

    private void sendData() {

        final ProgressDialog pd = new ProgressDialog(PayoutActivity.this );
        pd.setMessage("sending data");
        pd.setTitle("please wait...");
        pd.show();

        String phone = ed_phone.getText().toString();
        String note  = ed_note.getText().toString();

        if(phone.trim().equals("") || note.trim().equals("")){
            DynamicToast.makeError(PayoutActivity.this,"please fill all info").show();
            return;
        }

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).requestPayout(payout_name,phone,note).enqueue(new Callback<ErrorHandler>() {
            @Override
            public void onResponse(Call<ErrorHandler> call, Response<ErrorHandler> response) {
                if(response.code()==200){

                    if(response.body().getStatus().equals("ok")){
                        DynamicToast.makeSuccess(PayoutActivity.this,response.body().getMessage()).show();
                    }else{
                        DynamicToast.makeError(PayoutActivity.this,response.body().getMessage()).show();
                    }

                }
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ErrorHandler> call, Throwable t) {
                DynamicToast.makeError(PayoutActivity.this,"bad internet connection try again later").show();
                pd.dismiss();
            }
        });


    }
}