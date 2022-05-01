package rozapp.roz.app.Fcm;



import android.app.ActivityManager;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;


import java.io.Serializable;
import java.util.List;

import rozapp.roz.app.R;
import rozapp.roz.app.chat.ChatActivity;
import rozapp.roz.app.helper.CallData;
import rozapp.roz.app.helper.Database;
import rozapp.roz.app.helper.KhateebPattern;
import rozapp.roz.app.home.HomeActivity;
import rozapp.roz.app.models.AuthResponse;
import rozapp.roz.app.models.Contact;
import rozapp.roz.app.profile.TargetProfileActivity;


public class FirebaseService extends FirebaseMessagingService {


    private AuthResponse authResponse;
    private String mid;
    private Database db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        /**
         * be sure that data size > 0
         */

        db=new Database(this);

        Log.e("received","");

        if(!db.isLogin()){
            return;
        }
        authResponse= new CallData(this).getAuthResponse();
        Log.e("id",remoteMessage.getData().get("id"));
        Log.e("name",remoteMessage.getData().get("name"));
        Log.e("image",remoteMessage.getData().get("image"));
        Log.e("type",remoteMessage.getData().get("type"));

        DataModel model =new DataModel(Integer.parseInt(remoteMessage.getData().get("id")),remoteMessage.getData().get("type"),remoteMessage.getData().get("name"),remoteMessage.getData().get("image"));


        if(model.type.equals("message")){

            Contact contact=new Contact(model.getId(),model.getName(),"user",20,"1",20,model.image,"not available");

            if(isAppIsInBackground(getApplicationContext())){
                if (Build.VERSION.SDK_INT >= 25) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("from", "fcm");
                    intent.putExtra("target",(Serializable)contact);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    NotificationHelper noti = new NotificationHelper(this);

                    Notification.Builder nb = noti.getNotification1("Roz App", "you have new messages from "+model.name, pendingIntent);
                    nb.setStyle(new Notification.BigTextStyle().bigText("you have new messages from "+model.name));
                    Uri path = Uri.parse("android.resource://rozapp.roz.app/" + R.raw.message);
                    nb.setSound(path);
                    noti.notify(model.getId()*5, nb);
                }
                else {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("from", "fcm");
                    intent.putExtra("target", (Serializable) contact);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);
                    String channelId = getString(R.string.default_notification_channel_id);
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    Uri path = Uri.parse("android.resource://rozapp.roz.app/" + R.raw.message);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("you have new messages from " + model.name))
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Roz App")
                                    .setContentText("you have new messages from " + model.name)
                                    .setAutoCancel(true)
                                    .setSound(path)
                                    .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(channelId,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }
                    notificationManager.notify(model.getId()*5, notificationBuilder.build());
                }
            }


        }

        if(model.type.equals("call")){



            if(isAppIsInBackground(getApplicationContext())){
                if (Build.VERSION.SDK_INT >= 25) {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("target_id", model.getId()+"");
                    intent.putExtra("target_image", model.getImage());
                    intent.putExtra("target_name", model.getName());

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    NotificationHelper noti = new NotificationHelper(this);
                    Notification.Builder nb = noti.getNotification1("Roz App", "incoming call from "+model.name, pendingIntent);
                    nb.setStyle(new Notification.BigTextStyle().bigText("incoming call from "+model.name));
                    Uri path = Uri.parse("android.resource://rozapp.roz.app/" + R.raw.message);
                    nb.setSound(path);
                    noti.notify(model.getId()*10, nb);
                }
                else {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("target_id", model.getId()+"");
                    intent.putExtra("target_image", model.getImage());
                    intent.putExtra("target_name", model.getName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);
                    String channelId = getString(R.string.default_notification_channel_id);
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Uri path = Uri.parse("android.resource://rozapp.roz.app/" + R.raw.message);


                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText("incoming call from " + model.name))
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Roz App")
                                    .setContentText("incoming call from " + model.name)
                                    .setAutoCancel(true)
                                    .setSound(path)
                                    .setContentIntent(pendingIntent);

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(channelId,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }
                    notificationManager.notify(model.getId()*10, notificationBuilder.build());
                }
            }

        }

        if(model.type.equals("follow")){

                if (Build.VERSION.SDK_INT >= 25) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("target_id", model.getId()+"");
                    intent.putExtra("from", "follow");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    NotificationHelper noti = new NotificationHelper(this);
                    Notification.Builder nb = noti.getNotification1("Roz App", model.name+" follow you", pendingIntent);
                    nb.setStyle(new Notification.BigTextStyle().bigText(model.name+" follow you"));
                    Uri path = Uri.parse("android.resource://rozapp.roz.app/" + R.raw.message);
                    nb.setSound(path);
                    noti.notify(model.getId()*12, nb);
                }
                else {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("target_id", model.getId()+"");
                    intent.putExtra("from", "follow");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);
                    String channelId = getString(R.string.default_notification_channel_id);
                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Uri path = Uri.parse("android.resource://rozapp.roz.app/" + R.raw.message);
                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(this)
                                    .setStyle(new NotificationCompat.BigTextStyle().bigText(model.name+" follow you"))
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Roz App")
                                    .setContentText(model.name+" follow you")
                                    .setAutoCancel(true)
                                    .setSound(path)
                                    .setContentIntent(pendingIntent);
                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(channelId,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }
                    notificationManager.notify(model.getId()*12, notificationBuilder.build());
                }

        }


        }


    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        }

        return isInBackground;
    }


}


