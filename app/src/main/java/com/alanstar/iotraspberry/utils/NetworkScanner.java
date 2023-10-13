package com.alanstar.iotraspberry.utils;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 扫描局域网下的设备
 */
public class NetworkScanner {

    public static final String TAG = "NetworkScanner";
    private final DhcpInfo dhcpInfo;

    /**
     * 构造函数
     */
    public NetworkScanner(WifiManager wifiManager) {
        this.dhcpInfo = wifiManager.getDhcpInfo();
    }

    /**
     * 获取 IP 地址
     * @return IP 地址
     */
    public String getIpAddress() {
        Log.d(TAG, "ipAddress: " + dhcpInfo.ipAddress);
        return formatIpAddress(dhcpInfo.ipAddress);
    }

    /**
     * 获取 子网掩码
     * @return 子网掩码
     */
    public String getNetMask() {
        Log.d(TAG, "netMask: " + dhcpInfo.netmask);
        return formatIpAddress(dhcpInfo.netmask);
    }

    /**
     * 获取 网关地址
     * @return 网关地址
     */
    public String getGateway() {
        Log.d(TAG, "gateway: " + dhcpInfo.gateway);
        return formatIpAddress(dhcpInfo.gateway);
    }

    /**
     * 获取 首选 DNS
     * @return 首选 DNS
     */
    public String getFirstDNS() {
        Log.d(TAG, "firstDNS: " + dhcpInfo.dns1);
        return formatIpAddress(dhcpInfo.dns1);
    }

    /**
     * 获取 备选 DNS
     * @return 备选 DNS
     */
    public String getSecondDNS() {
        Log.d(TAG, "secondDNS: " + dhcpInfo.dns2);
        return formatIpAddress(dhcpInfo.dns2);
    }

    /**
     * 获取 服务器地址
     * @return 服务器地址
     */
    public String getServerAddress() {
        Log.d(TAG, "serverAddress: " + dhcpInfo.serverAddress);
        return formatIpAddress(dhcpInfo.serverAddress);
    }

    /**
     * 通过 ICMP 扫描可达的 IP
     * @param ip 通过 IP 可解析出网段信息
     * @return 可达 IP 地址列表
     */
    public List<String> scanLANReachable(String ip) throws NotSupportedIPTypeException {

        String baseIPAddress;
        String targetIPAddress;

        // 用带锁的队列使得多线程返回的结果按照顺序排列
        ConcurrentLinkedQueue<String> reachableDevices = new ConcurrentLinkedQueue<>();
        if (getIPType(ip).equals("A")) {
            // 提取 A 类: 125.20.0.100 -> 125.
            baseIPAddress = ip.substring(0, ip.indexOf(".")) + ".";
            for (int i = 1; i <=254; i++) {
                for (int j = 1; j <=254; j++) {
                    for (int k = 1; k <= 254; k++) {
                        // 拼接
                        targetIPAddress = baseIPAddress + i + "." + j + "." + k;

                        // 多线程 ping
                        String finalTargetIPAddress = targetIPAddress;
                        Thread multiPingThread = new Thread(multiPingRunnable(reachableDevices, finalTargetIPAddress));
                        multiPingThread.start();
                    }
                }
            }
        }
        else if (getIPType(ip).equals("B")) {
            // 提取 B 类: 172.20.0.100 -> 172.20.
            baseIPAddress = ip.substring(0, ip.indexOf(".", ip.indexOf(".") + 1)) + ".";
            for (int i = 1; i <= 254; i++) {
                for (int j = 1; j <= 254; j++) {
                    // 拼接
                    targetIPAddress = baseIPAddress + i + "." + j;

                    // 多线程 ping
                    String finalTargetIPAddress = targetIPAddress;
                    Thread multiPingThread = new Thread(multiPingRunnable(reachableDevices, finalTargetIPAddress));
                    multiPingThread.start();
                }
            }
        }
        else if (getIPType(ip).equals("C")) {
            // 提取 C 类: 192.168.31.100 -> 192.168.31.
            baseIPAddress = ip.substring(0, ip.lastIndexOf(".") + 1);
            for (int i = 1; i <= 254; i++) {
                // 拼接
                targetIPAddress = baseIPAddress + i;

                // 多线程 ping
                String finalTargetIPAddress = targetIPAddress;
                Thread multiPingThread = new Thread(multiPingRunnable(reachableDevices, finalTargetIPAddress));
                multiPingThread.start();
            }
        }
        else {
            throw new NotSupportedIPTypeException("不支持的IP类型");
        }
        return new ArrayList<>(reachableDevices);
    }

    /**
     * 多线程 ping
     * @param reachableDevices 带锁 list
     * @param finalTargetIPAddress for 拼接后的 IP 地址
     * @return null
     */
    Runnable multiPingRunnable(ConcurrentLinkedQueue<String> reachableDevices, String finalTargetIPAddress) {
        try {
            // 构建 ping 命令 (只 ping 1 次)
            ProcessBuilder processBuilder = new ProcessBuilder("ping", "-c", "1", finalTargetIPAddress);
            Process process = processBuilder.start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) { result.append(line); }

            // 阻塞等待命令执行完成
            int exitCode = process.waitFor();

            // 判断命令可达, 可达则加入列表
            if (exitCode == 0) {
                reachableDevices.add(finalTargetIPAddress);
            }
        } catch (IOException | InterruptedException e) {
            Log.d(TAG, "ping error: " + e.getMessage());
        }
        return null;
    }

    /**
     * 格式化 IP 地址
     * @param ip ip
     * @return like 192.168.1.1 的四段三位字符串
     */
    private String formatIpAddress(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 24) & 0xFF);
    }

    /**
     * 判断 IP 类型 (IPv4)
     * @param ip IP 地址
     * @return 类型 (A/B/C/D/E/Invalid)
     */
    public String getIPType(String ip) {
        String[] ipParts = ip.split("\\.");
        int firstPart = Integer.parseInt(ipParts[0]);

        if (firstPart >= 1 && firstPart <= 126) {
            return "A";
        } else if (firstPart >= 128 && firstPart <= 191) {
            return "B";
        } else if (firstPart >= 192 && firstPart <= 223) {
            return "C";
        } else if (firstPart >= 224 && firstPart <= 239) {
            return "D";
        } else if (firstPart >= 240 && firstPart <= 255) {
            return "E";
        } else {
            return "Invalid";
        }
    }
}
