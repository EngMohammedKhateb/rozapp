package rozapp.roz.app.setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.BlockAdabter;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.DashboardUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlockActivity extends AppCompatActivity {

    @BindView(R.id.arrow_back)
    ImageView arrow_back;

    @BindView(R.id.rv)
    RecyclerView rv;

    @BindView(R.id.pp)
    ProgressBar pp;

    private AuthResponse authResponse;

    private List<DashboardUser> users;
    private BlockAdabter adabter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        ButterKnife.bind(this);
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

        authResponse=new CallData(this).getAuthResponse();

        users=new ArrayList<>();
        adabter=new BlockAdabter(this,users);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adabter);
        pp.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getBlockList().enqueue(new Callback<List<DashboardUser>>() {
            @Override
            public void onResponse(Call<List<DashboardUser>> call, Response<List<DashboardUser>> response) {
                if(response.code()==200){
                    for(DashboardUser  user: response.body()){
                        users.add(user);
                    }
                    adabter.notifyDataSetChanged();
                }
                pp.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<DashboardUser>> call, Throwable t) {
                pp.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
            }
        });


    }
}