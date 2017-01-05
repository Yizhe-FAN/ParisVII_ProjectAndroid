package com.android_projet.yizhe_xiang.flashcard.manage;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android_projet.yizhe_xiang.flashcard.database.FlashCardProvider;
import com.android_projet.yizhe_xiang.flashcard.entity.OneCard;
import com.android_projet.yizhe_xiang.flashcard.R;

import java.util.HashSet;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowCardsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowCardsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowCardsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int adapterId;

    public EditText search;
    private ListView cardList;
    private MySimpleCursorAdapterShow adapterCardList;
    private MySimpleCursorAdapterCheckBox adapterMulDelete;
    public LoaderManager.LoaderCallbacks<Cursor> listViewCallBack;

    private Button multiDelete;

    private OnFragmentInteractionListener mListener;

    public ShowCardsFragment() {
        Log.d("++++", "ShowCardsFragment: new fragment");
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowCardsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowCardsFragment newInstance(String param1, String param2) {
        ShowCardsFragment fragment = new ShowCardsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("++++", "onCreate: showCardsFragment");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("++++", "onDestroy: showCardsFragment");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("++++", "onDestroyView: showCardsFragment");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("++++", "onSaveInstanceState: showCardsFragment");
        outState.putString("search", search.getText().toString());
        outState.putInt("adapter",adapterId);

        long[] array = adapterMulDelete.toLongArray();
        if (array == null) {
            Log.d("----", "saveInstanceState empty array");
        }
        if (array != null) {
            outState.putLongArray("selected", array);
            Log.d("----", "putLongArray size=" + array.length);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("++++", "onActivityCreated: showCardsFragment");
        Log.d("!!!!", "showCardsFragment "+getActivity().toString());

        if(savedInstanceState != null){
            //recover the text in search view
            search.setText(savedInstanceState.getString("search"));
            Log.d("++++", "onActivityCreated: "+savedInstanceState.getString("search"));

            //recover the selected item de delete mode
            long[] array = savedInstanceState.getLongArray("selected");
            if (array == null) {
                Log.d("----", "onCreate array is null");
            } else {
                Log.d("----", "onCreate array size = " + array.length);
            }
            if (array != null) {
                adapterMulDelete.setLongArray(array);
            }

            //recover adapter according mode
            adapterId = savedInstanceState.getInt("adapter");
            if(adapterId == 1){
                cardList.setAdapter(adapterMulDelete);
                multiDelete.setEnabled(true);
            }
            else{
                cardList.setAdapter(adapterCardList);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_cards, container, false);
        Log.d("++++", "onCreateView: showCardsfragment");

        multiDelete = (Button)v.findViewById(R.id.multiDelete);
        multiDelete.setEnabled(false);
        multiDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long[] idArray = adapterMulDelete.toLongArray();
                if (idArray == null) {
                    Log.d("+++", "idArray is null in supprimerLivres");
                    return;
                }
                ContentResolver contentResolver = getActivity().getContentResolver();

                for (long l : idArray) {
                    Uri.Builder builder = new Uri.Builder();
                    builder = builder.scheme("content")
                            .authority(FlashCardProvider.authority)
                            .appendPath("deleteonecard")
                            .appendPath(CardManageActivity.gameName);
                    Uri uri = ContentUris.appendId(builder, l).build();
                    contentResolver.delete(uri, null, null);
                }
                adapterMulDelete.clear();

                String temp = search.getText().toString();
                if( temp.length() != 0) {
                    Bundle args = new Bundle();
                    args.putCharSequence("filter", temp);
                    getLoaderManager().restartLoader(0, args, listViewCallBack);
                }
                else{
                    getLoaderManager().restartLoader(0, null, listViewCallBack);
                }
            }
        });

        cardList = (ListView)v.findViewById(R.id.cardList);
        adapterCardList = new MySimpleCursorAdapterShow(getContext(),
                R.layout.list_show_item,
                null,new String[]{"answer","box"},
                new int[]{R.id.itemAnswer,R.id.itemBox},0);

        adapterMulDelete = new MySimpleCursorAdapterCheckBox(getContext(),
                R.layout.list_item, null,
                new String[]{"answer","box"},
                new int[]{R.id.itemAnswer,R.id.itemBox},0);

        cardList.setAdapter(adapterCardList);

        search = (EditText)v.findViewById(R.id.searchText);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if( s.length() != 0) {
                    Bundle args = new Bundle();
                    args.putCharSequence("filter", s);
                    getLoaderManager().restartLoader(0, args, listViewCallBack);
                }else{
                    getLoaderManager().restartLoader(0, null, listViewCallBack);
                }
            }
        });


        listViewCallBack = new LoaderManager.LoaderCallbacks<Cursor>(){
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                if(args == null) {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("content").authority(FlashCardProvider.authority).appendPath("showcards").appendPath(CardManageActivity.gameName);
                    Uri uri = builder.build();
                    return new CursorLoader(getContext(), uri, null, null, null, null);
                }
                else{
                    String value = "%"+args.getCharSequence("filter").toString()+"%";
                    Log.d("filter", "onCreateLoader: "+value);
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("content").authority(FlashCardProvider.authority).appendPath("showcards").appendPath(CardManageActivity.gameName);
                    Uri uri = builder.build();
                    Log.d("filter", "onCreateLoader: "+uri.toString());
                    return new CursorLoader(getContext(),uri,null,"question like ?",new String[]{value},null);
                }
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                data.moveToFirst();
                Log.d("number de data", "onLoadFinished: "+data.getCount());

                adapterCardList.swapCursor(data);
                adapterMulDelete.swapCursor(data);

            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                adapterCardList.swapCursor(null);

            }
        };

        getLoaderManager().initLoader(0,null,listViewCallBack);

        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor c = adapterCardList.getCursor();
                c.moveToPosition(position);
                OneCard oneCard = new OneCard(c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getInt(5),
                        c.getInt(6), 0);
                mListener.oneCardSelected(oneCard);
                //c.close();
            }
        });


        return v;
    }

    public void changeTypeList(int model){
        if(model == 0){
            adapterId = 0;
            cardList.setAdapter(adapterCardList);
            multiDelete.setEnabled(false);
        }
        else {
            adapterId = 1;
            cardList.setAdapter(adapterMulDelete);
            multiDelete.setEnabled(true);
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
        void oneCardSelected(OneCard oneCard);
    }

    private static class MySimpleCursorAdapterCheckBox extends SimpleCursorAdapter {

        /** un HashSet pour stocker les id des items "checkes" par l'utilisateur*/
        private HashSet<Long> mSetId = new HashSet<>();

        MySimpleCursorAdapterCheckBox(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        private static class Holder {
            long id;
            ImageView checkBox;
            ImageView imageCard;
            ImageView audioCard;
            TextView levelText;
            TextView question;
        }

        /*transformer HashSet en long[] */
        long[] toLongArray() {
            if (mSetId.isEmpty())
                return null;
            long[] tab = new long[mSetId.size()];

            int i = 0;
            for (Long l : mSetId) {
                tab[i++] = l;
            }
            return tab;
        }

        void clear() {
            mSetId.clear();
        }

        /* transformer long[] en HashSet */
        void setLongArray(long[] array) {
            if (array == null || array.length == 0) {
                //Log.d(LOG, "void array setLongArray");
                return;
            }
            mSetId.clear();

            for (long l : array)
                mSetId.add(l);
            //Log.d(LOG, "setLongArray in adapter size=" + mSetId.size());
        }


        /* creation de view laisse a la classe mere,
        ici on cree un holder et on l'attache au view
         */
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            /* laisser a la classe mere la creation du vue */
            View view = super.newView(context, cursor, parent);
            //View view = mInflater.inflate(R.layout.list_item, parent, false);
            /* mettre en place le Holder */
            final Holder holder = new Holder();
            holder.question=(TextView)view.findViewById(R.id.itemQuestion);
            holder.checkBox = (ImageView) view.findViewById(R.id.itemCheckBox);
            holder.imageCard = (ImageView) view.findViewById(R.id.itemImage);
            holder.audioCard = (ImageView)view.findViewById(R.id.itemAudio);
            holder.levelText = (TextView)view.findViewById(R.id.itemLevel);
            /* attacher le holder au vue comme tag */
            view.setTag(holder);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.checkBox.isEnabled()){
                        holder.checkBox.setEnabled(false);
                    }
                    else{
                        holder.checkBox.setEnabled(true);
                    }

                    if(holder.checkBox.isEnabled())
                        mSetId.add(holder.id);
                    else
                        mSetId.remove(holder.id);
                }
            });

            return view;
        }



        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            /* laisser la classe mere a mettre les donnees texte dans la vue.*/

            super.bindView(view, context, cursor);
            /* Mettre a jour id et l'etat de CheckBox. */
            Holder holder = (Holder) view.getTag();
            holder.id = cursor.getLong(cursor.getColumnIndex("_id"));
            holder.checkBox.setEnabled(mSetId.contains(holder.id));
            holder.question.setText(cursor.getString(1).replace("|","'"));
            if (cursor.getString(2).length() < 1){
                holder.imageCard.setEnabled(false);
            }
            else {
                holder.imageCard.setEnabled(true);
            }

            if(cursor.getString(3).length() < 1){
                holder.audioCard.setEnabled(false);
            }
            else{
                holder.audioCard.setEnabled(true);
            }

            int level = cursor.getInt(5);
            switch (level){
                case 0:
                    holder.levelText.setText("Normal");
                    break;
                case 1:
                    holder.levelText.setText("Easy");
                    break;
                case 2:
                    holder.levelText.setText("Difficult");
                    break;
                case 3:
                    holder.levelText.setText("Trivial");
                    break;
            }

        }
    }/* end MySimpleCursorAdapter definition */

    private static class MySimpleCursorAdapterShow extends SimpleCursorAdapter {

        MySimpleCursorAdapterShow(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        private static class Holder {
            ImageView imageCard;
            ImageView audioCard;
            TextView levelText;
            TextView question;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = super.newView(context, cursor, parent);
            final Holder holder = new Holder();
            holder.question=(TextView)view.findViewById(R.id.itemQuestion);
            holder.imageCard = (ImageView) view.findViewById(R.id.itemImage);
            holder.audioCard = (ImageView)view.findViewById(R.id.itemAudio);
            holder.levelText = (TextView)view.findViewById(R.id.itemLevel);
            view.setTag(holder);

            return view;
        }


        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            super.bindView(view, context, cursor);
            Holder holder = (Holder) view.getTag();
            holder.question.setText(cursor.getString(1).replace("|","'"));
            if (cursor.getString(2).length() < 1){
                holder.imageCard.setEnabled(false);
            }
            else {
                holder.imageCard.setEnabled(true);
            }

            if(cursor.getString(3).length() < 1){
                holder.audioCard.setEnabled(false);
            }
            else{
                holder.audioCard.setEnabled(true);
            }

            int level = cursor.getInt(5);
            switch (level){
                case 0:
                    holder.levelText.setText("Normal");
                    break;
                case 1:
                    holder.levelText.setText("Easy");
                    break;
                case 2:
                    holder.levelText.setText("Difficult");
                    break;
                case 3:
                    holder.levelText.setText("Trivial");
                    break;
            }

        }
    }

}
