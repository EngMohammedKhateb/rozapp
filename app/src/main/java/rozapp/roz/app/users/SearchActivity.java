package rozapp.roz.app.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.FollowAdabter;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.DashboardUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.ed_search)
    EditText ed_search;
    @BindView(R.id.btn_search)
    RelativeLayout btn_search;

    @BindView(R.id.rv_users)
    RecyclerView rv_users;
    @BindView(R.id.pp)
    ProgressBar pp;
    @BindView(R.id.not_found)
    TextView not_found;

    private List<DashboardUser> users;
    private FollowAdabter adabter;
    private AuthResponse authResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        ui();
        SharedPreferences pref=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language=pref.getString("language","en");
        if(language.equals("en")) {
          back.setImageDrawable(getDrawable(R.drawable.arrow_left));
        }else{
          back.setImageDrawable(getDrawable(R.drawable.arrow_right));
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word= ed_search.getText().toString();
                if(word.trim().equals("")){
                    DynamicToast.makeError(SearchActivity.this,"type something to search").show();
                    return;
                }
                search(word);
            }
        });

    }

    private void ui() {
        authResponse=new CallData(this).getAuthResponse();
        users=new ArrayList<>();
        adabter=new FollowAdabter(this,users);
        rv_users.setLayoutManager(new LinearLayoutManager(this));
        rv_users.setAdapter(adabter);

    }

    private void search(String word){

        pp.setVisibility(View.VISIBLE);
        rv_users.setVisibility(View.GONE);
        not_found.setVisibility(View.GONE);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).searchUsers(word).enqueue(new Callback<List<DashboardUser>>() {
            @Override
            public void onResponse(Call<List<DashboardUser>> call, Response<List<DashboardUser>> response) {
                if(response.code()==200){
                    users.clear();

                    if(response.body().size() ==0 ){
                        not_found.setVisibility(View.VISIBLE);
                    }else{
                        not_found.setVisibility(View.GONE);
                    }
                    for (DashboardUser user:response.body()) {
                        users.add(user);
                    }
                    adabter.notifyDataSetChanged();


                }
                pp.setVisibility(View.GONE);
                rv_users.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<DashboardUser>> call, Throwable t) {

                pp.setVisibility(View.GONE);
                rv_users.setVisibility(View.VISIBLE);
            }
        });

    }



}