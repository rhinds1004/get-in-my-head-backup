/*
 * Copyright (c) 2017.  $author
 */

package tcss450.uw.edu.getinmyhead;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;


/**
 * displays the text from the selected Libitem and allows the user to control how many letters
 * shall be removed to aid in study.
 * @author Robert Hinds
 * @modified 5/22/2016
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReadTextFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReadTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadTextFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView mitemTextView;
    private int progressScaler = 10;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private SeekBar mSeekBar;

    public ReadTextFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReadTextFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReadTextFragment newInstance(String param1, String param2) {
        ReadTextFragment fragment = new ReadTextFragment();
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

    /**
     * When called, this method will create a view and also set the letters removed to the last
     * setting used by the user.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @author Robert Hinds
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read_text, container, false);
        mitemTextView = (TextView)view.findViewById(R.id.itemText);
        mitemTextView.setText(wordVanisher(getArguments().getString("item_text"),
                getArguments().getInt(getString(R.string.key_last_setting))));
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
        mSeekBar.setProgress(getArguments().getInt(getString(R.string.key_last_setting)));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int processChanged = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mitemTextView.setText(wordVanisher(getArguments().getString("item_text"), progress));
                processChanged = progress * progressScaler;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

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
        void onFragmentInteraction(Uri uri);
    }


    /**
     * Method removes a percentage of the letters from the words in a string based on the length of
     * each word and the desired value inputted by the user.
     * @param stringToVanish
     * @param numOfLettersToVanish
     * @return string with letters removed.
     * @author Robert Hinds
     */
    private String wordVanisher(String stringToVanish, int numOfLettersToVanish){
        String[] strArr = stringToVanish.split("\\P{L}+");
        StringBuilder sb = new StringBuilder(stringToVanish);
        long percentOfLettersToVanish;
        for(int i = 0; i < strArr.length; i++){
            int strLen = strArr[i].length();
            StringBuilder tempSB = new StringBuilder(strArr[i]);
            percentOfLettersToVanish = Math.round(strLen*(numOfLettersToVanish/10.0));
            if(percentOfLettersToVanish > (strLen-1) ){
                for(int j = strLen-1; j > 0; j--){
                    tempSB.setCharAt(j, '_');
                }
            } else {
                for(int j = strLen-1; j > (strLen - percentOfLettersToVanish) - 1; j--){
                    tempSB.setCharAt(j, '_');
                }
            }
            int pos = sb.indexOf(strArr[i]);
            sb.replace(pos, pos + strArr[i].length(), tempSB.toString());
            tempSB.delete(0,tempSB.length());
        }
        return sb.toString();
    }
}