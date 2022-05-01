package rozapp.roz.app.helper;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class RatKiller extends Application {
    private Socket mSocket;
    private static final String URL = Constants.SocketServer;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            IO.Options options = new IO.Options();
            options.query = "token="+"4"+"";
            mSocket = IO.socket(URL,options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    public Socket getmSocket(){
        return mSocket;
    }
}
