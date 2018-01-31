package com.example.wanghanp.losephone.tabview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.wanghanp.base.preference.BasePreference;
import com.example.wanghanp.base.preference.SettingPreference;
import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.myview.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interfacelistener
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MESSAGE_REFRESH_CONFIRM = 1001;

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
    NoScrollListView mListView;
    @InjectView(R.id.edit_content)
    EditText mContent;
    @InjectView(R.id.lay_editor)
    LinearLayout mContentlay;
    @InjectView(R.id.iv_confirm)
    ImageView mConfirm;
    @InjectView(R.id.iv_confirm2)
    ImageView mConfirm2;

    private boolean mSafeEnable = false;
    private boolean mMusicEnable = true;
    private boolean mElectricEnable = true;
    private boolean mMapEnable = true;
    private SettingPreference mSettingPreference;

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
        initView();
        return view;
    }
    private void initView() {
        mSettingPreference = new SettingPreference(getActivity(), BasePreference.Preference.APP_SETTING);
        checkeEnable(mMap,mSettingPreference.isRemindLocation());
    }

    @OnClick({R.id.bt_safe,R.id.bt_music,R.id.bt_electric,R.id.bt_map,R.id.iv_confirm,R.id.iv_confirm2})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.bt_safe:
                checkeEnable(mSafe,mSafeEnable);
                mSafeEnable = !mSafeEnable;
                ((MainActivity)getActivity()).mShowSafeBt = mSafeEnable;
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
                mSettingPreference.setMapRemind(mMapEnable);
                if (mMapEnable) {
                    startActivity(new Intent(getActivity(),LocationRemindActivity.class));
                }
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
     * This interfacelistener must be implemented by activities that contain this
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
