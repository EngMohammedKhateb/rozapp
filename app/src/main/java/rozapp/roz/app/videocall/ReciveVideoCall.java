package rozapp.roz.app.videocall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.pwittchen.swipe.library.rx2.Swipe;
import com.github.pwittchen.swipe.library.rx2.SwipeListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Ack;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.CategoryAdabter;
import rozapp.roz.app.adabters.ChatGiftsAdabter;
import rozapp.roz.app.adabters.VideoChatAdaber;
import rozapp.roz.app.events.CallClosedEvent;
import rozapp.roz.app.events.CallEndedEvent;
import rozapp.roz.app.events.CallRequestClosedEvent;
import rozapp.roz.app.events.ToggleButtons;
import rozapp.roz.app.events.VideoGiftEvent;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.home.HomeActivity;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ChatCategory;
import rozapp.roz.app.models.ChatGift;
import rozapp.roz.app.models.GiftMessage;
import rozapp.roz.app.models.PeerConnectedEvent;
import rozapp.roz.app.models.VideoChatMessage;
import rozapp.roz.app.serve.ChatApplication;
import rozapp.roz.app.setting.PlansActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReciveVideoCall extends AppCompatActivity {

    @BindView(R.id.target_user_image)
    CircleImageView target_user_image;

    @BindView(R.id.target_name_tv)
    TextView target_name_tv;
    @BindView(R.id.call_status_tv)
    TextView call_status_tv;
    @BindView(R.id.end_call_web)
    RelativeLayout end_call_web;
    @BindView(R.id.btn_accept)
    RelativeLayout btn_accept;
    @BindView(R.id.toggle_camera)
    RelativeLayout toggle_camera;
    @BindView(R.id.btn_attach)
    RelativeLayout btn_attach;

    @BindView(R.id.gift_anim)
    LinearLayout gift_anim;
    @BindView(R.id.gift_img)
    ImageView gift_img;
    @BindView(R.id.gift_icon)
    ImageView gift_icon;
    @BindView(R.id.text_coins)
    TextView text_coins;
    @BindView(R.id.gift_state)
    TextView gift_state;
    @BindView(R.id.mute)
    RelativeLayout mute;
    @BindView(R.id.private_call)
    RelativeLayout private_call;
    @BindView(R.id.web_screen)
    RelativeLayout web_screen;
    @BindView(R.id.btn_reject)
    RelativeLayout btn_reject;
    @BindView(R.id.android_page)
    ConstraintLayout android_page;

    @BindView(R.id.web_view)
    WebView web_view;
    @BindView(R.id.video_icon)
    ImageView video_icon;
    @BindView(R.id.mic_icon)
    ImageView mic_icon;



    @BindView(R.id.chat_bar)
    LinearLayout chat_bar;
    @BindView(R.id.ineat)
    LinearLayout ineat;
    @BindView(R.id.ed_message)
    EditText ed_message;
    @BindView(R.id.send_btn)
    ImageView send_btn;
    @BindView(R.id.btn_toggle_chat)
    RelativeLayout btn_toggle_chat;
    @BindView(R.id.rv_chat)
    RecyclerView rv_chat;

    boolean toggle_chat=false;
    private List<VideoChatMessage> messages;
    private VideoChatAdaber adapter;






    private String target_id;
    private String target_name;
    private String target_image;

    private AuthResponse authResponse;

    // audio

    private int requestcode = 1;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
    };

    boolean is_audio=true;
    boolean is_video=true;
    boolean request_closet=false;
    boolean peer_connect=false;

    private List<ChatCategory> categories;
    private List<ChatGift> gifts;
    private ChatGiftsAdabter giftsAdabter;
    private CategoryAdabter categoryAdabter;
    RecyclerView rv_cats;
    RecyclerView rv_gifts;
    BottomSheetDialog bottomSheetDialog;
    Animation animation;
    boolean is_call_ended=false;
    boolean no_enogh_coins=false;

    String last_gift_count="0";
    TextView tv_coins_count;

    boolean element=true;

    MediaPlayer mediaPlayer;

    Swipe swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_recive_video_call);
        ButterKnife.bind(this);
        animation = AnimationUtils.loadAnimation(this,R.anim.bounce);
        btn_accept.startAnimation(animation);
        gift_icon.startAnimation(animation);
        initData();
        setupWebView();
        if (!hasPermissions(this, PERMISSIONS)) {   askPermissions(); }
        clickListener();
        getCatsAndGifts();
        giftsControl();


        ring();

        ChatApplication.connectTOSocket(authResponse.getUser().getId()+"");


        messages=new ArrayList<>();
        adapter=new VideoChatAdaber(this,messages);
        rv_chat.setLayoutManager(new LinearLayoutManager(this));
        rv_chat.setAdapter(adapter);

        btn_toggle_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggle_chat){
                    rv_chat.setVisibility(View.GONE);
                    chat_bar.setVisibility(View.GONE);

                }else{
                    rv_chat.setVisibility(View.VISIBLE);
                    chat_bar.setVisibility(View.VISIBLE);
                }
                toggle_chat=!toggle_chat;
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message= ed_message.getText().toString();
                if(message.trim().equals("")){
                    return;
                }
                JSONObject jsonObject= new JSONObject();
                try {
                    jsonObject.put("from_id",authResponse.getUser().getId()+"");
                    jsonObject.put("from_name",authResponse.getUser().getName()+"");
                    jsonObject.put("to_id",target_id+"");
                    jsonObject.put("to_name",target_name+"");
                    jsonObject.put("message",message+"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ed_message.setText("");
                ChatApplication.getSocket().emit("video-chat-message",jsonObject);
            }
        });


        swipe = new Swipe();
        swipe.setListener(new SwipeListener() {

            @Override
            public void onSwipingLeft(MotionEvent event) {

            }

            @Override
            public boolean onSwipedLeft(MotionEvent event) {
                ineat.setVisibility(View.GONE);
                rv_chat.setVisibility(View.GONE);
                chat_bar.setVisibility(View.GONE);
                btn_toggle_chat.setVisibility(View.GONE);
                return false;
            }

            @Override
            public void onSwipingRight(MotionEvent event) {

            }

            @Override
            public boolean onSwipedRight(MotionEvent event) {
                ineat.setVisibility(View.VISIBLE);
                rv_chat.setVisibility(View.VISIBLE);
                chat_bar.setVisibility(View.VISIBLE);
                btn_toggle_chat.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public void onSwipingUp(MotionEvent event) {

            }

            @Override
            public boolean onSwipedUp(MotionEvent event) {

                return false;
            }

            @Override
            public void onSwipingDown(MotionEvent event) {

            }

            @Override
            public boolean onSwipedDown(MotionEvent event) {

                return false;
            }
        });

    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        swipe.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    private void ring(){
        try {
            mediaPlayer = new MediaPlayer();

            AssetFileDescriptor descriptor = ReciveVideoCall.this.getAssets().openFd("ring.mpeg");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setVolume(1f, 1f);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCatsAndGifts() {

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getCategoriesWithGifts().enqueue(new Callback<List<ChatCategory>>() {
            @Override
            public void onResponse(Call<List<ChatCategory>> call, Response<List<ChatCategory>> response) {
                if(response.code()==200){
                    for(ChatCategory cat:response.body()){
                        categories.add(cat);
                    }
                    for(ChatGift gift:response.body().get(0).getGifts()){
                        gifts.add(gift);
                    }
                    categoryAdabter.notifyDataSetChanged();
                    giftsAdabter.notifyDataSetChanged();
                    bottomSheetDialog.findViewById(R.id.rcs_id).setVisibility(View.VISIBLE);
                    bottomSheetDialog.findViewById(R.id.pp_gifts).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<ChatCategory>> call, Throwable t) {

            }
        });

    }
    private void giftsControl() {


        if (!authResponse.getUser().getType().equals("user")){
            btn_attach.setVisibility(View.GONE);
        }

        gifts=new ArrayList<>();
        categories=new ArrayList<>();
        giftsAdabter=new ChatGiftsAdabter(ReciveVideoCall.this,gifts);
        categoryAdabter=new CategoryAdabter(ReciveVideoCall.this,categories);

        LinearLayoutManager categorymanager
                = new LinearLayoutManager(ReciveVideoCall.this, LinearLayoutManager.HORIZONTAL, false);

        bottomSheetDialog=new BottomSheetDialog(ReciveVideoCall.this,R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet);

        tv_coins_count= (TextView) bottomSheetDialog.findViewById(R.id.coins_count_tv);
        tv_coins_count.setText(authResponse.getUser().getCoins());
        bottomSheetDialog.findViewById(R.id.recharge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ReciveVideoCall.this, PlansActivity.class));
            }
        });
        rv_cats= (RecyclerView) bottomSheetDialog.findViewById(R.id.cat_rv);
        rv_cats.setLayoutManager(categorymanager);
        rv_cats.setAdapter(categoryAdabter);

        rv_gifts= (RecyclerView) bottomSheetDialog.findViewById(R.id.gifts_rv);
        rv_gifts.setLayoutManager(new GridLayoutManager(ReciveVideoCall.this,4));
        rv_gifts.setAdapter(giftsAdabter);

        btn_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(categories.size()>0){
                    bottomSheetDialog.findViewById(R.id.rcs_id).setVisibility(View.VISIBLE);
                    bottomSheetDialog.findViewById(R.id.pp_gifts).setVisibility(View.GONE);
                }else{
                    bottomSheetDialog.findViewById(R.id.rcs_id).setVisibility(View.GONE);
                    bottomSheetDialog.findViewById(R.id.pp_gifts).setVisibility(View.VISIBLE);
                }
                bottomSheetDialog.show();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatGift(ChatGift gift) throws JSONException {

        bottomSheetDialog.dismiss();

        last_gift_count=gift.getCoins()+"";

        JSONObject giftObject=new JSONObject();
        giftObject.put("id",gift.getId());
        giftObject.put("user_id",gift.getUserId());
        giftObject.put("category_id",gift.getCategoryId());
        giftObject.put("name",gift.getName());
        giftObject.put("image",gift.getImage());
        giftObject.put("coins",gift.getCoins());
        giftObject.put("created_at",gift.getCreatedAt());
        giftObject.put("updated_at",gift.getUpdatedAt());

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("from_user",authResponse.getUser().getId()+"");
        jsonObject.put("to_user",target_id+"");
        jsonObject.put("payload",giftObject.toString());
        jsonObject.put("type","gift");

        ChatApplication.getSocket().emit("video-gift",jsonObject,new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject dataobject = (JSONObject) args[0];
                String msg,status;
                try {
                    msg= dataobject.getString("message");
                    status=   dataobject.getString("status");
                    if(status.equals("no")){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                DynamicToast.makeError(ReciveVideoCall.this,msg).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void setupWebView() {

        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setMediaPlaybackRequiresUserGesture(false);
        web_view.getSettings().setDomStorageEnabled(true);
        web_view.getSettings().setAllowFileAccessFromFileURLs(true);
        web_view.getSettings().setAllowUniversalAccessFromFileURLs(true);
        web_view.addJavascriptInterface(new WebviewInterface(this), "Interface");
        web_view.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        String[] PERMISSIONS = {
                                PermissionRequest.RESOURCE_AUDIO_CAPTURE,
                                PermissionRequest.RESOURCE_VIDEO_CAPTURE
                        };
                        request.grant(PERMISSIONS);
                    }
                });
            }
        });

        initWebView();
        Constants.incall=true;
    }

    public class WebviewInterface {
        Context context;
        WebviewInterface(Context context){
            this.context=context;
        }

        @JavascriptInterface
        public void connected(String val) {
            EventBus.getDefault().post(new PeerConnectedEvent("event"));
        }
        @JavascriptInterface
        public void toggleButtons() {
            EventBus.getDefault().post(new ToggleButtons());
        }
    }
    private void initWebView() {
        String filePath  = "file:android_asset/recive.html";

        web_view.loadUrl(filePath);
        web_view.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializePeer();
              callJavascriptFunction("javascript:setBackground(\""+Constants.Image_URL+target_image+"\")");
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallEndedEvent(CallEndedEvent event) {
        if(event.getRequester_id()==authResponse.getUser().getId() && event.getRequested_id()==Integer.parseInt(target_id)){
            is_call_ended=true;
            DynamicToast.makeError(ReciveVideoCall.this, " call ended due to coins").show();
            finish();
        }
        if(event.getRequester_id()==Integer.parseInt(target_id)  && event.getRequested_id()==authResponse.getUser().getId()){
            is_call_ended=true;
            DynamicToast.makeError(ReciveVideoCall.this, " call ended due to coins").show();
            finish();
        }
    }

    private void clickListener() {


        toggle_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callJavascriptFunction("javascript:toggleCamera(\""+target_id+"\")");
            }
        });

        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaPlayer.stop();
                if(!peer_connect){
                    DynamicToast.makeError(ReciveVideoCall.this,"waiting to connect").show();
                    return;
                }
                Constants.is_stream=true;
                android_page.setVisibility(View.GONE);
                web_screen.setVisibility(View.VISIBLE);



                JSONObject job=new JSONObject();

                JSONObject job_requester=new JSONObject();

                JSONObject jsonObject =new JSONObject();
                try {

                    job.put("id",authResponse.getUser().getId());
                    job.put("name",authResponse.getUser().getName());

                    job_requester.put("id",target_id);
                    job_requester.put("name",target_name);

                    jsonObject.put("requested_id",authResponse.getUser().getId());
                    jsonObject.put("requester_id",Integer.parseInt(target_id));
                    jsonObject.put("requested",job);
                    jsonObject.put("requester",job_requester);

                    ChatApplication.getSocket().emit("accept-call",jsonObject );

                } catch (JSONException e) {  e.printStackTrace();  }
            }
        });

        end_call_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                finish();

            }
        });

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_audio=!is_audio;
                mic_icon.setImageResource( is_audio? R.drawable.volume_up : R.drawable.vlume_off );
                callJavascriptFunction("javascript:toggleAudio(\""+is_audio+"\")");

            }
        });

        video_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_video=!is_video;
                video_icon.setImageResource( is_video? R.drawable.video_on : R.drawable.video_of );
                callJavascriptFunction("javascript:toggleVideo(\""+is_video+"\")");

            }
        });
    }
    private void initializePeer() {
        callJavascriptFunction("javascript:init(\""+authResponse.getUser().getId()+"\")");
    }
    private void callJavascriptFunction( String functionString) {
        web_view.evaluateJavascript(functionString, null);
    }
    private void initData(){

        authResponse=new CallData(this).getAuthResponse();
        target_id=getIntent().getStringExtra("target_id");
        target_name=getIntent().getStringExtra("target_name");
        target_image=getIntent().getStringExtra("target_image");

        target_name_tv.setText(target_name);
        call_status_tv.setText("incoming video call from"+ target_name+"\nwaiting to connect...");

        Picasso.with(this).load(Constants.Image_URL+target_image).into(target_user_image);

    }

    void permission() {

        if (ContextCompat.checkSelfPermission(ReciveVideoCall.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Toast.makeText(CallActivity.this, "You already granted the permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        if (ContextCompat.checkSelfPermission(ReciveVideoCall.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            //  Toast.makeText(CallActivity.this, "You already granted the permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 2);
        }
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
    public void onMessageEvent(PeerConnectedEvent event) {
        call_status_tv.setText("incomming call from "+target_name);
        peer_connect=true;
        Constants.incall=true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToggleButtonsEvent(ToggleButtons event) {

//        if(element){
//            end_call_web.setVisibility(View.GONE);
//            mute.setVisibility(View.GONE);
//            private_call.setVisibility(View.GONE);
//        }else{
//            end_call_web.setVisibility(View.VISIBLE);
//            mute.setVisibility(View.VISIBLE);
//            private_call.setVisibility(View.VISIBLE);
//        }
//        element = !element;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallClosedEvent(CallClosedEvent event) {
        if(event.getRequester_id()==authResponse.getUser().getId() && event.getRequested_id()==Integer.parseInt(target_id)){
            if(event.getCloser().getId() == authResponse.getUser().getId()){

            } else{
                DynamicToast.makeError(ReciveVideoCall.this,event.getCloser().getName()+" close the video call").show();
                finish();
            }
        }
        if(event.getRequester_id()==Integer.parseInt(target_id)  && event.getRequested_id()==authResponse.getUser().getId()){
            if(event.getCloser().getId() == authResponse.getUser().getId()){

            } else{
                DynamicToast.makeError(ReciveVideoCall.this,event.getCloser().getName()+" close the video call").show();
                finish();
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCallRequestClosedEvent(CallRequestClosedEvent event) {

        if(event.getRequester_id()==Integer.parseInt(target_id)  && event.getRequested_id()==authResponse.getUser().getId()){
            request_closet=true;
            DynamicToast.makeError(ReciveVideoCall.this, " call request closed").show();
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoChatMessage(VideoChatMessage event) {
        if(event.getFrom_id().equals(authResponse.getUser().getId()+"") && event.getTo_id().equals(target_id)){
            messages.add(event);
            adapter.notifyDataSetChanged();
        }
        if(event.getTo_id().equals(authResponse.getUser().getId()+"") && event.getFrom_id().equals(target_id)){
            messages.add(event);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        web_view.loadUrl("about:blank");
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        if(is_call_ended){
            Constants.incall=false;
            Constants.is_stream=false;
            return;
        }


        if (request_closet){
            Constants.incall=false;
            Constants.is_stream=false;
        }else{

            if(Constants.incall && !Constants.is_stream){

                // reject video call  requester_id requested_id requested.name requested.id

                JSONObject job=new JSONObject();

                JSONObject jsonObject =new JSONObject();
                try {

                    job.put("id",authResponse.getUser().getId());
                    job.put("name",authResponse.getUser().getName());
                    jsonObject.put("requested_id",authResponse.getUser().getId());
                    jsonObject.put("requester_id",Integer.parseInt(target_id));
                    jsonObject.put("requested",job);
                    ChatApplication.getSocket().emit("reject-call",jsonObject );

                } catch (JSONException e) {  e.printStackTrace();  }


            }


            Constants.incall=false;
            if(Constants.is_stream){

                // close video call

                JSONObject job=new JSONObject();

                JSONObject jsonObject =new JSONObject();
                try {

                    job.put("id",authResponse.getUser().getId());
                    job.put("name",authResponse.getUser().getName());
                    jsonObject.put("requested_id",authResponse.getUser().getId());
                    jsonObject.put("requester_id",Integer.parseInt(target_id));
                    jsonObject.put("closer",job);
                    ChatApplication.getSocket().emit("call-closed",jsonObject );

                } catch (JSONException e) {  e.printStackTrace();  }
            }
            Constants.is_stream=false;
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatCategoryEvent(ChatCategory event) {
        gifts.clear();
        for (ChatGift gift : event.getGifts()){
            gifts.add(gift);
        }

        for (ChatCategory cat:categories){
            if(cat.getId()==event.getId()){
                cat.setSelected("yes");
            }else{
                cat.setSelected("no");
            }
        }
        giftsAdabter.notifyDataSetChanged();
        categoryAdabter.notifyDataSetChanged();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoGiftEvent(VideoGiftEvent event){

        if(event.getFrom_user() == authResponse.getUser().getId()){
            KhateebPattern.playAssetSound(ReciveVideoCall.this,"notify_coin.wav");

            gift_state.setText("your gift sent successfully");
            text_coins.setText(last_gift_count+" coin");

            Log.e("payload :" ,event.getPayload());
            Gson gson=new Gson();
            GiftMessage gift= gson.fromJson(event.getPayload().toString(), GiftMessage.class);


            Picasso.with(ReciveVideoCall.this).load(Constants.Image_URL_Slashed+gift.getImage()).into(gift_img);
            gift_anim.setVisibility(View.VISIBLE);
            gift_img.startAnimation(animation);
            tv_coins_count.setText(event.getCoins());
            new Handler().postDelayed(() -> {
                gift_anim.setVisibility(View.GONE);
                gift_img.clearAnimation();
            }, 3000);


        }
        if(event.getFrom_user() == Integer.parseInt(target_id) && event.getTo_user() == authResponse.getUser().getId()){

            KhateebPattern.playAssetSound(ReciveVideoCall.this,"notify_coin.wav");

                Gson gson=new Gson();
                GiftMessage gift= gson.fromJson(event.getPayload().toString(), GiftMessage.class);
                gift_state.setText(target_name+" sent you a gift");
                text_coins.setText(gift.getCoins()+" coin");
                Picasso.with(ReciveVideoCall.this).load(Constants.Image_URL_Slashed+gift.getImage()).into(gift_img);
                gift_anim.setVisibility(View.VISIBLE);
                gift_img.startAnimation(animation);

                new Handler().postDelayed(() -> {
                    gift_anim.setVisibility(View.GONE);
                    gift_img.clearAnimation();
                }, 3000);



        }


    }


}