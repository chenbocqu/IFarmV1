package com.qican.ifarm.ui.farm;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.qican.ifarm.R;
import com.qican.ifarm.adapter.ComAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.Label;
import com.qican.ifarm.utils.CommonTools;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

@EActivity(R.layout.activity_addlabel)
public class AddLabelActivity extends Activity {
    public static final String KEY_LABEL = "KEY_LABEL";
    private CommonTools myTool;
    private Label label;
    private List<String> mDatas;
    @ViewById
    EditText edtLabel;
    @ViewById
    GridView gvLabel;
    private ComAdapter<String> mAdapter;

    @AfterViews
    void main() {
        myTool = new CommonTools(this);
        label = (Label) myTool.getParam(new Label());
        mDatas = label.getLabelList();

        mAdapter = new ComAdapter<String>(this, mDatas, R.layout.item_label) {
            @Override
            public void convert(ViewHolder helper, String label) {
                helper.setText(R.id.tvLabel, label);
                RelativeLayout rl = helper.getView(R.id.rl_item);
                MyClickListener mListener = new MyClickListener(label);
                rl.setOnClickListener(mListener);
            }
        };
        gvLabel.setAdapter(mAdapter);
    }

    class MyClickListener implements View.OnClickListener {
        private String item;

        public MyClickListener(String item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {
            new SweetAlertDialog(AddLabelActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("删除标签")
                    .setContentText("确认删除标签[" + item + "]吗？")
                    .setConfirmText("确定")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog dialog) {
                            dialog.dismissWithAnimation();
                            mDatas.remove(item);
                            mAdapter.notifyDataSetChanged();
                        }
                    })
                    .setCancelText("取消")
                    .show();
        }
    }


    @Click
    void llBack() {//返回键
        this.finish();
    }

    @Click
    void tvAdd() {
        String label = edtLabel.getText().toString().trim();
        if ("".equals(label)) {
            return;
        }
        if (label.length() > 7) {
            shakeEdtWithMsg(edtLabel, "标签长度不超过7哦！");
            return;
        }
        edtLabel.setText("");
        mDatas.add(label);
        mAdapter.notifyDataSetChanged();
    }

    private void shakeEdtWithMsg(final EditText edt, final String msg) {
        String info = edt.getText().toString().trim();
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
    }

    @Click(R.id.llCompel)
    void compelete() {
        Intent intent = new Intent();
        intent.putExtra(KEY_LABEL, label);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Click
    void tvDelAll() {
        // 删除所有标签
        mDatas.clear();
        mAdapter.notifyDataSetChanged();
    }
}
