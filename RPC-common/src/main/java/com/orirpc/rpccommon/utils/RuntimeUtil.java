package com.orirpc.rpccommon.utils;

public class RuntimeUtil {

    /**
     * 获取cpu核心数
     * @return
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
