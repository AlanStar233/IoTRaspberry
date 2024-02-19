package com.alanstar.iotraspberry.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.xuexiang.xui.widget.toast.XToast;

/**
 * 重载 XToast 中的方法，实现保留传统 Toast 逻辑的同时支持自定义 Toast 位置, 避免创建多余变量
 */
public class OverToast {

    public static Toast success(@NonNull Context context, String message, int xOffset, int yOffset) {
        Toast xToast = XToast.success(context, message);
        xToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, xOffset, yOffset);
        return xToast;
    }

    public static Toast error(@NonNull Context context, String message, int xOffset, int yOffset) {
        Toast xToast = XToast.error(context, message);
        xToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, xOffset, yOffset);
        return xToast;
    }

    public static Toast info(@NonNull Context context, String message, int xOffset, int yOffset) {
        Toast xToast = XToast.info(context, message);
        xToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, xOffset, yOffset);
        return xToast;
    }

    public static Toast warning(@NonNull Context context, String message, int xOffset, int yOffset) {
        Toast xToast = XToast.warning(context, message);
        xToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, xOffset, yOffset);
        return xToast;
    }

    public static Toast normal(@NonNull Context context, String message, int xOffset, int yOffset) {
        Toast xToast = XToast.normal(context, message);
        xToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM, xOffset, yOffset);
        return xToast;
    }
}
