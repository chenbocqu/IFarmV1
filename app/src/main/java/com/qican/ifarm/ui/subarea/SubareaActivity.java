package com.qican.ifarm.ui.subarea;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qican.ifarm.R;
import com.qican.ifarm.bean.Farm;
import com.qican.ifarm.bean.Subarea;
import com.qican.ifarm.ui.farm.FarmSettingFragment;
import com.qican.ifarm.ui.node.LineChartFragment;
import com.qican.ifarm.ui.node.NodeDataFragment;
import com.qican.ifarm.utils.CommonTools;

import java.util.ArrayList;
import java.util.List;


public class SubareaActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final String TAG = "FarmActivity";
    public static final String KEY_AREA_INFO = "KEY_AREA_INFO";
    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;

    private RealtimeDataFragment fgNodeData;
    private HistoryDataLineFragment fgLineChart;
    private FarmSettingofAreaFragment fgControl;
    private int tabCnt = 0;

    private int mPerScreenWidth;
    private int initLeftMargin = 0;
    private int llWidth;
    private CommonTools myTool;
    private Subarea mArea;

    /**
     * Tab显示内容TextView
     */
    private TextView mTabPondTv, mTabCameraTv, mTabPumpTv;
    private LinearLayout llPond, llCamera, llPump, llBack;//tab上面的点击按钮
    /**
     * Tab的那个引导线
     */
    private ImageView mTabLineIv;

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;
    private int offset = 55; //测量得重新去做

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nodeinfo);

        initView();
        initData();
        initEvent();

//        initTabLineWidth();
        myInitTabLine();
    }

    private void initData() {
    }

    private void myInitTabLine() {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        llPond.measure(w, h);
        llWidth = llPond.getMeasuredWidth() + offset;
        Log.i(TAG, "myInitTabLine: getWidth," + llPond.getWidth() + ",getMeasuredWidth," + llPond.getMeasuredWidth());

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.leftMargin = initLeftMargin;
        lp.width = llWidth;

        mTabLineIv.setLayoutParams(lp);
        //初始选中第一个
        setTvSelected(mTabPondTv);
    }


    /**
     * 设置滑动条的宽度为屏幕的1/3(根据Tab的个数而定)
     */
    private void initTabLineWidth() {

        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / tabCnt - 2 * initLeftMargin;
        mPerScreenWidth = screenWidth / tabCnt;//每个tab的宽度

        lp.leftMargin = initLeftMargin;

        mTabLineIv.setLayoutParams(lp);

        //初始选中第一个
        setTvSelected(mTabPondTv);
    }

    private void toTabByIndex(int curTabIndex) {
        DisplayMetrics dpMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay()
                .getMetrics(dpMetrics);
        screenWidth = dpMetrics.widthPixels;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        lp.width = screenWidth / tabCnt - 2 * initLeftMargin;
        lp.leftMargin = curTabIndex * screenWidth / tabCnt + initLeftMargin;

        mTabLineIv.setLayoutParams(lp);
    }

    /**
     * 处理文字的颜色变化
     *
     * @param tv
     */
    private void setTvUnselected(TextView tv) {
        tv.setTextColor(getResources().getColorStateList(R.color.tv_color_unselected));
    }

    private void setTvSelected(TextView tv) {
        resetTextView();
        tv.setTextColor(getResources().getColorStateList(R.color.tv_color_selected));
    }

    private void resetTextView() {
        setTvUnselected(mTabPondTv);
        setTvUnselected(mTabCameraTv);
        setTvUnselected(mTabPumpTv);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.vp_tab);
        mTabLineIv = (ImageView) findViewById(R.id.id_tab_line_iv);//底部的tabLine

        mTabPondTv = (TextView) findViewById(R.id.tv_pood);
        mTabCameraTv = (TextView) findViewById(R.id.tv_camera);
        mTabPumpTv = (TextView) findViewById(R.id.tv_pump);

        llPond = (LinearLayout) findViewById(R.id.ll_pond);
        llCamera = (LinearLayout) findViewById(R.id.ll_camera);
        llPump = (LinearLayout) findViewById(R.id.ll_pump);
        llBack = (LinearLayout) findViewById(R.id.ll_back);

        myTool = new CommonTools(this);

        loadFragments();
    }

    private void initEvent() {
        mViewPager.setOnPageChangeListener(this);

        llPond.setOnClickListener(this);
        llCamera.setOnClickListener(this);
        llPump.setOnClickListener(this);
        llBack.setOnClickListener(this);
    }

    private void loadFragments() {

        //所有订单
        fgNodeData = new RealtimeDataFragment();
        fgLineChart = new HistoryDataLineFragment();
        fgControl = new FarmSettingofAreaFragment();

        mArea = new Subarea();
        mArea = (Subarea) myTool.getParam(mArea);

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_AREA_INFO, mArea);

        fgNodeData.setArguments(bundle);
        fgLineChart.setArguments(bundle);
        fgControl.setArguments(bundle);

        mTabs.add(fgNodeData);
        mTabs.add(fgLineChart);
        mTabs.add(fgControl);

        tabCnt = mTabs.size();

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
        mViewPager.setAdapter(mAdapter);
    }

    @Override
    public void onPageScrolled(int position, float offset, int positionOffsetPixels) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv
                .getLayoutParams();
        if (offset > 0) {
            lp.leftMargin = (int) (offset * llWidth + position
                    * llWidth + initLeftMargin);
        }
//        if (offset > 0) {
//            lp.leftMargin = (int) (offset * mPerScreenWidth + position
//                    * mPerScreenWidth + initLeftMargin);
//        }
        Log.i("leftMargin", "leftMargin: -------[" + lp.leftMargin + "]");
        mTabLineIv.setLayoutParams(lp);
    }

    /**
     * 根据当前位置变换字体颜色
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                setTvSelected(mTabPondTv);
                break;
            case 1:
                setTvSelected(mTabCameraTv);
                break;
            case 2:
                setTvSelected(mTabPumpTv);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 处理点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: ");
        switch (v.getId()) {
            case R.id.ll_pond:
                //带滑动效果
                mViewPager.setCurrentItem(0, true);
                setTvSelected(mTabPondTv);
//                toTabByIndex(0);
                break;
            case R.id.ll_camera:
                mViewPager.setCurrentItem(1, true);
                setTvSelected(mTabCameraTv);
//                toTabByIndex(1);
                break;
            case R.id.ll_pump:
                mViewPager.setCurrentItem(2, true);
                setTvSelected(mTabPumpTv);
//                toTabByIndex(2);
                break;
            case R.id.ll_back:
                finish();
                break;
        }
    }
}
