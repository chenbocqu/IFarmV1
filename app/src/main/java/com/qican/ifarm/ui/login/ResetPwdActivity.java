/**
 * @Function：关于
 * @Author：残阳催雪
 * @Time：2016-8-9
 * @Email:qiurenbieyuan@gmail.com
 */
package com.qican.ifarm.ui.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.bean.PhoneInfo;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.ConstantValue;
import com.qican.ifarm.utils.IFarmData;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sf.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;


public class ResetPwdActivity extends Activity implements View.OnClickListener {
    private CommonTools myTool;
    private LinearLayout llBack;
    private TextView tvUserName;
    private EditText edtPassword, edtConPwd;
    private ImageView ivDelUserName, ivDelPassword, ivDelConPwd;
    private Button btnReset;
    private String userName, password, confirmPwd;
    private SweetAlertDialog mDialog;
    private PhoneInfo phoneInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpwd);
        initView();
        initData();

        initEvent();
    }

    private void initData() {
        phoneInfo = new PhoneInfo();
        phoneInfo = (PhoneInfo) myTool.getParam(phoneInfo);
        userName = phoneInfo.getPhone();

        tvUserName.setText(userName);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initEvent() {
        llBack.setOnClickListener(this);
        ivDelUserName.setOnClickListener(this);
        ivDelPassword.setOnClickListener(this);
        ivDelConPwd.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        setTextListener();
    }

    private void initView() {
        llBack = (LinearLayout) findViewById(R.id.ll_back);

        tvUserName = (TextView) findViewById(R.id.tv_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtConPwd = (EditText) findViewById(R.id.edt_conpwd);
        ivDelUserName = (ImageView) findViewById(R.id.iv_del_username);
        ivDelPassword = (ImageView) findViewById(R.id.iv_del_password);
        ivDelConPwd = (ImageView) findViewById(R.id.iv_del_conpwd);

        btnReset = (Button) findViewById(R.id.btn_reset);

        myTool = new CommonTools(this);
    }

    /**
     * 点击事件
     *
     * @param v：所点击的View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.iv_del_username:
                tvUserName.setText("");
                break;
            case R.id.iv_del_password:
                edtPassword.setText("");
                break;
            case R.id.iv_del_conpwd:
                edtConPwd.setText("");
                break;
            case R.id.btn_register:
                attempRegister();//登录
                break;
        }
    }

    private void attempRegister() {
        password = edtPassword.getText().toString();
        confirmPwd = edtConPwd.getText().toString();

        if (password.length() < 6) {
            YoYo.with(Techniques.Shake)
                    .duration(700).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    edtPassword.requestFocus();
                    edtPassword.setError("密码在6位以上！");
                }
            })
                    .playOn(edtPassword);
            return;
        }

        if (!password.equals(confirmPwd)) {
            YoYo.with(Techniques.Shake)
                    .duration(700).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    edtConPwd.requestFocus();
                    edtConPwd.setError("两次密码不一致！");
                }
            })
                    .playOn(edtConPwd);
            return;
        }

        mDialog = null;
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("正在注册···");
        mDialog.show();

        String url = ConstantValue.SERVICE_ADDRESS + "user/updateUser";
        OkHttpUtils.get().url(url)
                .addParams("signature", myTool.getToken())
                .addParams("userId", userName)
                .addParams("userPwd", password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        toOtherCase(e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        switch (response) {
                            case "success":
                                mDialog.setTitleText("重置成功")
                                        .setContentText("重置密码成功了，现在去登录吧！")
                                        .setConfirmText("好  的")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                new CountDownTimer(1000, 1000) {

                                                    @Override
                                                    public void onTick(long millisUntilFinished) {

                                                    }

                                                    @Override
                                                    public void onFinish() {
                                                        finish();
                                                    }
                                                }.start();
                                            }
                                        }).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                break;
                            case "error":
                                mDialog.setTitleText("修改失败")
                                        .setContentText("出现了一些小插曲，稍后重试哦！")
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                break;
                            default:
                                toOtherCase(response);
                                break;
                        }
                    }
                });
    }

    private void toOtherCase(String response) {
        mDialog.setTitleText("提示")
                .setContentText("服务器返回的信息：" + response + "！")
                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
    }


    private void setTextListener() {
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showIvDelByEdt(s, ivDelPassword);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtConPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showIvDelByEdt(s, ivDelConPwd);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 根据输入信息显示删除控件
     *
     * @param s
     * @param ivDel
     */
    private void showIvDelByEdt(CharSequence s, final ImageView ivDel) {
        if (s.length() != 0) {
            //没有显示时就不做操作
            if (ivDel.getVisibility() == View.GONE) {
                ivDel.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.ZoomIn)
                        .duration(700)
                        .playOn(ivDel);
            }
        } else {
            YoYo.with(Techniques.ZoomOut)
                    .duration(700)
                    .withListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            ivDel.setVisibility(View.GONE);
                        }
                    })
                    .playOn(ivDel);
        }
    }
}
