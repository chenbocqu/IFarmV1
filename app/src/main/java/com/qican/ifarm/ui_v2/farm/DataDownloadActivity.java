/**
 * 历史数据下载
 */

package com.qican.ifarm.ui_v2.farm;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.MonitorNode;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.TimePickerUtil;
import com.qican.ifarm.utils.TimeUtils;
import com.qican.ifarm.utils.WPSUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;

@EActivity(R.layout.activity_data_download)
public class DataDownloadActivity extends Activity {

    private CommonTools myTool;
    private SweetAlertDialog mDialog;
    Date starTimeDate, endTimeDate;

    MonitorNode mNode;

    enum BTN_MEANING {
        DOWNLOAD, FAIL, SUCCESS
    }

    private BTN_MEANING type = BTN_MEANING.DOWNLOAD;


    @ViewById(R.id.tv_title)
    TextView tvTitle;

    @ViewById
    TextView tvStartTime, tvEndTime;

    @ViewById(R.id.btn_download)
    Button btnDownload;

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
    }

    void initData() {
        tvTitle.setText("数据下载中心");

        mNode = (MonitorNode) myTool.getParam(MonitorNode.class);
        if (mNode == null || mNode.getDeviceId() == null) return;
    }

    private void initEvent() {

    }

    @Click
    public void llBack() {//返回键
        this.finish();
    }

    @Click
    void rlStartTime() {
        TimePickerUtil.pickTimeDialog(this, "时间选择", starTimeDate, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {

                if (starTimeDate != null && date.compareTo(starTimeDate) == 0) return;

                type = BTN_MEANING.DOWNLOAD;
                updateBtnStutas();

                starTimeDate = date;
                tvStartTime.setText(TimeUtils.formatDateTime(getTime(date)));
            }
        });
    }


    @Click
    void rlEndTime() {
        TimePickerUtil.pickTimeDialog(this, "时间选择", endTimeDate, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {

                if (endTimeDate != null && date.compareTo(endTimeDate) == 0) return;

                type = BTN_MEANING.DOWNLOAD;
                updateBtnStutas();

                endTimeDate = date;
                tvEndTime.setText(TimeUtils.formatDateTime(getTime(date)));
            }
        });
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    private String getDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @Click(R.id.btn_download)
    void download() {
        switch (type) {
            case DOWNLOAD:
            case FAIL://下载失败重试
                downLoadExcel();
                break;
            case SUCCESS:
                openExcel();
                break;
        }
    }

    private void openExcel() {
        if (downloadFile != null) {
            Intent intent = WPSUtils.getExcelFileIntent(downloadFile.getAbsolutePath());
            DataDownloadActivity.this.startActivity(intent); // 打开Excel
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

        if (starTimeDate.after(endTimeDate)) {
            myTool.showInfo("开始时间应在结束时间之前！");
            return;
        }

        startTime = getTime(starTimeDate);
        endTime = getTime(endTimeDate);

        if (mNode == null || mNode.getDeviceId() == null) return;
        myTool.log("请求参数[getDeviceId:" + mNode.getDeviceId() + ", startTime:" + startTime + "，endTime:" + endTime + "]");

        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.setTitleText("正在下载历史数据...").show();

        OkHttpUtils.get().url("http://" + ConstantValue.IP + ":8080/IFarm/collectorDeviceValue/historyCollectorDeviceExcel")
                .addParams("deviceId", mNode.getDeviceId())
                .addParams("beginTime", startTime)
                .addParams("endTime", endTime)    // 从00:00:00到23:59:59撒
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(),
                        mNode.getLocation() + "[" + getDate(starTimeDate) + "到"
                                + getDate(endTimeDate) + "].xls") {
                    @Override
                    public void inProgress(float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        type = BTN_MEANING.FAIL;
                        updateBtnStutas();
                        myTool.log("传感器参数请求异常,farmId=" + mNode.getDeviceId() + ",e：[" + e.getMessage() + "]");
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
            case DOWNLOAD:
                btnDownload.setText("立即下载");//下载失败重试
                break;
            case FAIL:
                mDialog.setTitleText("失败")
                        .setContentText("下载失败，是否重新下载？")
                        .setConfirmText("重试")
                        .setCancelText("不用了")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                downLoadExcel();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.WARNING_TYPE);

                btnDownload.setText("重新下载");//下载失败重试
                break;
            case SUCCESS:
                mDialog.setTitleText("成功")
                        .setContentText("下载成功，是否打开该文档？")
                        .setConfirmText("立即打开")
                        .setCancelText("不用了")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                openExcel();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                btnDownload.setText("立即打开");
                break;
        }
    }
}
