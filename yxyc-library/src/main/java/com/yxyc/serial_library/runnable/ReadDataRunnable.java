package com.yxyc.serial_library.runnable;

import android.util.Log;
import android.widget.Toast;

import com.yxyc.serial_library.CH340Application;
import com.yxyc.serial_library.driver.CH340Driver;
import com.yxyc.serial_library.event.MessageEvent;
import com.yxyc.serial_library.logger.YXYCLog;
import com.yxyc.serial_library.utils.CH340Util;

import org.greenrobot.eventbus.EventBus;

/**
 * Function:ReadDataRunnable
 */

public class ReadDataRunnable implements Runnable {

    private String TAG = ReadDataRunnable.class.getSimpleName();
    private boolean mStop = false; // 是否停止线程
    private int dataLength = 0;

    @Override
    public void run() {
        startReadThread();
    }

    /**
     * 开启读取数据线程
     */
    private void startReadThread() {
        while (!mStop) {
            byte[] receiveBuffer = new byte[dataLength];// 接收数据数组
            if (CH340Driver.getDriver() == null) {
                Log.e(TAG, "startReadThread: " + "设备未连接" );
                return;
            }
            // 读取缓存区的数据长度
            int length = CH340Driver.getDriver().ReadData(receiveBuffer, dataLength);

            if (length > 0) {
                EventBus.getDefault().post(new MessageEvent(0x12, receiveBuffer));
            } else {
                // 无数据
                EventBus.getDefault().post(new MessageEvent(0x11, null));
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

    public void setDataLength(int length) {
        this.dataLength = length;
    }
}
