package com.yxyc.serial_library.runnable;

import android.os.Message;
import android.util.Log;

import com.yxyc.serial_library.driver.CH340Driver;
import com.yxyc.serial_library.event.MessageEvent;
import com.yxyc.serial_library.event.YxycEvent;
import com.yxyc.serial_library.utils.CH340Constants;
import com.yxyc.serial_library.utils.CH340Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.yxyc.serial_library.utils.CH340Constants.FILD_DATA;
import static com.yxyc.serial_library.utils.CH340Constants.SUCCESS_DATA;

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
            /**
             * 默认值为10
             */
            if (dataLength <= 0) {
                dataLength = 10;
            }
            byte[] receiveBuffer = new byte[dataLength];// 接收数据数组
            if (CH340Driver.getDriver() == null) {
                Log.e(TAG, "startReadThread: " + "设备未连接");
                break;
            }
            // 读取缓存区的数据长度
            int length = CH340Driver.getDriver().ReadData(receiveBuffer, dataLength);
            Log.e(TAG, "startReadThread: " + CH340Util.bytesToHexString(receiveBuffer, receiveBuffer.length));


            if (length > 0) {
                EventBus.getDefault().post(new MessageEvent(SUCCESS_DATA, receiveBuffer));
            } else {
                // 无数据
                EventBus.getDefault().post(new MessageEvent(FILD_DATA, null));
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
