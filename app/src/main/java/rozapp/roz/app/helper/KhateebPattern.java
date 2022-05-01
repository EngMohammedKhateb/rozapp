package rozapp.roz.app.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class KhateebPattern {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {   Manifest.permission.WRITE_EXTERNAL_STORAGE };

    public static String giveDate() {
        Date cDate = new Date();
        String fDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cDate);
        return fDate;
    }
    public static String getTimeAmPm() {
        String delegate = "hh:mm aaa";

        String currentampm= DateFormat.format(delegate, Calendar.getInstance().getTime()).toString();
        return convertToEnglishDigits(currentampm);
    }

    public static boolean isTextArabic(String s) {
        for (int i = 0; i < s.length();) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }
    public static  boolean inArray(ArrayList<String> list , String user_id){
        boolean found= false;
        for (String i: list) {
            if(i.equals(user_id)){
                found=true;
                break;
            }

        }
        return found;
    }
    public static void goToPage(Context context, Activity currentActivity, Class nextActivity){
        Intent intent=new Intent(context,nextActivity.getClass());
        context.startActivity(intent);
    }

    public static String getFromJson(String key,String params){
        JSONObject MessageGetter= null;
        String value="";
        try {
            MessageGetter = new JSONObject(params);
            value=MessageGetter.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value.toString();
    }


    public static void showToast(Context context, String Msg){
        Toast.makeText(context,Msg, Toast.LENGTH_SHORT).show();
    }
    public static void showToastLong(Context context, String Msg){
        Toast.makeText(context,Msg, Toast.LENGTH_LONG).show();
    }

    public static void openFacebookPage(Context context,String type,String id,String url){
        try {
            PackageManager pm = context.getPackageManager();
            Uri uri;
            pm.getPackageInfo("com.facebook.katana", 0);
            if(type.equals("page")){
                uri = Uri.parse("fb://page/"+id);
            }else{
                uri = Uri.parse("fb://profile/"+id);

            }

            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch(Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    public static void openFacebookProfile(String url,Context context){
        String id=url.replace("https://www.facebook.com/","");
        id=id.replace("https://www.fb.com/","");

        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/"+id));
            context.startActivity(intent);
        }catch (Exception ex){
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }



    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    public static InterfaceServices getAuthServicesInstance(final String token){
        String apiurl= Constants.API_URL.toString();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json" )
                        .addHeader("Accept", "application/json" )
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();
        Retrofit retrofit=new Retrofit.Builder().client(client).baseUrl(apiurl)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        InterfaceServices service=retrofit.create(InterfaceServices.class);
        return  service;
    }

    public static InterfaceServices getAuthStripServicesInstance(final String token){

        String apiurl= Constants.STRIP_URL.toString();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/x-www-form-urlencoded" )
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();
        Retrofit retrofit=new Retrofit.Builder().client(client).baseUrl(apiurl)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        InterfaceServices service=retrofit.create(InterfaceServices.class);
        return  service;
    }
    public static void playAssetSound(Context context,String filename ) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();

            AssetFileDescriptor descriptor = context.getAssets().openFd(filename);
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

    public static InterfaceServices getServicesInstance(){
        String apiurl= Constants.API_URL.toString();
        Retrofit retrofit=new Retrofit.Builder().client(SSL_Sucess.getUnsafeOkHttpClient().build()).baseUrl(apiurl)
                .addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        InterfaceServices service=retrofit.create(InterfaceServices.class);
        return  service;
    }
    public static String getVideoIdFromYoutubeUrl(String url){
        String videoId = null;
        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()){
            videoId = matcher.group(1);
        }
        return videoId;
    }

    public static InterfaceServices getImageService(){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        String apiurl= Constants.API_URL.toString();
        Retrofit retrofit=new Retrofit.Builder().client(SSL_Sucess.getUnsafeOkHttpClient().build()).baseUrl(apiurl)
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        InterfaceServices service=retrofit.create(InterfaceServices.class);
        return  service;
    }




    public static String RandomGenerator(){
        Random rand = new Random();
        int random = rand.nextInt(5000000) + 300000;
        return random+"";
    }
    public static int intRandomGenerator(){
        Random rand = new Random();
        int random = rand.nextInt(5000000) + 300000;
        return random;
    }

    public static String fromBase64(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String toBase64(String message) {
        byte[] data;
        try {
            data = message.getBytes("UTF-8");
            String base64Sms = Base64.encodeToString(data, Base64.DEFAULT);
            return base64Sms;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String convertToEnglishDigits(String value) {
        String newValue = value.replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5")
                .replace("٦", "6").replace("٧", "7").replace("٨", "8").replace("٩", "9").replace("٠", "0")
                .replace("م", "pm").replace("ص", "am") ;
        return newValue;
    }


    public static boolean isProbablyArabic(String text){
        String textWithoutSpace = text.trim().replaceAll(" ",""); //to ignore whitepace
        for (int i = 0; i < textWithoutSpace.length()-15;) {
            int c = textWithoutSpace.codePointAt(i);
            //range of arabic chars/symbols is from 0x0600 to 0x06ff
            //the arabic letter 'لا' is special case having the range from 0xFE70 to 0xFEFF
            if (c >= 0x0600 && c <=0x06FF || (c >= 0xFE70 && c<=0xFEFF))
                i += Character.charCount(c);
            else
                return false;

        }
        return true;
    }
    public static boolean isEnglish(String text) {

        boolean onlyEnglish = false;

        for (char character : text.toCharArray()) {

            if (Character.UnicodeBlock.of(character) == Character.UnicodeBlock.BASIC_LATIN
                    || Character.UnicodeBlock.of(character) == Character.UnicodeBlock.LATIN_1_SUPPLEMENT
                    || Character.UnicodeBlock.of(character) == Character.UnicodeBlock.LATIN_EXTENDED_A
                    || Character.UnicodeBlock.of(character) == Character.UnicodeBlock.GENERAL_PUNCTUATION) {

                onlyEnglish = true;
            } else {

                onlyEnglish = false;
            }
        }

        return onlyEnglish;
    }

    public static String getTimeAgo(String dataDate) {

        String convTime = null;

        String prefix = "";
        String suffix = "Ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                if(second<0)second=1;
                convTime = second + " Seconds " + suffix;
            } else if (minute < 60) {
                if(minute<0)minute=0;
                convTime = minute + " Minutes "+suffix;
            } else if (hour < 24) {
                if(hour<0)hour=0;
                convTime = hour + " Hours "+suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    if(day<0)day=0;
                    convTime = (day / 360) + " Years " + suffix;
                } else if (day > 30) {
                    if(day<0)day=0;
                    convTime = (day / 30) + " Months " + suffix;
                } else {
                    if(day<0)day=0;
                    convTime = (day / 7) + " Week " + suffix;
                }
            } else if (day < 7) {
                convTime = day+" Days "+suffix;
            }
            if(convTime.contains("Days") && day >30) {
            convTime = dataDate.toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }



        return convTime;
    }
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    public static String removeLastItemFromString(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == 'x') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public static String LaravelDate(String s){
        String d= s.substring(0,10);
        String t= s.substring(11,18);
        String def=t.substring(0,2);

        int r=Integer.parseInt(def);
        r=r+3;
        String final_t="";
        if(r<10){

           final_t="0"+r+t.substring(2,7);
        }else{
            final_t=r+t.substring(2,7);
        }
        return getTimeAgo(d+" "+final_t);

    }

    public static boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == 0)
                return true;
        }
        return false;
    }

    private String getYouTubeId(String youTubeUrl) {
        String pattern = "https?://(?:[0-9A-Z-]+\\.)?(?:youtu\\.be/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|</a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static   String getRealPathFromURI(Uri contentUri ,Context context) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }else {
            return true;
        }
    }
    public static String getPath(Uri uri,Context context)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public static String getIMEINumber(@NonNull final Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return android_id;
    }
    /////////////////single tone



}
