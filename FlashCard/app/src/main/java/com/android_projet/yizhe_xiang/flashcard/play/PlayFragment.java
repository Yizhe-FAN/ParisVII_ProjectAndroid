package com.android_projet.yizhe_xiang.flashcard.play;


import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android_projet.yizhe_xiang.flashcard.R;
import com.android_projet.yizhe_xiang.flashcard.database.FlashCardProvider;
import com.android_projet.yizhe_xiang.flashcard.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //PlayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private Spinner spinner;
    private SimpleCursorAdapter spinnerAdapter;
    private Button begin;
    private String onSelectGame;
    private int onSelectPosition;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public PlayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayFragment newInstance(String param1, String param2) {
        PlayFragment fragment = new PlayFragment();
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        onSelectPosition=Integer.parseInt(mParam1);
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_play, container, false);
        spinner=(Spinner)view.findViewById(R.id.play_fragment_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onSelectPosition=position;
                TextView textView=(TextView) view;
                onSelectGame=textView.getText().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerAdapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_spinner_item, null,
                new String[]{"name"},
                new int[]{android.R.id.text1}, 0);
        spinner.setAdapter(spinnerAdapter);
        getLoaderManager().initLoader(0, null, this);
        begin=(Button)view.findViewById(R.id.begin);
        begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("+++selectgame:",onSelectGame+"");
                /*
                Toast annonce=Toast.makeText(getActivity(),onSelectGame,Toast.LENGTH_LONG);
                annonce.show();
                */
                new StartPlayCardActivity().execute();
            }
        });
        spinner.setSelection(onSelectPosition);
        return view;
    }
    class StartPlayCardActivity extends AsyncTask<String,Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            String onSelectFirstUse=null;
            Uri.Builder builder = new Uri.Builder();
            Uri uri=builder.scheme("content").authority(FlashCardProvider.authority).appendPath("fisrtuse").build();
            Cursor cursor=getContext().getContentResolver().query(uri,new String[]{"name","fisrtuse"},"name=?",new String[]{onSelectGame},null);
            if (cursor.moveToNext()) {
                onSelectFirstUse = cursor.getString(1);
            }
            Uri.Builder builder2 = new Uri.Builder();
            Uri uri2=builder2.scheme("content").authority(FlashCardProvider.authority).appendPath("updatelastuse").build();
            Date nowDate=new Date();
            String today=new SimpleDateFormat("yyyy-MM-dd").format(nowDate);
            ContentValues values = new ContentValues();
            values.put("lastuse",today);
            int resultID=getContext().getContentResolver().update(uri2,values,"name=?",new String[]{onSelectGame});
            Log.d("resultID: ",resultID+"");
            int espaceDays=0;
            try{
                Date firstUseDate=new SimpleDateFormat("yyyy-MM-dd").parse(onSelectFirstUse);
                espaceDays= (int)((nowDate.getTime() - firstUseDate.getTime())/86400000);
            }catch (Exception e){
                Log.d("Exception!",e.toString());
            }
            return espaceDays+"";
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            Log.d("espaceDay",result);
            Intent intent=new Intent(getActivity(),PlayCardActivity.class);
            intent.putExtra("espaceDay",result);
            intent.putExtra("gameName",onSelectGame);
            getActivity().startActivityForResult(intent, MainActivity.PLAY_REQUEST_CODE);
        }
    }
    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    */
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    */

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(FlashCardProvider.authority)
                .appendPath("allgames")
                .build();
        return new CursorLoader(getContext(), uri, new String[]{"_id", "name"},
                null, null, null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /*
        data.moveToFirst();
        while (data.moveToNext()) {
            int id = data.getInt(0);
            String name = data.getString(1);
            Log.d("+++longid:", id + "");
            Log.d("+++gamename:", name);
        }
        */
        spinnerAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        spinnerAdapter.swapCursor(null);
    }

    public int getOnSelectPosition(){
        return onSelectPosition;
    }
}
