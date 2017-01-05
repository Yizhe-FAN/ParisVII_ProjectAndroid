package com.android_projet.yizhe_xiang.flashcard.manage;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import android.widget.TextView;
import android.widget.Toast;

import com.android_projet.yizhe_xiang.flashcard.R;
import com.android_projet.yizhe_xiang.flashcard.database.FlashCardProvider;
import com.android_projet.yizhe_xiang.flashcard.main.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {//@link GameManageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameManageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameManageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";  //game list postion
    private static final String ARG_PARAM2 = "param2";  //download postion
    private static final String ARG_PARAM3 = "param3";  // ration button
    private static final String ARG_PARAM4 = "param4";  // new game name

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;

    private Spinner chooseGameSpinner;
    private SimpleCursorAdapter adapterChooseGame;

    private RadioButton rdManual;
    private RadioButton rdInternet;
    private RadioGroup mRadioGroup;
    private Button createGame;
    private EditText nameManual;
    private Spinner nameInternet;

    private String selectGame;
    private Button deleteGame;
    private int onSelectPosition;
    private int onSelectDownloadPosition;

    private long idDownload;
    private int idItemDownload;
    private DownloadManager downloadManager;
    private ContentResolver resolver;
    private BroadcastReceiver receiver;

    private Button deleteFile;
    private Button cardManage;

    //private OnFragmentInteractionListener mListener;

    public GameManageFragment() {
        // Required empty public constructor
        Log.d("++++++", "GameManageFragment: ");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameManageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameManageFragment newInstance(String param1, String param2,String param3,String param4) {
        GameManageFragment fragment = new GameManageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        Log.d("+++++", "newInstance: ");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
        }
        Log.d("++++++", "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        onSelectPosition=Integer.parseInt(mParam1);
        onSelectDownloadPosition=Integer.parseInt(mParam2);
        Log.d("++++++", "onCreateView: ");
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_game_manage, container, false);
        chooseGameSpinner = (Spinner)v.findViewById(R.id.chooseGame);

        chooseGameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectGame = ((TextView) view).getText().toString();
                onSelectPosition = position;
                String gameZipPath = Environment.getExternalStorageDirectory() + "/FlashCard/" + selectGame + "/" + selectGame + ".zip";
                File myGameZip = new File(gameZipPath);
                if (!myGameZip.exists()) {
                    deleteFile.setEnabled(false);
                }
                else {
                    deleteFile.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        adapterChooseGame = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_spinner_item,
                null,new String[]{"name"},new int[]{android.R.id.text1},0);
        chooseGameSpinner.setAdapter(adapterChooseGame);
        Log.d("onselectposition",onSelectPosition+"");
        chooseGameSpinner.setSelection(onSelectPosition);
        resolver = getActivity().getContentResolver();

        getLoaderManager().initLoader(0, null, this);

        rdManual = (RadioButton)v.findViewById(R.id.radioManual);
        rdInternet = (RadioButton)v.findViewById(R.id.radioInternet);
        mRadioGroup = (RadioGroup)v.findViewById(R.id.radioGroup);
        nameManual = (EditText)v.findViewById(R.id.nameManual);
        nameInternet = (Spinner)v.findViewById(R.id.nameInternet);
        createGame = (Button)v.findViewById(R.id.bCreateGame);
        deleteGame = (Button)v.findViewById(R.id.bDeleteGame);
        downloadManager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        nameInternet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSelectDownloadPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        nameInternet.setSelection(onSelectDownloadPosition);
        nameManual.setText(mParam4);
        if (mParam3.equals("1")){
            rdManual.setChecked(true);
            nameInternet.setEnabled(false);
            nameManual.setEnabled(true);
        }else{
            rdInternet.setChecked(true);
            nameInternet.setEnabled(true);
            nameManual.setEnabled(false);
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==rdManual.getId()){
                    nameInternet.setEnabled(false);
                    nameManual.setEnabled(true);
                }
                else if(checkedId==rdInternet.getId()){
                    nameInternet.setEnabled(true);
                    nameManual.setEnabled(false);
                }
            }
        });

        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rdManual.isChecked()) {
                    String newGame = nameManual.getText().toString();
                    if (newGame.length() == 0) {
                        Toast.makeText(getContext(), "Please type a name for the game.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        if (newGame.equals("vocabulaire_fr") || newGame.equals("Anais")) {
                            Toast.makeText(getContext(), "Game Name Reserved", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            ContentValues cv = new ContentValues();
                            Log.d("Create Game", newGame);
                            cv.put("name", newGame);
                            Uri.Builder builder = new Uri.Builder();
                            Uri uri = builder.scheme("content").authority(FlashCardProvider.authority).appendPath("creategame").build();
                            uri = resolver.insert(uri, cv);
                            long id = ContentUris.parseId(uri);

                            if (id == -1) {
                                Toast.makeText(getContext(), "Game Name Exist", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Create Successfully", Toast.LENGTH_SHORT).show();
                                getLoaderManager().restartLoader(0, null, GameManageFragment.this);
                            }
                        }
                    }
                } else if (rdInternet.isChecked()) {
                    idItemDownload = nameInternet.getSelectedItemPosition();
                    String gameUri = "";
                    String myGameName = "";
                    switch (idItemDownload) {
                        case 0:
                            myGameName = nameInternet.getSelectedItem().toString();
                            Toast.makeText(getContext(), "Please Choose a Game to DownLoad", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            myGameName = nameInternet.getSelectedItem().toString();
                            gameUri = "http://t.jgz.la/attachment/201612/1508/anais0719-134506/5851e8a5a47e5.zip";
                            break;
                        case 2:
                            myGameName = nameInternet.getSelectedItem().toString();
                            gameUri = "http://133673.site.jgz.la/file.php?accessoryId=771308";
                            break;
                        case 3:
                            myGameName = nameInternet.getSelectedItem().toString();
                            Toast.makeText(getContext(), "Not Available", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    if (gameUri.length() != 0) {
                        if (isSdCardExist()) {
                            String gamePath = Environment.getExternalStorageDirectory() + "/FlashCard/" + myGameName;
                            File myGame = new File(gamePath);
                            if (!myGame.exists()) {
                                String flashCardPath = Environment.getExternalStorageDirectory() + "/FlashCard";
                                File myApp = new File(flashCardPath);
                                if (!myApp.exists()) myApp.mkdir();
                                myGame.mkdir();
                                Log.d("mkdir","create game repertoire!");
                            }
                            String gameZipPath = Environment.getExternalStorageDirectory() + "/FlashCard/" + myGameName + "/" + myGameName + ".zip";
                            File myGameZipPath = new File(gameZipPath);

                            //quand il y a pas de .zip file on utilise service pour telecharger et installer
                            if (!myGameZipPath.exists()) {
                                Uri uri = Uri.parse(gameUri);
                                DownloadManager.Request req = new DownloadManager.Request(uri);
                                req.setTitle(myGameName+" is Downloading");
                                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                                req.setDestinationInExternalPublicDir("/FlashCard/" + myGameName, myGameName + ".zip");
                                idDownload = downloadManager.enqueue(req);
                                Intent intent = new Intent(getContext(), InstallService.class);
                                intent.putExtra("idDownload",idDownload);
                                intent.putExtra("myGameName",myGameName);
                                getActivity().startService(intent);
                            }
                            //quand il y a le .zip file. on utilise local class pour installer
                            else {
                                new InstallGame(myGameName).execute(gameZipPath);
                            }

                        }
                        else {
                            Log.d("SD Card Error", "SD Card is not Exist");
                        }
                    }
                }
            }
        });

        deleteFile = (Button)v.findViewById(R.id.bDeleteFile);
        deleteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSdCardExist()) {
                    String gameZipPath = Environment.getExternalStorageDirectory() + "/FlashCard/" + selectGame + "/" + selectGame + ".zip";
                    File myGameZip = new File(gameZipPath);
                    if (!myGameZip.exists()) {
                        Toast.makeText(getContext(),"Zip File Not Found",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        myGameZip.delete();
                        Toast.makeText(getContext(),"Zip File Deleted",Toast.LENGTH_SHORT).show();
                        deleteFile.setEnabled(false);
                    }
                }
                else {
                    Log.d("SD Card Error", "SD Card is not Exist");
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("download");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String installFinished = intent.getStringExtra("myGameName");
                int tag = intent.getIntExtra("tag",0);

                if(tag == 1){
                    getLoaderManager().restartLoader(0,null,GameManageFragment.this);
                    Toast.makeText(getContext(), "Game: "+installFinished+" Installed Successfully",Toast.LENGTH_SHORT).show();
                }
                else{
                    deleteFile.setEnabled(true);
                    Toast.makeText(getContext(), "Game: "+installFinished+" Already Installed",Toast.LENGTH_SHORT).show();
                }
            }
        };
        getActivity().registerReceiver(receiver,intentFilter);

        deleteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.d("Delete Game",selectGame);
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(FlashCardProvider.authority).appendPath("deletegame").appendPath(selectGame);
            Uri uri = builder.build();

            Log.d("Delete Game",uri.toString());
            int number = resolver.delete(uri,null,null);
            if(number == 0 ){
                Log.d("---","delete failed");
            }
            else{
                Toast.makeText(getContext(),"Delete Successfully", Toast.LENGTH_SHORT).show();
                getLoaderManager().restartLoader(0,null,GameManageFragment.this);
            }
            }
        });

        cardManage = (Button)v.findViewById(R.id.bCardManage);
        cardManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),CardManageActivity.class);
                intent.putExtra("GameName",selectGame);
                getActivity().startActivityForResult(intent, MainActivity.MANAGER_REQUEST_CODE);
            }
        });

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(FlashCardProvider.authority).appendPath("allgames").build();
        return new CursorLoader(getContext(),uri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        adapterChooseGame.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapterChooseGame.swapCursor(null);
    }

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    class InstallGame extends AsyncTask<String,Integer, String> {
        private String myGameName;

        public InstallGame(String gameName){
            myGameName = gameName;
        }

        @Override
        protected String doInBackground(String... params) {
            File zipFile=new File(params[0]);
            String gameDir=Environment.getExternalStorageDirectory()+"/FlashCard/"+myGameName;
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
                        int resultInstallGame=new InstallGameTool(getContext()).installInternetGame(mySQL);
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
                getLoaderManager().restartLoader(0,null,GameManageFragment.this);
                Toast.makeText(getContext(),"Game: "+myGameName+" Reinstalled Successfully",Toast.LENGTH_LONG).show();
            }else if (bmp.equals("deja")){
                Toast.makeText(getContext(),"Game: "+myGameName+" Already Installed",Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("+++++", "onDestroy: destroy !!!!");
        getActivity().unregisterReceiver(receiver);
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/
    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
       */
    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/

    public int getOnSelectPosition(){
        return onSelectPosition;
    }
    public int getOnSelectDownloadPosition(){
        return onSelectDownloadPosition;
    }
    public String getNewGameName(){
        if (nameManual.getText().length()>0){
            return nameManual.getText().toString();
        }else {
            return "";
        }
    }
    public String getRadioButtonState(){
        if (rdManual.isChecked()){
            return "1";
        }else{
            return "2";
        }
    }
}
