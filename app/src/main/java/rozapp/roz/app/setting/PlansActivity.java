package rozapp.roz.app.setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.PlanAdabter;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.Plan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlansActivity extends AppCompatActivity {

    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.rv_plans)
    RecyclerView rv_plans;

    private List<Plan> plans;
    private PlanAdabter planAdabter;

    AuthResponse authResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);
        ButterKnife.bind(this);
        authResponse=new CallData(this).getAuthResponse();

        plans=new ArrayList<>();
        planAdabter=new PlanAdabter(this,plans);
        rv_plans.setLayoutManager(new GridLayoutManager(PlansActivity.this,2));
        rv_plans.setAdapter(planAdabter);

        progress.setVisibility(View.VISIBLE);
        getAllPlans();
    }

    private void getAllPlans() {

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getAllPlans().enqueue(new Callback<List<Plan>>() {
            @Override
            public void onResponse(Call<List<Plan>> call, Response<List<Plan>> response) {
                if(response.code()==200){
                    for(Plan plan:response.body()){
                        plans.add(plan);
                    }
                }else{
                    DynamicToast.makeError(PlansActivity.this,"something went wrong");
                }
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Plan>> call, Throwable t) {
                DynamicToast.makeError(PlansActivity.this,"no internet connection");
                progress.setVisibility(View.GONE);
            }
        });

    }
}