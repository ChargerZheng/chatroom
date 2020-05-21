package com.zjc.chatroom.component;

import com.zjc.chatroom.domain.MessageEntity;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;



public class MyChatDecoder implements Decoder.Text<MessageEntity> {

    @Override
    public MessageEntity decode(String s) {
        return new MessageEntity();
    }

    @Override
    public boolean willDecode(String s) {
        return false;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
