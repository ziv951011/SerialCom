package com.yxyc.serial_library.event;

import com.yxyc.serial_library.MyEventBusIndex;

import org.greenrobot.eventbus.EventBus;

public class YxycEvent {
    public static EventBus init() {
        // 开启加速模式
        return EventBus.getDefault();
    }

}
