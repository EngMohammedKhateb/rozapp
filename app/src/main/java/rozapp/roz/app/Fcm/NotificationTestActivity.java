package rozapp.roz.app.Fcm;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import rozapp.roz.app.R;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.models.AuthResponse;

public class NotificationTestActivity extends AppCompatActivity {

    @BindView(R.id.notify)
    Button notify;

    private AuthResponse authResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_test);
        ButterKnife.bind(this);



      //  authResponse=new CallData(this).getAuthResponse();
        FirebaseMessaging.getInstance().subscribeToTopic("info");
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotificationToUser("/topics/info");
            }
        });




    }

    private void sendNotificationToUser(String to) {
        RootModel rootModel = new RootModel(to,new DataModel(3,"as","kl","as"));

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


}