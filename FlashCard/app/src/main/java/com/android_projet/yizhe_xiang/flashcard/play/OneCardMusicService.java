package com.android_projet.yizhe_xiang.flashcard.play;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
/*
Si une carte a une cassette audio
commencer la musique que le joueur choisit ou arrêter la musique
 */
public class OneCardMusicService extends Service {
    MediaPlayer mediaPlayer;
    public OneCardMusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mediaPlayer!=null){
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        String path=intent.getStringExtra("audiopath");
        try{
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        }catch (Exception e){
            Log.d("setDataSource","fail");
        }

        mediaPlayer.setLooping(false);
        mediaPlayer.start();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        /* arêter la musique et libérer les resources de MediaPlayer */
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }
}
