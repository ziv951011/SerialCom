package com.yxyc.serial_library.runnable;

import android.util.Log;
import android.widget.Toast;

import com.yxyc.serial_library.CH340Application;
import com.yxyc.serial_library.driver.CH340Driver;
import com.yxyc.serial_library.logger.YXYCLog;
import com.yxyc.serial_library.utils.CH340Util;

/**
 * Function:ReadDataRunnable
 */

public class ReadDataRunnable implements Runnable {

    private String TAG = ReadDataRunnable.class.getSimpleName();
    private boolean mStop = false; // 是否停止线程

    @Override
    public void run() {
        startReadThread();
    }

    /**
     * 开启读取数据线程
     */
    private void startReadThread() {
        while (!mStop) {
            byte[] receiveBuffer = new byte[4096];// 接收数据数组
            if (CH340Driver.getDriver() == null) {
                Log.e(TAG, "startReadThread: " + "设备未连接" );
                return;
            }
            // 读取缓存区的数据长度
            int length = CH340Driver.getDriver().ReadData(receiveBuffer, 4096);

            switch (length) {
                case 0: // 无数据
                    YXYCLog.i(TAG, "No data~");
                    break;
                default: // 有数据时的处理
                    // 将此处收到的数组转化为HexString
                    String hexString = CH340Util.bytesToHexString(receiveBuffer, length);
                    YXYCLog.i(TAG, "ReadHexString===" + hexString + ",length===" + length);
                    break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止读取任务
     */
    public void stopTask() {
        mStop = true;
    }

}
