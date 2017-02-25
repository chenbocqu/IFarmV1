package com.qican.ifarm.ui.control;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.CommonAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.ControlFunction;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.utils.PhoneCallUtils;

import java.io.Serializable;
import java.util.List;

public class ControlListFragment extends Fragment implements View.OnClickListener {
    private View view;
    private String TAG = "ControlListFragment";

    private List<ControlFunction> mData;
    private CommonTools myTool;
    private GridView gridView;
    private ControlFunAdapter mAdapter;
    private ImageView ivCall;
    //要跳转的一系列Activity
    private Class<? extends Activity>[] funActs = new Class[]{
            //分别跳转时，采用这个
            SelectFarmActivity.class,
            SelectFarmActivity.class,
            SelectFarmActivity.class,
            SelectFarmActivity.class,
            SelectFarmActivity.class,
            SelectFarmActivity.class,
            SelectFarmActivity.class,
            SelectFarmActivity.class,
            SelectFarmActivity.class};

    @DrawableRes
    private int[] iconRes = new int[]{//控制功能图标资源
            R.drawable.fun_irrigate,
            R.drawable.fun_air,
            R.drawable.fun_illuminate,
            R.drawable.fun_temperature,
            R.drawable.fun_humidity,
            R.drawable.fun_co2,
            R.drawable.fun_o2,
            R.drawable.fun_sunshade,
            R.drawable.fun_add};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_controllist, container, false);
        initView(view);
        initDatas();
        initEvent();
        return view;
    }

    private void initEvent() {
        ivCall.setOnClickListener(this);
    }

    private void initDatas() {
        mData = IFarmFakeData.getFunList();
        mData.add(new ControlFunction("新功能"));
        mAdapter = new ControlFunAdapter(getActivity(), mData, R.layout.item_control_function);
        gridView.setAdapter(mAdapter);
    }


    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        gridView = (GridView) v.findViewById(R.id.gridView);
        ivCall = (ImageView) v.findViewById(R.id.iv_call);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_call:
                PhoneCallUtils.call(ConstantValue.SERVICE_PHONE);//拨打客服电话
                break;
        }
    }

    class ControlFunAdapter extends CommonAdapter<ControlFunction> {

        public ControlFunAdapter(Context context, List<ControlFunction> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, final ControlFunction item) {
            ImageView ivItem = helper.getView(R.id.iv_item);
            helper.setText(R.id.tv_item, item.getName());
            //如果有网络图标资源，就从网络加载否则加载本地资源
            if (item.getImgUrl() != null) {
                helper.setImageByUrl(R.id.iv_item, item.getImgUrl());
            } else {
                setIconRes(ivItem, iconRes[item.getIndex()]);
            }
            helper.getView(R.id.rl_item).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    myTool.startActivity(funActs[item.getIndex()]);//分别跳转
                    myTool.startActivity(item, SelectFarmActivity.class);
                }
            });
        }

        private void setIconRes(ImageView ivItem, @DrawableRes int res) {
            ivItem.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), res));
        }
    }
}
