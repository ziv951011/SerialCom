package com.yxyc.serial_library.runnable;

import android.os.Message;
import android.util.Log;

import com.yxyc.serial_library.driver.CH340Driver;
import com.yxyc.serial_library.event.MessageEvent;
import com.yxyc.serial_library.event.YxycEvent;
import com.yxyc.serial_library.utils.CH340Constants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Function:ReadDataRunnable
 */

public class ReadDataRunnable implements Runnable {

    private String TAG = ReadDataRunnable.class.getSimpleName();
    private boolean mStop = false; // 是否停止线程
    private int dataLength = 0;
    private MessageEvent messageEvent;


    @Override
    public void run() {
        startReadThread();
    }


    /**
     * 开启读取数据线程
     */
    private void startReadThread() {

        /**
         * 增加同步锁机制
         */
        synchronized (ReadDataRunnable.class) {
            while (true) {
                /**
                 * 默认值为10
                 */
                if (dataLength <= 0) {
                    dataLength = 10;
                }
                byte[] receiveBuffer = new byte[dataLength];// 接收数据数组
                if (CH340Driver.getDriver() == null && mStop) {
                    Log.e(TAG, "startReadThread: " + "设备未连接");
                    break;
                }
                // 读取缓存区的数据长度
                int length = CH340Driver.getDriver().ReadData(receiveBuffer, dataLength);
                messageEvent = new MessageEvent();
                if (length > 0) {
                    Message message = Message.obtain();
                    message.obj = receiveBuffer;
                    messageEvent.setData(receiveBuffer);
                    messageEvent.setMsg(CH340Constants.SUCCESS_DATA);
                    YxycEvent.init().post(messageEvent);
                } else {
                    // 无数据
                    messageEvent.setData(null);
                    messageEvent.setMsg(CH340Constants.FILD_DATA);
                    YxycEvent.init().post(messageEvent);
                }

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

    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void getEventBus(MessageEvent messageEvent) {

    }
}
