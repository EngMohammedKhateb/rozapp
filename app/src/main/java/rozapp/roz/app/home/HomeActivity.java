package rozapp.roz.app.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rozapp.roz.app.R;
import rozapp.roz.app.chat.ChatActivity;
import rozapp.roz.app.events.CallAcceptedEvent;
import rozapp.roz.app.events.CallClosedEvent;
import rozapp.roz.app.events.CallEndedEvent;
import rozapp.roz.app.events.CallRejectedEvent;
import rozapp.roz.app.events.CallRequestClosedEvent;
import rozapp.roz.app.events.ChatMessageEvent;
import rozapp.roz.app.events.HomeBarEvent;
import rozapp.roz.app.events.IncomingRequestEvent;
import rozapp.roz.app.events.RefreshContactEvent;
import rozapp.roz.app.events.RefreshDashboardEvent;
import rozapp.roz.app.events.UserConnectedEvent;
import rozapp.roz.app.events.UserDisconnectedEvent;
import rozapp.roz.app.events.VideoGiftEvent;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.helper.Database;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.Contact;
import rozapp.roz.app.models.ProfileEvent;
import rozapp.roz.app.models.RefreshInfo;
import rozapp.roz.app.models.VideoChatMessage;
import rozapp.roz.app.pages.CardsFragment;
import rozapp.roz.app.pages.ChatFragment;
import rozapp.roz.app.pages.DashboardFragment;
import rozapp.roz.app.pages.ProfileFragment;
import rozapp.roz.app.profile.TargetProfileActivity;
import rozapp.roz.app.serve.ChatApplication;
import rozapp.roz.app.users.SearchActivity;
import rozapp.roz.app.videocall.ReciveVideoCall;

public class HomeActivity extends AppCompatActivity   {


    @BindView(R.id.search)
    ImageView search_iv;

    @BindView(R.id.logout_guest)
    ImageView logout_guest;

//    @BindView(R.id.settings)
//    ImageView setting_iv;

    @BindView(R.id.toolbar)
    RelativeLayout toolbar;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    AuthResponse authResponse;

    ProfileFragment profileFragment=new ProfileFragment();
    ChatFragment chatFragment=new ChatFragment();
    DashboardFragment dashboardFragment=new DashboardFragment();
    CardsFragment cardsFragment =new CardsFragment();

    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = dashboardFragment;


    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
    };

    private static Socket mSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);




        authResponse = new CallData(this).getAuthResponse();

        if(!authResponse.getUser().getName().equals("guest")){
            logout_guest.setVisibility(View.GONE);
        }else{
            logout_guest.setVisibility(View.VISIBLE);
            logout_guest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LogOut();
                }
            });
        }

        if(getIntent().hasExtra("from")){
            String from =getIntent().getStringExtra("from");
            if(from.equals("fcm")){
                Contact   target= (Contact) getIntent().getSerializableExtra("target");
                startActivity(new Intent( HomeActivity.this, ChatActivity.class).putExtra("target", (Serializable) target));
            }else if(from.equals("follow")){
                String   target_id= getIntent().getStringExtra("target_id");
                startActivity(new Intent( HomeActivity.this, TargetProfileActivity.class).putExtra("target_id",  target_id));
            }
        }



        FirebaseMessaging.getInstance().subscribeToTopic("follow"+authResponse.getUser().getId());
        FirebaseMessaging.getInstance().subscribeToTopic("call"+authResponse.getUser().getId());
        FirebaseMessaging.getInstance().subscribeToTopic("message"+authResponse.getUser().getId());

        refreshInfo();
        setOnNavItemSelected();
        fm.beginTransaction().add(R.id.frame, dashboardFragment, "4").commit();
        fm.beginTransaction().add(R.id.frame, cardsFragment, "3").hide(cardsFragment).commit();
        fm.beginTransaction().add(R.id.frame, chatFragment, "2").hide(chatFragment).commit();
        fm.beginTransaction().add(R.id.frame, profileFragment, "1").hide(profileFragment).commit();

//        setting_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(authResponse.getUser().getName().equals("guest")){
//                    return;
//                }
//                startActivity(new Intent(HomeActivity.this, SettingShooserActivity.class));
//            }
//        });

//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getItemId() == R.id.logout) {
//                    LogOut();
//                } else if (item.getItemId() == R.id.settings) {
//                    startActivity(new Intent(HomeActivity.this, SettingActivity.class));
//                } else if (item.getItemId() == R.id.pay_plan)  {
//                    startActivity(new Intent(HomeActivity.this, PlansActivity.class));
//                } else if (item.getItemId() == R.id.search)  {
//                    EventBus.getDefault().post(new ToggleSearchEvent());
//                }else if (item.getItemId() == R.id.request)  {
//                    startActivity(new Intent(HomeActivity.this, RequestClientActivity.class));
//                }else if (item.getItemId() == R.id.pay_out)  {
//                    startActivity(new Intent(HomeActivity.this, PayoutActivity.class));
//                }
//                return false;
//            }
//        });

        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            }
        });

        if(!authResponse.getUser().getName().equals("guest")){

//        {
//            IO.Options options = new IO.Options();
//            options.query = "token="+authResponse.getUser().getId()+"";
//
//            try {
//                mSocket = IO.socket(Constants.SocketServer,options);
//            } catch (URISyntaxException e) {}
//        }
//        if(!mSocket.connected()){
//            mSocket.connect();
//        }


             ChatApplication.connectTOSocket(authResponse.getUser().getId()+"");


        mSocket = ChatApplication.getSocket();

         mSocket.on("chat-message", new Emitter.Listener() {
             @Override
             public void call(Object... args) {
                 JSONObject dataobject = (JSONObject) args[0];
                 Gson gson=new Gson();
                 ChatMessageEvent data=gson.fromJson(String.valueOf(dataobject),ChatMessageEvent.class);
                 EventBus.getDefault().post(data);
             }
         });
         mSocket.on("user-connected", new Emitter.Listener() {
             @Override
             public void call(Object... args) {
                 JSONObject dataobject = (JSONObject) args[0];
                 Gson gson=new Gson();
                 UserConnectedEvent data=gson.fromJson(String.valueOf(dataobject), UserConnectedEvent.class);

                 EventBus.getDefault().post(data);
             }
         });
        mSocket.on("user-disconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject dataobject = (JSONObject) args[0];
                Gson gson=new Gson();
                UserDisconnectedEvent data=gson.fromJson(String.valueOf(dataobject), UserDisconnectedEvent.class);
                EventBus.getDefault().post(data);
            }
        });
        mSocket.on("incoming-request", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataobject = (JSONObject) args[0];
                Gson gson=new Gson();
                if(Constants.incall)
                   return;
                IncomingRequestEvent data=gson.fromJson(String.valueOf(dataobject), IncomingRequestEvent.class);
                EventBus.getDefault().post(data);

                if(Integer.parseInt(data.getRequested_id())==authResponse.getUser().getId()){
                    if(!Constants.incall && !Constants.is_stream){
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent (HomeActivity.this, ReciveVideoCall.class);
                                intent.putExtra("target_id",data.getRequester_id());
                                intent.putExtra("target_name",data.getRequester().getName());
                                intent.putExtra("target_image",data.getRequester().getImage());
                                startActivity(intent);
                            }
                        });
                    }
                }

            }
        });

        mSocket.on("call-rejected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataobject = (JSONObject) args[0];
                Gson gson=new Gson();

                CallRejectedEvent data=gson.fromJson(String.valueOf(dataobject), CallRejectedEvent.class);
                EventBus.getDefault().post(data);

            }
        });
        mSocket.on("call-ended", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataobject = (JSONObject) args[0];
                Gson gson=new Gson();

                CallEndedEvent data=gson.fromJson(String.valueOf(dataobject), CallEndedEvent.class);
                EventBus.getDefault().post(data);

            }
        });

        mSocket.on("call-accepted", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataobject = (JSONObject) args[0];
                Gson gson=new Gson();

                CallAcceptedEvent data=gson.fromJson(String.valueOf(dataobject), CallAcceptedEvent.class);
                EventBus.getDefault().post(data);

            }
        });
        mSocket.on("call-closed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataobject = (JSONObject) args[0];
                Gson gson=new Gson();

                CallClosedEvent data=gson.fromJson(String.valueOf(dataobject), CallClosedEvent.class);
                EventBus.getDefault().post(data);

            }
        });
        mSocket.on("video-gift", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataobject = (JSONObject) args[0];
                Gson gson=new Gson();

                VideoGiftEvent data=gson.fromJson(String.valueOf(dataobject), VideoGiftEvent.class);
                EventBus.getDefault().post(data);

            }
        });
        mSocket.on("close-request", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataobject = (JSONObject) args[0];
                Gson gson=new Gson();

                CallRequestClosedEvent data=gson.fromJson(String.valueOf(dataobject), CallRequestClosedEvent.class);
               //try catch required
                if(Constants.incall){
                    EventBus.getDefault().post(data);
                }

//as
            }
        });
        mSocket.on("video-chat-message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject dataobject = (JSONObject) args[0];
                Gson gson=new Gson();

                VideoChatMessage data=gson.fromJson(String.valueOf(dataobject), VideoChatMessage.class);
                EventBus.getDefault().post(data);

            }
        });
        }
    }

    private void refreshInfo() {

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).refreshInfo().enqueue(new Callback<RefreshInfo>() {
            @Override
            public void onResponse(Call<RefreshInfo> call, Response<RefreshInfo> response) {
                if(response.code()==200){
                    authResponse.setPk(response.body().getPk());
                    authResponse.setMin(response.body().getMin());
                    authResponse.setMax(response.body().getMax());
                    Gson gson =new Gson();
                    String store=gson.toJson(authResponse).toString();
                    Database.editAuthResponse(store);
                    if (response.body().getBlock().equals("1")){
                        LogOut();
                    }
                }
            }

            @Override
            public void onFailure(Call<RefreshInfo> call, Throwable t) {

            }
        });

    }

    public static Socket getSocket(){
        return mSocket;
    }


    private Emitter.Listener onconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                 //   DynamicToast.makeSuccess(HomeActivity.this,"socket connected successfully").show();

                }
            });
        }
    };
    private void setOnNavItemSelected() {
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                 switch(id){
                    case R.id.profile:
                        EventBus.getDefault().post(new ProfileEvent());
                        fm.beginTransaction().hide(active).show(profileFragment).commit();
                        active = profileFragment;
                        toolbar.setVisibility(View.GONE);
                      //  search_iv.setVisibility(View.GONE);

                        return true;

                    case R.id.chat:
                        fm.beginTransaction().hide(active).show(chatFragment).commit();
                        EventBus.getDefault().post(new RefreshContactEvent("asd"));
                        toolbar.setVisibility(View.GONE);
                        active = chatFragment;
                     //   search_iv.setVisibility(View.GONE);
                        return true;

                    case R.id.dash:
                        fm.beginTransaction().hide(active).show(dashboardFragment).commit();
                        EventBus.getDefault().post(new RefreshDashboardEvent("asd"));
                        toolbar.setVisibility(View.VISIBLE);
                        active = dashboardFragment;

                     //   search_iv.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.carsol:
                        fm.beginTransaction().hide(active).show(cardsFragment).commit();
                        toolbar.setVisibility(View.GONE);
                        active = cardsFragment;
                     //   search_iv.setVisibility(View.GONE);
                        return true;
                }
                return true;
            }
        });
    }
    
    
    private void askPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    void permission() {

        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Toast.makeText(CallActivity.this, "You already granted the permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            //  Toast.makeText(CallActivity.this, "You already granted the permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 2);
        }
    }


    private void LogOut() {





        FirebaseMessaging.getInstance().subscribeToTopic("follow"+authResponse.getUser().getId());
        FirebaseMessaging.getInstance().subscribeToTopic("call"+authResponse.getUser().getId());
        FirebaseMessaging.getInstance().subscribeToTopic("message"+authResponse.getUser().getId());
        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).logOut().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                DynamicToast.makeSuccess(HomeActivity.this,"good bye").show();

                new CallData(HomeActivity.this).LogOutHome();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                DynamicToast.makeSuccess(HomeActivity.this,"good bye").show();

                new CallData(HomeActivity.this).LogOutHome();
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
    public void onHomeBarEvent(HomeBarEvent event) {

//        if(event.getStatue().equals("gone")){
//            toolbar.setVisibility(View.GONE);
//        }else{
//            toolbar.setVisibility(View.VISIBLE);
//        }

      //  DynamicToast.makeSuccess(HomeActivity.this,message.getRequester().getName()).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!authResponse.getUser().getName().equals("guest")){
            mSocket.disconnect();
        }

    }
}