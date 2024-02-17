package com.alanstar.iotraspberry.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alanstar.iotraspberry.R;
import com.alanstar.iotraspberry.utils.TopBarController;
import com.qmuiteam.qmui.widget.QMUITopBar;

public class HomeFragment extends Fragment {

    // 引入组件
    QMUITopBar mTopBar;

    // 创建变量

    // 创建常量
    public static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // get view
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 注册组件
        initComponents(view);

        // Light: 初始化设置 TopBar
        TopBarController mTopBarController = new TopBarController();
        mTopBarController.clearTopBar(mTopBar);

        // 设置标题水平垂直居中
        mTopBar.setTitle("首页");

        return view;
    }

    // Light: 视图创建完毕
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Light: 注册组件
    private void initComponents(View view) {
        mTopBar = view.findViewById(R.id.mTopBar);
    }

    // Light: Fragment 获得焦点
    @Override
    public void onResume() {
        super.onResume();

    }

    // Light: Fragment 失去焦点
    @Override
    public void onPause() {
        super.onPause();

    }
}