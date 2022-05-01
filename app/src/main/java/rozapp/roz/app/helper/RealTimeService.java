package rozapp.roz.app.helper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rozapp.roz.app.models.AuthResponse;


public class RealTimeService extends Service{

    public static Socket socket;
    private AuthResponse authResponse;
    Socket mSocket;
    public RealTimeService(Context context) {
        authResponse=new CallData(context).getAuthResponse();
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {

        {
            IO.Options options = new IO.Options();
            options.query = "token="+"4"+"";

            try {
                mSocket = IO.socket(Constants.SocketServer,options);
            } catch (URISyntaxException e) {}
        }
     //   socket_server();
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //   Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//     Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
    }

    private void socket_server(){

        mSocket.on("connect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("connect : " ,"connected successfully");
            }
        });
        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("connect error : " ,"connected error");
            }
        });
        mSocket.connect();


    }
}
