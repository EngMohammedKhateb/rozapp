package rozapp.roz.app.setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ErrorHandler;


public class PrivacyPolicyActivity extends AppCompatActivity {

    @BindView(R.id.pp_privacy)
    ProgressBar pp_privacy;

    @BindView(R.id.scroll)
    ScrollView scroll;

    @BindView(R.id.tv_privacy)
    TextView tv_privacy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_RozApp);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        ButterKnife.bind(this);


        pp_privacy.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.GONE);
        KhateebPattern.getServicesInstance().privacyPolicy().enqueue(new Callback<ErrorHandler>() {
            @Override
            public void onResponse(Call<ErrorHandler> call, Response<ErrorHandler> response) {

                if(response.code()==200){
                    tv_privacy.setText(response.body().getMessage());
                }
                pp_privacy.setVisibility(View.GONE);
                scroll.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ErrorHandler> call, Throwable t) {

                pp_privacy.setVisibility(View.GONE);
                scroll.setVisibility(View.VISIBLE);
            }
        });

    }
}