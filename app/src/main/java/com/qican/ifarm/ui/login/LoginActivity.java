/**
 * @Function：关于
 * @Author：残阳催雪
 * @Time：2016-8-9
 * @Email:qiurenbieyuan@gmail.com
 */
package com.qican.ifarm.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;
import okhttp3.Call;

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final int TASKID_LOGIN = 1;
    private static final int REQUESR_FOR_USERNAME = 001;//注册成功返回用户名和密码

    private CommonTools myTool;
    private LinearLayout llBack;
    private EditText edtUserName, edtPassword;
    private ImageView ivDelUserName, ivDelPassword, ivHeadImg;
    private Button btnLogin, btnRegister, btnResetPwd;
    private SweetAlertDialog mLoginDialog;

    private String userName, password;
    private final String REGISTER = "REGISTER";//注册
    private final String RESETPWD = "RESETPWD";//重置密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();

        initEvent();
    }

    private void initData() {
        //如果有存用户名则显示出来
        if (!"".equals(myTool.getUserName())) {
            edtUserName.setText(myTool.getUserName());
            ivDelUserName.setVisibility(View.VISIBLE);
            edtPassword.requestFocus();
        }
        if (!"".equals(myTool.getUserHeadURL())) {
            myTool.showImage(myTool.getUserHeadURL(), ivHeadImg);
        }
    }

    private void initEvent() {
        llBack.setOnClickListener(this);
        ivDelUserName.setOnClickListener(this);
        ivDelPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnResetPwd.setOnClickListener(this);
        setTextListener();
    }

    private void initView() {
        llBack = (LinearLayout) findViewById(R.id.ll_back);

        edtUserName = (EditText) findViewById(R.id.edt_username);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        ivDelUserName = (ImageView) findViewById(R.id.iv_del_username);
        ivDelPassword = (ImageView) findViewById(R.id.iv_del_password);
        ivHeadImg = (ImageView) findViewById(R.id.iv_headimg);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnResetPwd = (Button) findViewById(R.id.btn_resetpwd);

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
                edtUserName.setText("");
                break;
            case R.id.iv_del_password:
                edtPassword.setText("");
                break;
            case R.id.btn_login:
                toLogin();//登录
                break;
            case R.id.btn_register:
                // 注册的手机验证
                verifyPhone(REGISTER);
                break;
            case R.id.btn_resetpwd:
                //重置密码的手机验证
                verifyPhone(RESETPWD);
                break;
        }
    }

    /**
     * 验证手机
     *
     * @param reseaon，用于注册，还是忘记密码
     */
    private void verifyPhone(final String reseaon) {
        myTool.log("verifyPhone！");
        //打开注册页面
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                // 解析注册结果
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");
                    switch (reseaon) {
                        case REGISTER:
                            // 验证手机和国家后，注册用户到服务器
                            myTool.startActivityForResult(new PhoneInfo(phone, country), RegisterActivity.class, REQUESR_FOR_USERNAME);
                            break;
                        case RESETPWD:
                            // 验证手机和国家后，注册用户到服务器
                            myTool.startActivityForResult(new PhoneInfo(phone, country), ResetPwdActivity.class, REQUESR_FOR_USERNAME);
                            break;
                    }
                }
            }
        });
        registerPage.show(this);
    }

    private void attempLogin() {
        byte[] data = new byte[0];
        try {
            data = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            myTool.showExceptionInfo(e);
            return;
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        myTool.log("base64：" + base64);

        mLoginDialog = null;
        mLoginDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("正在登录···");
        mLoginDialog.show();

        OkHttpUtils.post()
                .url(ConstantValue.SERVICE_ADDRESS + "user/login")
                .addParams("token", myTool.getToken())
                .addParams("userId", userName)
                .addParams("userPwd", base64)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mLoginDialog.setTitleText("登录失败")
                                .setContentText("登录失败，异常信息 e-->[" + e.toString() + "]")
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        mLoginDialog = null;
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        myTool.log("response:" + response);
                        String result[] = response.split(":");
                        String msg = response;
                        String token = "";
                        if (result.length == 2) {
                            msg = result[0];
                            token = result[1];
                        }
                        switch (msg) {
                            case "success":
                                myTool.setLoginFlag(true);
                                myTool.setUserName(userName);
                                myTool.setToken(token);
                                IFarmData.updateUserInfo(LoginActivity.this);

                                mLoginDialog.setTitleText("登录成功")
                                        .setContentText("登录成功，智能农场欢迎您！")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                finish();
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                new CountDownTimer(6000, 1000) {

                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        mLoginDialog.setConfirmText("立即开启（" + millisUntilFinished / 1000 + "s）");
                                    }

                                    @Override
                                    public void onFinish() {
                                        finish();
                                    }
                                }.start();
                                toLoginEm();//登录环信账号
                                break;
                            case "wrong":
                                mLoginDialog.setTitleText("登录失败")
                                        .setContentText("密码或用户名错误,请重新输入！")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                edtPassword.setText("");
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                break;
                            case "errorToken":
                                myTool.log("errorToken");
                                mLoginDialog.setTitleText("正在重新获取Token···");
                                attempGetToken();
                                break;
                        }
                    }
                });
    }

    private void toLogin() {
        userName = edtUserName.getText().toString().trim();
        password = edtPassword.getText().toString();

        if ("".equals(edtUserName.getText().toString().trim())) {
            YoYo.with(Techniques.Shake)
                    .duration(700).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    edtUserName.requestFocus();
                    edtUserName.setError("请输入手机号码！");
                }
            })
                    .playOn(edtUserName);
            return;
        }
        if (edtUserName.getText().toString().trim().length() != 11) {
            YoYo.with(Techniques.Shake)
                    .duration(700).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    edtUserName.requestFocus();
                    edtUserName.setError("格式有误，11位手机号码！");
                }
            })
                    .playOn(edtUserName);
            return;
        }
        if (edtPassword.getText().toString().length() < 6) {
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
        attempLogin();
    }

    private void attempGetToken() {
        OkHttpUtils.post().url(ConstantValue.SERVICE_ADDRESS + "user/getUserToken")
                .addParams("userId", userName)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        myTool.log("token Exception:" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        myTool.log("token response:" + response);
                        myTool.setToken(response);
                        attempLogin();
                    }
                });
    }

    //登录环信
    private void toLoginEm() {
        EMClient.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private void setTextListener() {
        edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    //没有显示时就不做操作
                    if (ivDelUserName.getVisibility() == View.GONE) {
                        ivDelUserName.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.ZoomIn)
                                .duration(700)
                                .playOn(findViewById(R.id.iv_del_username));
                    }
                } else {
                    YoYo.with(Techniques.ZoomOut)
                            .duration(700)
                            .withListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    ivDelUserName.setVisibility(View.GONE);
                                }
                            })
                            .playOn(findViewById(R.id.iv_del_username));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    //没有显示时就不做操作
                    if (ivDelPassword.getVisibility() == View.GONE) {
                        ivDelPassword.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.ZoomIn)
                                .duration(700)
                                .playOn(findViewById(R.id.iv_del_password));
                    }
                } else {
                    YoYo.with(Techniques.ZoomOut)
                            .duration(700)
                            .withListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    ivDelPassword.setVisibility(View.GONE);
                                }
                            })
                            .playOn(findViewById(R.id.iv_del_password));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //注册成功返回用户名和密码
        if (requestCode == REQUESR_FOR_USERNAME) {

        }
    }


}
