package com.android_projet.yizhe_xiang.flashcard.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.List;


public class FlashCardProvider extends ContentProvider {
    //Anaïs, c'est qui? essayez dans le OTHER
    public static String authority = "com.android_projet.yizhe_xiang.flashcard.provider.anais";
    private FlashCardSQLite helper;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    //return tous les jeux dans le tableau !
    private static final int ALLGAMES = 1;

    //return les cards dans un jeu mais box in (?)
    private static final int ALLCARDS = 2;

    //select firstuse from allgames_table where name = ?
    private static final int FIRSTUSE = 3;

    //every time after playing a game, update lastuse, where name = ?
    private static final int UPDATELASTUSE = 4;

    //insert a new game and create the its own table, if it does not already existe
    private static final int CREATEGAME = 5;

    //delete a game and drop its own table
    private static final int DELETEGAME = 6;

    //select all the cards of a game
    private static final int SHOWCARDS = 7;

    //delete one card
    private static final int DELETEONECARD = 8;

    //insert one card
    private static final int INSERTONECARD = 9;

    //update one card
    private static final int UPDATEONECARD = 10;

    //select one card
    private static final int SHOWONECARD = 11;

    static {
        matcher.addURI(authority, "allgames", ALLGAMES);
        matcher.addURI(authority, "allcards/*", ALLCARDS);
        matcher.addURI(authority, "fisrtuse", FIRSTUSE);
        matcher.addURI(authority, "updatelastuse", UPDATELASTUSE);
        matcher.addURI(authority, "creategame", CREATEGAME);
        matcher.addURI(authority, "deletegame/*", DELETEGAME);
        matcher.addURI(authority, "showcards/*", SHOWCARDS);
        matcher.addURI(authority, "deleteonecard/*/#", DELETEONECARD);
        matcher.addURI(authority, "insertonecard/*", INSERTONECARD);
        matcher.addURI(authority, "updateonecard/*", UPDATEONECARD);
        matcher.addURI(authority, "showonecard/*/#", SHOWONECARD);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        int number;
        switch (code){
            case DELETEGAME:
                String tableName = uri.getLastPathSegment().toString();
                Log.d("delete game",tableName);
                number = db.delete("allgames_table","name = ?", new String[]{ tableName });
                if(number != 0 ){
                    db.execSQL("drop table if exists "+tableName+" ;");
                }
                break;
            case DELETEONECARD:
                List argsRaw = uri.getPathSegments();
                String[] args = new String[]{ argsRaw.get(argsRaw.size()-1).toString(), argsRaw.get(argsRaw.size()-2).toString()};
                number = db.delete(args[1], "_id = "+args[0], null);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return number;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        Uri.Builder builder= new Uri.Builder();
        builder.scheme("content").authority(authority);
        long id;
        switch (code){
            case CREATEGAME:
                String tableName = values.getAsString("name");
                id = db.insert("allgames_table",null,values);
                builder.appendPath("creategame");
                if(id >= 0){
                    db.execSQL("create table "+tableName+" ("+
                            " _id integer primary key,"+
                            " question varchar(200) not null ,"+
                            " imgpath varchar(200) default '' ,"+
                            " audiopath varchar(200) default '' ,"+
                            " answer varchar(50) not null,"+
                            " level int default 0 ,"+
                            " box int default 1 "+
                    ");");
                }
                break;
            case INSERTONECARD:
                String gameName = uri.getLastPathSegment();
                id = db.insert(gameName,null,values);
                builder.appendPath("insertonecard");
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");

        }

        builder = ContentUris.appendId(builder,id);
        return builder.build();
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        helper=FlashCardSQLite.getInstance(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteDatabase db = helper.getWritableDatabase();
        int code=matcher.match(uri);
        Cursor cursor=null;
        switch (code){
            case ALLGAMES:
                cursor=db.query("allgames_table",null,null,null,null,null,null,null);
                break;
            case ALLCARDS:
                String tableName=uri.getLastPathSegment();
                Log.d("+++tableName: ",tableName);
                //cursor=db.query(tableName,null,selection,selectionArgs,null,null,null,null);
                cursor=db.rawQuery("select * from "+tableName+" where box in ("+selectionArgs[0]+")",null);
                break;
            case FIRSTUSE:
                cursor=db.query("allgames_table",projection,selection,selectionArgs,null,null,null,null);
                break;
            case SHOWCARDS:
                if(selection == null) {
                    String gameName = uri.getLastPathSegment().toString();
                    Log.d("showcards", "query: " + gameName);
                    cursor = db.query(gameName, null, null, null, null, null, null);
                }else{
                    String gameName = uri.getLastPathSegment().toString();
                    Log.d("filter", "query: " + selectionArgs[0]);
                    //cursor = db.query(gameName, null, selection, selectionArgs, null, null, null);
                    cursor= db.rawQuery("SELECT * FROM "+
                            gameName+" where question like ? ",selectionArgs);
                    Log.d("content privider number", "query: "+cursor.getCount());
                }
                break;
            case SHOWONECARD:
                List argsRaw = uri.getPathSegments();
                String[] args = new String[]{ argsRaw.get(argsRaw.size()-1).toString(), argsRaw.get(argsRaw.size()-2).toString()};
                cursor = db.query(args[1], null, "_id = "+args[0], null,null,null,null);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        int executeID = -1;
        switch (code){
            case UPDATELASTUSE:
                executeID = db.update("allgames_table",values,selection,selectionArgs);
                break;
            case UPDATEONECARD:
                String gameName = uri.getLastPathSegment().toString();
                executeID = db.update(gameName,values,selection,selectionArgs);
                break;
        }

        return executeID;
    }
}
