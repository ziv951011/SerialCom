Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.ziv951011:SerialCom:2.1'
	}

==============================================================================================================
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(MessageEvent messageEvent) {
        if (messageEvent.getMsg() == CH340Constants.SUCCESS_DATA) {

            /**
             * 校验两组数据是否相同
             */
            byte[] bs = {(byte) 0xff, (byte) 0xfe, 0x6a, 0x38, 0x25, (byte) 0x85, 0x33, (byte) 0x8e, 0x6f, 0x55};
            byte[] data = messageEvent.getData();
            Arrays.sort(bs);
            Arrays.sort(data);
            if (Arrays.equals(bs, data)) {
                Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_LONG).show();

                // 执行自己的代码部分
            }
            Log.e("=====", CH340Util.toHexString(messageEvent.getData()));
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
        unregisterReceiver(mUsbReceiver);
    }
