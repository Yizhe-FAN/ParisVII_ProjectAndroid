package com.android_projet.yizhe_xiang.flashcard.other;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android_projet.yizhe_xiang.flashcard.manage.InstallGameTool;
import com.android_projet.yizhe_xiang.flashcard.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {//@link OtherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OtherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/*
les informations, ici je ajouter code pour tester la fonction de téléchargement!  Comment tester? saisir le clé
 */


public class OtherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DownloadManager downloadManager;
    long downloadID;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    BroadcastReceiver receiver;
    private EditText otherBonus,addrHttp;
    String myGameName;
    String addr;

    // private OnFragmentInteractionListener mListener;

    public OtherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherFragment newInstance(String param1, String param2) {
        OtherFragment fragment = new OtherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        broadcast();
        View view=inflater.inflate(R.layout.fragment_other, container, false);
        downloadManager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        final Button ceshiAnais=(Button)view.findViewById(R.id.anais);
        addrHttp=(EditText)view.findViewById(R.id.addrhttp);
        ceshiAnais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myGameName=otherBonus.getText().toString();
                addr=addrHttp.getText().toString();
                if (addr.length()<10&&!addr.startsWith("http://")){
                    return;
                }
                if (isSdCardExist()){
                    String gamePath=Environment.getExternalStorageDirectory()+"/FlashCard/"+myGameName;
                    File myGame=new File(gamePath);
                    if (!myGame.exists()){

                        String path = Environment.getExternalStorageDirectory()+"/FlashCard";
                        File myPath = new File(path);
                        if(!myPath.exists()) myPath.mkdir();
                        myGame.mkdir();
                    }
                    String gameZipPath=Environment.getExternalStorageDirectory()+"/FlashCard/"+myGameName+"/"+myGameName+".zip";
                    File myGameZipPath=new File(gameZipPath);
                    if (!myGameZipPath.exists()){
                        Log.d("mygamePath",myGame.getAbsolutePath());
                        //Uri uri= Uri.parse("http://t.jgz.la/attachment/201612/1508/anais0719-134506/5851e8a5a47e5.zip");
                        //Uri uri= Uri.parse("http://133673.site.jgz.la/file.php?accessoryId=771308");
                        Uri uri=Uri.parse(addr);
                        DownloadManager.Request req= new DownloadManager.Request(uri);
                        req.setDestinationInExternalPublicDir("/FlashCard/"+myGameName,myGameName+".zip");
                        downloadID= downloadManager.enqueue(req);
                    }else {
                        Log.d("DownloadEx","zipDéjaDownlaod");
                        new InstallGame().execute(gameZipPath);
                    }

                }else{
                    Log.d("sdcarerror","sdcardisntExist");
                }

            }
        });
        otherBonus=(EditText)view.findViewById(R.id.other_bonus);
        otherBonus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String key=otherBonus.getText().toString();
                Pattern pattern = Pattern.compile("\\d{8}");
                Matcher matcher = pattern.matcher(key);
                boolean matchFlash=matcher.find();
                Log.d("matchFalsg",matchFlash+"");
                if (key.equals("vocabulaire_fr")||matchFlash||key.equals("Anais")){
                    ceshiAnais.setVisibility(View.VISIBLE);
                }else {
                    ceshiAnais.setVisibility(View.INVISIBLE);
                }
            }
        });
        if (mParam2.equals("noCard")){
            TextView noCard=(TextView)view.findViewById(R.id.noCard);
            noCard.setVisibility(View.VISIBLE);
        }
        return view;
    }
    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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
        getActivity().unregisterReceiver(receiver);
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
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    */
    private void broadcast(){
        IntentFilter intentFilter=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadID == reference) {
                    DownloadManager.Query question = new DownloadManager.Query();
                    question.setFilterById(downloadID);
                    Cursor cur= downloadManager.query(question);
                    if(cur.moveToNext()){
                        int fileNameIndex=cur.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_FILENAME);

                        String fileName=cur.getString(fileNameIndex);

                        Log.d("++download++","Download finish");
                        Log.d("fileName",fileName);
                        new InstallGame().execute(fileName);
                    }
                    cur.close();


                }
            }
        };
        getActivity().registerReceiver(receiver,intentFilter);
        Log.d("resgisReceiver","success");


    }
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
    class InstallGame extends AsyncTask<String,Integer, String> {
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
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String bmp) {
            if (bmp.equals("success")){
                Notification notification = new NotificationCompat.Builder(getActivity().getApplicationContext())
/*                                .setStyle(new NotificationCompat.InboxStyle()
                                        .setBigContentTitle("Don't Forget")
                                        .addLine("You have not played")
                                        .addLine(cursor.getString(1))
                                        .addLine("For "+temp+" days."))
*/
                        .setContentTitle("Install:")
                        .setContentText("Install success!")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                        .build();
                NotificationManager notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(7777,notification);
            }else if (bmp.equals("deja")){
                Toast annonce=Toast.makeText(getActivity(),"Game déjà Installé",Toast.LENGTH_LONG);
                annonce.show();
            }

        }
    }

}
