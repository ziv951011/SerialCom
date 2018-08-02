package com.yxyc.ch340_host;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yxyc.serial_library.CH340Application;
import com.yxyc.serial_library.driver.CH340Driver;
import com.yxyc.serial_library.event.MessageEvent;
import com.yxyc.serial_library.event.YxycEvent;
import com.yxyc.serial_library.utils.CH340Constants;
import com.yxyc.serial_library.utils.CH340Util;
import com.yxyc.serial_library.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;


/**
 * 如果开发者只需要接收单片机发送的数据
 * 长度传递0即可
 */
public class MainActivity extends AppCompatActivity implements CH340Driver.IUsbPermissionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean isFirst;//判断是否打开
    private Button btnSend;
    private EditText etContent;
    private static final String ACTION_USB_PERMISSION = "com.linc.USB_PERMISSION";
    // 设置发送数据的长度 调用者自定义即可
    private static final int LENGTH = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSend = findViewById(R.id.btnSend);
        etContent = findViewById(R.id.etContent);
        initData();
        initListener();
        EventBus.getDefault().register(this);
    }

    private void initListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void getEventBus(MessageEvent messageEvent) {
        if (messageEvent.getMsg() == CH340Constants.SUCCESS_DATA) {
            /**
             * 校验两组数据是否相同
             *
             * ff fe 08 83 c6 01 00 4b
             */
            byte[] bs = {(byte) 0xff, (byte) 0xfa, 0x08, (byte) 0x83, (byte) 0xc6, 0x01, 0x00, 0x4b};
            byte[] data = messageEvent.getData();

            if (Arrays.equals(bs, data)) {
                Log.e(TAG, "getEventBus: " + "相同");
            }
            Log.e(TAG, CH340Util.bytesToHexString(data, data.length));

        }


    }

    private void sendData() {
        String string = etContent.getText().toString();
        if (!TextUtils.isEmpty(string)) {
            byte[] bytes = StringUtils.hexStringToByte(string);
            int i = CH340Util.writeData(bytes);
            if (i != -10)
                Toast.makeText(MainActivity.this, "数据为:" + i, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "请检查您的设备连接情况!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "发送的数据不能为空！", Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        CH340Driver.setListener(this);
        if (!isFirst) {
            isFirst = true;
            // 假设数据长度为10
            CH340Application.initialize(MyApplication.getContext(), LENGTH);
        }

        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, usbFilter);
    }

    @Override
    public void result(boolean isGranted) {
        if (!isGranted) {
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            CH340Driver.getmUsbManager().requestPermission(CH340Driver.getmUsbDevice(), mPermissionIntent);
        }
        Toast.makeText(MainActivity.this, "is:" + isGranted, Toast.LENGTH_SHORT).show();
    }


    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Toast.makeText(MainActivity.this, action, Toast.LENGTH_SHORT).show();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            /**
                             * 设置数据
                             */
                            Toast.makeText(MainActivity.this, "设备授权成功", Toast.LENGTH_SHORT).show();
                            CH340Driver.loadDriver(MyApplication.getContext(), CH340Driver.getmUsbManager(), LENGTH);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "设备授权失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CH340Driver.stopRead();
        unregisterReceiver(mUsbReceiver);
        EventBus.getDefault().unregister(this);
    }
}
