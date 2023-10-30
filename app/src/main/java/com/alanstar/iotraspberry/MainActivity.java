package com.alanstar.iotraspberry;

import static com.alanstar.iotraspberry.utils.GlobalValue.DOKIT_PROD_ID;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.alanstar.iotraspberry.exceptions.NotSupportedIPTypeException;
import com.alanstar.iotraspberry.utils.NetworkScanner;
import com.didichuxing.doraemonkit.DoKit;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnPermissionCallback {

    Button btn_request;
    TextView tv_devices;

    int clickFlag = 0;
    public static final String TAG = "MainActivity";
    public static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化
        initComponents();

        if (!checkPermission()) {
            Toast.makeText(this, "权限缺失", Toast.LENGTH_SHORT).show();
        }
        
        // 权限确认与申请
        XXPermissions.with(this)
                .permission(Permission.ACCESS_FINE_LOCATION)
                .permission(Permission.ACCESS_COARSE_LOCATION)
                .request(this);

        // Debug: doKit 调试工具初始化
        new DoKit.Builder(this.getApplication())
                .productId(DOKIT_PROD_ID)
                .build();

        // 监听事件
        btn_request.setOnClickListener(this);
    }

    /**
     * 注册组件
     */
    private void initComponents() {
        btn_request = findViewById(R.id.btn_request);
        tv_devices = findViewById(R.id.tv_devices);
    }

    /**
     * 检查权限
     * @return 是否具有权限
     */
    private boolean checkPermission() {
        int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 处理权限请求结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 检查权限
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "已有权限", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "未申请到权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 打开 WLAN
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void enableWLAN() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WLAN 未启动!", Toast.LENGTH_SHORT).show();
            // 通过 popup 启动 Settings Panel
            Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
            startActivity(panelIntent);
        }
    }

    /**
     * 获取 WLAN 状态
     * @return WLAN 状态 (启用 true  禁用 false)
     */
    private boolean getWLANStatus() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 按钮监听
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_request) {
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
                        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
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
                        runOnUiThread(() -> tv_devices.setText(builder));
                    });
                    lanInfoGetter.start();
                }
                else {
                    Thread lanDevicesScanner = new Thread(() -> {
                        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        NetworkScanner scanner = new NetworkScanner(wifiManager);
                        try {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "正在扫描网络中...", Toast.LENGTH_SHORT).show());
                            String builder = "可用 IP 地址: " + scanner.scanLANReachable(scanner.getIpAddress());
                            Log.d(TAG, builder);
                            clickFlag = 0;

                            runOnUiThread(() -> tv_devices.setText(builder));
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
            Toast.makeText(this, "存在部分权限未授予", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 没有获取到权限事件
     * @param permissions            请求失败的权限组
     * @param doNotAskAgain          是否勾选了不再询问选项
     */
    @Override
    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
        if (doNotAskAgain) {
            Toast.makeText(this, "权限申请被永久拒绝, 请手动授予", Toast.LENGTH_SHORT).show();
            // 被永久拒绝就跳到对应页面提示授权
            XXPermissions.startPermissionActivity(getApplicationContext(), permissions);
        }
        Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
    }
}