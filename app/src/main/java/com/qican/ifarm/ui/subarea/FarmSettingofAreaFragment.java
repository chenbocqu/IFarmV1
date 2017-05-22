package com.qican.ifarm.ui.subarea;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.ui.camera.CameraTestActivity_;
import com.qican.ifarm.ui.farm.FarmInfoActivity_;
import com.qican.ifarm.ui.farm.SensorListActivity_;
import com.qican.ifarm.ui.node.FarmActivity;
import com.qican.ifarm.utils.CommonTools;

import java.util.ArrayList;
import java.util.List;

public class FarmSettingofAreaFragment extends Fragment implements View.OnClickListener {
    private static final int LABEL_MAX_SHOW_LEN = 20;
    private View view;
    private String TAG = "FarmSettingofAreaFragment";
    private RelativeLayout rlFarmInfo, rlDevice,rlCamera;
    private Subarea mArea;
    private Farm mFarm;
    private ImageView ivFarmImg;
    private List<TextView> tvLabels;
    private TextView tvLabel1, tvLabel2, tvLabel3, tvMore, tvName, tvDesc;

    private CommonTools myTool;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_control, container, false);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initData() {
        Bundle bundle = getArguments();
        mArea = (Subarea) bundle.getSerializable(SubareaActivity.KEY_AREA_INFO);
        mFarm = mArea.getFarm();

        tvLabels = new ArrayList<>();
        tvLabels.add(tvLabel1);
        tvLabels.add(tvLabel2);
        tvLabels.add(tvLabel3);

        tvName.setText(mFarm.getName());
        tvDesc.setText(mFarm.getDesc());
        setTvByLabel(mFarm.getLabelList());
        // 设置背景图片
        if (mFarm.getImgUrl() != null) {
            myTool.showImage(mFarm.getImgUrl(),
                    ivFarmImg,
                    R.drawable.defaultbg);
        }
    }

    private void initEvent() {
        rlFarmInfo.setOnClickListener(this);
        rlDevice.setOnClickListener(this);
        rlCamera.setOnClickListener(this);
    }


    private void initView(View v) {
        myTool = new CommonTools(getActivity());
        rlFarmInfo = (RelativeLayout) v.findViewById(R.id.rl_farminfo);
        rlDevice = (RelativeLayout) v.findViewById(R.id.rl_device_manage);
        rlCamera= (RelativeLayout) v.findViewById(R.id.rl_camera);

        tvLabel1 = (TextView) v.findViewById(R.id.tvLabel1);
        tvLabel2 = (TextView) v.findViewById(R.id.tvLabel2);
        tvLabel3 = (TextView) v.findViewById(R.id.tvLabel3);
        tvMore = (TextView) v.findViewById(R.id.tvMore);

        tvName = (TextView) v.findViewById(R.id.tv_name);
        tvDesc = (TextView) v.findViewById(R.id.tv_desc);
        ivFarmImg = (ImageView) v.findViewById(R.id.iv_farm_img);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_farminfo:
                myTool.startActivity(mFarm, FarmInfoActivity_.class);
                break;
            case R.id.rl_device_manage:
                myTool.startActivity(mFarm, SensorListActivity_.class);
                break;
            case R.id.rl_camera:
                myTool.startActivity(CameraTestActivity_.class);
                break;
        }
    }


    /**
     * 通过数据设置标签显示
     *
     * @param labelList
     */
    private void setTvByLabel(List<String> labelList) {
        boolean showMore = false;
        //全部设置为不见
        for (int i = 0; i < tvLabels.size(); i++) {
            tvLabels.get(i).setVisibility(View.GONE);
        }
        int totalLen = 0;
        for (int i = 0; i < tvLabels.size() && i < labelList.size(); i++) {
            totalLen = totalLen + labelList.get(i).length();
            if (totalLen > LABEL_MAX_SHOW_LEN) {
                showMore = true;
                break;
            }
            tvLabels.get(i).setVisibility(View.VISIBLE);
            tvLabels.get(i).setText(labelList.get(i));
        }
        // 本身比标签容量多，或者显示不够完全，则显示更多
        tvMore.setVisibility(
                labelList.size() > tvLabels.size() ||
                        showMore ?
                        View.VISIBLE : View.GONE);

    }
}
