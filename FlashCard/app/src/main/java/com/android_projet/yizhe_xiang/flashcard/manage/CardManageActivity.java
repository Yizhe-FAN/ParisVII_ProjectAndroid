package com.android_projet.yizhe_xiang.flashcard.manage;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android_projet.yizhe_xiang.flashcard.database.FlashCardProvider;
import com.android_projet.yizhe_xiang.flashcard.entity.OneCard;
import com.android_projet.yizhe_xiang.flashcard.R;
import com.android_projet.yizhe_xiang.flashcard.main.MainActivity;

public class CardManageActivity extends AppCompatActivity
        implements ShowCardsFragment.OnFragmentInteractionListener, AddOneCardFragment.OnFragmentInteractionListener{

    public static String gameName; //the cards of which game to manage
    private int model; //consulation mode 0, delete mode 1
    private OneCard cardSelected; //to save temp list item selected to reconstruct

    private ShowCardsFragment showCardsFragment;
    private ShowOneCardFragment showOneCardFragment;
    private AddOneCardFragment addOneCardFragment;

    private Toolbar toolbar;
    private Button delete;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("++++", "onCreate: cardManage");
        Log.d("!!!!", "activity "+toString());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_manage);

        toolbar = (Toolbar)findViewById(R.id.toolbarmanager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(MainActivity.RESULT_2);
                CardManageActivity.this.finish();
            }
        });


        delete = (Button)findViewById(R.id.bDeleteCard);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model = 1;
                showCardsFragment.changeTypeList(model);
                delete.setEnabled(false);
            }
        });

        add = (Button)findViewById(R.id.bAddCard);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOneCardFragment = AddOneCardFragment.newInstance("0","0");
                addOneCardFragment.show(getSupportFragmentManager().beginTransaction(),"addCard");
            }
        });

        //when the activity est created first time
        if(savedInstanceState == null){
            model = 0;
            gameName = getIntent().getStringExtra("GameName");
            showCardsFragment = ShowCardsFragment.newInstance("0","0");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.cardManage_frame,showCardsFragment,"show")//tag show for the showscards fragment
                    .addToBackStack("list")//in the stack tag list inital
                    .commit();
        }
        //configuration change
        else{
            model = savedInstanceState.getInt("model");
            gameName = savedInstanceState.getString("gameName");
            cardSelected = savedInstanceState.getParcelable("cardSelected");

            //fragment reconstruct themselves, don't need to recreate
            if(model == 1){
                //when delete mode set delete button disable
                delete.setEnabled(false);
            }
            else{
                //when consulation mode
                //in one card fragment showed last time, set two button disable
                //in show card fragment showed last time, don't need reset two button
                if(cardSelected != null){
                    delete.setEnabled(false);
                    add.setEnabled(false);
                }
            }
        }

        if(showCardsFragment != null) {
            Log.d("!!!!", "myShowCardsFragment "+showCardsFragment.toString());
        }
        else{
            showCardsFragment = (ShowCardsFragment) getSupportFragmentManager().findFragmentByTag("show");
        }

        if(showOneCardFragment != null) {
            Log.d("!!!!", "myShowOneCardFragment "+showCardsFragment.toString());
        }
        else{
            showOneCardFragment = (ShowOneCardFragment) getSupportFragmentManager().findFragmentByTag("one");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("model",model);
        outState.putString("gameName",gameName);
        if(cardSelected != null) outState.putParcelable("cardSelected",cardSelected);//if have selected one item
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // out of selected one card fragment, reset null
        cardSelected = null;

        //out of consulation mode, out of the activity
        if(getSupportFragmentManager().getBackStackEntryCount() == 0 && model == 0){
            setResult(MainActivity.RESULT_2);
            this.finish();
        }

        //out of delete mode, reconstruct shows cards fragment.
        //because before destroy the fragment, it don't execute savedInstanceState.
        else if(getSupportFragmentManager().getBackStackEntryCount() == 0 && model == 1){
            showCardsFragment = ShowCardsFragment.newInstance("0","0");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.cardManage_frame,showCardsFragment,"show")
                    .addToBackStack("list")
                    .commit();
            model = 0;
            delete.setEnabled(true);
        }
    }


    @Override
    public void oneCardSelected(OneCard card) {
        cardSelected = card;
        showOneCardFragment = ShowOneCardFragment.newInstance(card);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.cardManage_frame,showOneCardFragment,"one")
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        delete.setEnabled(false);
        add.setEnabled(false);

    }

    @Override
    public void createOneCard(String question, String answer, int level, int box) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content")
                .authority(FlashCardProvider.authority)
                .appendPath("insertonecard")
                .appendPath(gameName);
        Uri uri = builder.build();
        ContentValues cv = new ContentValues();
        cv.put("question", question);
        cv.put("answer" , answer);
        cv.put("level", level);
        cv.put("box", box);
        uri = getContentResolver().insert(uri, cv);
        long id = ContentUris.parseId(uri);
        if (id == -1) {
            Toast.makeText(this, "Insert Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Create Successfully", Toast.LENGTH_SHORT).show();
            String temp = showCardsFragment.search.getText().toString();
            if( temp.length() != 0) {
                Bundle args = new Bundle();
                args.putCharSequence("filter", temp);
                showCardsFragment.getLoaderManager().restartLoader(0, args, showCardsFragment.listViewCallBack);
            }
            else{
                showCardsFragment.getLoaderManager().restartLoader(0, null, showCardsFragment.listViewCallBack);
            }
        }
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

        this.finish();
        return  true;
    }


}
