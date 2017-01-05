package com.android_projet.yizhe_xiang.flashcard.play;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.content.CursorLoader;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android_projet.yizhe_xiang.flashcard.database.FlashCardProvider;
import com.android_projet.yizhe_xiang.flashcard.entity.OneCard;
import com.android_projet.yizhe_xiang.flashcard.R;
import com.android_projet.yizhe_xiang.flashcard.main.MainActivity;
import com.android_projet.yizhe_xiang.flashcard.other.OtherFragment;

import java.util.ArrayList;

public class PlayCardActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,OneCardFragment.OnFragmentInteractionListener
{
    private ArrayList<OneCard> cardArrayLists;             // list des cards doivent être affichées
    public String gameName;     //le nom du jeu que le joueur a choisi
    private int espaceDay;       //
    private String boxNumber;     // par exemple 1,2,4  c'est-à-dire les cartes qui continnent box=1 ou 2 ou 4
    private OneCardFragment oneCardFragment;   //un référence vers le fragment courant affiché
    private int now,max;   //now:  index of cardArrayLists    max:  size of cardArrayLists
    private Button next,pre;  //NEXT PREVIOUS

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
        quand on tourner téléphone, on conserve les infos siuvantes:
         */
        if (max!=0){
            outState.putString("now",now+"");
            outState.putString("max",max+"");
            outState.putString("gameName",gameName);
            outState.putParcelableArrayList("cardlist",cardArrayLists);
            outState.putString("restTime", oneCardFragment.getRestTime()+"");
            //cancel le TimeCounter de fragment courant
            oneCardFragment.cancelTimeCounter();
            outState.putString("answer", oneCardFragment.getAnswer());
            Log.d("onSaveInstance", oneCardFragment.toString());
            Log.d("restime!!!!", oneCardFragment.getRestTime()+"");
            Log.d("anwser!!!!", oneCardFragment.getAnswer()+"");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_card);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarplay);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (max!=0){
                    oneCardFragment.cancelTimeCounter();
                }
                setResult(MainActivity.RESULT_1);
                PlayCardActivity.this.finish();
            }
        });
        next=(Button)findViewById(R.id.suivant);
        pre=(Button)findViewById(R.id.precedent);
        if (savedInstanceState == null) {
            now=0;
            pre.setEnabled(false);
            boxNumber="1";
            cardArrayLists=new ArrayList<>();
            gameName=getIntent().getStringExtra("gameName");
            espaceDay=Integer.parseInt(getIntent().getStringExtra("espaceDay"));
            triBox();
            Log.d("expaceday  ",espaceDay+"");
            initCards();
        } else {
                gameName=savedInstanceState.getString("gameName");
                if (gameName==null){
                    pre.setEnabled(false);
                    next.setEnabled(false);
                    return;
                }
                //getSupportFragmentManager().popBackStack("myCard", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                max=Integer.parseInt(savedInstanceState.getString("max"));
                if (max==1){
                    next.setEnabled(false);
                }
                now = Integer.parseInt(savedInstanceState.getString("now"));
                if (now==0){
                    pre.setEnabled(false);
                }else if(now==max-1){
                    next.setEnabled(false);
                }
                int restTime=Integer.parseInt(savedInstanceState.getString("restTime"));
                Log.d("restTimeinElse",restTime+"");
                String answer=savedInstanceState.getString("answer");
                Log.d("AnswerinELSE",answer+"");
                cardArrayLists=savedInstanceState.getParcelableArrayList("cardlist");
                //Log.d("afterOnSaveINs",oneCardFragment.toString());
                oneCardFragment = OneCardFragment.newInstance(cardArrayLists.get(now),restTime,answer);
                Log.d("afterNEWnew", oneCardFragment.toString());
                getSupportFragmentManager().beginTransaction().replace(R.id.playcard_frame, oneCardFragment).commit();
        }
        //Log.d("name===",gameName);
    }
    // la première fois qu'on lance cet activité, lance
    private void initCards(){
        getLoaderManager().initLoader(0,null,PlayCardActivity.this);
    }
    /*
    deux boutons PREVIOUS NEXT avant de replacer le fragment courant,il faut cancel le TimeCounter
     */
    protected void nextCard(View v){
        if (now<max-1){
            oneCardFragment.cancelTimeCounter();
            oneCardFragment = OneCardFragment.newInstance(cardArrayLists.get(++now),0,"");
            getSupportFragmentManager().beginTransaction().replace(R.id.playcard_frame, oneCardFragment).commit();
            if (now==max-1){
                next.setEnabled(false);
            }
            pre.setEnabled(true);
        }else{
            Toast annonce=Toast.makeText(PlayCardActivity.this,"Fin",Toast.LENGTH_LONG);
            annonce.show();
        }
    }
    protected void preCard(View v){
        if (now<=0){
            Toast annonce=Toast.makeText(PlayCardActivity.this,"C'est la première carte",Toast.LENGTH_LONG);
            annonce.show();
        }else{
            oneCardFragment.cancelTimeCounter();
            oneCardFragment = OneCardFragment.newInstance(cardArrayLists.get(--now),0,"");
            getSupportFragmentManager().beginTransaction().replace(R.id.playcard_frame, oneCardFragment).commit();
            if (now==0){
                pre.setEnabled(false);
            }
            next.setEnabled(true);
        }
    }
    //quand le joueur trouve la bonne réponse, on met la carte courant, si level normal,facile, box+1
    //level difficile, la valeur de box ne change pas. trivial toujour 0!
    @Override
    public void addTocardAlreadyCorrect(){
        cardArrayLists.get(now).setFlagAlreadyCorrect(1);
        if (cardArrayLists.get(now).getLevel()==1||cardArrayLists.get(now).getLevel()==0){
            Uri.Builder builder = new Uri.Builder();
            ContentValues values=new ContentValues();
            int newBox=cardArrayLists.get(now).getBox()+1;
            values.put("box",newBox);
            Uri uri=builder.scheme("content").authority(FlashCardProvider.authority).appendPath("updateonecard").appendPath(gameName).build();
            int resultID=getContentResolver().update(uri,values,"rowid=?",new String[]{cardArrayLists.get(now).getId()+""});
            Log.d("resultID: ",resultID+"");
        }
    }
    //??
    @Override
    public void addTocardAlreadyWatch(){
        cardArrayLists.get(now).setFlagAlreadyCorrect(2);
    }
    // si le temps se coule, on afficher next card!
    @Override
    public void timeUp(){nextCard(new View(getApplicationContext()));}
    //quand on change le level, c'est-à-dire que le joueur choisit radioButton et puis clique OK!
    @Override
    public void updateCard(int level){
        cardArrayLists.get(now).setLevel(level);
        Uri.Builder builder = new Uri.Builder();
        ContentValues values=new ContentValues();
        values.put("level",level);
        if (level==1){
            int boxfacileValue=getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("boxfacile",4);
            values.put("box",boxfacileValue);
        }else if(level==2){
            int boxdifficileValue=getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("boxdifficile",1);
            values.put("box",boxdifficileValue);
        }else{
            values.put("box",0);
        }
        Uri uri=builder.scheme("content").authority(FlashCardProvider.authority).appendPath("updateonecard").appendPath(gameName).build();
        int resultID=getContentResolver().update(uri,values,"rowid=?",new String[]{cardArrayLists.get(now).getId()+""});
        Log.d("resultID: ",resultID+"");
    }
    //select toutes les cartes from gameName where box in(?)
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(FlashCardProvider.authority)
                .appendPath("allcards").appendPath(gameName)
                .build();
        return new CursorLoader(this, uri, null,
                "box in (?)", new String[]{boxNumber}, null);

    }
    // si le jeux n'a pas de carte assortie, on afficher otherfragment
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        while (data.moveToNext()){
            cardArrayLists.add(new OneCard(data.getInt(0),data.getString(1),data.getString(2),data.getString(3),data.getString(4),data.getInt(5),data.getInt(6),0));
        }
        max=cardArrayLists.size();
        Log.d("max>>>>",""+max);
        if (max==0){
            next.setEnabled(false);
            OtherFragment otherFragment=OtherFragment.newInstance("0","noCard");
            getSupportFragmentManager().beginTransaction().replace(R.id.playcard_frame, otherFragment).commit();
        }else {
            if (max==1){
                next.setEnabled(false);
            }
            oneCardFragment = OneCardFragment.newInstance(cardArrayLists.get(now),0,"");
            getSupportFragmentManager().beginTransaction().replace(R.id.playcard_frame, oneCardFragment).commit();
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    //triBox: comment on afficher les cartes? Si c'est le 15er jour,on doit afficher 1:les nouveaux cartes  3:tous les 3 jours  5:tous les 5 jours
    private void triBox(){
        int boxmaxValue=getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("boxmax",7);
        for (int i=2;i<boxmaxValue;i++){
            if (espaceDay%i==0) boxNumber+=","+i;
        }
        //boxNumber="1,2,3,4,5,6,7,8";
        Log.d("boxnumeber",boxNumber);
    }
    /*
    avant de finish this activity, il faut cancel TimeCounter si c'est besoin
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if (max!=0){
                oneCardFragment.cancelTimeCounter();
            }
            setResult(MainActivity.RESULT_1);
            this.finish();
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId() ) {
            case  R.id.menu_playfragment:
                setResult(MainActivity.RESULT_1);
                break;
            case R.id.menu_manage:
                setResult(MainActivity.RESULT_2);
                break;
            case  R.id.menu_settings:
                setResult(MainActivity.RESULT_3);
                break;
            case R.id.menu_other:
                setResult(MainActivity.RESULT_4);
                break;
            default:
        }
        if (max!=0){
            oneCardFragment.cancelTimeCounter();
        }
        this.finish();
        return  true;
    }
}
