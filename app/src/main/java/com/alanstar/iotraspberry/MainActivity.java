package com.alanstar.iotraspberry;

import static com.alanstar.iotraspberry.utils.GlobalValue.CONFIG_FILE_NAME;
import static com.alanstar.iotraspberry.utils.GlobalValue.DOKIT_PROD_ID;
import static com.alanstar.iotraspberry.utils.GlobalValue.MQTT_SERVER_DEFAULT_ADDRESS;
import static com.alanstar.iotraspberry.utils.GlobalValue.PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alanstar.iotraspberry.fragments.ConnectionFragment;
import com.alanstar.iotraspberry.fragments.HomeFragment;
import com.alanstar.iotraspberry.fragments.SettingsFragment;
import com.alanstar.iotraspberry.fragments.SubscribeFragment;
import com.alanstar.iotraspberry.utils.MyFragmentPagerAdapter;
import com.alanstar.iotraspberry.utils.TopBarController;
import com.didichuxing.doraemonkit.DoKit;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.qmuiteam.qmui.widget.QMUITopBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener, OnPermissionCallback {

    // 组件引入 & 变量定义
    private ViewPager mViewPager;
    public ArrayList<Fragment> fragmentList;
    private RadioGroup bottomBarGroup;

    // Light: Fragments 引入
    public HomeFragment homeFragment;   // 主页
    public ConnectionFragment connectionFragment;   // 扫描
    public SubscribeFragment subscribeFragment;   // 订阅
    public SettingsFragment settingsFragment;   // 设置

    // Light: 初始化一个 TopBarController
    public TopBarController topBarController = new TopBarController();
    // Light: 初始化一个公共变量 mTopBarStatement
    public int mTopBarStatement;

    // TAG
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 组件注册
        initComponents();

        // 注册 ViewPager
        initViewPager();

        // config 初始化
        initConfigFile();

        // RadioGroup 监听
        bottomBarGroup.setOnCheckedChangeListener(this);

        // 权限校验
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
    }

    // Light: 沉浸式工具栏
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.parseColor("#00AEEC"));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    // 组件注册
    private void initComponents() {
        mViewPager = findViewById(R.id.mViewPager);
        bottomBarGroup = findViewById(R.id.bottomBarRadioGroup);
    }

    // ViewPager 初始化
    private void initViewPager() {
        // Light: 注册 Fragments
        homeFragment = new HomeFragment();
        connectionFragment = new ConnectionFragment();
        subscribeFragment = new SubscribeFragment();
        settingsFragment = new SettingsFragment();

        // Light: 在 ArrayList 中加入新增的 Fragments
        fragmentList = new ArrayList<>();
        fragmentList.add(0, homeFragment);
        fragmentList.add(1, connectionFragment);
        fragmentList.add(2, subscribeFragment);
        fragmentList.add(3, settingsFragment);

        // set Adapter
        mViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        // ViewPager 初始化指向
        mViewPager.setCurrentItem(0);
        // 页面切换监听
        mViewPager.addOnPageChangeListener(this);
    }

    // Light: Settings 配置文件检测
    public void initConfigFile() {
        File config = new File(getFilesDir(), CONFIG_FILE_NAME);
        if (!config.exists()) {
            try {
                Toast.makeText(this, "配置文件不存在，正在创建...", Toast.LENGTH_SHORT).show();
                File configFile = new File(getFilesDir(), CONFIG_FILE_NAME);
                // 对配置文件写入配置
                JSONObject configJson = new JSONObject();
                configJson.put("MQTTServerAddress", MQTT_SERVER_DEFAULT_ADDRESS);

                // 配置写入文件
                FileWriter configWriter = new FileWriter(configFile);
                configWriter.write(configJson.toString());
                configWriter.close();
            } catch (IOException | JSONException e) {
                Log.e(TAG, "initConfigFile Error: ", e);
            }
        }
    }

    // 底部 Navi 切换逻辑
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        // Light: 获取当前 Fragment
        int index = mViewPager.getCurrentItem();
        Fragment fragment = (Fragment) Objects.requireNonNull(mViewPager.getAdapter()).instantiateItem(mViewPager, index);
        QMUITopBar fragmentTopBar = fragment.requireView().findViewById(R.id.mTopBar);

        // 切换
        if (checkedId == R.id.radioHome) {
            mViewPager.setCurrentItem(0, true);
        } else if (checkedId == R.id.radioConnection) {
            mViewPager.setCurrentItem(1, true);
        } else if (checkedId == R.id.radioSubscribe) {
            mViewPager.setCurrentItem(2, true);
        } else if (checkedId == R.id.radioSettings) {
            mViewPager.setCurrentItem(3, true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                bottomBarGroup.check(R.id.radioHome);
                break;
            case 1:
                bottomBarGroup.check(R.id.radioConnection);
                break;
            case 2:
                bottomBarGroup.check(R.id.radioSubscribe);
                break;
            case 3:
                bottomBarGroup.check(R.id.radioSettings);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
            } else {
                Toast.makeText(this, "未申请到权限", Toast.LENGTH_SHORT).show();
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