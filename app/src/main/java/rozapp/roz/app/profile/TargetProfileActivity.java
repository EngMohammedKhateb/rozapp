package rozapp.roz.app.profile;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rozapp.roz.app.Fcm.ApiClient;
import rozapp.roz.app.Fcm.ApiInterface;
import rozapp.roz.app.Fcm.DataModel;
import rozapp.roz.app.Fcm.RootModel;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.ImageAdabter;
import rozapp.roz.app.adabters.TargetGiftsAdabter;
import rozapp.roz.app.adabters.VideosAdabter;
import rozapp.roz.app.chat.ChatActivity;
import rozapp.roz.app.events.UserConnectedEvent;
import rozapp.roz.app.events.UserDisconnectedEvent;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.home.ImageViewrActivity;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.Contact;
import rozapp.roz.app.models.ErrorHandler;
import rozapp.roz.app.models.Media;
import rozapp.roz.app.models.TargetProfile;
import rozapp.roz.app.models.TargetProfileGifts;
import rozapp.roz.app.serve.ChatApplication;
import rozapp.roz.app.setting.ReportActivity;
import rozapp.roz.app.videocall.RequestVideoCall;

public class TargetProfileActivity extends AppCompatActivity {

    @BindView(R.id.gifts_rv)
    RecyclerView rv_gifts;
    @BindView(R.id.rv_images)
    RecyclerView rv_images;
    @BindView(R.id.video_rv)
    RecyclerView rv_vidoes;
    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.images_tv)
    TextView images_tv;


    @BindView(R.id.more_vert)
    ImageView more_vert;

    @BindView(R.id.videos_tv)
    TextView videos_tv;
    @BindView(R.id.user_name_tv)
    TextView user_name_tv;
    @BindView(R.id.email_tv)
    TextView email_tv;
    @BindView(R.id.location_tv)
    TextView location_tv;
    @BindView(R.id.followers_tv)
    TextView followers_tv;
    @BindView(R.id.btn_follow)
    ImageView btn_follow;


    @BindView(R.id.user_id_tv)
    TextView user_id_tv;


    @BindView(R.id.status_tv)
    TextView status_tv;
    @BindView(R.id.coins_ber)
    TextView coins_ber;
    @BindView(R.id.page)
    RelativeLayout page;
    @BindView(R.id.video_call)
    RelativeLayout video_call;
    @BindView(R.id.btf)
    RelativeLayout btf;
    @BindView(R.id.progress_page)
    ProgressBar progress_page;
    @BindView(R.id.progress_gifts)
    ProgressBar progress_gifts;
    @BindView(R.id.pp_follow)
    ProgressBar pp_follow;
    @BindView(R.id.user_image_p)
    ImageView imageView;
    @BindView(R.id.arrow_back)
    ImageView arrow_back;
    @BindView(R.id.chat_btn)
    ImageView chat_btn;
    @BindView(R.id.tab_layout)
    TabLayout tab_layout;


    private List<Media> images;
    private List<Media> videos;
    private List<TargetProfileGifts> gifts;

    private ImageAdabter imageAdabter;
    private VideosAdabter videoAdabter;
    private TargetGiftsAdabter giftsAdabter;


    private String target_id;
    private TargetProfile targetProfile;
    private AuthResponse authResponse;
    BottomSheetDialog bottomSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target_profile);
        ButterKnife.bind(this);
        ui();
        authResponse=new CallData(this).getAuthResponse();
        getTargetProfile();
        getGifts();
        getMedia();
        clickListener();

        ChatApplication.connectTOSocket(authResponse.getUser().getId()+"");



        bottomSheetDialog=new BottomSheetDialog(TargetProfileActivity.this,R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.block_report_sheet);

        bottomSheetDialog.findViewById(R.id.linear_block).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                ProgressDialog  progress= new ProgressDialog(TargetProfileActivity.this );
                progress.setMessage("block user");
                progress.setTitle("please wait...");
                progress.show();
                KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).blockUser(target_id).enqueue(new Callback<ErrorHandler>() {
                    @Override
                    public void onResponse(Call<ErrorHandler> call, Response<ErrorHandler> response) {
                        if(response.code()==200){
                            DynamicToast.makeSuccess(TargetProfileActivity.this,response.body().getMessage()).show();
                        }
                        progress.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ErrorHandler> call, Throwable t) {
                        progress.dismiss();
                    }
                });
            }
        });
        bottomSheetDialog.findViewById(R.id.linear_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TargetProfileActivity.this, ReportActivity.class).putExtra("target_id",target_id));
                bottomSheetDialog.dismiss();
            }
        });
        more_vert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
            }
        });
    }

    private void getMedia() {
        rv_images.setVisibility(View.GONE);
        rv_vidoes.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getTargetMedia(target_id).enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                if(response.code()==200){
                    for(Media media: response.body()){
                         if(media.getType().equals("image")){
                             images.add(media);
                         }else{
                             videos.add(media);
                         }
                    }
                    videoAdabter.notifyDataSetChanged();
                    imageAdabter.notifyDataSetChanged();
                    rv_images.setVisibility(View.VISIBLE);
                    rv_vidoes.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {

            }
        });
    }

    private void getGifts() {
        rv_gifts.setVisibility(View.GONE);
        progress_gifts.setVisibility(View.VISIBLE);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getTargetGifts(target_id).enqueue(new Callback<List<TargetProfileGifts>>() {
            @Override
            public void onResponse(Call<List<TargetProfileGifts>> call, Response<List<TargetProfileGifts>> response) {
                if(response.code()==200){
                    for(TargetProfileGifts gift: response.body()){
                        if(!proccess(gift)){
                            gifts.add(gift);
                        }


                    }
                    giftsAdabter.notifyDataSetChanged();
                    rv_gifts.setVisibility(View.VISIBLE);
                    progress_gifts.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<TargetProfileGifts>> call, Throwable t) {

            }
        });
    }
    private boolean proccess(TargetProfileGifts gift){
        boolean found =false;
        for(int i=0 ; i< gifts.size() ;i++){
            if(gifts.get(i).getId() == gift.getId()){
                found =true;
                int c= gifts.get(i).getCount();
                c++;
                gifts.get(i).setCount(c);
            }
        }
        return found;
    }
    private void clickListener() {


        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    rv_images.setVisibility(View.VISIBLE);
                    rv_vidoes.setVisibility(View.GONE);
                }else{

                    rv_images.setVisibility(View.GONE);
                    rv_vidoes.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


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

        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pp_follow.setVisibility(View.VISIBLE);

                KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).toggleFollow(target_id).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code()==200){

                            if(targetProfile.isFollowed()){
                                targetProfile.setFollowers(targetProfile.getFollowers()-1);
                            }else{
                                targetProfile.setFollowers(targetProfile.getFollowers()+1);
                            }


                            followers_tv.setText(" "+targetProfile.getFollowers()+" followers");
                            targetProfile.setIsFollowed(!targetProfile.isFollowed());

                            if(targetProfile.isFollowed()){
                                btn_follow.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                                btf.setBackground(getResources().getDrawable(R.drawable.circle_green_gradient));
                            }else{
                                btn_follow.setImageDrawable(getResources().getDrawable(R.drawable.add_person));
                                btf.setBackground(getResources().getDrawable(R.drawable.circle_gradeint));
                            }

                            sendNotification();
                            pp_follow.setVisibility(View.GONE);
                            btn_follow.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });



            }
        });

        chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(targetProfile.getDisableChat()==0){
                    Intent intent=new Intent(TargetProfileActivity.this, ChatActivity.class);
                    intent.putExtra("target",(Contact) new Contact(targetProfile.getId(),targetProfile.getName(),targetProfile.getType(),targetProfile.getCoins(),targetProfile.getOnline(),targetProfile.getCoinsPerMinute(),targetProfile.getDisableChat(),targetProfile.getDisableVideo(),targetProfile.getImage(),targetProfile.getStatue()));
                    startActivity(intent);
                }else{
                    DynamicToast.makeError(TargetProfileActivity.this,"this person lock the chat").show();
                }

            }
        });

        video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(targetProfile.getDisableVideo()==0){
                     startActivity(new Intent(TargetProfileActivity.this, RequestVideoCall.class)
                             .putExtra("target_id",targetProfile.getId()+"")
                             .putExtra("target_name",targetProfile.getName()+"")
                             .putExtra("target_image",targetProfile.getImage()+"")
                     );
                }else{
                    DynamicToast.makeError(TargetProfileActivity.this,"this person lock the video calls").show();
                }
            }
        });

    }

    private void sendNotification() {

        RootModel rootModel = new RootModel("/topics/follow"+targetProfile.getId(),new DataModel(authResponse.getUser().getId(), "follow",authResponse.getUser().getName(),authResponse.getUser().getImage()));

        ApiInterface apiService =  ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ResponseBody> responseBodyCall = apiService.sendNotification(rootModel);

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.d(TAG,"Successfully notification send by using retrofit.");
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void ui() {
        target_id=getIntent().getStringExtra("target_id");
        user_id_tv.setText("ID : "+target_id);
        gifts=new ArrayList<>();
        giftsAdabter=new TargetGiftsAdabter(this,gifts);
        rv_gifts.setLayoutManager(new LinearLayoutManager(TargetProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv_gifts.setAdapter(giftsAdabter);

        images=new ArrayList<>();
        imageAdabter=new ImageAdabter(this,images);
        rv_images.setLayoutManager(new LinearLayoutManager(TargetProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv_images.setAdapter(imageAdabter);

        videos=new ArrayList<>();
        videoAdabter=new VideosAdabter(this,videos);
        rv_vidoes.setLayoutManager(new LinearLayoutManager(TargetProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv_vidoes.setAdapter(videoAdabter);
        videos_tv.setTypeface(null, Typeface.NORMAL);
        images_tv.setTypeface(null, Typeface.BOLD);

        videos_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        images_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        videos_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv_images.setVisibility(View.GONE);
                rv_vidoes.setVisibility(View.VISIBLE);

                videos_tv.setTypeface(null, Typeface.BOLD);
                images_tv.setTypeface(null, Typeface.NORMAL);

                videos_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                images_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            }
        });
        images_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rv_images.setVisibility(View.VISIBLE);
                rv_vidoes.setVisibility(View.GONE);

                videos_tv.setTypeface(null, Typeface.NORMAL);
                images_tv.setTypeface(null, Typeface.BOLD);

                videos_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                images_tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            }
        });


    }

    private void getTargetProfile() {

        page.setVisibility(View.GONE);
        progress_page.setVisibility(View.VISIBLE);

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getTargetProfile(target_id).enqueue(new Callback<TargetProfile>() {
            @Override
            public void onResponse(Call<TargetProfile> call, Response<TargetProfile> response) {
                if(response.code()==200){
                    targetProfile= response.body();
                    page.setVisibility(View.VISIBLE);
                    progress_page.setVisibility(View.GONE);
                    user_name_tv.setText(targetProfile.getName().toString());
                    email_tv.setText(targetProfile.getMessages()+"");
                    location_tv.setText(targetProfile.getCountryName());
                    status_tv.setText(targetProfile.getStatue());
                    followers_tv.setText(" "+targetProfile.getFollowers()+" followers");

                    if(targetProfile.isFollowed()){
                        btn_follow.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                        btf.setBackground(getResources().getDrawable(R.drawable.circle_green_gradient));
                    }else{
                        btn_follow.setImageDrawable(getResources().getDrawable(R.drawable.add_person));
                        btf.setBackground(getResources().getDrawable(R.drawable.circle_gradeint));
                    }


                    Picasso.with(TargetProfileActivity.this).load(Constants.Image_URL+targetProfile.getImage()).into(imageView);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(TargetProfileActivity.this, ImageViewrActivity.class).putExtra("image",Constants.Image_URL+targetProfile.getImage()));

                        }
                    });
                    coins_ber.setText(" "+targetProfile.getCoinsPerMinute()+" token/minute");

                    video_call.setVisibility(View.VISIBLE);
                    if(targetProfile.getDisableChat()==0){
                        chat_btn.setVisibility(View.VISIBLE);
                    }else{
                        chat_btn.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<TargetProfile> call, Throwable t) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserConnectedEvent(UserConnectedEvent event) {


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserDisconnectedEvent(UserDisconnectedEvent event) {

    }


}