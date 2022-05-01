package rozapp.roz.app.home;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ProfileEvent;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSettings extends AppCompatActivity {


    @BindView(R.id.btn_upload_image)
    RelativeLayout btn_upload_image;
    @BindView(R.id.btn_upload_video)
    RelativeLayout btn_upload_video;
    @BindView(R.id.btn_upload_profile)
    RelativeLayout btn_upload_profile;
    @BindView(R.id.btn_change_statue)
    RelativeLayout btn_change_statue;

    @BindView(R.id.change_statue_tv)
    TextView change_statue_tv;
    @BindView(R.id.tv_upload_image)
    TextView tv_upload_image;
    @BindView(R.id.tv_upload_video)
    TextView tv_upload_video;
    @BindView(R.id.tv_upload_profile)
    TextView tv_upload_profile;

    @BindView(R.id.pp_profile)
    ProgressBar pp_profile;
    @BindView(R.id.pp_video)
    ProgressBar pp_video;
    @BindView(R.id.pp_image)
    ProgressBar pp_image;
    @BindView(R.id.pp_change_statue)
    ProgressBar pp_change_statue;

    @BindView(R.id.arrow_back)
    ImageView arrow_back;


    private Dialog myDialog;
    private AuthResponse authResponse;
    ActivityResultLauncher<String> mGetContent;

    String upload_type="profile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
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
        KhateebPattern.verifyStoragePermissions(ProfileSettings.this);
        myDialog = new Dialog(this);
        authResponse =new CallData(this).getAuthResponse();
        btn_change_statue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatue();
            }
        });
        btn_upload_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_type="profile";
                mGetContent.launch("image/*");
            }
        });

        btn_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_type="add_media";
                mGetContent.launch("image/*");
            }
        });


        mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                if (result!=null){
                    Intent intent=new Intent(ProfileSettings.this,CropperActivity.class);
                    intent.putExtra("upload_type",upload_type);
                    intent.putExtra("data",result.toString());
                    startActivityForResult(intent,101);
                }


            }
        });

        btn_upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, 105);

            }
        });



    }


    private void updateStatue() {
        Button btnupdate,btn_close;
        EditText ed_msg;
        myDialog.setContentView(R.layout.pub_edit_statue);
        btnupdate =(Button) myDialog.findViewById(R.id.update_btn);
        btn_close = (Button) myDialog.findViewById(R.id.close_btn);
        ed_msg=(EditText) myDialog.findViewById(R.id.ed_message);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String upmsg=ed_msg.getText().toString();

                if (upmsg.trim().equals("")){
                    KhateebPattern.showToastLong(ProfileSettings.this,"cant create empty message");
                    myDialog.dismiss();
                    return;
                }
                myDialog.dismiss();
                sendChangeRequest(ed_msg.getText().toString());
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }

    private void sendChangeRequest(String statue) {

        change_statue_tv.setVisibility(View.GONE);
        pp_change_statue.setVisibility(View.VISIBLE);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).changeStatue(statue).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200){
                    DynamicToast.makeSuccess(ProfileSettings.this,"statue changed successfully").show();
                    EventBus.getDefault().post(new ProfileEvent());
                }
                change_statue_tv.setVisibility(View.VISIBLE);
                pp_change_statue.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                change_statue_tv.setVisibility(View.VISIBLE);
                pp_change_statue.setVisibility(View.GONE);
            }
        });

    }
    public void changeImage(String filePath ) {
        if(!KhateebPattern.verifyStoragePermissions(ProfileSettings.this)){

            return;
        }

        final ProgressDialog pd = new ProgressDialog(ProfileSettings.this );
        pd.setMessage("uploading image");
        pd.setTitle("please wait...");

        pd.show();


        File file = new File(filePath);


        //create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).changeProfileImage(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    DynamicToast.makeSuccess(ProfileSettings.this,"image changed successfully").show();
                }


                Log.e("response code",response.code()+" ");
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DynamicToast.makeError(ProfileSettings.this,"something went wrong"+t.getLocalizedMessage()).show();
                pd.dismiss();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==-1 && requestCode==101){
            String result=data.getStringExtra("RESULT");
            Uri resultUri=null;
            if(result!=null){
              resultUri=Uri.parse(result);
              if(data.getStringExtra("upload_type").equals("profile")){
                  changeImage(resultUri.getPath());
              }else{
                  addMediaImage(resultUri.getPath());
              }

            }
        }else if(requestCode==105){

            if(data != null){
                Uri selectedImageUri = data.getData();
                addMediaVideo(getPath(selectedImageUri));
            }

        }
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
    private void addMediaImage(String path) {
        if(!KhateebPattern.verifyStoragePermissions(ProfileSettings.this)){
            return;
        }
        final ProgressDialog pd = new ProgressDialog(ProfileSettings.this );
        pd.setMessage("uploading image");
        pd.setTitle("please wait...");
        pd.show();
        File file = new File(path);
        //create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).addMediaImage(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    DynamicToast.makeSuccess(ProfileSettings.this,"image added successfully").show();
                }
                   Log.e("response code",response.code()+" ");
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DynamicToast.makeError(ProfileSettings.this,"something went wrong"+t.getLocalizedMessage()).show();
                pd.dismiss();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted and now can proceed


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ProfileSettings.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }
    private void addMediaVideo(String path) {

        ActivityCompat.requestPermissions(ProfileSettings.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        if(!KhateebPattern.verifyStoragePermissions(ProfileSettings.this)){
            return;
        }
        final ProgressDialog pd = new ProgressDialog(ProfileSettings.this );
        pd.setMessage("uploading video");
        pd.setTitle("please wait...");
        pd.show();
        File file = new File(path);
        //create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("video"), file);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("video", file.getName(), requestFile);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).addMediaVideo(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code()==200){
                    DynamicToast.makeSuccess(ProfileSettings.this,"video added successfully").show();
                }
                Log.e("response code",response.code()+" ");
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                DynamicToast.makeError(ProfileSettings.this,"something went wrong"+t.getLocalizedMessage()).show();
                pd.dismiss();
            }
        });

    }
}