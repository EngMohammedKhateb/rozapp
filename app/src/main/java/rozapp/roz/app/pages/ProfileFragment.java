package rozapp.roz.app.pages;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.GiftsAdabter;
import rozapp.roz.app.adabters.ImageAdabter;
import rozapp.roz.app.adabters.VideosAdabter;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.helper.Database;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.home.HomeActivity;
import rozapp.roz.app.home.ImageViewrActivity;
import rozapp.roz.app.home.ProfileSettings;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.AuthedUserProfile;
import rozapp.roz.app.models.Media;
import rozapp.roz.app.models.MyGifys;
import rozapp.roz.app.models.ProfileEvent;
import rozapp.roz.app.setting.SettingActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rozapp.roz.app.setting.SettingShooserActivity;

public class ProfileFragment extends Fragment {

    @BindView(R.id.gifts_rv)
    RecyclerView rv_gifts;
    @BindView(R.id.rv_images)
    RecyclerView rv_images;
    @BindView(R.id.video_rv)
    RecyclerView rv_vidoes;
    @BindView(R.id.progress)
    ProgressBar progress;

    @BindView(R.id.gifts_tv)
    TextView gifts_tv;
    @BindView(R.id.user_id_tv)
    TextView user_id_tv;

    @BindView(R.id.user_name_tv)
    TextView user_name_tv;
    @BindView(R.id.email_tv)
    TextView email_tv;
    @BindView(R.id.location_tv)
    TextView location_tv;
    @BindView(R.id.followers_tv)
    TextView followers_count_tv;
    @BindView(R.id.gift_tv)
    TextView gifts_count_tv;
    @BindView(R.id.messages_tv)
    TextView messages_count_tv;
    @BindView(R.id.status_tv)
    TextView status_tv;
    @BindView(R.id.page)
    RelativeLayout page;
    @BindView(R.id.progress_page)
    ProgressBar progress_page;
    @BindView(R.id.progress_gifts)
    ProgressBar progress_gifts;
    @BindView(R.id.user_image_p)
    ImageView imageView;
    @BindView(R.id.settings)
    ImageView settings;
    @BindView(R.id.btn_edit)
    RelativeLayout btn_edit;
    @BindView(R.id.coins_ber)
    TextView coins_ber;

    @BindView(R.id.tab_layout)
    TabLayout tab_layout;

    private List<Media> videos;
    private List<Media> images;


    private ImageAdabter imageAdabter;
    private List<MyGifys> gifts;

    private VideosAdabter videoAdabter;
    private AuthResponse authResponse;
    private GiftsAdabter giftsAdabter;
    private AuthedUserProfile authedUserProfile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this,v);
        ui();

        authResponse=new CallData(getContext()).getAuthResponse();
        clickHandler();
        page.setVisibility(View.GONE);
        progress_page.setVisibility(View.VISIBLE);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(authResponse.getUser().getName().equals("guest")){
                    return;
                }
                startActivity(new Intent(getContext(), SettingShooserActivity.class));
            }
        });





        return v;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProfileEvent(ProfileEvent event) {
        LoadCurrentUserInfo();
    }

    private void LoadCurrentUserInfo(){

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getAuthedUserProfile().enqueue(new Callback<AuthedUserProfile>() {
            @Override
            public void onResponse(Call<AuthedUserProfile> call, Response<AuthedUserProfile> response) {
                if(response.code()==200){
                    authedUserProfile= response.body();
                    user_name_tv.setText(authedUserProfile.getName().toString());
                    email_tv.setText(authedUserProfile.getMessages()+"");
                    location_tv.setText(authedUserProfile.getCountryName());
                    followers_count_tv.setText(authedUserProfile.getFollowers()+"");
                    gifts_count_tv.setText(authedUserProfile.getGifts()+"");
                    messages_count_tv.setText(authedUserProfile.getMessages_count()+"");
                    coins_ber.setText(" "+authedUserProfile.getCoins()+" token");
                    status_tv.setText(authedUserProfile.getStatue());
                    Picasso.with(getContext()).load(Constants.Image_URL+authedUserProfile.getImage()).into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getContext(), ImageViewrActivity.class).putExtra("image",Constants.Image_URL+authedUserProfile.getImage()));

                        }
                    });
                    authResponse.getUser().setDisableChat(authedUserProfile.getDisableChat());
                    authResponse.getUser().setDisableVideo(authedUserProfile.getDisableVideo());
                    authResponse.getUser().setCoinsPerMinute(authedUserProfile.getCoinsPerMinute());
                    authResponse.getUser().setCoins(authedUserProfile.getCoins()+"");
                    authResponse.getUser().setMessages(authedUserProfile.getMessages());
                    authResponse.getUser().setType(authedUserProfile.getType());
                    authResponse.getUser().setName(authedUserProfile.getName());


                    user_id_tv.setText("ID : "+authResponse.getUser().getId());

                    authResponse.getUser().setImage(authedUserProfile.getImage()+"");
                    Gson gson =new Gson();
                    String store=gson.toJson(authResponse).toString();
                    Database.editAuthResponse(store);

                    page.setVisibility(View.VISIBLE);
                    progress_page.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<AuthedUserProfile> call, Throwable t) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        LoadCurrentUserInfo();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);//Register
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);//unregister
    }


    private void clickHandler() {

         getUserImages();
         getUserVideos();
         getUserGifts();
         tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
             @Override
             public void onTabSelected(TabLayout.Tab tab) {
                 if (tab.getPosition() == 0){
                     refreshImages();
                     rv_images.setVisibility(View.VISIBLE);

                     rv_vidoes.setVisibility(View.GONE);
                 }else{
                     getUserVideos();
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
    }

    private void getUserImages() {
        progress.setVisibility(View.VISIBLE);
        rv_images.setVisibility(View.GONE);
        rv_vidoes.setVisibility(View.GONE);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getUserImages().enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                if(response.code()==200){
                    for(Media media:response.body()){
                        images.add(media);
                    }
                    imageAdabter.notifyDataSetChanged();
                    rv_images.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {

            }
        });
    }
    private void refreshImages() {

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getUserImages().enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                if(response.code()==200){
                    images.clear();
                    for(Media media:response.body()){
                        images.add(media);
                    }
                    imageAdabter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {

            }
        });
    }
    private void getUserVideos() {

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getUserVideos().enqueue(new Callback<List<Media>>() {
            @Override
            public void onResponse(Call<List<Media>> call, Response<List<Media>> response) {
                if(response.code()==200){
                    videos.clear();
                    for(Media media:response.body()){
                        videos.add(media);
                    }
                    videoAdabter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<List<Media>> call, Throwable t) {

            }
        });
    }

    private void getUserGifts() {
        progress_gifts.setVisibility(View.VISIBLE);

        rv_gifts.setVisibility(View.GONE);
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getUserGifts().enqueue(new Callback<List<MyGifys>>() {
            @Override
            public void onResponse(Call<List<MyGifys>> call, Response<List<MyGifys>> response) {
                if(response.code()==200){
                    for(MyGifys gift:response.body()){
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
            public void onFailure(Call<List<MyGifys>> call, Throwable t) {

            }
        });
    }

    private boolean proccess(MyGifys gift){
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

    private void ui(){
        images=new ArrayList<>();
        videos=new ArrayList<>();
        gifts=new ArrayList<>();
        imageAdabter=new ImageAdabter(getContext(),images);
        videoAdabter=new VideosAdabter(getContext(),videos);
        giftsAdabter=new GiftsAdabter(getContext(),gifts);

        rv_vidoes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        LinearLayoutManager rvgifts
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_gifts.setLayoutManager(rvgifts);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_images.setLayoutManager(layoutManager);

        rv_images.setAdapter(imageAdabter);
        rv_vidoes.setAdapter(videoAdabter);
        rv_gifts.setAdapter(giftsAdabter);


        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(authResponse.getUser().getName().equals("guest")){
                    return;
                }
                startActivity(new Intent(getContext(), ProfileSettings.class));
            }
        });

    }


}