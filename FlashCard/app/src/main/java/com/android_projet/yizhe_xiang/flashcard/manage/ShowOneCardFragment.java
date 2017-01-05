package com.android_projet.yizhe_xiang.flashcard.manage;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android_projet.yizhe_xiang.flashcard.database.FlashCardProvider;
import com.android_projet.yizhe_xiang.flashcard.entity.OneCard;
import com.android_projet.yizhe_xiang.flashcard.R;
import com.android_projet.yizhe_xiang.flashcard.play.OneCardFragment;
import com.android_projet.yizhe_xiang.flashcard.play.OneCardMusicService;
import com.android_projet.yizhe_xiang.flashcard.play.PlayCardActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {//@link ShowOneCardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowOneCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowOneCardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private OneCard mParam1;

    private int level;
    private int box;

    private TextView idCard;
    private TextView question;
    private TextView reponse;
    private TextView showBox;
    private RadioButton easy;
    private RadioButton difficult;
    private RadioButton trivial;
    private RadioButton normal;

    private ImageView cardImage;
    private Button cardAudio;
    private Button modify;
    //private OnFragmentInteractionListener mListener;

    public ShowOneCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ShowOneCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowOneCardFragment newInstance(OneCard param1) {
        ShowOneCardFragment fragment = new ShowOneCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getParcelable(ARG_PARAM1);
        }
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("---mParam1 AutoSaved---", "onActivityCreated: "+mParam1.getQuestion());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_show_one_card, container, false);

        idCard = (TextView)v.findViewById(R.id.idCard);
        idCard.setText(""+mParam1.getId());

        question = (EditText)v.findViewById(R.id.cardQuestion);
        question.setText(mParam1.getQuestion().replace("|","'"));

        reponse = (EditText)v.findViewById(R.id.cardReponse);
        reponse.setText(mParam1.getAnswer());

        easy = (RadioButton)v.findViewById(R.id.showEasy);
        difficult = (RadioButton)v.findViewById(R.id.showDifficult);
        trivial = (RadioButton)v.findViewById(R.id.showTrivial);
        normal = (RadioButton)v.findViewById(R.id.normal);

        cardImage = (ImageView)v.findViewById(R.id.cardImage);
        cardAudio = (Button)v.findViewById(R.id.cardAudio);

        cardAudio.setEnabled(false);
        if (!(mParam1.getAudiopath().length()<1)){
            cardAudio.setEnabled(true);
            cardAudio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sdPath= Environment.getExternalStorageDirectory()+"/FlashCard";
                    sdPath+="/"+CardManageActivity.gameName;
                    sdPath+="/"+mParam1.getAudiopath();
                    Log.d("audioPath",sdPath);
                    Intent intent=new Intent(getContext(),OneCardMusicService.class);
                    intent.putExtra("audiopath",sdPath);
                    getActivity().startService(intent);
                }
            });
        }
        if (!(mParam1.getImgpath().length()<1)){
            String sdPath= Environment.getExternalStorageDirectory()+"/FlashCard";
            sdPath+="/"+CardManageActivity.gameName;
            sdPath+="/"+mParam1.getImgpath();
            Log.d("imgPath",sdPath);
            new AfficherImg().execute(sdPath);
        }

        level = mParam1.getLevel();
        box = mParam1.getBox();

        switch (level){
            case 0:
                normal.setChecked(true);
                break;
            case 1:
                easy.setChecked(true);
                break;
            case 2:
                difficult.setChecked(true);
                break;
            case 3:
                trivial.setChecked(true);
                break;
        }

        showBox = (TextView)v.findViewById(R.id.showBox);
        showBox.setText("every "+box+" day(s) to learn again.");

        modify = (Button)v.findViewById(R.id.cardModify);
        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri.Builder builder = new Uri.Builder();
                ContentValues values=new ContentValues();
                values.put("question",question.getText().toString());
                values.put("answer",reponse.getText().toString());

                if (easy.isChecked()){
                    level = 1;
                    box = getActivity().getSharedPreferences("myFlashCard", Activity.MODE_PRIVATE).getInt("boxfacile",4);
                }else if(difficult.isChecked()){
                    level = 2;
                    box = getActivity().getSharedPreferences("myFlashCard", Activity.MODE_PRIVATE).getInt("boxdifficile",1);
                }else if(trivial.isChecked()){
                    level = 3;
                    box = 0;
                }else if(normal.isChecked()){
                    if (level == 3){
                        box = 1;
                    }
                    level = 0;
                }

                values.put("box",box);
                values.put("level",level);
                Uri uri = builder.scheme("content")
                        .authority(FlashCardProvider.authority)
                        .appendPath("updateonecard")
                        .appendPath(CardManageActivity.gameName).build();

                int res = getActivity().getContentResolver()
                        .update(uri,values,"rowid = ?",new String[]{ mParam1.getId()+"" });

                if(res >= 0 ){
                    Toast.makeText(getContext(),"Update Successfully",Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
                else{
                    Toast.makeText(getContext(),"Update Failed",Toast.LENGTH_SHORT).show();
                }

            }
        });

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
/*    public void onButtonPressed(Uri uri) {
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
 /*   public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
   }
*/

    @Override
    public void onDestroy() {
        Log.d("++++", "onDestroy: showOneCardFragment");
        super.onDestroy();

        Intent intent=new Intent(getContext(),OneCardMusicService.class);
        getActivity().stopService(intent);

        getActivity().findViewById(R.id.bDeleteCard).setEnabled(true);
        getActivity().findViewById(R.id.bAddCard).setEnabled(true);
    }

    class AfficherImg extends AsyncTask<String,Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bm= BitmapFactory.decodeFile(params[0]);
            return bm;
        }
        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Bitmap bmp) {
            cardImage.setImageBitmap(bmp);
        }
    }
}
