package com.easemob.push.server.model.sender;

public enum PushStrategy {
    // 厂商通道优先，厂商失败选择环信通道
    VENDOR_EASEMOB_CHANNEL(0),

    // 只使用环信通道 (默认选择)
    EASEMOB_CHANNEL(1),

    // 只使用厂商通道（失败后丢弃）
    VENDOR_CHANNEL(2),

    // 环信通道优先、离线后选择厂商通道
    EASEMOB_VENDOR_CHANNEL(3);

    private int value;

    PushStrategy(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
