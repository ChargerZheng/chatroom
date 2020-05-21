package com.zjc.chatroom.component;


import com.alibaba.fastjson.JSON;
import com.zjc.chatroom.domain.MessageEntity;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MyChatEncoder implements Encoder.Text<MessageEntity> {

    @Override
    public void init(EndpointConfig endpointConfig) { }

    @Override
    public void destroy() { }

    @Override
    public String encode(MessageEntity messageEntity){
        return JSON.toJSONString(messageEntity);
    }
}
