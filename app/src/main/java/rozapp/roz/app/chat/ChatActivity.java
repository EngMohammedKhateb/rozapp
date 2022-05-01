package rozapp.roz.app.chat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import okhttp3.ResponseBody;
import rozapp.roz.app.Fcm.ApiClient;
import rozapp.roz.app.Fcm.ApiInterface;
import rozapp.roz.app.Fcm.DataModel;
import rozapp.roz.app.Fcm.RootModel;
import rozapp.roz.app.R;
import rozapp.roz.app.adabters.CategoryAdabter;
import rozapp.roz.app.adabters.ChatAdabter;
import rozapp.roz.app.adabters.ChatGiftsAdabter;
import rozapp.roz.app.events.ChatMessageEvent;
import rozapp.roz.app.events.RefreshContactEvent;
import rozapp.roz.app.events.UserConnectedEvent;
import rozapp.roz.app.events.UserDisconnectedEvent;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;
import rozapp.roz.app.helper.KhateebPattern;

import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.ChatCategory;
import rozapp.roz.app.models.ChatGift;
import rozapp.roz.app.models.ChatMessage;
import rozapp.roz.app.models.Contact;
import rozapp.roz.app.profile.TargetProfileActivity;
import rozapp.roz.app.serve.ChatApplication;
import rozapp.roz.app.setting.PlansActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rozapp.roz.app.videocall.ReciveVideoCall;
import rozapp.roz.app.videocall.RequestVideoCall;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.user_image_iv)
    CircleImageView user_image;
    @BindView(R.id.user_name_tv)
    TextView user_name_tv;
    @BindView(R.id.status_dot)
    View status_dot;
    @BindView(R.id.rv_chat)
    RecyclerView rv_chat;
    @BindView(R.id.pp_chat)
    ProgressBar pp_chat;
    @BindView(R.id.ed_message)
    EditText ed_message;
    @BindView(R.id.btn_attach)
    ImageView btn_attach;
    @BindView(R.id.arrow_back)
    ImageView arrow_back;
    @BindView(R.id.send_btn)
    ImageView send_btn;

    @BindView(R.id.video_call)
    RelativeLayout video_call;

    private ChatAdabter chatAdabter;
    private List<ChatMessage> messages;
    private Contact target;
    private AuthResponse authResponse;

    private List<ChatCategory> categories;
    private List<ChatGift> gifts;

    private ChatGiftsAdabter giftsAdabter;
    private CategoryAdabter categoryAdabter;
    RecyclerView rv_cats;
    RecyclerView rv_gifts;
    BottomSheetDialog bottomSheetDialog;

    TextView tv_coins_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new CallData(getApplicationContext()).getCurrentTheme().equals("dark")){
            setTheme(R.style.DarkTheme_RozApp);
        }else{
            setTheme(R.style.Theme_RozApp);
        }


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        ui();

        ChatApplication.connectTOSocket(authResponse.getUser().getId()+"");


        getRoomMessages();
        getCatsAndGifts();

        SharedPreferences pref=getSharedPreferences("settings", Activity.MODE_PRIVATE);
        String language=pref.getString("language","en");
        if(language.equals("en")) {
            arrow_back.setImageDrawable(getDrawable(R.drawable.arrow_left));
        }else{
            arrow_back.setImageDrawable(getDrawable(R.drawable.arrow_right));
        }

        video_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoCall();
            }
        });

        if (!authResponse.getUser().getType().equals("user")){
            btn_attach.setVisibility(View.GONE);
        }
        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        gifts=new ArrayList<>();
        categories=new ArrayList<>();
        giftsAdabter=new ChatGiftsAdabter(ChatActivity.this,gifts);
        categoryAdabter=new CategoryAdabter(ChatActivity.this,categories);

        LinearLayoutManager categorymanager
                = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.HORIZONTAL, false);

        bottomSheetDialog=new BottomSheetDialog(ChatActivity.this,R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet);
        tv_coins_count=bottomSheetDialog.findViewById(R.id.coins_count_tv);
        tv_coins_count.setText(authResponse.getUser().getCoins());


        bottomSheetDialog.findViewById(R.id.recharge_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
                startActivity(new Intent(ChatActivity.this, PlansActivity.class));
            }
        });

        rv_cats= (RecyclerView) bottomSheetDialog.findViewById(R.id.cat_rv);
        rv_cats.setLayoutManager(categorymanager);
        rv_cats.setAdapter(categoryAdabter);

        rv_gifts= (RecyclerView) bottomSheetDialog.findViewById(R.id.gifts_rv);
        rv_gifts.setLayoutManager(new GridLayoutManager(ChatActivity.this,4));
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

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String message=ed_message.getText().toString();
               if(message.trim().equals(""))return;
                JSONObject jsonObject=new JSONObject();

                try {
                    jsonObject.put("from_user",authResponse.getUser().getId()+"");
                    jsonObject.put("to_user",target.getId()+"");
                    jsonObject.put("message",message+"");
                    jsonObject.put("msg_type", "message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ChatApplication.getSocket().emit("chat-message",jsonObject,new Ack() {
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
                                        DynamicToast.makeError(ChatActivity.this,msg).show();
                                    }
                                });

                            }else{
                                sendNotification();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                ed_message.setText("");
            }
        });

    }


    private void sendNotification() {

        RootModel rootModel = new RootModel("/topics/message"+target.getId(),new DataModel(authResponse.getUser().getId(),"message",authResponse.getUser().getName(),authResponse.getUser().getImage()));

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

    private void ui() {

        authResponse=new CallData(this).getAuthResponse();
        target= (Contact) getIntent().getSerializableExtra("target");

        if(target.getOnline().equals("0")){
            video_call.setVisibility(View.GONE);
        }else{
            video_call.setVisibility(View.VISIBLE);
        }
        Picasso.with(this).load(Constants.Image_URL+target.getImage()).into(user_image);
        user_name_tv.setText(target.getName());
        if(target.getOnline().equals("0")){
            status_dot.setBackground(getResources().getDrawable(R.drawable.circle_grey));
        }else{
            status_dot.setBackground(getResources().getDrawable(R.drawable.circlegreen));
        }


        messages=new ArrayList<>();
        chatAdabter=new ChatAdabter(ChatActivity.this,messages);
        rv_chat.setLayoutManager(new LinearLayoutManager(this));
        rv_chat.setAdapter(chatAdabter);

        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, TargetProfileActivity.class).putExtra("target_id",target.getId()+""));

            }
        });

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserConnectedEvent(UserConnectedEvent event) {
        if(event.getId() == target.getId()){
            video_call.setVisibility(View.VISIBLE);
            status_dot.setBackground(getResources().getDrawable(R.drawable.circlegreen));
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserDisconnectedEvent(UserDisconnectedEvent event) {
        if(event.getId() == target.getId()){
            video_call.setVisibility(View.GONE);
            status_dot.setBackground(getResources().getDrawable(R.drawable.circle_grey));
        }
    }
    private void getRoomMessages() {

        KhateebPattern.getAuthServicesInstance(authResponse.getAccessToken()).getChatMessages(target.getId()+"").enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if(response.code()==200){
                    for(ChatMessage message: response.body()){
                        messages.add(message);
                    }
                    chatAdabter.notifyDataSetChanged();
                    rv_chat.scrollToPosition(chatAdabter.getItemCount()-1);
                }
                pp_chat.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                pp_chat.setVisibility(View.GONE);
            }
        });

    }

    private void videoCall(){


        startActivity(new Intent(ChatActivity.this, RequestVideoCall.class).putExtra("target_id",target.getId()).putExtra("target_name",target.getName()).putExtra("target_image",target.getImage()));

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().post(new RefreshContactEvent("asd"));
        EventBus.getDefault().unregister(this);
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
    public void onChatMessageEvent(ChatMessageEvent message) {

        if(message.getFromUser()==authResponse.getUser().getId() && message.getToUser()==target.getId()){
            messages.add(new ChatMessage(message.getId(),message.getFromUser(),message.getToUser(),message.getRoomId(),message.getMessage(),message.getMsgType(),message.getSrc(),message.getStatue(),message.getDeletedAt(),message.getCreatedAt(),message.getUpdatedAt(),target.getName()));
        }
        if(message.getFromUser()==target.getId() && message.getToUser()==authResponse.getUser().getId()){
            messages.add(new ChatMessage(message.getId(),message.getFromUser(),message.getToUser(),message.getRoomId(),message.getMessage(),message.getMsgType(),message.getSrc(),message.getStatue(),message.getDeletedAt(),message.getCreatedAt(),message.getUpdatedAt(),target.getName()));

            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("room_id",message.getRoomId());
                jsonObject.put("from_user",message.getFromUser());
                ChatApplication.getSocket().emit("message_readed",jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

                MediaPlayer mediaPlayer;
                try {
                    mediaPlayer = new MediaPlayer();

                    AssetFileDescriptor descriptor = ChatActivity.this.getAssets().openFd("message.mpeg");
                    mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();

                    mediaPlayer.prepare();
                    mediaPlayer.setVolume(1f, 1f);
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
        tv_coins_count.setText(message.getTyper().getCoins()+"" );
        chatAdabter.notifyDataSetChanged();
        rv_chat.scrollToPosition(chatAdabter.getItemCount()-1);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChatGift(ChatGift gift) throws JSONException {

        bottomSheetDialog.dismiss();

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
        jsonObject.put("to_user",target.getId()+"");
        jsonObject.put("message",giftObject.toString());
        jsonObject.put("msg_type","gift");

        ChatApplication.getSocket().emit("chat-message",jsonObject,new Ack() {
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
                                DynamicToast.makeError(ChatActivity.this,msg).show();
                            }
                        });
                  }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



}


