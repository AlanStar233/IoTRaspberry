package com.alanstar.iotraspberry.exceptions;

import androidx.annotation.NonNull;

/**
 * 不支持的 IP 类型 Exception
 */
public class NotSupportedIPTypeException extends Exception {

    /**
     * 不支持的 IP 类型 (常见于 type D/E 这类保留地址)
     * @param message 错误信息
     */
    public NotSupportedIPTypeException(String message) {
        super(message);
    }

    /**
     * 异常详细信息
     * @return 异常详情
     */
    @NonNull
    @Override
    public String toString() {
        return "NotSupportedIPTypeException: " + getMessage();
    }
}
