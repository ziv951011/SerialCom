package com.yxyc.serial_library.driver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.yxyc.serial_library.logger.YXYCLog;
import com.yxyc.serial_library.runnable.ReadDataRunnable;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;


/**
 * Function:初始化ch340驱动
 */

public class CH340Driver {

    private static final String TAG = CH340Driver.class.getSimpleName();
    private static final String ACTION_USB_PERMISSION = "com.linc.USB_PERMISSION";
    private static final byte parity = 0;
    private static final byte stopBit = 1;
    private static final byte dataBit = 8;
    private static final int baudRate = 9600;
    private static final byte flowControl = 0;
    @SuppressLint("StaticFieldLeak")
    private static CH34xUARTDriver driver;
    private static boolean isOpenDeviceCH340 = false;
    private static ReadDataRunnable readDataRunnable;
    private static UsbManager mUsbManager;
    private static IUsbPermissionListener listener;
    private static UsbDevice mUsbDevice;
    private static final ExecutorService mThreadPool = Executors.newSingleThreadExecutor();
    public static UsbDevice getmUsbDevice() {
        return mUsbDevice;
    }

    public static void setListener(IUsbPermissionListener listener) {
        CH340Driver.listener = listener;
    }

    /**
     * initialize ch340 parameters.
     *
     * @param context Application context.
     */
    public static void initCH340(Context context, int length) {
        if (context == null) return;
        Context appContext = context.getApplicationContext();
        mUsbManager = (UsbManager) appContext.getSystemService(Context.USB_SERVICE);
        if (mUsbManager != null) {
            HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
            YXYCLog.e(TAG, "deviceHashMap.size()=" + deviceHashMap.size());
            for (UsbDevice device : deviceHashMap.values()) {
                YXYCLog.i(TAG, "ProductId:" + device.getProductId() + ",VendorId:" + device.getVendorId());
                if (device.getProductId() == 29987 && device.getVendorId() == 6790) {
                    mUsbDevice = device;
                    if (mUsbManager.hasPermission(device)) {
                        loadDriver(appContext, mUsbManager, length);
                    } else {
                        if (listener != null) {
                            listener.result(false);
                        }
                    }
                    break;
                }
            }
        }
    }

    /**
     * load ch340 driver.
     *
     * @param appContext
     * @param usbManager
     * @param length
     */
    public static void loadDriver(Context appContext, UsbManager usbManager, int length) {
        driver = new CH34xUARTDriver(usbManager, appContext, ACTION_USB_PERMISSION);
        // 判断系统是否支持USB HOST
        if (!driver.UsbFeatureSupported()) {
            YXYCLog.e(TAG, "Your mobile phone does not support USB HOST, please change other phones to try again!");
        } else {
            openCH340(length);
        }
    }

    /**
     * config and open ch340.
     *
     * @param length
     */
    private static void openCH340(int length) {
        int ret_val = driver.ResumeUsbList();
        YXYCLog.d(TAG, ret_val + "");
        // ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
        if (ret_val == -1) {
            YXYCLog.d(TAG, ret_val + "Failed to open device!");
            driver.CloseDevice();
        } else if (ret_val == 0) {
            if (!driver.UartInit()) {  //对串口设备进行初始化操作
                YXYCLog.d(TAG, ret_val + "Failed device initialization!");
                YXYCLog.d(TAG, ret_val + "Failed to open device!");
                return;
            }
            YXYCLog.d(TAG, ret_val + "Open device successfully!");
            if (!isOpenDeviceCH340) {
                isOpenDeviceCH340 = true;
                configParameters(length);//配置ch340的参数、需要先配置参数
            }
        } else {
            YXYCLog.d(TAG, "The phone couldn't find the device！");
        }
    }

    /**
     * config ch340 parameters.
     * 配置串口波特率，函数说明可参照编程手册
     *
     * @param length
     */
    private static void configParameters(int length) {
        boolean isSetConfig = driver.SetConfig(baudRate, dataBit, stopBit, parity, flowControl);
        if (isSetConfig) {
            YXYCLog.i(TAG, "Serial port Settings success~");
            if (readDataRunnable == null) {
                readDataRunnable = new ReadDataRunnable();
                readDataRunnable.setDataLength(length);
            }
            mThreadPool.execute(readDataRunnable);

        } else {
            YXYCLog.e(TAG, "Serial port Settings failed！");
        }
    }

    /**
     * 关闭线程池
     */
    public static void stopRead() {
        if (!mThreadPool.isShutdown()) {
            mThreadPool.shutdown();
        }
    }

    /**
     * ch340 is or not open.
     *
     * @return
     */
    public static boolean isIsOpenDeviceCH340() {
        return isOpenDeviceCH340;
    }

    /**
     * get ch340 driver.
     *
     * @return
     */
    public static CH34xUARTDriver getDriver() {
        return driver;
    }

    public static UsbManager getmUsbManager() {
        return mUsbManager;
    }

    public interface IUsbPermissionListener {
        void result(boolean isGranted);
    }
}
