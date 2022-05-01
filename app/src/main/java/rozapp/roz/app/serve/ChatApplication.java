package rozapp.roz.app.serve;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Constants;

import rozapp.roz.app.models.AuthResponse;

public class ChatApplication {



    public  static Socket mSocket;

    public static void connectTOSocket(String id){

        if(mSocket != null   ){
            if(mSocket.connected()){
                return;
            }
        }

        {
            {
                IO.Options options = new IO.Options();
                options.query = "token="+id+"";

                try {
                    mSocket = IO.socket(Constants.SocketServer,options);
                    mSocket.connect();
                } catch (URISyntaxException e) {}
            }
        }
    }
    public static  Socket getSocket() {
        return mSocket;
    }
}