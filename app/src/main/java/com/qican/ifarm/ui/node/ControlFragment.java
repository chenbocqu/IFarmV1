package com.qican.ifarm.ui.node;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qican.ifarm.R;
import com.qican.ifarm.utils.CommonTools;

public class ControlFragment extends Fragment {
    private View view;
    private String TAG = "ControlFragment";

    private CommonTools myTool;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_control, container, false);
        initView(view);
        return view;
    }


    private void initView(View v) {
        myTool = new CommonTools(getActivity());
    }
}
