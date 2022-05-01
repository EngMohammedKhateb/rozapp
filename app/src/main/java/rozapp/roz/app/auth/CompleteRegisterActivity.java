package rozapp.roz.app.auth;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.helper.Database;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.home.CropperActivity;
import rozapp.roz.app.home.HomeActivity;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ErrorHandler;
import rozapp.roz.app.models.ErrorResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteRegisterActivity extends AppCompatActivity {

    @BindView(R.id.gender_spnner_id)
    Spinner spinner;
    @BindView(R.id.country_spnner_id)
    Spinner country_spinner;
    @BindView(R.id.change_image_btn)
    RelativeLayout change_image_btn;
    @BindView(R.id.btn_register)
    RelativeLayout btn_register;
    @BindView(R.id.tv_reg)
    TextView tv_reg;
    @BindView(R.id.progress_reg)
    ProgressBar progress_reg;
    @BindView(R.id.pp_loading_image)
    ProgressBar pp_loading_image;
    @BindView(R.id.profile_image_id)
    CircleImageView profile_image_id;


    String[] countries =new String [261];
    JSONArray jsonArray;


    String image="default";


    String country_name;
    String country_code;
    String country_image;
    String gender;

    String name;
    String email;

    boolean loading=false;
    Database db;
    ActivityResultLauncher<String> mGetContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_register);
        ButterKnife.bind(this);

        name=getIntent().getStringExtra("name");
        email=getIntent().getStringExtra("email");
        db = new Database(this);
        if (db.isLogin()) {
            startActivity(new Intent(CompleteRegisterActivity.this, HomeActivity.class));
            finish();
        }
        setSpinnerGender();
        setCountries();
        attachToAdabter();

        KhateebPattern.verifyStoragePermissions(CompleteRegisterActivity.this);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });

        change_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mGetContent.launch("image/*");
            }
        });


        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Intent intent=new Intent( CompleteRegisterActivity.this, CropperActivity.class);
                intent.putExtra("upload_type","upload_type");
                intent.putExtra("data",result.toString());
                activityResultLaunch.launch(intent);
                }
            });
        }
            ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult datares) {
                        Intent data=datares.getData();
                        String result=data.getStringExtra("RESULT");
                        Uri resultUri=null;
                        if(result!=null){
                            resultUri=Uri.parse(result);

                            uploadImage(resultUri.getPath());
                        }
                    }
            });

    private void uploadImage(String filePath) {

        if(!KhateebPattern.verifyStoragePermissions(CompleteRegisterActivity.this)){

            return;
        }

        pp_loading_image.setVisibility(View.VISIBLE);
        File file = new File(filePath);
        //create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        KhateebPattern.getServicesInstance().uploadImage(body).enqueue(new Callback<ErrorHandler>() {
            @Override
            public void onResponse(Call<ErrorHandler> call, Response<ErrorHandler> response) {
                if(response.code()==200){
                    image=response.body().getMessage();
                    Picasso.with(CompleteRegisterActivity.this).load(Constants.Image_URL+image).into(profile_image_id);
                    DynamicToast.makeSuccess(CompleteRegisterActivity.this,"image uploaded successfully").show();
                }else{
                    DynamicToast.makeError(CompleteRegisterActivity.this,"bad internet connection").show();

                }
                pp_loading_image.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ErrorHandler> call, Throwable t) {
                DynamicToast.makeError(CompleteRegisterActivity.this,"bad internet connection..."+t.getLocalizedMessage()).show();
                pp_loading_image.setVisibility(View.GONE);
            }
        });


    }
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
    private void createNewAccount() {
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(email.trim().equals("")){
                    return;
                }

                if(loading)return;
                loading=true;

                tv_reg.setVisibility(View.GONE);
                progress_reg.setVisibility(View.VISIBLE);

                KhateebPattern.getServicesInstance().createAccount( name ,email,email+"password",gender,country_image,country_code,country_name,image).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        Gson gson =new Gson();
                        if(response.code()==200){

                            String store=gson.toJson(response.body()).toString();
                            db.createSession(  store );
                            DynamicToast.makeSuccess(CompleteRegisterActivity.this,"welcome "+response.body().getUser().getName()).show();
                            startActivity(new Intent(CompleteRegisterActivity.this, HomeActivity.class));
                            finish();
                        }else if(response.code()==400){
                            try {
                                JSONObject jsonObject=new JSONObject(response.errorBody().string());
                                ErrorResponse error_response=gson.fromJson(String.valueOf(jsonObject),ErrorResponse.class);
                                DynamicToast.makeError(CompleteRegisterActivity.this, error_response.getMessage()+"").show();
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
                        DynamicToast.makeError(CompleteRegisterActivity.this, "something went wrong => bad internet connection").show();
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