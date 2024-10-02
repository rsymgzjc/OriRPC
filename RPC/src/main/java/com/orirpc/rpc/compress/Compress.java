package com.orirpc.rpc.compress;

import com.orirpc.rpccommon.extension.SPI;

@SPI
public interface Compress {

    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
