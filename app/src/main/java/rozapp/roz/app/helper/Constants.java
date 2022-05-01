package rozapp.roz.app.helper;

import java.util.ArrayList;
import java.util.List;

import rozapp.roz.app.models.Media;

public class Constants {

//    public static final String API_URL="http://192.168.1.103:80/api/android/";
//    public static final String Image_URL="http://192.168.1.103:80";
//    public static final String Image_URL_Slashed="http://192.168.1.103:80/";
//    public static final String Site_URL="http://192.168.1.103:80/";


    public static final String API_URL="https://roz-app.com/api/android/";
    public static final String Image_URL="https://roz-app.com";
    public static final String Image_URL_Slashed="https://roz-app.com/";
    public static final String Site_URL="https://roz-app.com/";

    public static final String STRIP_URL="https://api.stripe.com/";

    public static boolean incall =false;

    public static boolean is_stream=false;

    //  public static final String SocketServer="http://192.168.1.103:4000";
     public static final String SocketServer="http://45.85.147.38:4000";

     public static List<Media> images=new ArrayList<>();
}
