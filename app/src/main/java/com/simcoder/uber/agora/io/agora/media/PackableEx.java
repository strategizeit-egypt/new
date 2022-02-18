package com.simcoder.uber.agora.io.agora.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
