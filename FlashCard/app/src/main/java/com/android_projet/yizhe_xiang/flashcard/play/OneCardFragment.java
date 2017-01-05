package com.android_projet.yizhe_xiang.flashcard.play;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
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

import com.android_projet.yizhe_xiang.flashcard.entity.OneCard;
import com.android_projet.yizhe_xiang.flashcard.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //OneCardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OneCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OneCardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private EditText votreanswer;   //EditText pour saisir la réponse
    public TextView question,answer,level,box,textAnswer,timeText,timeLeft;
    //question  textAnswer  TimeLeft    ---> label
    //level box    conserve level et box d'une carte
    //answer timeText    la bonne réponse et entier du temps rest
    private Button ok,confirm,mp3Button;   //ok pour changer le level, confirm pour vérifier la réponse
    private RadioButton facile,difficile,trivial;    //trois ration bouton
    //private boolean popFlag;    //  normalement false, en cas de le joueur trouve la bonne réponse, on mettre true.
    private boolean timeFlag;  //  premier créer: true! c'est-à-dire le time_counter n'exécute qu'une seule fois.
    // TODO: Rename and change types of parameters
    private OneCard mParam1;  //OneCard
    private int mParam2;     //timeRest en cas de tourner l'écran
    private String mParam3;   //answer vous avez saisi
    private MyCount mc;         //time_counter
    private OnFragmentInteractionListener mListener;
    private ImageView imageView;

    public OneCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param //param1 Parameter 1.
     * @param //param2 Parameter 2.
     * @return A new instance of fragment OneCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OneCardFragment newInstance(OneCard oneCard, int restTime, String answer) {
        OneCardFragment fragment = new OneCardFragment();
        Bundle args = new Bundle();
        //la carte
        args.putParcelable(ARG_PARAM1, oneCard);
        //par défaut 0.  Si restTime > 0, c'est le cas où on tourne l'apparail!
        args.putInt(ARG_PARAM2,restTime);
        //par défaut "" Si length()!=0, c'est le cas où on entre la réponse, on tourne l'apparail!
        args.putString(ARG_PARAM3,answer);
        fragment.setArguments(args);
        Log.d("newInstance===",fragment.toString());
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreate===",this.toString());
        //popFlag=false;
        timeFlag=true;
        if (getArguments() != null) {
            mParam1 = (OneCard) getArguments().get(ARG_PARAM1);
            mParam2 = (int) getArguments().getInt(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_one_card, container, false);
        // --------------------------- initialisation des membres ----------------------
        facile=(RadioButton)view.findViewById(R.id.facile);
        difficile=(RadioButton)view.findViewById(R.id.difficile);
        trivial=(RadioButton)view.findViewById(R.id.trivial);
        ok=(Button)view.findViewById(R.id.levelbouton);
        confirm=(Button)view.findViewById(R.id.confirm);
        question=(TextView)view.findViewById(R.id.cardquestion);
        answer=(TextView)view.findViewById(R.id.cardanswer);
        textAnswer=(TextView)view.findViewById(R.id.textAnswer);
        timeText=(TextView)view.findViewById(R.id.time);
        timeLeft=(TextView)view.findViewById(R.id.timeLeft);
        votreanswer=(EditText)view.findViewById(R.id.votreanswer);
        level=(TextView)view.findViewById(R.id.cardLevel);
        box=(TextView)view.findViewById(R.id.cardBox);
        imageView=(ImageView) view.findViewById(R.id.carimgid);
        mp3Button=(Button)view.findViewById(R.id.mp3button);
        // ---------------------------Fin!!! initialisation des membres ----------------------
        //---------------set value---------------
        question.setText(mParam1.getQuestion().replace("|","'"));
        answer.setText(mParam1.getAnswer());
        level.setText(mParam1.getLevel()+"");
        box.setText(mParam1.getBox()+"");
        if (mParam1.getLevel()==1){
            facile.setChecked(true);
        }else if(mParam1.getLevel()==2){
            difficile.setChecked(true);
        }else if (mParam1.getLevel()==3){
            trivial.setChecked(true);
        }
        mp3Button.setEnabled(false);
        if (!(mParam1.getAudiopath().length()<1)){
            mp3Button.setEnabled(true);
            mp3Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sdPath= Environment.getExternalStorageDirectory()+"/FlashCard";
                    PlayCardActivity playCardActivity=(PlayCardActivity) getActivity();
                    sdPath+="/"+playCardActivity.gameName;
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
            PlayCardActivity playCardActivity=(PlayCardActivity) getActivity();
            sdPath+="/"+playCardActivity.gameName;
            sdPath+="/"+mParam1.getImgpath();
            Log.d("imgPath",sdPath);
            new AfficherImg().execute(sdPath);
        }
        //---------------Fin!!! set value---------------
        //la fonction va exécuter si on cliquer le bouton CONFIRM
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //récupérer la réponse que le joueur saisit
                String myAnswer=votreanswer.getText().toString();
                if (myAnswer.equals(answer.getText().toString())){
                    Toast annonce=Toast.makeText(getActivity(),"Correct!tu es génie!!!",Toast.LENGTH_LONG);
                    annonce.show();
                    //afficher la bonne réponse
                    answer.setVisibility(View.VISIBLE);
                    textAnswer.setVisibility(View.VISIBLE);
                    //on peut pas cliquer CONFIRM
                    confirm.setEnabled(false);
                    //informer l'activité  FlagAlreadyCorrect==1
                    mListener.addTocardAlreadyCorrect();
                    //on peut pas saisir en cas de trouver la bonne
                    votreanswer.setEnabled(false);

                    timeText.setVisibility(View.INVISIBLE);
                    timeLeft.setVisibility(View.INVISIBLE);
                    mc.cancel();
                }else{
                    //la réponse que le joueur saisit n'est pas bonne
                    Toast annonce=Toast.makeText(getActivity(),"essayez encore une fois",Toast.LENGTH_LONG);
                    annonce.show();
                }
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facile.isChecked()){
                    Toast annonce=Toast.makeText(getActivity(),"facile",Toast.LENGTH_LONG);
                    annonce.show();
                    mListener.updateCard(1);
                }else if(difficile.isChecked()){
                    Toast annonce=Toast.makeText(getActivity(),"difficile",Toast.LENGTH_LONG);
                    annonce.show();
                    mListener.updateCard(2);
                }else if(trivial.isChecked()){
                    Toast annonce=Toast.makeText(getActivity(),"trivail",Toast.LENGTH_LONG);
                    annonce.show();
                    mListener.updateCard(3);
                }else{
                    Toast annonce=Toast.makeText(getActivity(),"non select",Toast.LENGTH_LONG);
                    annonce.show();
                }

            }
        });
        //on tourne l'apparil, on garde les données
        if (mParam3.length()!=0){
            votreanswer.setText(mParam3);
        }
        Log.d("oncreateView","onCreateView");
        Log.d("=mgetFAC",mParam1.getFlagAlreadyCorrect()+"");
        Log.d("==restT",mParam2+"");
        Log.d("=TimeText",timeText.getText().toString());
        Log.d("addr",this.toString());
        //  2. FlagAlreadyCorrect==1  quand on créer nouveau fragment, si cette carte est lequel on a trouvé la bonne réponse(Flag==1)
        if (mParam1.getFlagAlreadyCorrect()==1){
            //afficher tout, ne peut pas CONFIRM
            answer.setVisibility(View.VISIBLE);
            textAnswer.setVisibility(View.VISIBLE);
            confirm.setEnabled(false);
            votreanswer.setEnabled(false);
            votreanswer.setText(answer.getText());
            timeText.setVisibility(View.INVISIBLE);
        }else {
            // FlagAlreadyCorrect==2 : ce n'est pas la première fois
            // mParam2==0  : ce n'est pas le cas on tourne l'apparail
            //par exemple  on voir la carte 3, et puis on clique PREVIOUS, carte 2, ensuite on clique NEXT,
            if (mParam1.getFlagAlreadyCorrect()==2&&mParam2==0){
                timeText.setVisibility(View.INVISIBLE);
                confirm.setEnabled(false);
                votreanswer.setEnabled(false);
            }else {
                // timeFlag==false   c'est la première fois
                // mParam2==0  : ce n'est pas le cas on tourne l'apparail
                if (timeFlag&&mParam2==0){
                    //FlagAlreadyCorrect==2
                    timeFlag=false;
                    mListener.addTocardAlreadyWatch();
                    //commencer le time_count
                    timeLeft.setVisibility(View.VISIBLE);
                    int timeleftValue=getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("timeleft",30);
                    mc = new MyCount(timeleftValue*1000, 1000);
                    mc.start();
                }else {   //ici (FlagAlreadyCorrect==2) && (mParam2 != 0) && (timeFlag==false)
                            //********  On tourne l'apparail **************
                        timeLeft.setVisibility(View.VISIBLE);
                        mc = new MyCount(mParam2*1000, 1000);
                        mc.start();
                }
                }
            }
            return view;
        }
        // TODO: Rename method, update argument and hook method into UI event
    /*
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
        }
    }
    */

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

        public interface OnFragmentInteractionListener {
            // TODO: Update argument type and name
            //void onFragmentInteraction(Uri uri);
            //quand on trouve la bonne réponse
            void addTocardAlreadyCorrect();
            //la première fois qu'on voir la carte
            void addTocardAlreadyWatch();
            //quand le temps se coule
            void timeUp();
            //quand on change le level, on doit mis à jour cardLists dans l'activity aussi
            void updateCard(int level);
        }

    /*定义一个倒计时的内部类*/
        class MyCount extends CountDownTimer {
            public MyCount(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);
            }
            @Override
            public void onFinish() {
                timeText.setVisibility(View.INVISIBLE);
                votreanswer.setEnabled(false);
                confirm.setEnabled(false);
                timeLeft.setVisibility(View.INVISIBLE);
                if (getActivity()!=null){
                    mListener.timeUp();
                }
            }
            @Override
            public void onTick(long millisUntilFinished) {
                timeText.setText(millisUntilFinished / 1000 + "");
                //mParam2=(int) millisUntilFinished/1000;
            }
        }

    // retouner le temps reste
    public int getRestTime(){
        String timeLeft=timeText.getText().toString();
        if (timeLeft.length()!=0){
            return Integer.parseInt(timeLeft);
        }else {
            return 0;
        }
    }
    // retourne la réponse que le joueur saisit
    public String getAnswer(){
        String myAnswer=votreanswer.getText().toString();
        if (myAnswer.length()==0){
            return "";
        }else{
            return myAnswer;
        }
    }

    //quand on passer à la suite, on doit cancel time_counter
    public void cancelTimeCounter(){
        if (mc!=null){
            mc.cancel();
        }
        timeText.setText("0");
        Intent intent=new Intent(getContext(),OneCardMusicService.class);
        getActivity().stopService(intent);
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
            imageView.setImageBitmap(bmp);
        }
    }
}
