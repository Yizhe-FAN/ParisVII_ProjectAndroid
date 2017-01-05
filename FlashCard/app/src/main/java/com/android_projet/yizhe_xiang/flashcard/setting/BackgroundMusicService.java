package com.android_projet.yizhe_xiang.flashcard.setting;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.android_projet.yizhe_xiang.flashcard.R;


/*
commencer la musique que le joueur choisit ou arrêter la musique
 */
public class BackgroundMusicService extends Service {
    MediaPlayer mediaPlayer;
    public BackgroundMusicService() {
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
        int bgm=intent.getIntExtra("bgm",0);
        Log.d("music_bgm",bgm+"");
        switch (bgm){
            case 0:
                mediaPlayer=MediaPlayer.create(this, R.raw.bgm0);
                break;
            case 1:
                mediaPlayer=MediaPlayer.create(this,R.raw.bgm1);
                break;
            case 2:
                mediaPlayer=MediaPlayer.create(this,R.raw.bgm2);
                break;
        }
        mediaPlayer.setLooping(true);
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
