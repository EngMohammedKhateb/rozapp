package rozapp.roz.app.users;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.AllUsersAdabter;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.DashboardUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllUsersActivity extends AppCompatActivity {


    @BindView(R.id.rv_users)
    RecyclerView rv;

    @BindView(R.id.pp)
    ProgressBar pp;

    @BindView(R.id.arrow_back)
    ImageView arrow_back;


    @BindView(R.id.ed_search)
    EditText ed_search;
    @BindView(R.id.btn_search)
    ImageView btn_search;

    private AuthResponse authResponse;

    private List<DashboardUser> users;
    private AllUsersAdabter allUsersAdabter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_users);
        ButterKnife.bind(this);
        authResponse=new CallData(this).getAuthResponse();
        ui();
        getDashboardUser("all");
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
        getLatest();
    }

    private void getLatest() {


        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                allUsersAdabter.getFilter().filter(charSequence);
                Log.e("filter :",ed_search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void ui() {
        users=new ArrayList<>();

        allUsersAdabter=new AllUsersAdabter(AllUsersActivity.this,users);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(allUsersAdabter);

    }
    private void getDashboardUser( String filter ) {

        users.clear();
        allUsersAdabter.notifyDataSetChanged();
        pp.setVisibility(View.VISIBLE);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getDashboardUsers(filter).enqueue(new Callback<List<DashboardUser>>() {
            @Override
            public void onResponse(Call<List<DashboardUser>> call, Response<List<DashboardUser>> response) {

                if (response.code()==200){
                    users.clear();
                    for (DashboardUser user:response.body()) {
                        users.add(user);
                    }
                }
                allUsersAdabter.notifyDataSetChanged();
                pp.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<DashboardUser>> call, Throwable t) {
                pp.setVisibility(View.GONE);
            }
        });

    }

}