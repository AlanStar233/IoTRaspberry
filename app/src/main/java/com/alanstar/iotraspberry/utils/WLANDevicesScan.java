package com.alanstar.iotraspberry.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * WLAN 扫描
 */
public class WLANDevicesScan implements OnPermissionCallback {

    public static final String TAG = "WLANDevicesScan";
    Activity activity;

    /**
     * 构造函数
     */
    public WLANDevicesScan(Activity activity) {
        this.activity = activity;
    }

    /**
     * 获取设备列表
     */
    public List<ScanResult> getNetworkDevices() {

        // 初始化一个设备列表和筛选结果列表
        List<ScanResult> scanResults = new ArrayList<>();
        List<ScanResult> devicesInSameNetwork = new ArrayList<>();

        // 初始化 WifiManager
        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        // 获取当前连接的 Wi-Fi 网络的SSID
        String currentNetworkSSID = wifiManager.getConnectionInfo().getSSID();

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 没有权限则申请权限
            Log.d(TAG, "No permissions");
            XXPermissions.with(activity)
                    .permission(Permission.ACCESS_FINE_LOCATION)
                    .permission(Permission.ACCESS_COARSE_LOCATION)
                    .request(this);
        } else {
            Log.d(TAG, "Have permissions");
            // 开始扫描
            wifiManager.getScanResults();

            scanResults = wifiManager.getScanResults();
            // Log 打印一下确认数据存在
            Log.d(TAG, "Current Connected SSID:" + currentNetworkSSID);
            for (ScanResult scanResult : scanResults) {
                Log.d(TAG, "SSID: " + scanResult.SSID + " BSSID: " + scanResult.BSSID + " Level: " + scanResult.level);
            }

            // TODO: 筛选同一网络下的设备
            for (ScanResult scanResult : scanResults) {
                if (scanResult.SSID.equals(currentNetworkSSID)) {
                    devicesInSameNetwork.add(scanResult);
                }
            }
        }
//        return devicesInSameNetwork;
        return scanResults;
    }

    /**
     * 获取权限事件
     * @param permissions           请求成功的权限组
     * @param allGranted            是否全部授予了
     */
    @Override
    public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
        if (!allGranted) {
            activity.runOnUiThread(() -> Toast.makeText(activity, "存在部分权限未授予", Toast.LENGTH_SHORT).show());
            return;
        }
        activity.runOnUiThread(() -> Toast.makeText(activity, "权限获取成功", Toast.LENGTH_SHORT).show());
    }

    /**
     * 没有获取到权限事件
     * @param permissions            请求失败的权限组
     * @param doNotAskAgain          是否勾选了不再询问选项
     */
    @Override
    public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
        if (doNotAskAgain) {
            activity.runOnUiThread(() -> Toast.makeText(activity, "权限申请被永久拒绝, 请手动授予", Toast.LENGTH_SHORT).show());
            // 被永久拒绝就跳到对应页面提示授权
            XXPermissions.startPermissionActivity(activity, permissions);
        }
        activity.runOnUiThread(() -> Toast.makeText(activity, "权限获取成功", Toast.LENGTH_SHORT).show());

    }
}
