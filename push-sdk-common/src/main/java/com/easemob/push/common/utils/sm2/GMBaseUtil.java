package com.easemob.push.common.utils.sm2;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class GMBaseUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}
