/**
 * 二维码显示框
 */
package com.qican.ifarm.ui.userinfo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.ComUser;
import com.qican.ifarm.utils.CommonTools;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;


public class TwodcodeDialog extends Dialog implements View.OnClickListener {

    private static final String BITMAP = "BITMAP";
    private Context mContext;
    private ImageView ivHeadImg, ivQRCode;
    private CommonTools myTool;
    private TextView tvNickName;
    private LinearLayout llDialog;
    private ImageView ivSex;
    private ComUser userInfo;
    public final static int SET_IMG = 1;


    public TwodcodeDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    public TwodcodeDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_twodcode);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        llDialog.setOnClickListener(this);
    }

    private void initData() {
        if (!myTool.isLogin()) {
            return;
        }
        userInfo = myTool.getComUserInfoById(myTool.getUserId());
        if (userInfo == null) {
            return;
        }
        if (!"".equals(userInfo.getHeadImgUrl())) {
            myTool.showImage(userInfo.getHeadImgUrl(),
                    ivHeadImg,
                    "男".equals(userInfo.getSex()) ?
                            R.drawable.default_head_male :
                            R.drawable.default_head_female);
        }
        tvNickName.setText(userInfo.getNickName());
        myTool.showSex(userInfo.getSex(), ivSex);

        produceQRcode();
    }

    private void produceQRcode() {

        final JSONObject object = new JSONObject();

        try {

            object.put("id", myTool.getUserId());
            object.put("type", "userinfo");

        } catch (JSONException e) {
            myTool.showInfo("error : " + e.getMessage());
        }

        new Thread() {
            @Override
            public void run() {
                super.run();

                final Bitmap bitmap = QRCodeEncoder.syncEncodeQRCode(object.toString(), 200);

                Message message = new Message();

                Bundle bundle = new Bundle();
                bundle.putParcelable(BITMAP, bitmap);

                message.setData(bundle);
                message.what = SET_IMG;
                mHandler.sendMessage(message);

            }
        }.start();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_IMG:

                    Bundle bundle = msg.getData();
                    Bitmap bitmap = bundle.getParcelable(BITMAP);

                    if (bitmap != null) ivQRCode.setImageBitmap(bitmap);
                    else myTool.showInfo("生成二维码失败.");

                    break;
            }
        }
    };

    private void initView() {
        ivHeadImg = (ImageView) findViewById(R.id.iv_headimg);
        tvNickName = (TextView) findViewById(R.id.tv_nickname);
        ivSex = (ImageView) findViewById(R.id.iv_sex);
        ivQRCode = (ImageView) findViewById(R.id.iv_qrcode);

        llDialog = (LinearLayout) findViewById(R.id.ll_dialog);

        myTool = new CommonTools(mContext);
    }


    @Override
    public void show() {
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            try {
                super.show();
                initData();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void dismiss() {
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            try {
                super.dismiss();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void cancel() {
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            try {
                super.cancel();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_dialog:
                dismiss();
                break;
        }
    }
}
