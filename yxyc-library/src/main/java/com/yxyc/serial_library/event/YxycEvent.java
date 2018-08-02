package com.yxyc.serial_library.event;


import org.greenrobot.eventbus.EventBus;

public class YxycEvent {
    public static EventBus init() {
        return EventBus.getDefault();
    }

}
