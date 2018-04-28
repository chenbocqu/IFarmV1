package com.qican.ifarm.ui.farm;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.data.NetRequest;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.DialogUtils;
import com.qican.ifarm.utils.WPSUtils;
import com.qican.ifarm.view.MyCalendarDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

@EActivity(R.layout.activity_download_historydata)
public class DownloadExcelActivity extends Activity {
    private CommonTools myTool;
    private SweetAlertDialog mDialog;

    enum BTN_MEANING {
        FAIL, SUCCESS
    }

    private BTN_MEANING type = BTN_MEANING.FAIL;


    @ViewById(R.id.tv_title)
    TextView tvTitle;
    @ViewById
    TextView tvStartTime, tvEndTime;
    @ViewById
    CircularProgressButton btnWithText;

    private NetRequest netRequest;

    private Farm mFarm;
    private String startTime = "", endTime = "";
    private File downloadFile;

    @AfterViews
    void main() {
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        myTool = new CommonTools(this);
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        netRequest = new NetRequest(this);
    }

    void initData() {
        tvTitle.setText("下载历史数据");
        mFarm = new Farm();
        mFarm = (Farm) myTool.getParam(mFarm);

        if (mFarm == null) {
            //请求数据

        }

    }

    private void initEvent() {

    }

    @Click
    public void llBack() {//返回键
        this.finish();
    }

    @Click
    void rlStartTime() {
        //选取开始时间
        DialogUtils.showChooseDateDialog(this, "选择开始日期", "确定", "取消", new MyCalendarDialog.ClickCallBack() {
            @Override
            public void onOk(MyCalendarDialog dlg) {
                dlg.dismissDlg();
            }

            @Override
            public void onCancel(MyCalendarDialog dlg) {
                dlg.dismissDlg();
            }
        }, tvStartTime);
    }

    @Click
    void rlEndTime() {
        //选取开始时间
        DialogUtils.showChooseDateDialog(this, "选择结束日期", "确定", "取消", new MyCalendarDialog.ClickCallBack() {
            @Override
            public void onOk(MyCalendarDialog dlg) {
                dlg.dismissDlg();
            }

            @Override
            public void onCancel(MyCalendarDialog dlg) {
                dlg.dismissDlg();
            }
        }, tvEndTime);
    }

    @Click
    void btnWithText() {
        switch (type) {
            case FAIL://下载失败重试
                downLoadExcel();
                break;
            case SUCCESS:
                if (downloadFile != null) {
                    Intent intent = WPSUtils.getExcelFileIntent(downloadFile.getAbsolutePath());
                    DownloadExcelActivity.this.startActivity(intent); // 打开Excel
                }
                break;
        }
    }

    private void downLoadExcel() {

        if ("".equals(tvStartTime.getText().toString())) {
            myTool.showInfo("没有选择起始时间！");
            return;
        }
        if ("".equals(tvEndTime.getText().toString())) {
            myTool.showInfo("没有选择结束时间！");
            return;
        }

        startTime = tvStartTime.getText().toString() + " 00:00:00";
        endTime = tvEndTime.getText().toString() + " 23:59:59";

        myTool.log("请求参数[startTime:" + startTime + "，endTime:" + endTime + "]");

        OkHttpUtils.get().url(myTool.getServAdd() + "sensor/historySensorValuesExcel")
                .addParams("userId", myTool.getUserId())
                .addParams("signature", myTool.getToken())
                .addParams("farmId", mFarm.getId())
//                .addParams("sensorId", "20160004")
                .addParams("beginTime", startTime)
                .addParams("endTime", endTime)    // 从00:00:00到23:59:59撒
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        mFarm.getName() + "的历史数据[" + tvStartTime.getText().toString() + "到"
                                + tvEndTime.getText().toString() + "].xls") {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        btnWithText.setProgress((int) (progress * 100));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        updateBtnStutas();
                        myTool.log("传感器参数请求异常,farmId=" + mFarm.getId() + ",e：[" + e.getMessage() + "]");
                    }

                    @Override
                    public void onResponse(final File file, int id) {
                        type = BTN_MEANING.SUCCESS;
                        updateBtnStutas();

                        downloadFile = file;
                        myTool.showInfo("已保存至[" + file.getAbsolutePath() + "]");
                    }
                });
    }

    /**
     * 更新btn的状态
     */
    private void updateBtnStutas() {
        switch (type) {
            case FAIL:
                btnWithText.setProgress(-1);    // 下载失败
                btnWithText.setText("重新下载");//下载失败重试
                break;
            case SUCCESS:
                btnWithText.setProgress(100);
                btnWithText.setText("立即打开");
                break;
        }
    }
}
