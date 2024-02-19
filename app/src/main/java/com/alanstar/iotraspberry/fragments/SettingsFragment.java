package com.alanstar.iotraspberry.fragments;

import static com.alanstar.iotraspberry.utils.GlobalValue.CONFIG_FILE_NAME;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alanstar.iotraspberry.R;
import com.alanstar.iotraspberry.utils.OverToast;
import com.alanstar.iotraspberry.utils.TopBarController;
import com.qmuiteam.qmui.widget.QMUITopBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    // 引入组件
    QMUITopBar mTopBar;

    EditText et_broker_address;
    Button btn_SettingsUpdate;

    // 创建变量
    boolean isFirstLoad = true;     // 是否为第一次加载 Fragment

    // 创建常量
    public static final String TAG = "SettingsFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // get view
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // 注册组件
        initComponents(view);

        // Light: 初始化设置 TopBar
        TopBarController mTopBarController = new TopBarController();
        mTopBarController.clearTopBar(mTopBar);

        // 设置标题水平垂直居中
        mTopBar.setTitle("设置");

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

        et_broker_address = view.findViewById(R.id.et_broker_address);
        btn_SettingsUpdate = view.findViewById(R.id.btn_SettingsUpdate);
    }

    // Light: Fragment 获得焦点
    @Override
    public void onResume() {
        super.onResume();

        // Light: Fragment 初加载填充值
        if (isFirstLoad) {
            isFirstLoad = false;
            Thread configRefresherThread = new Thread(configRefresherRunnable);
            configRefresherThread.start();
        } else {
            Thread configRefresherThread = new Thread(configRefresherRunnable);
            configRefresherThread.start();
        }

        btn_SettingsUpdate.setOnClickListener(this);
    }

    // Light: Fragment 失去焦点
    @Override
    public void onPause() {
        super.onPause();

    }

    // Light: config 更新器
    Runnable configRefresherRunnable = () -> {

        try {
            File configFile = new File(requireContext().getFilesDir(), CONFIG_FILE_NAME);

            FileReader configReader = new FileReader(configFile);
            BufferedReader configBufferedReader = new BufferedReader(configReader);

            StringBuilder configStringBuilder = new StringBuilder();
            String configLine;
            while ((configLine = configBufferedReader.readLine()) != null) {
                configStringBuilder.append(configLine);
            }
            configBufferedReader.close();

            // 数据提取
            JSONObject configJson = new JSONObject(configStringBuilder.toString());
            String mqttAddress = configJson.getString("MQTT_SERVER_ADDRESS");

            // 数据更新
            requireActivity().runOnUiThread(() -> et_broker_address.setText(mqttAddress));
        } catch (IOException | JSONException e) {
            Log.d(TAG, "设置更新器错误: ", e);
        }
    };

    // Light: config 数值提交器
    Runnable configValueSubmitterRunnable = () -> {

        try {
            File configFile = new File(requireContext().getFilesDir(), CONFIG_FILE_NAME);

            // 读取配置文件
            FileReader configReader = new FileReader(configFile);
            BufferedReader configBufferedReader = new BufferedReader(configReader);
            StringBuilder configStringBuilder = new StringBuilder();

            String configLine;
            while ((configLine = configBufferedReader.readLine()) != null) {
                configStringBuilder.append(configLine);
            }
            configBufferedReader.close();

            // 将读取到的内容转换为 JSONObject
            JSONObject configJson = new JSONObject(configStringBuilder.toString());

            // Light: 更新数据
            configJson.put("MQTT_SERVER_ADDRESS", et_broker_address.getText().toString());

            // 以默认的覆盖模式写回文件
            FileWriter configWriter = new FileWriter(configFile, false);
            configWriter.write(configJson.toString());
            configWriter.close();

        } catch (IOException | JSONException e) {
            Log.d(TAG, "设置更新器错误: ", e);
        }
    };
    // Light: 按钮点击事件
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_SettingsUpdate) {
            // 处理按钮点击事件
            Thread configValueSubmitterThread = new Thread(configValueSubmitterRunnable);
            configValueSubmitterThread.start();
            OverToast.success(requireActivity(), "设置项已更新", 0, 300).show();
        }
    }
}