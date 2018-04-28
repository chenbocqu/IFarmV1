// 九宫格控件
package com.qican.ifarm.ui_v2.farm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.lwkandroid.widget.ninegridview.NineGirdImageContainer;
import com.lwkandroid.widget.ninegridview.NineGridBean;
import com.lwkandroid.widget.ninegridview.NineGridView;
import com.previewlibrary.GPreviewBuilder;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ImageAdapter;
import com.qican.ifarm.adapter.ImageLoader;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Img;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.bean.MyNineGridBean;
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

public class FarmInfov2Fragment extends FragmentWithOnResume implements View.OnClickListener {
    private static final int REQUEST_FOR_FARM_INFO = 1;
    private View view;
    private CommonTools myTool;

    NetRequest netRequest;
    Farm mFarm;
    ImageView ivFarmImg;
    TextView tvName, tvDesc, tvTime;
    List<Img> mUrls;
    TakePhoto takePhoto;

    //    NineGridImageView<Img> mNineGridView;
    NineGridView mNineGridView;
    ImageAdapter mAdapter;
    List<NineGridBean> mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFarm = (Farm) getArguments().getSerializable(FarmListsFragment.FARM_KEY);

        view = inflater.inflate(R.layout.fragment_farminfov2, container, false);
        initView();
        initDatas();
        initEvent();

        return view;
    }

    private void initDatas() {

        if (myTool == null) return;
        mData = new ArrayList<>();

        mUrls = new ArrayList<>();
        mUrls.add(new Img("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2856723681,2411189826&fm=27&gp=0.jpg"));
        mUrls.add(new Img("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1665207864,746409922&fm=27&gp=0.jpg"));
        mUrls.add(new Img("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1602552054,373587514&fm=27&gp=0.jpg"));
//        mUrls.add("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3430625942,2154503364&fm=27&gp=0.jpg");
//        mUrls.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1597441762,3207335089&fm=27&gp=0.jpg");
//        mUrls.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=292523158,2144826712&fm=27&gp=0.jpg");

        mUrls.add(new Img("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1067103305,4099506914&fm=27&gp=0.jpg"));
        mUrls.add(new Img("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=793198046,4140276775&fm=200&gp=0.jpg"));
        mUrls.add(new Img("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4227489361,534897407&fm=200&gp=0.jpg"));


        for (int i = 0; i < mUrls.size(); i++) {
            MyNineGridBean bean = new MyNineGridBean(mUrls.get(i).getUrl(), mUrls.get(i).getUrl(), mUrls.get(i));
            mData.add(bean);
        }

        myTool.showImage(mFarm.getImgUrl(), ivFarmImg, R.mipmap.default_farm_img);
        tvName.setText(mFarm.getName());
        tvDesc.setText(mFarm.getDesc());
        tvTime.setText(TimeUtils.formatDateTime(mFarm.getTime()));

        mAdapter = new ImageAdapter();

        Label label = mFarm.getLabel();
        List<TextView> tvList = new ArrayList<>();
        tvList.add((TextView) view.findViewById(R.id.tv_label1));
        tvList.add((TextView) view.findViewById(R.id.tv_label2));
        tvList.add((TextView) view.findViewById(R.id.tv_label3));

        DataBindUtils.setLabel(tvList, null, label);

        //设置图片加载器，这个是必须的，不然图片无法显示
        mNineGridView.setImageLoader(new ImageLoader());
//设置显示列数，默认3列
        mNineGridView.setColumnCount(3);
//设置是否为编辑模式，默认为false
        mNineGridView.setIsEditMode(false);
//设置单张图片显示时的尺寸，默认100dp
        mNineGridView.setSingleImageSize(150);
//设置单张图片显示时的宽高比，默认1.0f
        mNineGridView.setSingleImageRatio(1f);
//设置最大显示数量，默认9张
        mNineGridView.setMaxNum(9);
//设置图片显示间隔大小，默认3dp
        mNineGridView.setSpcaeSize(3);
//设置“+”号的图片
        mNineGridView.setIcAddMoreResId(R.drawable.ic_ninegrid_addmore);
//设置各类点击监听
        mNineGridView.setOnItemClickListener(new NineGridView.onItemClickListener() {
            @Override
            public void onNineGirdAddMoreClick(int cha) {
                //编辑模式下，图片展示数量尚未达到最大数量时，会显示一个“+”号，点击后回调这里
            }

            @Override
            public void onNineGirdItemClick(final int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {
                //点击图片的监听
                //在你点击时，调用computeBoundsBackward（）方法
                Rect bounds = new Rect();
                Img img = (Img) gridBean.getT();
                imageContainer.getImageView().getGlobalVisibleRect(bounds);
                img.setBounds(bounds);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        GPreviewBuilder.from(FarmInfov2Fragment.this)
                                .setData(mUrls)
                                .setCurrentIndex(position)
                                .setType(GPreviewBuilder.IndicatorType.Number)
                                .start();
                    }
                });
            }

            @Override
            public void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {
                //编辑模式下，某张图片被删除后回调这里
            }
        });
        mNineGridView.setDataList(mData);
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

        mNineGridView = (NineGridView) view.findViewById(R.id.ngiv);
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
        mUrls.clear();
        List<TImage> imgs = result.getImages();
        for (TImage image : imgs)
            mUrls.add(new Img(image.getPath()));

        mData.clear();
        for (int i = 0; i < mUrls.size(); i++) {
            MyNineGridBean bean = new MyNineGridBean(mUrls.get(i).getUrl(), mUrls.get(i).getUrl(), mUrls.get(i));
            mData.add(bean);
        }
        mNineGridView.setDataList(mData);
    }

//    @Override
//    public void onItemImageClick(Context context, final ImageView imageView, final int index, final List<Img> list) {
//        myTool.showInfo("Click [" + index + "].");
//
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                //在你点击时，调用computeBoundsBackward（）方法
//                Rect bounds = new Rect();
//                imageView.getGlobalVisibleRect(bounds);
//                list.get(index).setBounds(bounds);
//
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        GPreviewBuilder.from(FarmInfov2Fragment.this)
//                                .setData(list)
//                                .setCurrentIndex(index)
//                                .setType(GPreviewBuilder.IndicatorType.Number)
//                                .start();
//                    }
//                });
//
//            }
//        }.start();
//    }

    Handler mHandler = new Handler();

    /**
     * * 查找信息
     * 从第一个完整可见item逆序遍历，如果初始位置为0，则不执行方法内循环
     */
//    private void computeBoundsBackward(View itemView) {
//
//        for (int i = firstCompletelyVisiblePos; i < mUrls.size(); i++) {
////            View itemView = mNineGridView.findViewByPosition(i);
//            View itemView = mNineGridView;
//            Rect bounds = new Rect();
//            if (itemView != null) {
//                ImageView thumbView = (ImageView) itemView.findViewById(R.id.iv);
//                thumbView.getGlobalVisibleRect(bounds);
//            }
//            mUrls.get(i).setBounds(bounds);
//        }
//    }
}
