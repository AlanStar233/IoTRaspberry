package com.alanstar.iotraspberry.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alanstar.iotraspberry.R;
import com.alanstar.iotraspberry.recycler.SubscribeAdapter;
import com.alanstar.iotraspberry.recycler.SubscribeItem;
import com.alanstar.iotraspberry.utils.TopBarController;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.ArrayList;
import java.util.List;

public class SubscribeFragment extends Fragment {

    // 引入组件
    QMUITopBar mTopBar;

    RecyclerView rv_subscribe_list;

    // 创建变量
    List<SubscribeItem> items = new ArrayList<>();

    // 创建常量
    public static final String TAG = "SubscribeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // get view
        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);

        // 注册组件
        initComponents(view);

        // Light: 初始化设置 TopBar
        TopBarController mTopBarController = new TopBarController();
        mTopBarController.clearTopBar(mTopBar);

        // Light: Mock 一些值
        items.add(new SubscribeItem("mqtt://broker.biliforum.cn:1883", "testTopic", "mqtt", R.drawable.iot_app_icon));
        items.add(new SubscribeItem("mqtt://broker.biliforum.cn:1884", "testTopic1", "mqtts", R.drawable.iot_app_icon));
        items.add(new SubscribeItem("mqtt://broker.biliforum.cn:1885", "testTopic2", "ws", R.drawable.home_check));
        items.add(new SubscribeItem("mqtt://broker.biliforum.cn:1886", "testTopic3", "wss", R.drawable.info_check));
        items.add(new SubscribeItem("mqtt://broker.biliforum.cn:1887", "testTopic4", "tcp", R.drawable.settings_check));
        items.add(new SubscribeItem("mqtt://broker.biliforum.cn:1888", "testTopic5", "wss", R.drawable.iot_app_icon));

        // Light: RecyclerView 初始化
        rv_subscribe_list.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_subscribe_list.setAdapter(new SubscribeAdapter(requireContext().getApplicationContext(), items));

        // 设置标题水平垂直居中
        mTopBar.setTitle("订阅");

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

        rv_subscribe_list = view.findViewById(R.id.rv_subscribe_list);
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