package com.qican.ifarm.ui.farm;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.listener.OnDialogListener;
import com.qican.ifarm.ui.userinfo.PicChooseDialog;
import com.qican.ifarm.utils.CommonTools;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

@EActivity(R.layout.activity_farminfo)
public class FarmInfoActivity extends TakePhotoActivity implements OnDialogListener {
    private static final int REQUEST_LABEL = 1;
    private static final int LABEL_MAX_SHOW_LEN = 10;//能标签显示的最大长度

    public static final String KEY_FARM_INFO = "KEY_FARM_INFO";
    private CommonTools myTool;
    private String farmName, farmDesc;
    private SweetAlertDialog mDialog;
    private TakePhoto takePhoto;

    private PicChooseDialog mPicDialog;
    CompressConfig compressConfig = new CompressConfig.Builder().setMaxSize(100 * 1024).setMaxPixel(800).create();//最大200K
    CropOptions cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(true).create();

    @ViewById
    EditText edtFarmName, edtFarmDesc;

    @ViewsById({R.id.tvLabel1, R.id.tvLabel2, R.id.tvLabel3})
    List<TextView> tvLabels;

    @ViewById(R.id.tvMore)
    TextView tvMore;

    @ViewById(R.id.tv_title)
    TextView tvTitle;

    @ViewById(R.id.ll_icon_menu)
    LinearLayout llIconMenu;

    @ViewById(R.id.iv_icon_menu)
    ImageView ivIconMenu;

    @ViewById(R.id.pb_upload)
    ProgressBar pbUpload;

    @ViewById
    TextView tvChoose;

    @ViewById(R.id.iv_farmimg)
    ImageView ivFarmImg;

    private Farm mFarm;

    private List<String> labels;
    private Label farmLabel;
    private String labelStr = "";

    @AfterViews
    void initData() {
        myTool = new CommonTools(this);

        tvTitle.setText("农场信息");
        llIconMenu.setVisibility(View.VISIBLE);
        ivIconMenu.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.save));

        mFarm = new Farm();
        mFarm = (Farm) myTool.getParam(mFarm);

        edtFarmName.setText(mFarm.getName());
        edtFarmDesc.setText(mFarm.getDesc());
        // 设置背景图片
        if (mFarm.getImgUrl() != null) {
            myTool.showImage(mFarm.getImgUrl(),
                    ivFarmImg,
                    R.drawable.defaultbg);
        }

        farmLabel = mFarm.getLabel();
        if (farmLabel != null) {
            setTvByLabel(farmLabel.getLabelList());
            labelStr = farmLabel.getStrByList(farmLabel.getLabelList());
        }
        takePhoto = getTakePhoto();
    }

    private void initEvent() {
        mPicDialog.setOnDialogListener(this);
    }

    @Click(R.id.ll_icon_menu)
    void save() {
        //判断登录
        if (!myTool.isLoginWithDialog()) {
            return;
        }
        if (isEmptyWithShakeMsg(edtFarmName, "农场名不能为空哦！")) {
            return;
        }
        if (isEmptyWithShakeMsg(edtFarmDesc, "还没有输入描述唷！")) {
            return;
        }
        farmName = edtFarmName.getText().toString().trim();
        farmDesc = edtFarmDesc.getText().toString().trim();

        mFarm.setName(farmName);
        mFarm.setDesc(farmDesc);
        mFarm.setLabels(labelStr);

        mDialog.setTitleText("正在修改···");
        mDialog.showContentText(false);
        if (!mDialog.isShowing()) {
            mDialog.show();
        } else {
            mDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        }

        OkHttpUtils.post().url(myTool.getServAdd() + "farm/updateFarmCollector")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("farmId", mFarm.getId())
                .addParams("farmName", farmName)
                .addParams("farmDescribe", farmDesc)
                .addParams("farmLabel", labelStr)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        modifyFailed("异常信息：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        myTool.log("添加农场response：" + response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            String msg = obj.getString("response");

                            switch (msg) {
                                case "success":

                                    Intent intent = new Intent();
                                    intent.putExtra(KEY_FARM_INFO, mFarm);
                                    setResult(RESULT_OK, intent);

                                    modifySuccess();
                                    break;
                                case "error":
                                    modifyFailed("服务器返回失败！");
                                    break;
                                case "lose effXXXX":
                                    //需要重新获取token
                                    break;
                                default:
                                    modifyFailed(msg);
                            }


                        } catch (JSONException e) {
                            modifyFailed(e.getMessage());
                        }

                    }
                });
    }

    private void modifySuccess() {
        mDialog.setTitleText("修改成功")
                .setContentText("恭喜，您已成功修改了农场信息！")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        exitWithDelay();
                    }
                })
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

    /**
     * 延迟退出
     */
    @Background
    void exitWithDelay() {
        try {
            Thread.sleep(500);
            this.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void modifyFailed(String msg) {
        mDialog.setTitleText("添加失败")
                .setContentText(msg)
                .setConfirmText("重试")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        save();
                    }
                })
                .setCancelText("取消")
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    /**
     * 判断EditText是否为空，为空震动且提示
     *
     * @param edt
     * @param msg
     * @return
     */
    private boolean isEmptyWithShakeMsg(final EditText edt, final String msg) {
        String info = edt.getText().toString().trim();
        if ("".equals(info)) {
            YoYo.with(Techniques.Shake)
                    .duration(1000)
                    .withListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            edt.requestFocus();
                            edt.setError(msg);
                        }
                    })
                    .playOn(edt);
            return true;
        }
        return false;
    }

    @AfterViews
    void main() {
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        labels = new ArrayList<>();
        farmLabel = new Label();

        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mPicDialog = new PicChooseDialog(this, R.style.Translucent_NoTitle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LABEL && resultCode == RESULT_OK) {
            //返回结果直接给我的收货地址
            farmLabel = (Label) data.getSerializableExtra(AddLabelActivity.KEY_LABEL);
            labelStr = farmLabel.getStrByList(farmLabel.getLabelList());
            setTvByLabel(farmLabel.getLabelList());
            myTool.log(farmLabel.toString() + "labelStr:" + labelStr);
        }
    }

    /**
     * 通过数据设置标签显示
     *
     * @param labelList
     */
    private void setTvByLabel(List<String> labelList) {
        boolean showMore = false;
        tvChoose.setVisibility(labelList.isEmpty() ? View.VISIBLE : View.GONE);
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

    @Click
    public void llBack() {//返回键
        this.finish();
    }

    @Click
    void rlChoosePic() {
        mPicDialog.show();
    }

    //选择标签
    @Click
    void rlLabel() {
        myTool.startActivityForResult(farmLabel, AddLabelActivity_.class, REQUEST_LABEL);
    }

    @Override
    public void dialogResult(Dialog dialog, String msg) {
        switch (msg) {
            case PicChooseDialog.FORM_CAMERA:
                takePhoto.onEnableCompress(compressConfig, false);
                takePhoto.onPickFromCaptureWithCrop(myTool.getUserHeadFileUri(), cropOptions);
                break;
            case PicChooseDialog.FROM_FILE:
                takePhoto.onEnableCompress(compressConfig, false);
                takePhoto.onPickFromDocumentsWithCrop(myTool.getUserHeadFileUri(), cropOptions);
                break;
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        myTool.showInfo("选择失败:" + msg);
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        myTool.showInfo("取消选择");
    }

    @Override
    public void takeSuccess(final TResult result) {
        super.takeSuccess(result);

        pbUpload.setVisibility(View.VISIBLE);

        String url = myTool.getServAdd() + "farm/uploadFarm";

        //上传头像至服务器
        File picFile = new File(result.getImage().getPath());
        String fileName = mFarm.getId() + "_农场背景.png";

        // 更新图像
        OkHttpUtils.post().url(url)
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("farmId", mFarm.getId())
                .addParams("flag", "2")//flag=2表示更新背景图像
                .addFile("mFile", fileName, picFile)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        setPbGone();
                        myTool.showInfo("上传失败，请稍后再试！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        setPbGone();
                        myTool.log("onResponse: " + response);
                        switch (response) {
                            case "success":
                                Bitmap bitmap = BitmapFactory.decodeFile(result.getImage().getPath());
                                ivFarmImg.setImageBitmap(bitmap);
                                myTool.showInfo("上传成功！");
                                break;
                            case "error":
                                myTool.showInfo("上传失败，请稍后再试！");
                                break;
                            case "overSize":
                                myTool.showInfo("上传失败，图像超过最大限制了！");
                                break;
                        }
                    }
                });
    }

    //进度环设置为不见
    private void setPbGone() {
        pbUpload.setVisibility(View.GONE);
    }
}
