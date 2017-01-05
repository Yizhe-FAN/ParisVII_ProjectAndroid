package com.android_projet.yizhe_xiang.flashcard.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android_projet.yizhe_xiang.flashcard.R;
import com.android_projet.yizhe_xiang.flashcard.manage.GameManageFragment;
import com.android_projet.yizhe_xiang.flashcard.manage.InstallService;
import com.android_projet.yizhe_xiang.flashcard.other.OtherFragment;
import com.android_projet.yizhe_xiang.flashcard.play.PlayFragment;
import com.android_projet.yizhe_xiang.flashcard.setting.BackgroundMusicService;
import com.android_projet.yizhe_xiang.flashcard.setting.SettingFragment;

public class MainActivity extends AppCompatActivity {
    //quatre button dans la base
    private Button play,manage,settings,other;
    //quatre fragments correspondant aux quatre boutons
    PlayFragment playFragment;
    GameManageFragment gameManageFragment;
    SettingFragment settingFragment;
    OtherFragment otherFragment;
    //conserver le fragment courant,1:playfragment  2:gameManageFragment  3:settingFragment  4:otherFragment  de sorte que on l'affiche après  tournoiement
    int fragId;
    //le code correspondant à l'activité PlayCardActivity
    public static int PLAY_REQUEST_CODE = 1000;
    public static int MANAGER_REQUEST_CODE = 1005;

    //quand on retourne,1001:playfragment  1002:gameManageFragment  1003:settingFragment  1004:otherFragment
    public final static int RESULT_1 = 1001;
    public final static int RESULT_2 = 1002;
    public final static int RESULT_3 = 1003;
    public final static int RESULT_4 = 1004;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        outState.putInt("fragId",fragId);
        if (fragId==1){
            //la position du spinner!
            outState.putInt("play_onSelect",playFragment.getOnSelectPosition());
        }else if(fragId==2){
            //la position du spinner de Game Management
            outState.putInt("play_onSelect",gameManageFragment.getOnSelectPosition());
            //la position du spinner de DownLoad list
            outState.putInt("internet_onSelect",gameManageFragment.getOnSelectDownloadPosition());
            //l'état de radioBouton
            outState.putString("radion_state",gameManageFragment.getRadioButtonState());
            //le texte de EditText
            outState.putString("new_game",gameManageFragment.getNewGameName());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialisation des quatre boutons
        play=(Button)findViewById(R.id.play);
        manage=(Button)findViewById(R.id.game);
        settings=(Button)findViewById(R.id.settings);
        other=(Button)findViewById(R.id.other);
        //Initialiser Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        /*
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nintent = new Intent(MainActivity.this, BackgroundMusicService.class);
                stopService(nintent);
                MainActivity.this.finish();
            }
        });
        */
        if(savedInstanceState == null){
            // first lancer cette l'activité, on afficher platframent par défaut, initialiser la musique si c'est bosoin.
            playFragment=PlayFragment.newInstance("0","0");
            setButtonFalse(play);
            getSupportFragmentManager().beginTransaction().add(R.id.main_frame,playFragment).commit();
            fragId = 1;
            initMusic();
            Intent noteIntent = new Intent(this,NoteService.class);
            startService(noteIntent);
        }
        else{
            //quand on tourne l'apparail! On récupère tous les données
            int idValue = savedInstanceState.getInt("fragId");
            Log.d("fragId", ""+idValue);
            fragId = idValue;
            int onSelectPosition=savedInstanceState.getInt("play_onSelect");
            switch (fragId){
                case 1:
                    playFragment = PlayFragment.newInstance(""+onSelectPosition,"0");
                    setButtonFalse(play);
                    getSupportFragmentManager().beginTransaction().add(R.id.main_frame,playFragment).commit();
                    break;
                case 2:
                    Log.d("test!!!!",onSelectPosition+"");
                    gameManageFragment = GameManageFragment.newInstance(""+onSelectPosition,""+savedInstanceState.getInt("internet_onSelect"),savedInstanceState.getString("radion_state"),savedInstanceState.getString("new_game"));
                    setButtonFalse(manage);
                    getSupportFragmentManager().beginTransaction().add(R.id.main_frame,gameManageFragment).commit();
                    break;
                case 3:
                    settingFragment = SettingFragment.newInstance("0","0");
                    setButtonFalse(settings);
                    getSupportFragmentManager().beginTransaction().add(R.id.main_frame,settingFragment).commit();
                    break;
                case 4:
                    otherFragment = OtherFragment.newInstance("0","0");
                    setButtonFalse(other);
                    getSupportFragmentManager().beginTransaction().add(R.id.main_frame,otherFragment).commit();
                    break;
                default:
                    Log.d("switch...", "onCreate: fragId not match");
                    break;
            }
        }

    }
    /*
    si le joueur met BackgroundMusic ON, on joue la musique!
     */
    private void initMusic(){
        int bgmOnOrOff=getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("bgmonoroff",0);
        if (bgmOnOrOff==1){
            int bgmSpf=getSharedPreferences("myFlashCard", Activity.MODE_PRIVATE).getInt("bgm",0);
            Intent nintent = new Intent(this, BackgroundMusicService.class);
            nintent.putExtra("bgm",bgmSpf);
            startService(nintent);
        }
    }
    /*
    quand on cliquer les quatre boutons, on change le fragment courant!
    */
    protected void changeFragment(View view){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        if (view == play){
            if(playFragment == null){
                playFragment=PlayFragment.newInstance("0","0");
            }
            setButtonFalse(play);
            fragmentTransaction.replace(R.id.main_frame,playFragment);
            fragId = 1;
        }else if (view == manage) {
            if (gameManageFragment == null) {
                gameManageFragment = GameManageFragment.newInstance("0", "0","1","");
            }
            setButtonFalse(manage);
            fragmentTransaction.replace(R.id.main_frame,gameManageFragment);
            fragId = 2;
        }else if(view == settings){
            if (settingFragment == null ) {
                settingFragment=SettingFragment.newInstance("0","0");
            }
            setButtonFalse(settings);
            fragmentTransaction.replace(R.id.main_frame,settingFragment);
            fragId = 3;
        }else if (view == other){
            if (otherFragment==null){
                otherFragment=OtherFragment.newInstance("0","0");
            }
            setButtonFalse(other);
            fragmentTransaction.replace(R.id.main_frame,otherFragment);
            fragId = 4;
        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    //quand on cliquer back, on doit ateindre la musique! sinon? essayer!
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent nintent = new Intent(this, BackgroundMusicService.class);
            stopService(nintent);

            Intent mintent = new Intent(this, InstallService.class);
            stopService(mintent);

            this.finish();
        }
        return true;
    }
    // Toolbar menu!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId() ) {
            case  R.id.menu_playfragment:
                changeFragment(play);
                return  true;
            case R.id.menu_manage:
                changeFragment(manage);
                return true;
            case  R.id.menu_settings:
                changeFragment(settings);
                return  true;
            case R.id.menu_other:
                changeFragment(other);
                return true;
            default:
        }
        return  false;
    }
    //quand on lance l'activité, et puis on retouner en cliquant le menu de Toolbar
    @Override
    protected void onActivityResult(int reqCode,int resCode,Intent intent){

        if(reqCode == PLAY_REQUEST_CODE){
            switch (resCode){
                case RESULT_1:
                    break;
                case RESULT_2:
                    changeFragment(manage);
                    break;
                case RESULT_3:
                    changeFragment(settings);
                    break;
                case RESULT_4:
                    changeFragment(other);
                    break;
                default:
            }
        }
        else if(reqCode == MANAGER_REQUEST_CODE){
            switch (resCode){
                case RESULT_1:
                    changeFragment(play);
                    break;
                case RESULT_2:
                    break;
                case RESULT_3:
                    changeFragment(settings);
                    break;
                case RESULT_4:
                    changeFragment(other);
                    break;
                default:
            }
        }
        else{

        }
    }
    //amélorer UI
    private void setButtonFalse(Button button){
        play.setEnabled(true);
        manage.setEnabled(true);
        settings.setEnabled(true);
        other.setEnabled(true);
        button.setEnabled(false);
    }
}
