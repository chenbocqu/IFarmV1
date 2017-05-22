package com.qican.ifarm.ui.camera;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.adapter.IFCameraAdapter;
import com.qican.ifarm.adapter.ViewHolder;
import com.qican.ifarm.bean.IFarmCamera;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.listener.OnItemClickListener;
import com.qican.ifarm.utils.CommonTools;
import com.qican.ifarm.utils.IFarmFakeData;
import com.qican.ifarm.view.refresh.PullListView;
import com.qican.ifarm.view.refresh.PullToRefreshLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_videolist)
public class VideoListActivity extends Activity implements OnItemClickListener<IFarmCamera>, PullToRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    CommonTools myTool;

    @ViewById(R.id.tv_title)
    TextView tvTitle;

    @ViewById
    PullToRefreshLayout pullToRefreshLayout;

    @ViewById(R.id.pullListView)
    PullListView mListView;

    @ViewById(R.id.edt_search)
    EditText edtSearch;

    @ViewById(R.id.spinner_video)
    NiceSpinner spVideo;

    IFCameraAdapter mAdapter;

    Subarea mSubarea;
    private List<IFarmCamera> mDatas;
    private List<IFarmCamera> mSourceDatas;
    private List<String> spDatas;

    @AfterViews
    void main() {
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        pullToRefreshLayout.setOnRefreshListener(this);
    }

    private void initData() {
        tvTitle.setText("选择机位");
        tvTitle.requestFocus();

        mSubarea = (Subarea) myTool.getParam(Subarea.class);

        mSourceDatas = IFarmFakeData.getCameraList();
        mDatas = new ArrayList<>();
        mDatas.addAll(mSourceDatas);

        spDatas = new ArrayList<>();
        for (IFarmCamera camera : mDatas) {
            spDatas.add(camera.getShedNo() + "号棚");
        }
        spVideo.attachDataSource(spDatas);
        spVideo.addOnItemClickListener(this);

        mAdapter = new IFCameraAdapter(this, mDatas, R.layout.item_camera);
        mAdapter.setItemClickListener(this);
        mListView.setAdapter(mAdapter);
    }

    private void initView() {
        myTool = new CommonTools(this);
        mListView.setDividerHeight(20);
    }


    @Click
    void llBack() {
        this.finish();
    }

    @Click
    void btnSearch() {
        searchVideo(edtSearch.getText().toString());
    }

    private void searchVideo(String values) {
        String keys[] = values.split(" ");//搜索关键词
        String showKey = "";
        for (String key : keys) {
            showKey = showKey + key + ";";
        }
        mDatas.clear();
        mDatas.addAll(mSourceDatas);

        //移除不符合筛选条件的选项（注意从后往前不用维护List的长度）
        for (int i = mDatas.size() - 1; i >= 0; i--) {
            IFarmCamera camera = mDatas.get(i);
            for (String key : keys) {
                if (!camera.getName().contains(key)
                        && !(camera.getShedNo() + "号棚").contains(key)
                        && !camera.getLocation().contains(key))
                    mDatas.remove(i);
            }
        }

        myTool.showInfo("共查找到[ " + mDatas.size() + " ]项结果");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(ViewHolder helper, IFarmCamera item) {
        item.setSubarea(mSubarea);
        myTool.startActivity(item, VideoOfShedActivity_.class);
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        pullToRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshLayout.refreshFinish(true);
            }
        }, 800);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        pullToRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshLayout.loadMoreFinish(true);
            }
        }, 800);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        searchVideo(spDatas.get(position));
    }
}
