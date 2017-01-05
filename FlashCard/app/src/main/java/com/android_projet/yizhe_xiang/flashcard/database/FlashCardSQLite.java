package com.android_projet.yizhe_xiang.flashcard.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by MagicienDeAnaïs on 2016/11/28 0028.
 */

public class FlashCardSQLite extends SQLiteOpenHelper {
    private static int VERSION = 1;
    private static String FlashCardBD = "FlashCardBD";
    private static FlashCardSQLite instance;

    private String create_allgames="create table allgames_table( " +
            " _id integer primary key," +
            " name varchar(50) not null unique, "  +
            " source int default 0, "  +
            " lastuse varchar(10) default '1992-11-05'," +
            " fisrtuse varchar(10) default '2016-11-05'"  +
            ");";
    private String create_testGame1="create table Game1 (" +
            " _id integer primary key,"                   +
            " question varchar(200) not null ,"          +
            " imgpath varchar(200) default '' ,"          +
            " audiopath varchar(200) default '' ,"          +
            " answer varchar(50) not null,"                   +
            " level int default 0 ,"          +
            " box int default 1 "        +
            ");";
    private String create_testGame2="create table Game2 (" +
            " _id integer primary key,"                   +
            " question varchar(200) not null ,"          +
            " imgpath varchar(200) default '' ,"          +
            " audiopath varchar(200) default '' ,"          +
            " answer varchar(50) not null,"                   +
            " level int default 0 ,"          +
            " box int default 1 "        +
            ");";

    public static FlashCardSQLite getInstance(Context context){
        if( instance == null ){
            instance = new FlashCardSQLite(context);
        }
        return instance;
    }

    private FlashCardSQLite(Context context){
        super(context, FlashCardBD, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_allgames);
        db.execSQL(create_testGame1);
        db.execSQL(create_testGame2);
        db.execSQL("insert into allgames_table(name) values('Game1') ");
        db.execSQL("insert into allgames_table(name) values('Game2') ");
        db.execSQL("insert into Game2(question,answer) values('Which one is the copital of the France? Paris, Lyon, Marseille, Lille','Paris')");
        db.execSQL("insert into Game2(question,answer) values('which drink the french like? Vin, Water, Orange, Beer','Vin')");
        db.execSQL("insert into Game2(question,answer) values('what is the real name of the Paris 7? MarieCurrie, Sud, Diderot, Denis','Diderot')");
        db.execSQL("insert into Game2(question,answer) values('She likes me? Yes, No','No')");
        db.execSQL("insert into Game2(question,answer,box) values('test box 2','Non',2)");
        db.execSQL("insert into Game2(question,answer,box) values('test box 3','Non',3)");
        db.execSQL("insert into Game2(question,answer,box) values('test box 4','Non',4)");
        db.execSQL("insert into Game2(question,answer,box) values('test box 5','Non',5)");
        db.execSQL("insert into Game2(question,answer,box) values('test box 6','Non',6)");
        db.execSQL("insert into Game2(question,answer,box) values('test box 7','Non',7)");
        db.execSQL("insert into Game1(question,answer) values('JavaScript is a fantastic langauage? Yes, No','No')");
        db.execSQL("insert into Game1(question,answer) values('Omar like very much Python? Yes, No, Not at all','Not at all')");
        db.execSQL("insert into Game1(question,answer) values('Ocaml is very simple for us? Yes, No, Nooooon','Nooooon')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists allgames_table ;");
            db.execSQL("drop table if exists Game1 ;");
            db.execSQL("drop table if exists Game2 ;");
            onCreate(db);
        }
    }
}
