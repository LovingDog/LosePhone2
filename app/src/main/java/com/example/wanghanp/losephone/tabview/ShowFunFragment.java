package com.example.wanghanp.losephone.tabview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wanghanp.base.bean.TakePhotoBean;
import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.losephone.map.MapViewFragment;
import com.example.wanghanp.myview.ShowPhotosActivity;
import com.example.wanghanp.myview.ZoomImageView;
import com.example.wanghanp.util.MobileInfo;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowFunFragment.OnFragmentInteractionListener} interfacelistener
 * to handle interaction events.
 * Use the {@link ShowFunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowFunFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    @InjectView(R.id.recyclerview_takephotos)
    public RecyclerView mTakePhotoRecyclerView;

    private CommonAdapter<TakePhotoBean> mCommonAdapter;
    public int mSpanCount = 10;
    private int mScreenWidth;
    private List<TakePhotoBean> mPath;
    public ArrayList<String> mList;
    private MapViewFragment mMapViewFragment;
    private FragmentTransaction mFragmentTransaction;

    public ShowFunFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowFunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowFunFragment newInstance(List<TakePhotoBean> mPath, String param2) {
        ShowFunFragment fragment = new ShowFunFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        args.putSerializable(ARG_PARAM1, (Serializable) mPath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPath = (List<TakePhotoBean>) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        ButterKnife.inject(this, view);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        mTakePhotoRecyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTakePhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mSpanCount));//设置为listview的布局
        mTakePhotoRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置动画
        mScreenWidth = MobileInfo.getInstance(getActivity()).getScreenWidth();
//        mTakePhotoRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//添加分割线
        initTakePhotosAdpater(mPath);
        initFragment();

    }
    private void initFragment() {
        mMapViewFragment = MapViewFragment.getInstance(mPath.size() == 0 ? " ":mPath.get(0).getPath());
        mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.add(R.id.container,mMapViewFragment);
        mFragmentTransaction.commit();
    }

    public void initTakePhotosAdpater(List<TakePhotoBean> path) {

        mCommonAdapter = new CommonAdapter<TakePhotoBean>(getActivity(), R.layout.list_item_takephotos, path) {
            @Override
            protected void convert(ViewHolder holder, TakePhotoBean takePhotoBean, int position) {
                ZoomImageView img = holder.getView(R.id.iv_takephotos);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP );
                img.setLayoutParams(new LinearLayout.LayoutParams(mScreenWidth / mSpanCount, mScreenWidth / mSpanCount));
                Glide.with(mContext).load(takePhotoBean.getPath())
                        .override(mScreenWidth / mSpanCount, mScreenWidth / mSpanCount)
                        .into(img);
            }
        };
        mTakePhotoRecyclerView.setAdapter(mCommonAdapter);
        mCommonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(position);
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mUploadPresenter.upLoad();
//                    }
//                },3000);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    private void startActivity(int position) {
        mList = new ArrayList<String>();
        for (TakePhotoBean bean :
                mPath) {
            mList.add(bean.getPath());
        }
        startActivity(new Intent(getActivity(), ShowPhotosActivity.class)
                .putExtra(ShowPhotosActivity.LIST_INDEX, position)
                .putStringArrayListExtra(ShowPhotosActivity.LIST_EXTRA, mList));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

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
