package rozapp.roz.app.helper;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketService extends Service {
    final String TAG="SocketService";
    JSONObject user_data;
    private Socket socket;

    private final IBinder mBinder = new MyBinder();
    public class MyBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        try {
            IO.Options options = new IO.Options();
            options.query = "token="+"4"+"";
            socket = IO.socket(Constants.SocketServer,options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Log.d(TAG, "onCreate");
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d(getClass().getCanonicalName(), "Connected to server");
            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... arg0) {
                Log.d(getClass().getCanonicalName(), "Disconnected from server");
            }

        });
        socket.on("friendcall", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                Log.d(TAG, "Handling friendcall");
                try {
                    String callFrom = data.getString("from");
                    Log.d(TAG, "Call from : " + callFrom);
                } catch (JSONException e) {
                    Log.d(TAG, "friend call object cannot be parsed");
                }

            }
        });

        Log.d(TAG, "onStartCommand. Socket should be up");
        socket.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        if(extras!=null){
            try {
                user_data = new JSONObject(extras.getString("USER_DATA"));
                socket.emit("giveMeID", user_data.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return Service.START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onDestroy(){
        socket.disconnect();
        super.onDestroy();
        Log.d(TAG,"onDestroy");
    }



}