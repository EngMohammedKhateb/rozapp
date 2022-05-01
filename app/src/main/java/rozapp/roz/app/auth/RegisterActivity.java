package rozapp.roz.app.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Database;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.home.HomeActivity;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ErrorResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.gender_spnner_id)
    Spinner spinner;
    @BindView(R.id.country_spnner_id)
    Spinner country_spinner;
    @BindView(R.id.first_step)
    LinearLayout first_step;
    @BindView(R.id.second_step)
    LinearLayout second_step;
    @BindView(R.id.btn_continue)
    RelativeLayout btn_continue;
    @BindView(R.id.change_image_btn)
    RelativeLayout change_image_btn;
    @BindView(R.id.btn_register)
    RelativeLayout btn_register;
    @BindView(R.id.btn_first)
    ImageView btn_first;
    @BindView(R.id.first_name_ed)
    EditText first_name_ed;
    @BindView(R.id.last_name_ed)
    EditText last_name_ed;
    @BindView(R.id.email_ed)
    EditText email_ed;
    @BindView(R.id.password_ed)
    EditText password_ed;
    @BindView(R.id.go_login_tv)
    TextView go_login_tv;
    @BindView(R.id.tv_reg)
    TextView tv_reg;
    @BindView(R.id.progress_reg)
    ProgressBar progress_reg;

    String[] countries =new String [261];
    JSONArray jsonArray;


    String image_url="default";
    boolean uploaded=false;

    String country_name;
    String country_code;
    String country_image;
    String gender;

    String first_name;
    String last_name;
    String email;
    String password;
    boolean loading=false;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        db = new Database(this);
        if (db.isLogin()) {
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }
        setSpinnerGender();
        setCountries();
        attachToAdabter();

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstname=first_name_ed.getText().toString();
                String lastname=last_name_ed.getText().toString();

                if(firstname.trim().equals("") || lastname.trim().equals("") ){
                    DynamicToast.makeWarning(RegisterActivity.this, "please complete all information", 5).show();
                    return;
                }
                if(gender.equals("empty")){
                    DynamicToast.makeWarning(RegisterActivity.this, "please select gender", 5).show();
                    return;
                }


                first_step.setVisibility(View.GONE);
                second_step.setVisibility(View.VISIBLE);
            }
        });

        btn_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                first_step.setVisibility(View.VISIBLE);
                second_step.setVisibility(View.GONE);
            }
        });

        change_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploaded=true;
            }
        });



        go_login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                first_name=first_name_ed.getText().toString();
                last_name=last_name_ed.getText().toString();
                email=email_ed.getText().toString();
                password=password_ed.getText().toString();
                if(first_name.trim().equals("") || last_name.trim().equals("")){
                    DynamicToast.makeError(RegisterActivity.this, "please enter first name and last name").show();
                    return;
                }
                if(email.trim().equals("")){
                    DynamicToast.makeError(RegisterActivity.this, "please enter email address").show();
                    return;
                }
                if(password.trim().equals("")){
                    DynamicToast.makeError(RegisterActivity.this, "please enter the password").show();
                    return;
                }
                if(password.trim().length() < 8){
                    DynamicToast.makeError(RegisterActivity.this, "please type strong password type greater than 8 char").show();
                    return;
                }
                if(loading)return;
                loading=true;
                String name=first_name.trim()+" "+last_name.trim();
                tv_reg.setVisibility(View.GONE);
                progress_reg.setVisibility(View.VISIBLE);

                KhateebPattern.getServicesInstance().createAccount( name ,email,password,gender,country_image,country_code,country_name,image_url).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        Gson gson =new Gson();
                        if(response.code()==200){

                            String store=gson.toJson(response.body()).toString();
                            db.createSession(  store );
                            DynamicToast.makeSuccess(RegisterActivity.this,"welcome "+response.body().getUser().getName()).show();
                            startActivity(new Intent(RegisterActivity.this, ConfirmPasswordActivity.class));
                            finish();
                        }else if(response.code()==400){
                            try {
                                JSONObject jsonObject=new JSONObject(response.errorBody().string());
                                ErrorResponse error_response=gson.fromJson(String.valueOf(jsonObject),ErrorResponse.class);
                                DynamicToast.makeError(RegisterActivity.this, error_response.getMessage()+"").show();
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }
                        }
                        tv_reg.setVisibility(View.VISIBLE);
                        loading=false;
                        progress_reg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        DynamicToast.makeError(RegisterActivity.this, "something went wrong => bad internet connection").show();
                        tv_reg.setVisibility(View.VISIBLE);
                        progress_reg.setVisibility(View.GONE);
                        loading=false;
                    }
                });


            }
        });



    }

    private void setSpinnerGender() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gende_rarray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    gender="empty";
                }else if(i==1){
                    gender="male";

                }else{
                    gender="female";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                gender="empty";
            }
        });

    }

    private void attachToAdabter() {


        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,countries);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        country_spinner.setAdapter(aa);
        country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                     JSONObject jsonObject = jsonArray.getJSONObject(i);
                     country_name = jsonObject.getString("name");
                     country_code = jsonObject.getString("code");
                     country_image = jsonObject.getString("image");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }

    private void setCountries() {
        try {
            jsonArray = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                countries[i]=name;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("country.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}