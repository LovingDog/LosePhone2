package com.example.wanghanp.losephone.tabview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.wanghanp.losephone.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @InjectView(R.id.bt_safe)
    Button mSafe;
    @InjectView(R.id.bt_music)
    Button mMusic;
    @InjectView(R.id.bt_electric)
    Button mElectic;
    @InjectView(R.id.bt_map)
    Button mMap;
    @InjectView(R.id.autotext_search)
    EditText mAutoCompleteTextView;
    @InjectView(R.id.lay_search)
    RelativeLayout mSearchLay;
    @InjectView(R.id.listview)
    ListView mListView;
    @InjectView(R.id.edit_content)
    EditText mContent;

    private boolean mSafeEnable = false;
    private boolean mMusicEnable = true;
    private boolean mElectricEnable = true;
    private boolean mMapEnable = true;
    private ArrayList<String> mSearchReulst;
    private ArrayAdapter<String> mAdapter;
    private List<Tip> mTipList;


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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.inject(this, view);
        mSearchReulst = new ArrayList<>();
        initTextChangeListener();
        initCompleteText(mSearchReulst);
        return view;
    }

    private void initCompleteText(final ArrayList<String> mSearchReulst) {

        mAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,mSearchReulst);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = mSearchReulst.get(i).toString();
                if (!name.equals(mAutoCompleteTextView.getText().toString())) {
                    mAutoCompleteTextView.setText(mSearchReulst.get(i).toString());
                }
                mListView.setVisibility(View.GONE);
            }
        });
    }

    private void doSearch(String city) {
        InputtipsQuery inputquery = new InputtipsQuery(city, "");
        inputquery.setCityLimit(true);//限制在当前城市

        Inputtips inputTips = new Inputtips(getActivity().getApplicationContext(), inputquery);
        inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> list, int i) {
                Log.d("wanghp007", "onGetInputtips: list.size() = " +list.size());
                mTipList = list;
                mSearchReulst.clear();
                for (int j = 0; j < list.size(); j++) {
                    Tip tip = list.get(j);
                    mSearchReulst.add(tip.getDistrict()+tip.getName());
                }
                if (mAdapter != null) {
//                    initCompleteText(mSearchReulst);
//                    mAutoCompleteTextView.re
                    mListView.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

        inputTips.requestInputtipsAsyn();
    }

    private void initTextChangeListener() {
        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                doSearch(editable.toString());
                mAutoCompleteTextView.setSelection(editable.toString().length());
            }
        });
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick({R.id.bt_safe,R.id.bt_music,R.id.bt_electric,R.id.bt_map})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.bt_safe:
                checkeEnable(mSafe,mSafeEnable);
                mSafeEnable = !mSafeEnable;
                break;

            case R.id.bt_music:
                checkeEnable(mMusic,mMusicEnable);
                mMusicEnable = !mMusicEnable;
                break;

            case R.id.bt_electric:
                checkeEnable(mElectic,mElectricEnable);
                mElectricEnable = !mElectricEnable;
                break;

            case R.id.bt_map:
                checkeEnable(mMap,mMapEnable);
                mMapEnable = !mMapEnable;
                break;

        }
    }

    private void checkeEnable(View view,boolean enable){
        if (enable) {
            view.setBackgroundResource(R.mipmap.offline_diagnose_unable);
        } else {
            view.setBackgroundResource(R.mipmap.offline_diagnose_enable);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
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
}
