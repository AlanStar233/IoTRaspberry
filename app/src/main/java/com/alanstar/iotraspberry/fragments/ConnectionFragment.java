package com.alanstar.iotraspberry.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.alanstar.iotraspberry.R;
import com.alanstar.iotraspberry.exceptions.NotSupportedIPTypeException;
import com.alanstar.iotraspberry.utils.NetworkScanner;
import com.alanstar.iotraspberry.utils.TopBarController;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.qmuiteam.qmui.widget.QMUITopBar;

import java.util.List;

public class ConnectionFragment extends Fragment implements View.OnClickListener, OnPermissionCallback {

    // 引入组件
    QMUITopBar mTopBar;

    TextView tv_conn_devices;
    Button btn_conn_request;

    // 创建变量

    // 创建常量
    public static final String TAG = "ConnectionFragment";
    private int clickFlag = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // get view
        View view = inflater.inflate(R.layout.fragment_connection, container, false);

        // 注册组件
        initComponents(view);

        // Light: 初始化设置 TopBar
        TopBarController mTopBarController = new TopBarController();
        mTopBarController.clearTopBar(mTopBar);

        // 设置标题水平垂直居中
        mTopBar.setTitle("扫描");

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

        tv_conn_devices = view.findViewById(R.id.tv_conn_devices);
        btn_conn_request = view.findViewById(R.id.btn_conn_request);
    }

    // Light: Fragment 获得焦点
    @Override
    public void onResume() {
        super.onResume();

        btn_conn_request.setOnClickListener(this);
    }

    // Light: Fragment 失去焦点
    @Override
    public void onPause() {
        super.onPause();

        clickFlag = 0;      // 当 Fragment 失去焦点时, 重置 clickFlag 为默认值
        Toast.makeText(requireContext().getApplicationContext(), "重置 clickFlag", Toast.LENGTH_SHORT).show();
    }

    /**
     * 打开 WLAN
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void enableWLAN() {
        WifiManager wifiManager = (WifiManager) requireContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(requireContext().getApplicationContext(), "WLAN 未启动!", Toast.LENGTH_SHORT).show();
            // 通过 popup 方式启动 Settings Panel
            Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
            startActivity(panelIntent);
        }
    }

    /**
     * 获取 WLAN 状态
     * @return WLAN 状态 (启用 true  禁用 false)
     */
    private boolean getWLANStatus() {
        WifiManager wifiManager = (WifiManager)  requireContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    // Light: 按钮点击事件
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_conn_request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                enableWLAN();
            }
            // 如果没权限则申请权限
            Log.d(TAG, "Permission: " + getWLANStatus());
            if (!getWLANStatus()) {
                XXPermissions.with(this)
                        .permission(Permission.ACCESS_FINE_LOCATION)
                        .permission(Permission.ACCESS_COARSE_LOCATION)
                        .request(this);
            }
            // 有权限则尝试获取列表
            else {
                if (clickFlag != 1) {
                    Thread lanInfoGetter = new Thread(() -> {
                        // 用 Runnable 在新线程上执行 net IO 操作
                        WifiManager wifiManager = (WifiManager) requireContext().getSystemService(Context.WIFI_SERVICE);
                        NetworkScanner scanner = new NetworkScanner(wifiManager);
                        String builder = "IP 地址: " + scanner.getIpAddress() + "\n" +
                                "子网掩码: " + scanner.getNetMask() + "\n" +
                                "网关地址: " + scanner.getGateway() + "\n" +
                                "服务器地址: " + scanner.getServerAddress() + "\n" +
                                "首选 DNS 地址: " + scanner.getFirstDNS() + "\n" +
                                "备用 DNS 地址: " + scanner.getSecondDNS() + "\n";
                        Log.d(TAG, builder);

                        clickFlag = 1;

                        // 填充到 TextView (转到 UI 线程)
                        requireActivity().runOnUiThread(() -> tv_conn_devices.setText(builder));
                    });
                    lanInfoGetter.start();
                }
                else {
                    Thread lanDevicesScanner = new Thread(() ->{
                        WifiManager wifiManager = (WifiManager) requireContext().getSystemService(Context.WIFI_SERVICE);
                        NetworkScanner scanner = new NetworkScanner(wifiManager);
                        try {
                            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext().getApplicationContext(), "正在扫描网络中...", Toast.LENGTH_SHORT).show());
                            String builder = "可用 IP 地址: " + scanner.scanLANReachable(scanner.getIpAddress());
                            Log.d(TAG, builder);
                            clickFlag = 0;

                            requireActivity().runOnUiThread(() -> tv_conn_devices.setText(builder));
                        } catch (NotSupportedIPTypeException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    lanDevicesScanner.start();
                }
            }
        }
    }

    /**
     * 获取权限事件
     * @param permissions           请求成功的权限组
     * @param allGranted            是否全部授予了
     */
    @Override
    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
        if (!allGranted) {
            Toast.makeText(requireContext().getApplicationContext(), "存在部分权限未授予", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(requireContext().getApplicationContext(), "权限获取成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
        if (doNotAskAgain) {
            Toast.makeText(requireContext().getApplicationContext(), "权限申请被永久拒绝, 请手动授予", Toast.LENGTH_SHORT).show();
            // 被永久拒绝就跳到对应页面提示授权
            XXPermissions.startPermissionActivity(requireContext().getApplicationContext(), permissions);
        }
        Toast.makeText(requireContext().getApplicationContext(), "权限获取成功", Toast.LENGTH_SHORT).show();
    }
}