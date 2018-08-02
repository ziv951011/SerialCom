package com.yxyc.serial_library.event;




public class MessageEvent{

    private int msg;
    private byte[] data;

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


}
