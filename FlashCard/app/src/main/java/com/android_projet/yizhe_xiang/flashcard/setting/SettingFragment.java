package com.android_projet.yizhe_xiang.flashcard.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android_projet.yizhe_xiang.flashcard.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {//@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //private Button musicOff,musicOn;
    private Spinner bgmlist;
    private Switch switcher;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        View view=inflater.inflate(R.layout.fragment_setting, container, false);
        switcher = (Switch) view.findViewById(R.id.switcher);
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    int bgmSpf=getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("bgm",0);
                    Intent nintent = new Intent(getContext(), BackgroundMusicService.class);
                    nintent.putExtra("bgm",bgmSpf);
                    getContext().startService(nintent);
                    bgmlist.setEnabled(false);
                    SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putInt("bgmonoroff",1);
                    editor.commit();
                }
                else{
                    Intent nintent = new Intent(getContext(), BackgroundMusicService.class);
                    getContext().stopService(nintent);
                    bgmlist.setEnabled(true);
                    SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putInt("bgmonoroff",0);
                    editor.commit();
                }
            }
        });
        /*
        dans ce partie,on résoud deux questions:
        1: l'état de BackgroudMusic   ON  or  OFF
        2: A laquelle chanson le joueur veut jouer
         */
        //************************musique init*******************************
        //musicOn=(Button)view.findViewById(R.id.musicon);
        int bgmOnOrOff=getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("bgmonoroff",0);
        /*musicOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bgmSpf=getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("bgm",0);
                Intent nintent = new Intent(getContext(), BackgroundMusicService.class);
                nintent.putExtra("bgm",bgmSpf);
                getContext().startService(nintent);
                musicOn.setEnabled(false);
                musicOff.setEnabled(true);
                bgmlist.setEnabled(false);
                SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putInt("bgmonoroff",1);
                editor.commit();
            }
        });
        musicOff=(Button)view.findViewById(R.id.musicoff);
        musicOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nintent = new Intent(getContext(), BackgroundMusicService.class);
                getContext().stopService(nintent);
                musicOn.setEnabled(true);
                musicOff.setEnabled(false);
                bgmlist.setEnabled(true);
                SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putInt("bgmonoroff",0);
                editor.commit();
            }
        });*/
        bgmlist=(Spinner)view.findViewById(R.id.bgmlist);
        bgmlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putInt("bgm",position);
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        int bgmSpf=getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("bgm",0);
        bgmlist.setSelection(bgmSpf);

        if (bgmOnOrOff==0){
            switcher.setChecked(false);
            //musicOn.setEnabled(true);
            //musicOff.setEnabled(false);
            bgmlist.setEnabled(true);
        }else{
            switcher.setChecked(true);
            //musicOn.setEnabled(false);
            //musicOff.setEnabled(true);
            bgmlist.setEnabled(false);
        }
        //************************musique FIN*******************************
        /*
        il y a combien de box? combien de fois que le joueur peut répondre correctement à une carte!
         */
        //************************boxmax init*******************************
        Spinner boxmax=(Spinner)view.findViewById(R.id.boxmax);
        boxmax.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putInt("boxmax",position+7);
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        int boxmaxValue=getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("boxmax",7);
        boxmax.setSelection(boxmaxValue-7);
        //************************boxmax FIN*******************************
        //************************boxfacile init*******************************
        Spinner boxfacile=(Spinner)view.findViewById(R.id.boxfacile);
        boxfacile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putInt("boxfacile",position+4);
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        int boxfacileValue=getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("boxfacile",4);
        boxfacile.setSelection(boxfacileValue-4);
        //************************boxfacile FIN*******************************
        //************************boxdifficile init*******************************
        Spinner boxdifficile=(Spinner)view.findViewById(R.id.boxdifficile);
        boxdifficile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putInt("boxdifficile",position+1);
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        int boxdifficileValue=getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("boxdifficile",1);
        boxdifficile.setSelection(boxdifficileValue-1);
        //************************boxdifficile FIN*******************************
        //************************timeleft init*******************************
        final Spinner timeleft=(Spinner)view.findViewById(R.id.tileleftspinner);
        timeleft.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                TextView textView=(TextView)view;
                int timeleftnow=30;
                switch (position){
                    case 0:
                        timeleftnow=15;
                        break;
                    case 1:
                        timeleftnow=20;
                        break;
                    case 2:
                        timeleftnow=25;
                        break;
                    case 3:
                        timeleftnow=30;
                        break;
                    case 4:
                        timeleftnow=5;
                        break;
                }
                editor.putInt("timeleft",timeleftnow);
                editor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        int timeleftValue=getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("timeleft",30);
        if (timeleftValue==15){
            timeleft.setSelection(0);
        }else if (timeleftValue==20){
            timeleft.setSelection(1);
        } else if (timeleftValue==25){
            timeleft.setSelection(2);
        }else if(timeleftValue==30){
            timeleft.setSelection(3);
        }else {
            timeleft.setSelection(4);
        }
        //************************timeleft FIN*******************************
        //************************reminder init******************************
        final EditText reminderContent = (EditText) view.findViewById(R.id.reminder);
        reminderContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String interText = editable.toString();
                if (interText.length()!= 0){
                    int intervalle = Integer.parseInt(interText);
                    SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putInt("intervalle",intervalle);
                    editor.commit();
                }else {
                    SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    editor.putInt("intervalle",0);
                    editor.commit();
                    reminderContent.setText(0+"");
                }
            }
        });
        int reminderText=getActivity().getSharedPreferences("myFlashCard",Activity.MODE_PRIVATE).getInt("intervalle",7);
        reminderContent.setText(reminderText+"");
        //************************reminder FIN*******************************
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
}
