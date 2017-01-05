package com.android_projet.yizhe_xiang.flashcard.manage;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android_projet.yizhe_xiang.flashcard.R;
import com.android_projet.yizhe_xiang.flashcard.manage.GameManageFragment;
import com.android_projet.yizhe_xiang.flashcard.manage.InstallGameTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class InstallService extends Service {

    DownloadManager downloadManager;
    BroadcastReceiver receiver;

    public InstallService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final long idDownload = intent.getLongExtra("idDownload",-1);
        final String myGameName = intent.getStringExtra("myGameName");
        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);

        Log.d("---install service---", "onStartCommand: id-> "+idDownload+" GameName-> "+myGameName);

        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (idDownload == reference) {
                    DownloadManager.Query question = new DownloadManager.Query();
                    question.setFilterById(idDownload);
                    Cursor cur= downloadManager.query(question);
                    if(cur.moveToNext()){
                        int fileNameIndex=cur.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_FILENAME);
                        String fileName=cur.getString(fileNameIndex);
                        new InstallGame(myGameName).execute(fileName);
                        unregisterReceiver(receiver);
                    }
                    cur.close();
                    stopSelf();
                }
            }
        };

        registerReceiver(receiver,intentFilter);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("---InstallService---", "onDestroy: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class InstallGame extends AsyncTask<String,Integer, String> {
        private String myGameName;

        public InstallGame(String gameName){
            myGameName = gameName;
        }

        @Override
        protected String doInBackground(String... params) {
            File zipFile=new File(params[0]);
            String gameDir= Environment.getExternalStorageDirectory()+"/FlashCard/"+myGameName;
            int resultUnZip=-1;
            try{
                resultUnZip= InstallGameTool.upZipFile(zipFile,gameDir);
                if (resultUnZip==0){
                    Log.d("Unzip===","AfterUnzip");
                    String txtPath=gameDir+"/"+myGameName+".txt";
                    File fileTxt=new File(txtPath);
                    if (!fileTxt.exists()){
                        Log.d("txtGame","txtIsntExist");
                    }else{
                        BufferedReader raf=null;
                        raf=new BufferedReader(new InputStreamReader(new FileInputStream(fileTxt), "utf-8"));
                        String mySQL="";
                        String str="";
                        while((str=raf.readLine())!=null){
                            mySQL+=str;
                        }
                        Log.d("AllSQl2",mySQL);
                        int resultInstallGame=new InstallGameTool(getApplicationContext()).installInternetGame(mySQL);
                        if (resultInstallGame==0){
                            return "success";

                        }else if (resultInstallGame==1){
                            return "deja";

                        }
                        Log.d("==========","finish???");

                        if (raf!=null) {
                            raf.close();
                        }
                    }
                }else {
                    Log.d("Unzip===","UnzipFailed");
                }
            }catch (Exception e){
                Log.d("unzipException",e.toString());
            }
            return "";
        }

        @Override
        protected void onPostExecute(String bmp) {
            if (bmp.equals("success")){
                Notification notification = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("Install Finished")
                        .setContentText("Install Game: "+myGameName+" Successfully!")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .build();
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1000,notification);

                Intent mIntent = new Intent();
                mIntent.setAction("download");
                mIntent.putExtra("myGameName", myGameName);
                mIntent.putExtra("tag",1);
                sendBroadcast(mIntent);

            }else if (bmp.equals("deja")){
                Notification notification = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("Already Installed")
                        .setContentText("Game: "+myGameName+" Already Installed!")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .build();
                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1001,notification);


                Intent mIntent = new Intent();
                mIntent.setAction("download");
                mIntent.putExtra("myGameName", myGameName);
                mIntent.putExtra("tag",0);
                sendBroadcast(mIntent);
            }
        }
    }
}
