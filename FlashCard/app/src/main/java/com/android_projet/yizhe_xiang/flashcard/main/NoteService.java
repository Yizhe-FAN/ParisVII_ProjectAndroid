package com.android_projet.yizhe_xiang.flashcard.main;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.android_projet.yizhe_xiang.flashcard.R;
import com.android_projet.yizhe_xiang.flashcard.database.FlashCardProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteService extends Service {
    int intervalle;


    public NoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intervalle = getSharedPreferences("myFlashCard", Activity.MODE_PRIVATE).getInt("intervalle",7);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new NoteSender().execute();
        return START_STICKY;
    }

    class NoteSender extends AsyncTask<String,Integer,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            stopSelf();
        }

        @Override
        protected String doInBackground(String... params) {
            Uri.Builder builder = new Uri.Builder();
            Uri uri = builder.scheme("content").authority(FlashCardProvider.authority).appendPath("allgames").build();
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            int number = 0;
            while(cursor.moveToNext()){
                String lastUseSql = cursor.getString(3);
                try {
                    Date lastUse = new SimpleDateFormat("yyyy-MM-dd").parse(lastUseSql);
                    Date today = new Date();
                    int temp = (int)((today.getTime() - lastUse.getTime())/86400000);
                    if (temp >= intervalle){
                        Notification notification = new NotificationCompat.Builder(getApplicationContext())
/*                                .setStyle(new NotificationCompat.InboxStyle()
                                        .setBigContentTitle("Don't Forget")
                                        .addLine("You have not played")
                                        .addLine(cursor.getString(1))
                                        .addLine("For "+temp+" days."))
*/
                                .setContentTitle("Don't Forget !")
                                .setContentText(cursor.getString(1)+" "+temp+" days not played.")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setVisibility(Notification.VISIBILITY_PUBLIC)
                                .build();
                        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(number,notification);
                        number++;
                    }


                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            return null;
        }
    }


}
