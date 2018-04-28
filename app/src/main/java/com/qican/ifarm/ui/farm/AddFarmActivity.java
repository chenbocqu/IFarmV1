package com.qican.ifarm.ui.farm;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

@EActivity(R.layout.activity_addfarm)
public class AddFarmActivity extends Activity {
    private static final int REQUEST_LABEL = 1;
    private static final int LABEL_MAX_SHOW_LEN = 10;//能标签显示的最大长度
    private CommonTools myTool;
    private String farmName, farmDesc;
    private SweetAlertDialog mDialog;

    @ViewById
    EditText edtFarmName, edtFarmDesc;

    @ViewsById({R.id.tvLabel1, R.id.tvLabel2, R.id.tvLabel3})
    List<TextView> tvLabels;

    @ViewById(R.id.tvMore)
    TextView tvMore;
    @ViewById
    TextView tvChoose;

    private List<String> labels;
    private Label farmLabel;
    private String labelStr = "";

    /**
     * 添加农场
     */
    @Click(R.id.btnAdd)
    void addFarm() {
        myTool.log("addFarm开始");
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

        mDialog.setTitleText("正在添加···");
        mDialog.showContentText(false);
        if (!mDialog.isShowing()) {
            mDialog.show();
        } else {
            mDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        }

        OkHttpUtils.post().url(myTool.getServAdd() + "farm/addFarm")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("farmName", farmName)
                .addParams("farmDescribe", farmDesc)
                .addParams("farmLabel", labelStr)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("添加农场异常：" + e.getMessage());
                        addFailed("异常信息：" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        myTool.log("添加农场response：" + response);
                        switch (response) {
                            case "success":
                                addSuccess();
                                break;
                            case "error":
                                break;
                            case "lose effXXXX":
                                //需要重新获取token
                                break;
                        }
                    }
                });
    }

    private void addSuccess() {
        mDialog.setTitleText("添加成功")
                .setContentText("恭喜，您已成功添加了农场！")
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

    private void addFailed(String msg) {
        mDialog.setTitleText("添加失败")
                .setContentText(msg)
                .setConfirmText("重试")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        addFarm();
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
        myTool = new CommonTools(this);
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        labels = new ArrayList<>();
        farmLabel = new Label();
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

    //选择标签
    @Click
    void rlLabel() {
        myTool.startActivityForResult(farmLabel, AddLabelActivity_.class, REQUEST_LABEL);
    }
}
