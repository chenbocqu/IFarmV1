package com.qican.ifarm.ui_v2.farm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.PermissionManager;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.listener.BeanCBWithTkCk;
import com.qican.ifarm.ui.farm.FarmInfoActivity_;
import com.qican.ifarm.ui_v2.base.FragmentWithOnResume;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.DataBindUtils;
import com.qican.ifarm.utils.ScreenShotUtil;
import com.qican.ifarm.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class FarmInfoFragment extends FragmentWithOnResume implements View.OnClickListener {
    private static final int REQUEST_FOR_FARM_INFO = 1;
    private View view;
    private CommonTools myTool;

    NetRequest netRequest;
    Farm mFarm;
    ImageView ivFarmImg;
    TextView tvName, tvDesc, tvTime;
    List<String> mUrls;
    TakePhoto takePhoto;

    List<String> mPaths;
    List<ImageView> ivList;
    String TAG = "DEBUG";

    @Override
    public void update() {
        super.update();
        mFarm = (Farm) getArguments().getSerializable(FarmListsFragment.FARM_KEY);
        Log.i("DEBUG", "FarmInfoFragment update(), getView()" + view);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: " + view);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            myTool.log("onHiddenChanged");
            initDatas();
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: " + view);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFarm = (Farm) getArguments().getSerializable(FarmListsFragment.FARM_KEY);

//        view = inflater.inflate(R.layout.fragment_farminfo, container, false);
        view = inflater.inflate(R.layout.fragment_farminfo_pad, container, false);
        initView();
        initDatas();
        initEvent();

        return view;
    }

    private void initDatas() {

        if (myTool == null) {
            Log.i("DEBUG", "Farm info fragment myTool == null!");
            return;
        }

        mUrls = new ArrayList<>();
        mPaths = new ArrayList<>();
        mUrls.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2856723681,2411189826&fm=27&gp=0.jpg");
        mUrls.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1665207864,746409922&fm=27&gp=0.jpg");
        mUrls.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1602552054,373587514&fm=27&gp=0.jpg");
        mUrls.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3430625942,2154503364&fm=27&gp=0.jpg");
        mUrls.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1597441762,3207335089&fm=27&gp=0.jpg");
        mUrls.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=292523158,2144826712&fm=27&gp=0.jpg");

        mUrls.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1067103305,4099506914&fm=27&gp=0.jpg");
        mUrls.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=793198046,4140276775&fm=200&gp=0.jpg");
        mUrls.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4227489361,534897407&fm=200&gp=0.jpg");

        Log.i("DEBUG", "initDatas: " + mUrls);
        myTool.showImage(mFarm.getImgUrl(), ivFarmImg, R.mipmap.default_farm_img);

        tvName.setText(mFarm.getName());
        tvDesc.setText(mFarm.getDesc());
        tvTime.setText(TimeUtils.formatDateTime(mFarm.getTime()));

        Label label = mFarm.getLabel();
        List<TextView> tvList = new ArrayList<>();
        tvList.add((TextView) view.findViewById(R.id.tv_label1));
        tvList.add((TextView) view.findViewById(R.id.tv_label2));
        tvList.add((TextView) view.findViewById(R.id.tv_label3));

        DataBindUtils.setLabel(tvList, null, label);

        ivList = new ArrayList<>();
        ivList.add((ImageView) view.findViewById(R.id.iv_farm_img0));
        ivList.add((ImageView) view.findViewById(R.id.iv_farm_img1));
        ivList.add((ImageView) view.findViewById(R.id.iv_farm_img2));
        ivList.add((ImageView) view.findViewById(R.id.iv_farm_img3));
        ivList.add((ImageView) view.findViewById(R.id.iv_farm_img4));
        ivList.add((ImageView) view.findViewById(R.id.iv_farm_img5));
        ivList.add((ImageView) view.findViewById(R.id.iv_farm_img6));
        ivList.add((ImageView) view.findViewById(R.id.iv_farm_img7));
        ivList.add((ImageView) view.findViewById(R.id.iv_farm_img8));

        setHeight();

        DataBindUtils.setImgs(getActivity(), ivList, mUrls);
    }

    private void setHeight() {

        float ratio = 0.5f / 3f; // 平板

        myTool.setHeightByWindow(view.findViewById(R.id.ll_imgs1), ratio);
        myTool.setHeightByWindow(view.findViewById(R.id.ll_imgs2), ratio);
        myTool.setHeightByWindow(view.findViewById(R.id.ll_imgs3), ratio);

    }

    private void initEvent() {
        view.findViewById(R.id.ll_farm_data).setOnClickListener(this);
        view.findViewById(R.id.ll_share).setOnClickListener(this);
        view.findViewById(R.id.ll_add_photo).setOnClickListener(this);
        view.findViewById(R.id.ll_modify).setOnClickListener(this);
        ivFarmImg.setOnClickListener(this);
    }

    private void initView() {
        takePhoto = getTakePhoto();
        myTool = new CommonTools(getActivity());
        netRequest = new NetRequest(getActivity());
        ivFarmImg = (ImageView) view.findViewById(R.id.iv_img);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        tvDesc = (TextView) view.findViewById(R.id.tv_desc);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_img:
                myTool.previewImg(ivFarmImg, mFarm.getImgUrl());
                break;

            case R.id.ll_farm_data:
                myTool.startActivity(mFarm, DeviceDataListActivity.class);
                break;

            case R.id.ll_add_photo:
                try {
                    takePhoto.onPickMultiple(9);
                } catch (Exception e) {
                    myTool.log("Error!");
                }
                break;

            case R.id.ll_share:

                // 截取整个屏幕
//                Bitmap bitmap = ScreenShotUtil.captureScreen(getActivity());
                Bitmap bitmap = ScreenShotUtil.getBitmapFromView(view);
                if (bitmap == null) {
                    myTool.showInfo("分享失败，稍后重试！");
                    return;
                }

                if (!ScreenShotUtil.saveImageToGallery(getActivity(), bitmap))
                    myTool.showInfo("保存失败！");

                break;

            // 修改农场信息
            case R.id.ll_modify:

                Intent intent = new Intent(getActivity(), FarmInfoActivity_.class);

                //跳转到详细信息界面
                Bundle bundle = new Bundle();
                bundle.putSerializable(mFarm.getClass().getName(), mFarm);

                intent.putExtras(bundle);

                startActivityForResult(intent, REQUEST_FOR_FARM_INFO);

//                myTool.startActivity(mFarm, FarmInfoActivity_.class);
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_FOR_FARM_INFO:

                if (resultCode == getActivity().RESULT_OK) {

                    // 刷新数据
                    OkHttpUtils.post().url(myTool.getServAdd() + "farm/getFarmColletorsList")
                            .addParams("userId", myTool.getUserId())
                            .addParams("signature", myTool.getToken())
                            .addParams("farmId", mFarm.getId())
                            .build()
                            .execute(new BeanCBWithTkCk<List<com.qican.ifarm.beanfromservice.Farm>>() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    e.printStackTrace();
                                    myTool.log("农场列表e：" + e.getMessage());
                                }

                                @Override
                                public void onResponse(List<com.qican.ifarm.beanfromservice.Farm> farmList, int id) {
                                    //处理token失效问题
                                    if (farmList == null) {
                                        myTool.showTokenLose();
                                        return;
                                    }
                                    if (farmList.size() != 1) return;
                                    mFarm = new Farm(farmList.get(0));
                                    initDatas();
                                }
                            });
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(getActivity(), type, invokeParam, this);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    InvokeParam invokeParam;

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        mPaths.clear();
        List<TImage> imgs = result.getImages();
        for (TImage image : imgs)
            mPaths.add(image.getPath());
        DataBindUtils.setImgs(getActivity(), ivList, mPaths);
    }

}
