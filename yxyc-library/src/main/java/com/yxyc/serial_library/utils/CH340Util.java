package com.yxyc.serial_library.utils;

import android.support.annotation.NonNull;

import com.yxyc.serial_library.driver.CH340Driver;
import com.yxyc.serial_library.logger.YXYCLog;

/**
 * Function:CH340数据处理工具类
 */

public class CH340Util {

    private static final String TAG = CH340Util.class.getSimpleName();

    /**
     * write data in ch340.
     *
     * @param byteArray 字节数组
     * @return 返回写入的结果，-1表示写入失败！
     */
    public static int writeData(@NonNull byte[] byteArray) {
        // 将此处收到的数组转化为HexString
//        String string = StringUtils.bytesToHexString(byteArray);
        if (CH340Driver.getDriver() == null) {
            // 没有设备
            return -10;
        }
        return CH340Driver.getDriver().WriteData(byteArray, byteArray.length);

    }

    /**
     * byte[]转换为hexString
     *
     * @param buffer 数据
     * @param size   字符数
     * @return 返回转换后的十六进制字符串
     */
    public static String bytesToHexString(byte[] buffer, final int size) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (buffer == null || size <= 0) return null;
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(buffer[i] & 0xff);
            if (hex.length() < 2) stringBuilder.append(0);
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }


    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }
}
